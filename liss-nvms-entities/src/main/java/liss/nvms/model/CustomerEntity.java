package liss.nvms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import liss.nvms.abstracts.UserAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="Customer")
@Table(name="customers")

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerEntity extends UserAbstract{
	
	@Column(name="name")
	@JsonProperty(value = "name")
    private String name;
	
	@Column(name="code")
	@JsonProperty(value = "code")
    private String code;
	
	@Column(name="adress")
	@JsonProperty(value = "adress", required = true)
    private String adress;
	
	@Column(name="city")
	@JsonProperty(value = "city")
    private String city;
	
	@Column(name="country")
	@JsonProperty(value = "country", required = false )
    private String country;
	
	@Column(name="postal_code")
	@JsonProperty(value = "postal_code", required = false )
    private String postalCode;
	
	@Column(name="tva")
	@JsonProperty(value = "tva", required = false )
    private String tva;
	
	@Column(name="email")
	@JsonProperty(value = "email", required = true )
    private String email;
	
	@Column(name="fax")
	@JsonProperty(value = "fax", required = false )
    private String fax;
	
	@Column(name="phone")
	@JsonProperty(value = "phone", required = false )
    private String phone;
	
	@Column(name="contact_person")
	@JsonProperty(value = "contact_person", required = false )
    private String contactPerson;
	
	@Column(name="payment_mode")
	@JsonProperty(value = "payment_mode", required = false )
    private String paymentMode;

	@Column(name="payment_period")
	@JsonProperty(value = "payment_period", required = false )
    private Long paymentPeriod;
	
	

	
}
