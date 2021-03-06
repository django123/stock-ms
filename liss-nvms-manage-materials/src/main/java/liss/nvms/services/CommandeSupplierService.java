package liss.nvms.services;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import liss.nvms.controllers.NoteSupplierClass;
import liss.nvms.date.DateUtils;
import liss.nvms.model.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import liss.nvms.httpException.HttpServiceExceptionHandle;
import liss.nvms.repository.CommandLineSupplierRepository;
import liss.nvms.repository.CommandSupplierRepository;
import liss.nvms.repository.StockRepository;
import liss.nvms.utils.HttpErrorCodes;


@Service
@Transactional(rollbackFor = {HttpServiceExceptionHandle.class, SQLException.class }, noRollbackFor = EntityNotFoundException.class)
public class CommandeSupplierService {
	@Autowired StockRepository stockrep;
	@Autowired CommandLineSupplierRepository cmdLineSupRep;
	@Autowired CommandSupplierRepository cmdSupRep;
	@Autowired CommandLineSupplierRepository cmdLineSuplRep;
	
	/*** afficher les commande fournisseurs **/
    public  Map<String, Object> getCommandes(String code, String fournisseur, Date date1, Date date2, int page, int limit) {
		
		try {
			
			Map<String, Object> results = new HashMap<>();
			Pageable paging = PageRequest.of( page, limit);
	
			 Page<CommandSupplierEntity> stockPage = null;
			 if(code != null) stockPage = cmdSupRep.getAllCommandeSupplierByCmdeCode(paging, code);
			 else {
				 if(fournisseur == null && (date1 != null && date2 != null)) stockPage = cmdSupRep.getAllCommandeSupplierByCreatedDate(paging, date1, date2);
				 else if(fournisseur != null && (date1 == null && date2 == null)) stockPage = cmdSupRep.getAllCommandeSupplier(paging, fournisseur);
				 else if(fournisseur != null && (date1 != null && date2 != null)) stockPage = cmdSupRep.getAllCommandeSupplierByCreatedDateSupplier(paging, fournisseur,date1, date2);
				 else stockPage = cmdSupRep.getAllCommandeSupplier(paging);
			 }
			 
			 if(stockPage == null) return new HashMap<>();
			 results.put("data", stockPage.getContent().stream().map(item ->{
				 Map<String, Object> param = new HashMap<>();
				 Double sum = 0.0;
				 for (CommandLineSupplierEntity line : item.getCommandLineSuppliers()) {
					if(line.getPrice() != null && line.getBottleQuantity() != null) sum += line.getBottleQuantity() * line.getPrice();
				 }
				 param.put("product_number", item.getCommandLineSuppliers().size());
				 param.put("dead_line", item.getDeadLine());
				 param.put("delivery_date", item.getDeliveryDate());
				 param.put("validate", item.getIsValid());
				 param.put("sum", sum);
				 param.put("reference", item.getId());
				 param.put("code", item.getCode());
				 param.put("supplier", item.getSupplier());
				 
				 return param;
			 }).collect(Collectors.toList()));
			 results.put("currentPage", stockPage.getNumber());
			 results.put("totalItems", stockPage.getTotalElements());
			 results.put("totalPages", stockPage.getTotalPages());
			 
			 return results;

		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes _code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),_code);
		}
		
	}
    
    /*** afficher les details d'une commande **/
    public  Map<String, Object> getDetailsCommande(String commandeID){
    	
    	try {
			
    		CommandSupplierEntity cmd = cmdSupRep.getCommandeSupplierById(commandeID);
    		if(cmd == null) throw new HttpServiceExceptionHandle("",HttpErrorCodes.INTERNAL_SERVER_ERROR);
    		Map<String, Object> results = new HashMap<>();
    		 Long sumBox = 0L; Long sumBotle = 0L;
    		 Double sum = 0.0;
			 for (CommandLineSupplierEntity line : cmd.getCommandLineSuppliers()) {
				if(line.getPrice() != null && line.getBottleQuantity() != null) sum += line.getBottleQuantity() * line.getPrice();
				if(line.getBottleQuantity() != null) sumBotle += line.getBottleQuantity();
				if(line.getQuantityLayer() != null && line.getBoxLayer() != null) sumBox += line.getQuantityLayer() * line.getBoxLayer();
			 }
		     results.put("data", cmd.getCommandLineSuppliers());
		     results.put("validate", cmd.getIsValid());
		     results.put("amount", sum);
		     results.put("sumBox", sumBox);
		     results.put("sumBotle", sumBotle);
		     results.put("code", cmd.getCode());
		     results.put("supplier", cmd.getSupplier());
		 
			 return results;
    		
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
    	
    }
    
    /*** ajouter une commande fournisseurs **/
    public  CommandSupplierEntity addCommande(CommandSupplierEntity cmd) {
		try {
			String code = "NVF"+System.currentTimeMillis();
			cmd.setCode(code);
			return cmdSupRep.save(cmd);
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}
    
    /** valider une commande supplier **/
    public int ValidateCommandSupplier(String commandID) {
    	try {
    		CommandSupplierEntity cmd = cmdSupRep.getCommandeSupplierById(commandID);
    		if(cmd == null) throw new HttpServiceExceptionHandle("",HttpErrorCodes.INTERNAL_SERVER_ERROR);
    		cmd.getCommandLineSuppliers().stream().map(line ->{
			 	 Long QteStock = line.getBottleQuantity() + line.getStock().getQuantity();
				 	 StockEntity stock = stockrep.getStockById(line.getStock().getId());
			 	if(stock == null) throw new HttpServiceExceptionHandle("Un produit est introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			 	 stock.setQuantity(QteStock);
				 	 return stockrep.save(stock);
    		}).collect(Collectors.toList());
    		
			return cmdSupRep.validateCommandeSupplier(commandID);
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
    }
    
    /** Modifier une line de produit pour le fournisseur **/
    public CommandSupplierEntity UpdateLineCommandSupplier(CommandLineSupplierEntity line) {
    	
    	try {
    		
    		if(line.getId() == null) throw new HttpServiceExceptionHandle("Command introuval",HttpErrorCodes.INTERNAL_SERVER_ERROR);
    		CommandLineSupplierEntity cmdLine = cmdLineSupRep.getCommandeLineSupplierById(line.getId());
    		if(cmdLine == null) throw new HttpServiceExceptionHandle("Command introuval",HttpErrorCodes.INTERNAL_SERVER_ERROR);
    		if(line.getStock().getId() == null) throw new HttpServiceExceptionHandle("Produit en stock introuval",HttpErrorCodes.INTERNAL_SERVER_ERROR);
    		
    		cmdLine.setBottleQuantity(line.getBottleQuantity());
    		cmdLine.setBoxLayer(line.getBoxLayer());
    		cmdLine.setPrice(line.getPrice());
    		cmdLine.setQuantityBoxe(line.getQuantityBoxe());
    		cmdLine.setQuantityLayer(line.getQuantityLayer());
    		cmdLine.setStock(line.getStock());
    		
    		
    		cmdLine= cmdLineSupRep.save(cmdLine);
    		
    		return cmdLine.getCommand_supplier();
    		
    		
    		
    	}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
    }
    
    /** modifier une commande fournisseur **/
    
    public CommandSupplierEntity updateCommandSupplier(CommandSupplierEntity cmd) {
    	
		try {
			    CommandSupplierEntity cmdSup  = cmdSupRep.getCommandeSupplierById(cmd.getId()); 
				if(cmdSup  == null) new HttpServiceExceptionHandle("command introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
				cmdSup.setDeadLine(cmd.getDeadLine());
				cmdSup.setDeliveryDate(cmd.getDeliveryDate());
				return cmdSupRep.save(cmdSup);
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}

    }
    
    
    /**** supression d'une commande fournisseur **/
    public Map<String, Object> deleteSupplierCmd(String cmmdId) {
    	try {
    		
    		int val = cmdSupRep.deleteCommandeSupplier(cmmdId);
    		if(val != 0) return getDetailsCommande(cmmdId);
			return 	null;
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
    }
    
    
    /**** supression d'une line commande fournisseur **/
    public Map<String, Object> deleteLineSupplierCmd(String cmmdId) {
    	
    	try {
    		
    		 CommandLineSupplierEntity line = cmdLineSuplRep.getCommandeLineSupplierById(cmmdId);
    		
    		int val = cmdLineSuplRep.deleteCommandeLineSupplier(cmmdId);
  
    		if(val != 0) {
 
    			return getDetailsCommande(line.getCommand_supplier().getId());
    		}
			return 	null;
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
    	
    }


	/**** generation rapport line de commande d'un supplier
	 * @throws JRException *****/
	public byte[] generateNote(String commandeID) throws JRException{
		try{
			byte[] bytes = null;
			JasperPrint print = new JasperPrint();
			InputStream jasperStream = null;
			List<NoteSupplierClass> tableInvoice = new ArrayList<>();
			CommandSupplierEntity cmd = cmdSupRep.getCommandeSupplierById(commandeID);
			if(cmd == null) throw new HttpServiceExceptionHandle("",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			Map<String, Object> results = new HashMap<>();
			Double sum = 0.0;
			Long box = 0L;
			for (CommandLineSupplierEntity line : cmd.getCommandLineSuppliers()) {
				if(line.getPrice() != null) {
					String designation = line.getStock().getProduct().getName() + " " + line.getStock().getFormat().getName();
					Double sumLine = line.getBottleQuantity() * line.getPrice();
					sum += sumLine;
					Long boxTotal = line.getBoxLayer() * line.getQuantityLayer();
					tableInvoice.add( new NoteSupplierClass(line.getBoxLayer(), line.getQuantityLayer(), boxTotal, line.getBottleQuantity(), line.getPrice(), designation, sumLine));
				}
			}
			// Get your data source
			JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(tableInvoice);
			results.put("data", jrBeanCollectionDataSource);
			results.put("invoiceType", "Note Livraison");
			results.put("validate", cmd.getIsValid());
			results.put("adresse", cmd.getSupplier().getAdress());
			results.put("numero", cmd.getCode());
			results.put("customerName", cmd.getSupplier().getName());
			results.put("codePostal", cmd.getSupplier().getPostal_code() + "" + cmd.getSupplier().getCity());
			results.put("date",  DateUtils.formatDateStrg(cmd.getDeliveryDate()));
			results.put("pays", cmd.getSupplier().getPostal_code());
			results.put("total", sum);
			results.put("echeance",  DateUtils.formatDateStrg(cmd.getDeadLine()));
			jasperStream = (InputStream) this.getClass().getResourceAsStream("/SupplierInvoice.jasper");
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
