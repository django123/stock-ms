package liss.nvms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import liss.nvms.abstracts.UserAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "CommandLineCustomer")
@Table(name="command_line_customers")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommandLineCustomerEntity extends UserAbstract {
	
    @Column(name = "quantity",  columnDefinition="Int default '0'")
    @JsonProperty(value = "quantity", required = false)
    private int quantity = 0;
    
    @Column(name = "price",  columnDefinition="Decimal(10,2) default '0.00'")
    @JsonProperty(value = "price")
    private Double price = 0.0;
    
    @Column(name = "discount",  columnDefinition="Decimal(10,2) default '0.00'")
    @JsonProperty(value = "discount")
    private Double remise = 0.0;
	
    @ManyToOne
    @JsonProperty(value = "stock")
    private StockEntity stock;
	
    @ManyToOne
    @JoinColumn(name="command_customer_uuid", nullable=false)
    @JsonProperty(value = "command")
    @JsonBackReference
    private CommandCustomerEntity command_customer;
	

}
