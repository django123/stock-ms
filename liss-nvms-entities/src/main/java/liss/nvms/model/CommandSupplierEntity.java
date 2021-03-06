package liss.nvms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import liss.nvms.abstracts.UserAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor @NoArgsConstructor
@Entity(name = "CommandSupplier")
@Table(name = "command_suppliers")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommandSupplierEntity extends UserAbstract {
	
	@Column(name="command_ref")
	@JsonProperty(value="command_ref")
	private String code;

    @Column(name = "description")
    @JsonProperty(value = "description", required = false)
    private String description;

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
	
    
    @OneToMany(mappedBy = "command_supplier",cascade = CascadeType.ALL)
    @JsonProperty(value = "commands", required = false)
    @JsonManagedReference
    private List<CommandLineSupplierEntity> commandLineSuppliers = new ArrayList<>();

    @ManyToOne
    @JsonProperty(value = "supplier", required = false)
    private SupplierEntity supplier;
    
    
    
    
    
}
