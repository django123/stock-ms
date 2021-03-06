package liss.nvms.Clivraison;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import liss.nvms.abstracts.UserAbstract;
import liss.nvms.model.CommandCustomerEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "DeliveryCustomer")
@Table(name="customer_delivery")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeliveryCustomerEntity extends UserAbstract{
	
    @Column(name = "note_ref")
    @JsonProperty(value = "code")
    private String reference;
    
	@Column(name = "delivery_date")
	@JsonProperty(value = "delivery_date", required = false)
	@Temporal(TemporalType.DATE)
    private Date deliveryDate;
    
	@Column(name = "dead_line")
	@Temporal(TemporalType.DATE)
	@JsonProperty(value = "dead_line", required = false)
    private Date deadLine;
	
	@Column(name = "validated",  columnDefinition="boolean default false")
    @JsonProperty(value = "validated")
    private Boolean isValid = false;
	
    @OneToMany(mappedBy = "customer_delivery",cascade = CascadeType.ALL)
    @JsonProperty(value = "delivery_lines", required = false)
    @JsonManagedReference
    private List<DeliveryLineCustomerEntity> deliveryLines = new ArrayList<>();
	
    @ManyToOne
    @JsonProperty(value = "command", required = false)
    private CommandCustomerEntity command;

}
