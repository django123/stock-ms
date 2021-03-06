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
import liss.nvms.model.SupplierEntity;
import liss.nvms.services.SupplierService;
import liss.nvms.utils.HttpErrorCodes;
import liss.nvms.utils.HttpErrorMessage;

@RestController
@RequestMapping("suppliers")
public class SupplierController {
	
	@Autowired SupplierService supplierService;
	
	
	/** afficher tous les fournisseurs **/
	@GetMapping
	public ResponseEntity<?> getSuppliers(HttpServletRequest request, @RequestParam(value = "name", required = false) String query,
									@RequestParam(value = "page", required = false, defaultValue = "0") int page,
									@RequestParam(value = "limit", required = false, defaultValue = "5000") int limit){
		try{
			 return new ResponseEntity<>(supplierService.getAllSupplier(query, page, limit), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}

	}
	
	@PostMapping
	public ResponseEntity<?> addSupplier(@RequestBody SupplierEntity supplier){
		try{
			if(supplier == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			 return new ResponseEntity<>(supplierService.addSupplier(supplier), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}

	}
	
	@PutMapping
	public ResponseEntity<?> updateSupplier(@RequestBody SupplierEntity supplier){
		try{
			if(supplier == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			 return new ResponseEntity<>(supplierService.updateSupplier(supplier), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}

	}

	/** delete  supplier**/
	@GetMapping("delete")
	public ResponseEntity<?> deleteCustomer(HttpServletRequest request, @RequestParam(value = "reference", required = true) String param){
		try{
			return new ResponseEntity<>(supplierService.deleteSupplier(param), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	

}
