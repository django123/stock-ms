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

import liss.nvms.Flivraison.DeliverySupplierEntity;
import liss.nvms.httpException.HttpServiceExceptionHandle;
import liss.nvms.services.DeliverySupplierService;
import liss.nvms.utils.HttpErrorMessage;

@RestController
@RequestMapping("suppliers/deliveries")
public class DeliverySupplierController {
	
	@Autowired DeliverySupplierService deliverySupplierService;
	
	/** Ajouter une note de livraison**/
	@PostMapping
	public ResponseEntity<?> addCustomer(@RequestBody DeliverySupplierEntity deliverySupp){
		try{
			 return new ResponseEntity<>(deliverySupplierService.AddDeliverySupplier(deliverySupp), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	/** Aficher une note de livraison**/
	@GetMapping
	public ResponseEntity<?> getCmdCustomer(HttpServletRequest request, @RequestParam(value = "code", required = false) String code,
			@RequestParam(value = "supplier", required = false) String supplier,
			@RequestParam(value = "date1", required = false) Date date1, @RequestParam(value = "date2", required = false) Date date2,
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "limit", required = false, defaultValue = "5000") int limit){
		try{
			
			
			if(code == null || code.equals("null") || code.trim().length() == 0 ) code = null;
			if(supplier == null || supplier.equals("null") || supplier.equals("undefined") || supplier.trim().length() == 0 ) supplier = null;
			if(date1 == null) date1 = null;
			if(date2 == null) date2 = null;
			
			return new ResponseEntity<>(deliverySupplierService.getAllDeliveryNote(code, supplier, date1, date2, page, limit), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	/** Afficher une note d'approvisionnement**/
	@GetMapping("delivery")
	public ResponseEntity<?> get(HttpServletRequest request, @RequestParam(value = "reference", required = false) String ref){
		try{
			
			return new ResponseEntity<>(deliverySupplierService.getDeliveryNoteDetail(ref), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}

	/** delete  note**/
	@GetMapping("delete")
	public ResponseEntity<?> deleteNote(HttpServletRequest request, @RequestParam(value = "reference", required = true) String param){
		try{
			return new ResponseEntity<>(deliverySupplierService.deleteSupplierDelivery(param), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}

}
