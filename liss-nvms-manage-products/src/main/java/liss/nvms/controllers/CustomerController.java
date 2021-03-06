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
import liss.nvms.model.CustomerEntity;
import liss.nvms.services.CustomerService;
import liss.nvms.utils.HttpErrorCodes;
import liss.nvms.utils.HttpErrorMessage;

@RestController
@RequestMapping("customers")
public class CustomerController {
	
	@Autowired CustomerService customerService;
	
	/** afficher tous les clients **/
	@GetMapping
	public ResponseEntity<?> getCustomers(HttpServletRequest request, @RequestParam(value = "name", required = false) String query,
									@RequestParam(value = "page", required = false, defaultValue = "0") int page,
									@RequestParam(value = "limit", required = false, defaultValue = "5000") int limit){
		try{
			 return new ResponseEntity<>(customerService.getAllCustomer(query, page, limit), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}

	}
	/** ajouter un client **/
	@PostMapping
	public ResponseEntity<?> addCustomer(@RequestBody CustomerEntity customer){
		try{
			
			if(customer == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			
			 return new ResponseEntity<>(customerService.addCustomer(customer), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	/** modifier  un client **/
	@PutMapping
	public ResponseEntity<?> updateCustomer(@RequestBody CustomerEntity customer){
		try{
			if(customer == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			 return new ResponseEntity<>(customerService.updateCustomer(customer), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}

	}


	/** delete la factures**/
	@GetMapping("delete")
	public ResponseEntity<?> deleteCustomer(HttpServletRequest request, @RequestParam(value = "reference", required = true) String param){
		try{
			return new ResponseEntity<>(customerService.deleteCustomer(param), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	

}
