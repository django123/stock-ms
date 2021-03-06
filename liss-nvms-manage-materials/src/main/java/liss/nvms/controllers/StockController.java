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
import liss.nvms.model.MapStringWithValue;
import liss.nvms.model.StockEntity;
import liss.nvms.services.StockService;
import liss.nvms.utils.HttpErrorMessage;

@RestController
@RequestMapping("stocks")
public class StockController {
	
	@Autowired StockService stockService;
	
	
	/** afficher les format de produit pour les MVTs E/S **/
	@GetMapping("event")
	public ResponseEntity<?> productFormatForEvents(HttpServletRequest request, @RequestParam(value = "name", required = false) String query,
									@RequestParam(value = "page", required = false, defaultValue = "0") int page,
									@RequestParam(value = "limit", required = false, defaultValue = "5000") int limit){
		try{
			 return new ResponseEntity<>(stockService.productForEvents(query, page, limit) , HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}

	}
	
	
	/** afficher le stock des produits **/
	@GetMapping
	public ResponseEntity<?> getStocks(HttpServletRequest request, @RequestParam(value = "name", required = false) String query,
									@RequestParam(value = "page", required = false, defaultValue = "0") int page,
									@RequestParam(value = "limit", required = false, defaultValue = "100") int limit){
		try{
			 return new ResponseEntity<>(stockService.getAllStock( query, page, limit) , HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}

	}
	
	/*** ajouter un stock de produit **/
	
	@PostMapping
	public ResponseEntity<?> addStock(HttpServletRequest request,@RequestBody StockEntity stock){
		try{
			 return new ResponseEntity<>(stockService.addStock(stock) , HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}

	}
	
	
	/*** modifier un stock de produit **/
	
	@PutMapping
	public ResponseEntity<?> updateStock(HttpServletRequest request, @RequestBody StockEntity stock){
		try{
			 return new ResponseEntity<>(stockService.updateStock( stock) , HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}

	}

	/** delete  stock**/
	@GetMapping("delete")
	public ResponseEntity<?> deleteStock(HttpServletRequest request, @RequestParam(value = "reference", required = true) String param){
		try{
			return new ResponseEntity<>(stockService.deleteStock(param), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	/** Update code to stock **/
	@PutMapping("code-update")
	public ResponseEntity<?> updateSipPassWord(HttpServletRequest request, @RequestBody MapStringWithValue model) {
		try{
			 return new ResponseEntity<>(stockService.updateStockCode(model.getRef(), model.getValue()), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}


}
