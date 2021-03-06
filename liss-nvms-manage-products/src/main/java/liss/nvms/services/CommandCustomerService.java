package liss.nvms.services;

import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import liss.nvms.model.*;
import liss.nvms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.icu.text.DecimalFormat;

import liss.nvms.date.DateUtils;
import liss.nvms.date.GenerateReference;
import liss.nvms.httpException.HttpServiceExceptionHandle;
import liss.nvms.invoice.classes.InvoiceCustomerClass;
import liss.nvms.invoice.classes.ReportChartDog;
import liss.nvms.invoice.classes.ReportChartItem;
import liss.nvms.invoice.classes.ReportChartTable;
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
public class CommandCustomerService {
	
	@Autowired CustomerRepository custRep;
	@Autowired CommandCustomerRepository commandCustomerRep;
	@Autowired CommandLineCustomerRepository commandLineRep;
	@Autowired StockRepository stockrep;
	@Autowired InvoiceRepository invoiceRep;
	
	/*** afficher les commande customer **/
    public  Map<String, Object> getCommandes(String code, String customer, Date date1, Date date2, int page, int limit) {
		
		try {
			
			Map<String, Object> results = new HashMap<>();
			Pageable paging = PageRequest.of( page, limit);
	
			 Page<CommandCustomerEntity> stockPage = null;
			 if(code != null) stockPage = commandCustomerRep.getAllCommandeCustomerByCmdeCode(paging, code);
			 else {
				 if(customer == null && (date1 != null && date2 != null)) stockPage = commandCustomerRep.getAllCommandeCustomerByCreatedDate(paging, date1, date2);
				 else if(customer != null && (date1 == null && date2 == null)) stockPage = commandCustomerRep.getAllCommandeCustomer(paging, customer);
				 else if(customer != null && (date1 != null && date2 != null)) stockPage = commandCustomerRep.getAllCommandeCustomerByCreatedDateCustomer(paging, customer,date1, date2);
				 else stockPage = commandCustomerRep.getAllCommandeCustomer(paging);
			 }
			 
			 if(stockPage == null) return new HashMap<>();
			// Double sumP = 0.0; Double sumI = 0.0; Double SumAP = 0.0; Double SumAI = 0.0;
			 Double [] somme = {0.0, 0.0, 0.0, 0.0};
			 List<Map<String, Object>> rst = stockPage.getContent().stream().map(item -> {
				 Map<String, Object> param = new HashMap<>();
				 Double sum = 0.0; Double sumA = 0.0;
				 for (CommandLineCustomerEntity line : item.getCommandLineCustomers()) {
					if(line.getPrice() != null) {
						sum += line.getQuantity() * ( line.getPrice() - line.getRemise());
						sumA += line.getQuantity() * line.getStock().getPrice();
						if(item.getIsValid()) {
							somme[0] += line.getQuantity() * ( line.getPrice() - line.getRemise());
							somme[2] += line.getQuantity() * line.getStock().getPrice();
						}
						else {
							somme[1] += line.getQuantity() * ( line.getPrice() - line.getRemise());
							somme[3] += line.getQuantity() * line.getStock().getPrice();
						}
					}
				 }
				 param.put("product_number", item.getCommandLineCustomers().size());
				 param.put("sum", sum);
				 param.put("validate", item.getIsValid());
				 param.put("dead_line", item.getDeadLine());
				 param.put("tva", item.getTva());
				 param.put("delivery_date", item.getDeliveryDate());
				 param.put("achat", sumA);
				 param.put("reference", item.getId());
				 param.put("code", item.getCode());
				 param.put("Customer", item.getCustomer()); 
				 return param;
			 }).collect(Collectors.toList());
			 
			 
			 
			 results.put("cashed_sales", somme[0]);
			 results.put("purchase_cashed", somme[2]);
			 
			 results.put("recovered_sales", somme[1]);
			 results.put("recovered_purchase", somme[3]);
			 
			 results.put("data", rst);
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
			
    		CommandCustomerEntity cmd = commandCustomerRep.getCommandeCustomerById(commandeID);
    		if(cmd == null) throw new HttpServiceExceptionHandle("Commande introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
    		Map<String, Object> results = new HashMap<>();
    		 Double sum = 0.0;
			 for (CommandLineCustomerEntity line : cmd.getCommandLineCustomers()) {
				if(line.getPrice() != null) sum += line.getQuantity() * ( line.getPrice() - line.getRemise());
			 }
		     results.put("data", cmd.getCommandLineCustomers());
		     results.put("delivery_date", cmd.getDeliveryDate());
		     results.put("dead_line", cmd.getDeadLine());
		     results.put("tva", cmd.getTva());
		     results.put("reference", cmd.getId());
		     results.put("amount", sum);
		     results.put("validate", cmd.getIsValid());
		     results.put("code", cmd.getCode());
		     results.put("customer", cmd.getCustomer());
			 return results;
    		
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
    	
    }
    
    
    /*** ajouter une commande customer **/
    public  CommandCustomerEntity addCommande(CommandCustomerEntity cmd) {
		try {
			
		    int nbreCmd = commandCustomerRep.countCustomerCommand();
			String code = GenerateReference._fGenerateRef(nbreCmd+1, "L");
			cmd.setCode(code);
			cmd.getCommandLineCustomers().stream().map(item ->{
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
			return commandCustomerRep.save(cmd);
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}
    
    /************* Modifier une line de commande customer ***************/
    public  Map<String, Object> updateCommandeCustomer(CommandLineCustomerEntity newCommandLine) {
		try {
			
			if(newCommandLine.getId() == null) throw new HttpServiceExceptionHandle("Command introuval",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			CommandLineCustomerEntity line = commandLineRep.getCommandeLineCustomerById(newCommandLine.getId());
    		if(line == null) throw new HttpServiceExceptionHandle("Command introuval",HttpErrorCodes.INTERNAL_SERVER_ERROR);
    		
    		StockEntity stockActuel = stockrep.getStockById(newCommandLine.getStock().getId());
    		if(stockActuel == null) throw new HttpServiceExceptionHandle("Command introuval",HttpErrorCodes.INTERNAL_SERVER_ERROR);
    		
    		int diff = 0;
    		if(newCommandLine.getQuantity() > line.getQuantity()) diff = newCommandLine.getQuantity() - line.getQuantity();
    		else diff = line.getQuantity() - newCommandLine.getQuantity();
    		
    		if(diff  > stockActuel.getQuantity()) {
					String message = "Quantité demandé pour le produit : "+stockActuel.getProduct().getName()+"-"+stockActuel.getFormat().getName()
							          +" est superieur au stock actuel ("+stockActuel.getQuantity()+")";
					throw new HttpServiceExceptionHandle(message,HttpErrorCodes.INTERNAL_SERVER_ERROR);
    		}
    		
    		Long aux = stockActuel.getQuantity() - diff;
    		stockActuel.setQuantity(aux);
    		
			stockActuel = stockrep.save(stockActuel);
			line.setStock(stockActuel);
			line.setPrice(newCommandLine.getPrice());
			line.setQuantity(newCommandLine.getQuantity());
    		line.setRemise(newCommandLine.getRemise());
		
			
			return getDetailsCommande(line.getCommand_customer().getId());
			
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}
    
    /** valider une commande customer **/
    public int ValidateCommandCustomer(String commandID) {
    	try {
			return commandCustomerRep.validateCommandeCustomer(commandID);	
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
    }

    /*** faire le bilan de vente de l'entreprise **/
    public List< Map<String, Object> >bilanVenteEntreprise(Date date1, Date date2){
    	try {
    		int [] cpt = {0}; Double [] Gsomme = {0.0, 0.0};
    		/*** afiicher tous les clients ayanats commandé sur une periode **/
    		List<CustomerEntity> customers = commandCustomerRep.getCustomerWhoHaveCmd(date1, date2);
    		List< Map<String, Object> > map = customers.stream().map(custmer ->{
    			final String CUSTOMERID = custmer.getId();
    			Map<String, Object> param = new HashMap<>();
    			Double [] somme = {0.0, 0.0};
    			commandCustomerRep.getListCommandeCustomerByCreatedDateCustomer(CUSTOMERID, date1, date2).stream().map(command ->{
    				 for (CommandLineCustomerEntity line : command.getCommandLineCustomers()) {
    					if(line.getPrice() != null) {
    						if(command.getIsValid()) somme[0] += line.getQuantity() * ( line.getPrice() - line.getRemise());
    						else somme[1] += line.getQuantity() * ( line.getPrice() - line.getRemise());	
    					}
    				 }
    				 
    				 if(command.getTva() != null) {
    					 somme[1] += (somme[1] * command.getTva()) + somme[1];
    					 somme[0] += (somme[0] * command.getTva()) + somme[0];
    				 }
    
    				 return param;
        		}).collect(Collectors.toList());
    			cpt[0] += 1;
    			if(cpt[0] < 6) {
	    			 param.put("PAYER", somme[0]);
					 param.put("IMPAYER", somme[1]);
					 param.put("customer", custmer.getName());
					 return param;
    			}else {
    				Gsomme[0] += somme[0];
    				Gsomme[1] += somme[1];
    				return null;
    			}
				 
    		}).collect(Collectors.toList());
    		Map<String, Object> autres = new HashMap<>();
    		autres.put("PAYER", Gsomme[0]);
    		autres.put("IMPAYER", Gsomme[1]);
    		autres.put("customer", "Autres");
    		if(cpt[0] > 5) map.add(autres);
    		
    		return map;
    	}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
    	
    }
    
    
    /** imprimer le bilan de vente de l'entreprise **/
    
    public byte[] imprimerBilanVente(Date date1, Date date2) throws JRException {
    	   byte[] bytes = null;
		   JasperPrint print = new JasperPrint();
		   InputStream jasperStream = null;
	    try{
	    	List< Map<String, Object>> List = bilanVenteEntreprise(date1, date2);
	    	List< ReportChartItem > listCharts = new ArrayList<ReportChartItem>();
	    	List<ReportChartTable> listChartsTables = new ArrayList<>();
	    	List<ReportChartDog> listChartsDog = new ArrayList<>();
	        List.stream().map(item->{
	    		Double p = 0.0; Double i = 0.0; String customer = "";
	    		ReportChartTable reportChartTable = new ReportChartTable();
	    		if(item != null) {
		    		for (Map.Entry<String, Object> entry : item.entrySet()) {
		    			ReportChartItem reportItem = new ReportChartItem();
		    			if("PAYER".equals(entry.getKey())) {
		    				p = (Double) entry.getValue();
		    				reportChartTable.setPaye(new DecimalFormat("####0.00").format(p));
		    			}
		    			if("IMPAYER".equals(entry.getKey())) {
		    				i = (Double) entry.getValue();
		    				reportChartTable.setImpaye(new DecimalFormat("####0.00").format(i));
		    			}
		    			if("customer".equals(entry.getKey())) {
		    				customer = (String) entry.getValue();
		    				reportChartTable.setCustomer(customer);
		    			}
		    		 }  
		    		reportChartTable.setTotal(new DecimalFormat("####0.00").format(i+p));
		    		listChartsTables.add(reportChartTable);
		    		
		    		listChartsDog.add(new ReportChartDog(customer, p+i));
		    		
		    		listCharts.add(new ReportChartItem(customer,"PAYER", p));
		    		listCharts.add(new ReportChartItem(customer,"IMPAYER", i));
		    		listCharts.add(new ReportChartItem(customer,"GLOBAL", p+i));
	    		}
	    		return 1;
	    	}).collect(Collectors.toList());
	    	
	    	Map<String, Object> results = new HashMap<String, Object>();
	    	results.put("CHART_DATASET", new JRBeanCollectionDataSource(listCharts));
	    	results.put("dataTable", new JRBeanCollectionDataSource(listChartsTables));
	    	results.put("CHART_DOG", new JRBeanCollectionDataSource(listChartsDog));
	    	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	    	results.put("periode", formatter.format(date1)+" - "+formatter.format(date2));
	    	jasperStream = (InputStream) this.getClass().getResourceAsStream("/CustomerInvoiceChart.jasper");
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
    
    /** supprimer une commande customer **/
    public  Map<String, Object> deleteCmdCustomer(String commandID) {
    	try {
    		
    		int val = commandCustomerRep.deleteCommandeCustomer(commandID);
    		if(val != 0) return getDetailsCommande(commandID);
			return 	null;
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
    }
    
    
    /** supprimer une commande line customer **/
    public  Map<String, Object> deleteCmdLineCustomer(String commandID) {
    	try {
    		
    		CommandLineCustomerEntity line = commandLineRep.getCommandeLineCustomerById(commandID);
    		int val = commandLineRep.deleteCommandeLineCustomer(commandID);
    		if(val != 0) {
    			return getDetailsCommande(line.getCommand_customer().getId());
    		}
			return 	null;
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
    }


	/** supprimer une commande customer **/
    public boolean deleteCommandCustomer(String comment, boolean isReApprov, String reference){
    	try{

           CommandCustomerEntity cmdCustomer = commandCustomerRep.getCommandCustomerById(reference);
			if(cmdCustomer == null) throw new HttpServiceExceptionHandle("Commande introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			if(cmdCustomer.getIsDeleted() == true) cmdCustomer.setIsDeleted(false);
			else cmdCustomer.setIsDeleted(true);
			cmdCustomer.setComment(comment);
			if(isReApprov) {
				cmdCustomer.getCommandLineCustomers().stream().map(item -> {
					StockEntity stockActuel = stockrep.getStockById(item.getStock().getId());
					Long aux = stockActuel.getQuantity() + item.getQuantity();
					stockActuel.setQuantity(aux);
					return stockrep.save(stockActuel);
				}).collect(Collectors.toList());
			}
    		return true;
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
	}
    
    
    
    
   /**** generation facture pdf d'un client
 * @throws JRException *****/

   public byte[] generateInvoice(String commandeID) throws JRException {

	   try {
		   byte[] bytes = null;
		   JasperPrint print = new JasperPrint();
		   InputStream jasperStream = null;
		   List<InvoiceCustomerClass> tableInvoice = new ArrayList<>();

		   InvoiceCustomerEntity cmd =  invoiceRep.getInvoiceCustomerById(commandeID);//commandCustomerRep.getCommandeCustomerById(commandeID);
		   if(cmd == null) throw new HttpServiceExceptionHandle("Facture introuvable pour impression",HttpErrorCodes.INTERNAL_SERVER_ERROR);
		   Map<String, Object> results = new HashMap<>();
		   Double sum = 0.0;
		   for (CommandLineCustomerEntity line : cmd.getCommandCustomer().getCommandLineCustomers()) {
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
		   results.put("invoiceType", "Facture");
		   results.put("reference", cmd.getId());
		   results.put("base", new DecimalFormat("####0.00").format(sum));
		   results.put("taxable", new DecimalFormat("####0.00").format(sum));
		   results.put("validate", cmd.getCommandCustomer().getIsValid());
		   results.put("numero", cmd.getCode());
		   results.put("date",  DateUtils.formatDateStrg(cmd.getCommandCustomer().getDeliveryDate()));
		   results.put("clientNumber", cmd.getCommandCustomer().getCustomer().getCode());
		   results.put("customerName", cmd.getCommandCustomer().getCustomer().getName());
		   results.put("adresse", cmd.getCommandCustomer().getCustomer().getAdress());
		   results.put("codePostal", cmd.getCommandCustomer().getCustomer().getPostalCode() + "" + cmd.getCommandCustomer().getCustomer().getCity());
		   results.put("pays", cmd.getCommandCustomer().getCustomer().getPostalCode());
		   results.put("total", new DecimalFormat("####0.00").format(sum));
		   Double sumTva = (cmd.getCommandCustomer().getTva() != null ? sum * cmd.getCommandCustomer().getTva() : 0.0);
		   results.put("tva", new DecimalFormat("####0.00").format(sumTva));
		   results.put("totalTva", new DecimalFormat("####0.00").format(sumTva));
		   results.put("SumTotal",  new DecimalFormat("####0.00").format(sum + sumTva));
		   results.put("echeance",  DateUtils.formatDateStrg(cmd.getCommandCustomer().getDeadLine()));

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
   
   /*** modifier une commande customer ***/
   
   public CommandCustomerEntity updateCommandCustomer(CommandCustomerEntity cmd) {
   	
		try {
				CommandCustomerEntity cmdCus  = commandCustomerRep.getCommandCustomerById(cmd.getId()); 
				if(cmdCus  == null) new HttpServiceExceptionHandle("command introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
				cmdCus.setDeadLine(cmd.getDeadLine());
				cmdCus.setDeliveryDate(cmd.getDeliveryDate());
				return commandCustomerRep.save(cmdCus);
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}

   }

   private void Test() {
	// TODO Auto-generated method stub

}

}
