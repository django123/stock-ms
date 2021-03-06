package liss.nvms.invoice.classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class InvoiceCustomerClass {
		
	   private String article = "";
	
	    private int quantity = 0;
	    
	    private Double price = 0.0;
	    
	    private Double remise = 0.0;
		
	    private String designation;
	    
	    private Double total = 0.0;
	    
	  
}
