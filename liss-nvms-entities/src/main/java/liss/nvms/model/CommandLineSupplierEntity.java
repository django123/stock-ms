package liss.nvms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import liss.nvms.abstracts.UserAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "CommandLineSupplier")
@Table(name="command_line_suppliers")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommandLineSupplierEntity extends UserAbstract {

    @Column(name = "boxLayer", columnDefinition="INT default '0'")
    @JsonProperty(value = "boxLayer")
    private Long boxLayer = 0L;
    
    @Column(name = "quantityLayer",  columnDefinition="INT default '0'")
    @JsonProperty(value = "quantityLayer")
    private Long quantityLayer = 0L;
    
    @Column(name = "quantityBoxe", columnDefinition="INT default '0'")
    @JsonProperty(value = "quantityBoxe")
    private Long quantityBoxe = 0L;
    
    @Column(name = "bottleQuantity", columnDefinition="INT default '0'")
    @JsonProperty(value = "bottleQuantity")
    private Long bottleQuantity = 0L;
    
    @Column(name = "price", columnDefinition="Decimal(10,2) default '0.00'" )
    @JsonProperty(value = "price")
    private Double price = 0.0;

    @ManyToOne
    @JsonProperty(value = "stock")
    private StockEntity stock;
    
    
    @ManyToOne
    @JoinColumn(name="command_supplier_uuid", nullable=false)
    @JsonProperty(value = "command")
    @JsonBackReference
    private CommandSupplierEntity command_supplier;
    
    

}
