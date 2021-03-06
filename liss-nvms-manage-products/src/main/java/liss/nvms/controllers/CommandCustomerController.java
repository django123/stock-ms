package liss.nvms.controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import liss.nvms.httpException.HttpServiceExceptionHandle;
import liss.nvms.model.CommandCustomerEntity;
import liss.nvms.model.CommandLineCustomerEntity;
import liss.nvms.services.CommandCustomerService;
import liss.nvms.utils.HttpErrorMessage;
import net.sf.jasperreports.engine.JRException;

@RestController
@RequestMapping("orders/customer")
public class CommandCustomerController {
	
	@Autowired CommandCustomerService commandCustomerService;
	
	
	
	@GetMapping("report")
	public ResponseEntity<?> test(HttpServletRequest request,@RequestParam(value = "date1", required = false) Date date1, @RequestParam(value = "date2", required = false) Date date2){
		try{
			if(date1 == null) date1 = new Date();
			if(date2 == null) date2 = new Date();
			return new ResponseEntity<>(commandCustomerService.bilanVenteEntreprise(date1, date2), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}

	}
	
	@GetMapping("report/print")
	public ResponseEntity<?> impressionRapportEntreprise(HttpServletRequest request,@RequestParam(value = "date1", required = false) Date date1, @RequestParam(value = "date2", required = false) Date date2) throws JRException{

		try{
			byte[] bytes = commandCustomerService.imprimerBilanVente(date1, date2);
			return ResponseEntity
				      .ok()
				      // Specify content type as PDF
				      .header("Content-Type", "application/pdf; charset=UTF-8")
				      // Tell browser to display PDF if it can
				      .header("Content-Disposition", "inline; filename=rapport.pdf")
				      .body(bytes);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
		

	}
	
	
	@GetMapping
	public ResponseEntity<?> getCmdCustomer(HttpServletRequest request, @RequestParam(value = "code", required = false) String code,
			@RequestParam(value = "customer", required = false) String customer,
			@RequestParam(value = "date1", required = false) Date date1, @RequestParam(value = "date2", required = false) Date date2,
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "limit", required = false, defaultValue = "5000") int limit){
		try{
			
			
			if(code == null || code.equals("null") || code.trim().length() == 0 ) code = null;
			if(customer == null || customer.equals("null") || customer.equals("undefined") || customer.trim().length() == 0 ) customer = null;
			if(date1 == null) date1 = null;
			if(date2 == null) date2 = null;
			
			return new ResponseEntity<>(commandCustomerService.getCommandes(code, customer, date1, date2, page, limit), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}

	}
	
	
	@GetMapping("order/detail")
	public ResponseEntity<?> getCmdDetail(HttpServletRequest request, @RequestParam(value = "reference", required = false) String code){
		try{
			 return new ResponseEntity<>(commandCustomerService.getDetailsCommande(code), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	
	/** Ajouter une commande customer **/
	@PostMapping
	public ResponseEntity<?> addCustomerCommande(HttpServletRequest request, @RequestBody CommandCustomerEntity cmd){
		try{
			 return new ResponseEntity<>(commandCustomerService.addCommande(cmd), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}

	}
	
	/** modifier une line de  commande customer **/
	@PutMapping
	public ResponseEntity<?> updateCustomerCommande(HttpServletRequest request, @RequestBody CommandLineCustomerEntity cmd){
		try{
			 return new ResponseEntity<>(commandCustomerService.updateCommandeCustomer(cmd), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}

	}
	
	/** valider une comande fournisseur **/
	@GetMapping("order/validate")
	public ResponseEntity<?> validateOrder(HttpServletRequest request, @RequestParam(value = "reference", required = false) String code){
		try{
			 return new ResponseEntity<>(commandCustomerService.ValidateCommandCustomer(code), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}

	/** delete comande customer **/
	@GetMapping("delete")
	public ResponseEntity<?> deleteOrder(HttpServletRequest request, @RequestParam(value = "reference", required = false) String code){
		try{
			 return new ResponseEntity<>(commandCustomerService.deleteCmdCustomer(code), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	
	/** delete command line customer **/
	@GetMapping("line/delete")
	public ResponseEntity<?> deleteLineOrder(HttpServletRequest request, @RequestParam(value = "reference", required = false) String code){
		try{
			 return new ResponseEntity<>(commandCustomerService.deleteCmdLineCustomer(code), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	/** generate invoice customer
	 * @throws JRException **/
	@GetMapping("/invoice")
	public ResponseEntity<?> generateReport(HttpServletRequest request, @RequestParam(value = "invoice") String code, @RequestParam(value = "reference") String ref) throws JRException {

		try{
			byte[] bytes = commandCustomerService.generateInvoice(ref);
			return ResponseEntity
				      .ok()
				      // Specify content type as PDF
				      .header("Content-Type", "application/pdf; charset=UTF-8")
				      // Tell browser to display PDF if it can
				      .header("Content-Disposition", "inline; filename=\"" + code + ".pdf\"")
				      .body(bytes);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}

	/** delete  product**/
	@GetMapping("deleted")
	public ResponseEntity<?> deleteProduct(HttpServletRequest request,@RequestParam String comment,@RequestParam Boolean isReApprov, @RequestParam(value = "reference", required = true) String param){
		try{
			return new ResponseEntity<>(commandCustomerService.deleteCommandCustomer(comment, isReApprov,param), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	
	/** modification d'une commande client**/
	
	@PutMapping("order")
	public ResponseEntity<?> updateCustomerCommande(HttpServletRequest request, @RequestBody CommandCustomerEntity cmd){
		try{
			 return new ResponseEntity<>(commandCustomerService.updateCommandCustomer(cmd), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}

	}

}
