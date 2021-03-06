package liss.nvms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import liss.nvms.abstracts.UserAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "InvoiceCustomer")
@Table(name = "invoice_customer")
public class InvoiceCustomerEntity extends UserAbstract {
	
	@Column(name="invoice_ref", unique = true)
	@JsonProperty(value="invoice_ref")
	private String code;

	@Column(name = "delivery_date")
	@JsonProperty(value = "delivery_date", required = false)
	@Temporal(TemporalType.DATE)
    private Date deliveryDate;
    
	@Column(name = "dead_line")
	@Temporal(TemporalType.DATE)
	@JsonProperty(value = "dead_line", required = false)
    private Date deadLine;
	
	@JsonProperty(value = "base", required = false)
	@Column(name="base", columnDefinition = "Decimal(10,2) default '0.00'")
    private Double base;
	
	@JsonProperty(value = "taxable", required = false)
	@Column(name="taxable", columnDefinition = "Decimal(10,2) default '0.00'")
    private Double taxable;
    
    /** tva en pourcentage **/
	@JsonProperty(value = "tva", required = false)
	@Column(name="tva", columnDefinition = "Decimal(10,2) default '0.00'")
    private Double tva;

    
    @JsonProperty(value = "command", required = false)
    @ManyToOne
    @JoinColumn(name = "command_customer_uuid")
    private CommandCustomerEntity commandCustomer;
}
