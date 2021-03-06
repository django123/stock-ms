package liss.nvms.services;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import liss.nvms.model.CommandCustomerEntity;
import liss.nvms.model.CommandLineCustomerEntity;
import liss.nvms.repository.CommandCustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.icu.text.DecimalFormat;

import liss.nvms.Clivraison.DeliveryCustomerEntity;
import liss.nvms.Clivraison.DeliveryLineCustomerEntity;
import liss.nvms.additional.DeliveryCustomerRepository;
import liss.nvms.date.DateUtils;
import liss.nvms.httpException.HttpServiceExceptionHandle;
import liss.nvms.invoice.classes.InvoiceCustomerClass;
import liss.nvms.model.StockEntity;
import liss.nvms.repository.StockRepository;
import liss.nvms.utils.HttpErrorCodes;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@Service
@Transactional(rollbackFor = {HttpServiceExceptionHandle.class, SQLException.class }, noRollbackFor = EntityNotFoundException.class)
public class DeliveryCustomerService {
	
	@Autowired DeliveryCustomerRepository deliverycustomerRep;
	@Autowired StockRepository stockrep;
	@Autowired CommandCustomerRepository commandCustomerRep;

	/** Ajouter une note de livraison **/
	public DeliveryCustomerEntity AddDeliveryCustomer(DeliveryCustomerEntity delivery) {
		
		try {
			String code = "LREF"+System.currentTimeMillis();
			delivery.setReference(code);
			delivery.getDeliveryLines().stream().map(item ->{
				StockEntity stockActuel = stockrep.getStockById(item.getStock().getId());
				if(item.getQuantity()  > stockActuel.getQuantity()) {
					String message = "Quantité demandé pour le produit : "+stockActuel.getProduct().getName()+"-"+stockActuel.getFormat().getName()
							          +" est superieur au stock actuel ("+stockActuel.getQuantity()+")";
					throw new HttpServiceExceptionHandle(message,HttpErrorCodes.INTERNAL_SERVER_ERROR);
				}else {
					Long aux = stockActuel.getQuantity() - item.getQuantity();
					stockActuel.setQuantity(aux);
					return stockrep.save(stockActuel);
				}
				
			}).collect(Collectors.toList());
			
			return deliverycustomerRep.save(delivery);
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
	}   
	
	
	/** afficher les notes de livraisons **/
	public  Map<String, Object> getAllDeliveryNote(String code, String customer, Date date1, Date date2, int page, int limit) {
		
		try {
			
			Map<String, Object> results = new HashMap<>();
			Pageable paging = PageRequest.of( page, limit);
	
			 Page<DeliveryCustomerEntity> stockPage = null;
			 if(code != null) stockPage = deliverycustomerRep.getAllDeliveryCustomerByCmdeCode(paging, code);
			 else {
				 if(customer == null && (date1 != null && date2 != null)) stockPage = deliverycustomerRep.getAllDeliveryCustomerByCreatedDate(paging, date1, date2);
				 else if(customer != null && (date1 == null && date2 == null)) stockPage = deliverycustomerRep.getAllNoteDeliveryCustomer(paging, customer);
				 else if(customer != null && (date1 != null && date2 != null)) stockPage = deliverycustomerRep.getAllDeliveryCustomerByCreatedDateCustomer(paging, customer,date1, date2);
				 else stockPage = deliverycustomerRep.getAllDeliveryCustomer(paging);
			 }
			 
			 results.put("data", stockPage.getContent().stream().map(item -> {
				 Map<String, Object> param = new HashMap<>();
				 param.put("reference", item.getId());
				 param.put("reference", item.getId());
				 param.put("customer", item.getCommand().getCustomer().getName());
				 param.put("code", item.getReference());
				 param.put("command", item.getCommand().getCode());
				 return param;
			   }).collect(Collectors.toList())
			 );
			 results.put("currentPage", stockPage.getNumber());
			 results.put("totalItems", stockPage.getTotalElements());
			 results.put("totalPages", stockPage.getTotalPages());
			 
			 
			return results;
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes _code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),_code);
		}
		
	}
	
	/** Valider une note de livraison **/
	public int ValidateDeliveryCustomer(String ref) {
		try {
			DeliveryCustomerEntity delivery = deliverycustomerRep.getDeliveryCustomerById(ref);
			if(delivery == null) throw new HttpServiceExceptionHandle("Erreur",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			return deliverycustomerRep.validateDeliveryCustomer(ref);
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
	}
	
	
	/**** generer un bon d livraison @throws JRException *****/
	public byte[] generateDeliveryInvoice(String deliveryID) throws JRException {

		try {
			byte[] bytes = null;
			JasperPrint print = new JasperPrint();
			InputStream jasperStream = null;
			List<InvoiceCustomerClass> tableInvoice = new ArrayList<>();
			CommandCustomerEntity cmd = commandCustomerRep.getCommandeCustomerById(deliveryID);
			if(cmd == null) throw new HttpServiceExceptionHandle("",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			Map<String, Object> results = new HashMap<>();
			Double sum = 0.0;
			for (CommandLineCustomerEntity line : cmd.getCommandLineCustomers()) {
				if(line.getPrice() != null) {
					String designation = line.getStock().getProduct().getName() + " " + line.getStock().getFormat().getName();
					Double sumLine = line.getQuantity() * ( line.getPrice() - line.getRemise());
					sum += sumLine;
					tableInvoice.add( new InvoiceCustomerClass(line.getStock().getCode(),line.getQuantity(), line.getPrice(), line.getRemise(), designation, sumLine));
				}
			}

			// Get your data source
			JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(tableInvoice);

			results.put("data", jrBeanCollectionDataSource);
			results.put("reference", cmd.getId());
			results.put("invoiceType", "Note de Livraison");
			results.put("numero", (cmd.getCode() == null ? "xxx" : cmd.getCode())  );
			results.put("date", DateUtils.formatDateStrg(cmd.getDeliveryDate()));
			results.put("clientNumber", cmd.getCustomer().getCode());
			results.put("customerName", cmd.getCustomer().getName());
			results.put("adresse", cmd.getCustomer().getAdress());
			results.put("codePostal", cmd.getCustomer().getPostalCode() + "" + cmd.getCustomer().getCity());
			results.put("pays", cmd.getCustomer().getPostalCode());
			results.put("base", new DecimalFormat("####0.00").format(sum));
			results.put("taxable", new DecimalFormat("####0.00").format(sum));
			results.put("total", new DecimalFormat("####0.00").format(sum));
			Double sumTva = (cmd.getTva() != null ? sum * cmd.getTva() : 0.0);
			results.put("tva", new DecimalFormat("####0.00").format(sumTva));
			results.put("totalTva", sumTva);
			results.put("SumTotal",  new DecimalFormat("####0.00").format(sum + sumTva));
			results.put("echeance", DateUtils.formatDateStrg(cmd.getDeadLine()));

			jasperStream = (InputStream) this.getClass().getResourceAsStream("/CustomerNote.jasper");
			//Créer l'objet JaperReport avec le flux à partir du fichier jasper
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
			// Transmettez le rapport, les paramètres et la source de données à JasperPrint en cas de connexion à la base de données.
			print =  JasperFillManager.fillReport(jasperReport, results, new JREmptyDataSource());
			// return the PDF in bytes
			bytes = JasperExportManager.exportReportToPdf(print);
			return bytes;
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}

	}




}
