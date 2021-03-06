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
import liss.nvms.model.ProductEntity;
import liss.nvms.services.ProductService;
import liss.nvms.utils.HttpErrorCodes;
import liss.nvms.utils.HttpErrorMessage;


@RestController
@RequestMapping("products")
public class ProductController {
	
	@Autowired ProductService productService;

	@GetMapping
	public ResponseEntity<?> getProducts(HttpServletRequest request, @RequestParam(value = "name", required = false) String query,
									@RequestParam(value = "page", required = false, defaultValue = "0") int page,
									@RequestParam(value = "limit", required = false, defaultValue = "5000") int limit){
		
		try{
			 return new ResponseEntity<>(productService.getAllProduct(query, page, limit) , HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
		
	}
	
	/** ajouter un produit **/
	@PostMapping
	public ResponseEntity<?> addProducts(HttpServletRequest request, @RequestBody ProductEntity product){
		
		try{
			if(product == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			 return new ResponseEntity<>(productService.addProduct(product) , HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	/** modifier un produit **/
	@PutMapping
	public ResponseEntity<?> updateProduct(HttpServletRequest request, @RequestBody ProductEntity product){
		
		try{
			if(product == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			 return new ResponseEntity<>(productService.addProduct(product) , HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
		
	}


	/** delete  product**/
	@GetMapping("delete")
	public ResponseEntity<?> deleteProduct(HttpServletRequest request, @RequestParam(value = "reference", required = true) String param){
		try{
			return new ResponseEntity<>(productService.deleteProduct(param), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
}
