package liss.nvms.Flivraison;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import liss.nvms.abstracts.UserAbstract;
import liss.nvms.model.StockEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "DeliveryLineSupplier")
@Table(name="delivery_line_suppliers")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeliveryLineSupplierEntity extends UserAbstract{
	    @Column(name = "boxLayer")
	    @JsonProperty(value = "boxLayer")
	    private Long boxLayer = 0L;
	    
	    @Column(name = "quantityLayer")
	    @JsonProperty(value = "quantityLayer")
	    private Long quantityLayer = 0L;
	    
	    @Column(name = "quantityBoxe")
	    @JsonProperty(value = "quantityBoxe")
	    private Long quantityBoxe = 0L;
	    
	    @Column(name = "bottleQuantity")
	    @JsonProperty(value = "bottleQuantity")
	    private Long bottleQuantity = 0L;
	    
	    @Column(name = "price", columnDefinition="Decimal(10,2) default '0.00'" )
	    @JsonProperty(value = "price")
	    private Double price = 0.0;
	
	    @ManyToOne
	    @JsonProperty(value = "stock")
	    private StockEntity stock;
	    
	    @ManyToOne
	    @JoinColumn(name="supplier_delivery_uuid", nullable=false)
	    @JsonProperty(value = "supplier_delivery")
	    @JsonBackReference
	    private DeliverySupplierEntity supplier_delivery;
}
