package liss.nvms.composite;

import java.io.Serializable;
import java.util.Objects;


public class StockPK implements Serializable{
	private String format;
    private String product;
 
 public StockPK() {}
    
 public StockPK(String format, String product_id) {
  this.format = format;
  this.product = product_id;
 }
 

 @Override
 public boolean equals(Object o) {
     if (this == o) return true;
     if (o == null || getClass() != o.getClass()) return false;
     StockPK stockPK = (StockPK) o;
     return format.equals(stockPK.format) &&
    		 product.equals(stockPK.product);
 }

 @Override
 public int hashCode() {
     return Objects.hash(format, product);
 }
 
 
}
