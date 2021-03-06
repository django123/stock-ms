package liss.nvms.controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import liss.nvms.httpException.HttpServiceExceptionHandle;
import liss.nvms.model.InvoiceCustomerEntity;
import liss.nvms.services.CustomerInvoiceService;
import liss.nvms.utils.HttpErrorMessage;
import net.sf.jasperreports.engine.JRException;

@RestController
@RequestMapping("customers/invoices")
public class CustomerInvoiceController {
	
@Autowired CustomerInvoiceService invoiceService;
	
	/** Ajouter une facture**/
	@PostMapping
	public ResponseEntity<?> addCustomerInvoice(@RequestBody InvoiceCustomerEntity invoice){
		try{
			 return new ResponseEntity<>(invoiceService.addCustomerInvoice(invoice), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	/** Afficher les factures**/
	@GetMapping
	public ResponseEntity<?> getInvoiceCustomesr(HttpServletRequest request, @RequestParam(value = "code", required = false) String code,
			@RequestParam(value = "customer", required = false) String customer,
			@RequestParam(value = "date1", required = false) Date date1, @RequestParam(value = "date2", required = false) Date date2,
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "limit", required = false, defaultValue = "5000") int limit){
		try{
			
			
			if(code == null || code.equals("null") || code.trim().length() == 0 ) code = null;
			if(customer == null || customer.equals("null") || customer.equals("undefined") || customer.trim().length() == 0 ) customer = null;
			if(date1 == null) date1 = null;
			if(date2 == null) date2 = null;
			
			return new ResponseEntity<>(invoiceService.getAllCustomerInvoice(code, customer, date1, date2, page, limit), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	
	/** Afficher les factures**/
	@GetMapping("command-not-invoice")
	public ResponseEntity<?> getInvoiceCustomesr(HttpServletRequest request, @RequestParam(value = "param", required = false) String param){
		try{
			return new ResponseEntity<>(invoiceService.getCmdWhoNothaveInvoice(param), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	/** delete la factures**/
	@GetMapping("delete")
	public ResponseEntity<?> deleteInvoice(HttpServletRequest request, @RequestParam(value = "reference", required = true) String param){
		try{
			return new ResponseEntity<>(invoiceService.deleteInvoiceCustomer(param), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
    /** update invoice **/
	@PutMapping
	public ResponseEntity<?> updateInvoieCustomer(@RequestBody InvoiceCustomerEntity invoice){
		try{
			return new ResponseEntity<>(invoiceService.updateInvoiceCustomer(invoice), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	
	/** generate invoice customer
	 * @throws JRException **/
	@GetMapping("/invoice/print")
	public ResponseEntity<?> generateReport(HttpServletRequest request, @RequestParam(value = "invoice") String code, @RequestParam(value = "reference") String ref) throws JRException {

		try{
			byte[] bytes = null;
			return ResponseEntity
				      .ok()
				      // Specify content type as PDF
				      .header("Content-Type", "application/pdf; charset=UTF-8")
				      // Tell browser to display PDF if it can
				      .header("Content-Disposition", "inline; filename=\"" + code + ".pdf\"")
				      .body(null);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}

}
