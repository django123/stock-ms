package liss.nvms.Flivraison;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import liss.nvms.abstracts.UserAbstract;
import liss.nvms.model.CommandCustomerEntity;
import liss.nvms.model.CommandSupplierEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "DeliverySupplier")
@Table(name="delivery_suppliers")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeliverySupplierEntity extends UserAbstract{

	@Column(name="delivery_ref")
	@JsonProperty(value="delivery_ref")
	private String code;

	@Column(name = "validated",  columnDefinition="boolean default false")
    @JsonProperty(value = "validated")
    private Boolean isValid = false;
	
    @ManyToOne
    @JsonProperty(value = "command", required = false)
    private CommandSupplierEntity command;
    
    @OneToMany(mappedBy = "supplier_delivery",cascade = CascadeType.ALL)
    @JsonProperty(value = "delivery_lines", required = false)
    @JsonManagedReference
    private List<DeliveryLineSupplierEntity> deliveryLineSuppliers = new ArrayList<>();
}
