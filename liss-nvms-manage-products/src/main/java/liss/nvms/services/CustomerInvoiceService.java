package liss.nvms.services;


import liss.nvms.date.DateUtils;
import liss.nvms.date.GenerateReference;
import liss.nvms.httpException.HttpServiceExceptionHandle;
import liss.nvms.invoice.classes.InvoiceCustomerClass;
import liss.nvms.model.CommandCustomerEntity;
import liss.nvms.model.CommandLineCustomerEntity;
import liss.nvms.model.CustomerEntity;
import liss.nvms.model.InvoiceCustomerEntity;
import liss.nvms.model.StockEntity;
import liss.nvms.repository.CommandCustomerRepository;
import liss.nvms.repository.InvoiceRepository;
import liss.nvms.utils.HttpErrorCodes;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = {HttpServiceExceptionHandle.class, SQLException.class }, noRollbackFor = EntityNotFoundException.class)
public class CustomerInvoiceService {

    @Autowired InvoiceRepository invoiceRep;
    @Autowired CommandCustomerRepository cmdCusRep;
  
    
    /*** afficher les factures**/
    public  Map<String, Object> getAllCustomerInvoice(String code, String customer, Date date1, Date date2, int page, int limit) {
		
		try {
			
			Map<String, Object> results = new HashMap<>();
			Pageable paging = PageRequest.of( page, limit);
	
			 Page<InvoiceCustomerEntity> stockPage = null;
			 if(code != null) stockPage = invoiceRep.getAllInvoiceCustomerByinvoiceeCode(paging, code);
			 else {
				 if(customer == null && (date1 != null && date2 != null)) stockPage = invoiceRep.getAllInvoiceCustomerByCreatedDate(paging, date1, date2);
				 else if(customer != null && (date1 == null && date2 == null)) stockPage = invoiceRep.getAllInvoiceCustomer(paging, customer);
				 else if(customer != null && (date1 != null && date2 != null)) stockPage = invoiceRep.getAllInvoiceCustomerByCreatedDateCustomer(paging, customer,date1, date2);
				 else stockPage = invoiceRep.getAllInvoiceCustomer(paging);
			 }
			 
			 if(stockPage == null) return new HashMap<>();
			 results.put("data", stockPage.getContent());
			 results.put("currentPage", stockPage.getNumber());
			 results.put("totalItems", stockPage.getTotalElements());
			 results.put("totalPages", stockPage.getTotalPages());
			 
			 return results;

		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes _code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),_code);
		}
		
	}
    
    
    /** afficher les commandes a facturer **/
    public List<CommandCustomerEntity> getCmdWhoNothaveInvoice(String param) {
    	try {
    		if(param == null) param = "";
    		return  cmdCusRep.getCmdWhoNotHaveInvoice(param);
    	}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
    }
   
    /*** ajouter une commande customer **/
    public  InvoiceCustomerEntity addCustomerInvoice(InvoiceCustomerEntity invoice) {
		try {
			int nbreInvoice = invoiceRep.countCustomerInvoice();
			String code = GenerateReference._fGenerateRef(nbreInvoice+1, "F");
			invoice.setCode(code);
			cmdCusRep.validateCommandeCustomer(invoice.getCommandCustomer().getId());
			return invoiceRep.save(invoice);
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}
    
    /************* Modifier une facture client ***************/
    public  InvoiceCustomerEntity updateInvoiceCustomer(InvoiceCustomerEntity invoice) {
		try {
			
			InvoiceCustomerEntity invoiceCustomer = invoiceRep.getInvoiceCustomerById(invoice.getId()); 
			
			if(invoiceCustomer == null) throw new HttpServiceExceptionHandle("facture introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
    		invoiceCustomer.setDeadLine(invoice.getDeadLine());
    		invoiceCustomer.setDeliveryDate(invoice.getDeliveryDate());
    		invoiceCustomer.setTaxable(invoice.getTaxable());
    		invoiceCustomer.setTva(invoice.getTva());
    		return invoiceRep.save(invoiceCustomer);
			
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}
   
    /** supprimer facture client **/
    public int deleteInvoiceCustomer(String invoiceRef) {
    	try {
    		InvoiceCustomerEntity invoice = invoiceRep.getInvoiceCustomerById(invoiceRef);
    		if(invoice == null) throw new HttpServiceExceptionHandle("Facture introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
    		invoice.setIsDeleted(true);
    		int ux = cmdCusRep.inValidateCommandeCustomer(invoice.getCommandCustomer().getId());
    		if(ux != 0) invoiceRep.save(invoice);
    		else throw new HttpServiceExceptionHandle("Commande dans la facture introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			return 	ux;
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
    }
    
   /**** generation PDF Facture  
 * @throws JRException *****/
    public byte[] generatePdfInvoice(String commandeID) throws JRException {
    	
    	try {
    		byte[] bytes = null;
    		JasperPrint print = new JasperPrint();	
    	    InputStream jasperStream = null;
    	    List<InvoiceCustomerClass> tableInvoice = new ArrayList<>();
    		CommandCustomerEntity cmd = null;// commandCustomerRep.getCommandeCustomerById(commandeID);
    		if(cmd == null) throw new HttpServiceExceptionHandle("Commande introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
    		Map<String, Object> results = new HashMap<>();
    		 Double sum = 0.0;
			 for (CommandLineCustomerEntity line : cmd.getCommandLineCustomers()) {
				if(line.getPrice() != null) {
					String designation = line.getStock().getProduct().getName() + " " + line.getStock().getFormat().getName();
					Double sumLine = line.getQuantity() * ( line.getPrice() - line.getRemise());
					sum += sumLine;
					tableInvoice.add( new InvoiceCustomerClass(line.getStock().getCode(),  line.getQuantity(), line.getPrice(), line.getRemise(), designation, sumLine));
				}
			 }
		   // Get your data source
			JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(tableInvoice);
				
		     results.put("data", jrBeanCollectionDataSource);
		     results.put("invoiceType", "Facture");
		     results.put("reference", cmd.getId());
		     results.put("validate", cmd.getIsValid());
		     results.put("numero", cmd.getCode());
		     results.put("date",  DateUtils.formatDateStrg(cmd.getDeliveryDate()));
		     results.put("clientNumber", cmd.getCustomer().getCode());
		     results.put("customerName", cmd.getCustomer().getName());
		     results.put("adresse", cmd.getCustomer().getAdress());
		     results.put("codePostal", cmd.getCustomer().getPostalCode() + "" + cmd.getCustomer().getCity());
		     results.put("pays", cmd.getCustomer().getPostalCode());
		     results.put("total", sum);
		     results.put("echeance",  DateUtils.formatDateStrg(cmd.getDeadLine()));
		    
		     jasperStream = (InputStream) this.getClass().getResourceAsStream("/CustomerInvoice.jasper");
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
