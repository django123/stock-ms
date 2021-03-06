package liss.nvms.controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.sf.jasperreports.engine.JRException;
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
import liss.nvms.model.CommandLineSupplierEntity;
import liss.nvms.model.CommandSupplierEntity;
import liss.nvms.services.CommandeSupplierService;
import liss.nvms.utils.HttpErrorMessage;

@RestController
@RequestMapping("orders/supplier")
public class CommandeSupplierController {
	
	@Autowired CommandeSupplierService cmdService;
	
	
	/*** afficher les commandes fournisseur **/
	@GetMapping
	public ResponseEntity<?> getCmdSuppl(HttpServletRequest request, @RequestParam(value = "code", required = false) String code,
									@RequestParam(value = "supplier", required = false) String supplier,
									@RequestParam(value = "date1", required = false) Date date1, @RequestParam(value = "date2", required = false) Date date2,
									@RequestParam(value = "page", required = false, defaultValue = "0") int page,
									@RequestParam(value = "limit", required = false, defaultValue = "5000") int limit){
		try{
			
			
			 if(code == null || code.equals("null") || code.trim().length() == 0 ) code = null;
			 if(supplier == null || supplier.equals("null") || supplier.trim().length() == 0 ) supplier = null;
			 if(date1 == null) date1 = null;
			 if(date2 == null) date1 = null;
			 
			 return new ResponseEntity<>(cmdService.getCommandes(code, supplier, date1, date2, page, limit), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	
	/*** afficher les commandes fournisseur **/
	@GetMapping("order/detail")
	public ResponseEntity<?> getCmdDetail(HttpServletRequest request, @RequestParam(value = "reference", required = false) String code){
		try{
			 return new ResponseEntity<>(cmdService.getDetailsCommande(code), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	
	/** Ajouter une commande fournisseur **/
	@PostMapping
	public ResponseEntity<?> addCmdSuppl(HttpServletRequest request, @RequestBody CommandSupplierEntity cmd){
		try{
			 return new ResponseEntity<>(cmdService.addCommande(cmd), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}

	}
	
	/** modifier une commande fournisseur **/
	@PutMapping
	public ResponseEntity<?> updateSupplierCommand(HttpServletRequest request, @RequestBody CommandLineSupplierEntity cmd){
		try{
			 return new ResponseEntity<>(cmdService.UpdateLineCommandSupplier(cmd), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}

	}
	
	/** modifier une commande fournisseur **/
	@PutMapping("order")
	public ResponseEntity<?> updateCommandSupplier(HttpServletRequest request, @RequestBody CommandSupplierEntity cmd){
		try{
			 return new ResponseEntity<>(cmdService.updateCommandSupplier(cmd), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}

	}
	
	/** valider une comande fournisseur **/
	@GetMapping("order/validate")
	public ResponseEntity<?> validateOrder(HttpServletRequest request, @RequestParam(value = "reference", required = false) String code){
		try{
			 return new ResponseEntity<>(cmdService.ValidateCommandSupplier(code), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	
	
	/** delete commande fournissuer **/
	@GetMapping("delete")
	public ResponseEntity<?> deleteOrder(HttpServletRequest request, @RequestParam(value = "reference", required = false) String code){
		try{
			 return new ResponseEntity<>(cmdService.deleteSupplierCmd(code), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	/** delete command line fourniseur **/
	@GetMapping("line/delete")
	public ResponseEntity<?> deleteLineOrder(HttpServletRequest request, @RequestParam(value = "reference", required = false) String code){
		try{
			 return new ResponseEntity<>(cmdService.deleteLineSupplierCmd(code), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}

	/** generate note supplier
	 * @throws JRException **/
	@GetMapping("/note")
	public ResponseEntity<?> generateReport(HttpServletRequest request, @RequestParam(value = "invoice") String code, @RequestParam(value = "reference") String ref) throws JRException {

		try{
			byte[] bytes = cmdService.generateNote(ref);
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
