package liss.nvms.controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import liss.nvms.Clivraison.DeliveryCustomerEntity;
import liss.nvms.httpException.HttpServiceExceptionHandle;
import liss.nvms.services.DeliveryCustomerService;
import liss.nvms.utils.HttpErrorMessage;
import net.sf.jasperreports.engine.JRException;

@RestController
@RequestMapping("customers/deliveries")
public class DeliveryCustomerController {
	
	@Autowired DeliveryCustomerService deliveryCustomerService;
	
	/** Ajouter une note de livraison**/
	@PostMapping
	public ResponseEntity<?> addCustomer(@RequestBody DeliveryCustomerEntity deliveryCust){
		try{
			 return new ResponseEntity<>(deliveryCustomerService.AddDeliveryCustomer(deliveryCust), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	/** Aficher une note de livraison**/
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
			
			return new ResponseEntity<>(deliveryCustomerService.getAllDeliveryNote(code, customer, date1, date2, page, limit), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	
	/** valider une  note de livraison **/
	@GetMapping("validate")
	public ResponseEntity<?> validateOrder(HttpServletRequest request, @RequestParam(value = "reference", required = false) String code){
		try{
			 return new ResponseEntity<>(deliveryCustomerService.ValidateDeliveryCustomer(code), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	
	/** generate note livraison customer
	 * @throws JRException **/
	@GetMapping("/invoice")
	public ResponseEntity<?> generateReport(HttpServletRequest request, @RequestParam(value = "invoice") String code, @RequestParam(value = "reference") String ref) throws JRException {

		try{
			byte[] bytes = deliveryCustomerService.generateDeliveryInvoice(ref);
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

}
