package liss.nvms.model;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "CommandCustomer")
@Table(name="command_customer")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommandCustomerEntity extends UserAbstract {
	
	@Column(name="command_ref")
	@JsonProperty(value="command_ref")
	private String code;
   	@Column(name = "tva")
   	@JsonProperty(value = "tva")
   	private Double tva;
	
	@Column(name = "validated",  columnDefinition="boolean default false")
    @JsonProperty(value = "validated")
    private Boolean isValid = false;
	
	@Column(name = "delivery_date")
	@JsonProperty(value = "delivery_date", required = false)
	@Temporal(TemporalType.DATE)
    private Date deliveryDate;
    
	@Column(name = "dead_line")
	@Temporal(TemporalType.DATE)
	@JsonProperty(value = "dead_line", required = false)
    private Date deadLine;
	
	
    @Column(name = "description")
    @JsonProperty(value = "description", required = false)
    private String description;

    @Column(name = "comment")
    @JsonProperty(value = "comment", required = false)
    private String comment;
   
    @OneToMany(mappedBy = "command_customer",cascade = CascadeType.ALL)
    @JsonProperty(value = "commands", required = false)
    @JsonManagedReference
    private List<CommandLineCustomerEntity> commandLineCustomers = new ArrayList<>();

    @ManyToOne
    @JsonProperty(value = "customer", required = false)
    private CustomerEntity customer;


    
    
}
