package liss.nvms.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import liss.nvms.abstracts.UserAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="Supplier")
@Table(name="suppliers")

@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierEntity extends UserAbstract{
	
	@Column(name="code", unique = true)
	@JsonProperty(value = "code")
    private String code;
	
	@Column(name = "delivery_date")
	@JsonProperty(value = "delivery_date", required = false)
	@Temporal(TemporalType.DATE)
    private Date deliveryDate;
    
	@Column(name = "dead_line")
	@Temporal(TemporalType.DATE)
	@JsonProperty(value = "dead_line", required = false)
    private Date deadLine;
	
	@Column(name="name")
	@JsonProperty(value = "name", required = true)
    private String name;
	
	@Column(name="adress")
	@JsonProperty(value = "adress" , required = true)
    private String adress;
	
	@Column(name="city")
	@JsonProperty(value = "city")
    private String city;
	
	@Column(name="country")
	@JsonProperty(value = "country")
    private String country;
	
	@Column(name="postal_code")
	@JsonProperty(value = "postal_code", required = false )
    private String postal_code;
	
	@Column(name="tva", unique = true)
	@JsonProperty(value = "tva", required = false )
    private String tva;
	
	@Column(name="email", unique = true)
	@JsonProperty(value = "email", required = true )
    private String email;
	
	@Column(name="fax", unique = true)
	@JsonProperty(value = "fax", required = false )
    private String fax;
	
	@Column(name="phone", unique = true)
	@JsonProperty(value = "phone", required = false )
    private String phone;
	
	@Column(name="contact_person", unique = true)
	@JsonProperty(value = "contact_person", required = false )
    private String contact_person;
}
