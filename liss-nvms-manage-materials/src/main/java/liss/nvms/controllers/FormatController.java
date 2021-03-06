package liss.nvms.controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import liss.nvms.httpException.HttpServiceExceptionHandle;
import liss.nvms.model.FormatEntity;
import liss.nvms.services.FormatService;
import liss.nvms.utils.HttpErrorCodes;
import liss.nvms.utils.HttpErrorMessage;

@RestController
@RequestMapping("formats")
public class FormatController {

	
	
	@Autowired FormatService formatService;
	
	
	/** get all format in database **/
	@GetMapping
	public ResponseEntity<?> getFormats(HttpServletRequest request){
		
		try{
			 return new ResponseEntity<>(formatService.getAllFormats() , HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
		
	}
	
	/** ajouter un format **/
	
	@PostMapping
	public ResponseEntity<?> addFormat(HttpServletRequest request, @RequestBody FormatEntity format){
		
		try{
			if(format == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			 return new ResponseEntity<>(formatService.addFormat(format) , HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
		
	}
	
	/** mofifier un format **/
	@PutMapping
	public ResponseEntity<?> updateFormat(HttpServletRequest request, @RequestBody FormatEntity format){
		
		try{
			if(format == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			 return new ResponseEntity<>(formatService.addFormat(format), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
		
	}

	/** delete  format**/
	@GetMapping("delete")
	public ResponseEntity<?> deleteFormat(HttpServletRequest request, @RequestParam(value = "reference", required = true) String param){
		try{
			return new ResponseEntity<>(formatService.deleteFormat(param), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	
	
	
}
