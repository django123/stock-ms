package liss.nvms.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.*;

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
@Entity(name="Product")
@Table(name="products")
@JsonIgnoreProperties( ignoreUnknown = true)
public class ProductEntity extends UserAbstract{
	
	@Column(name="product_ref", unique = true)
	@JsonProperty(value="product_ref")
	private String code;
	
	@Column(name = "name")
	@JsonProperty(value = "name", required = true)
	private String name;
	
	@Column(name = "description")
	@JsonProperty(value = "description")
	private String description;


}
