package liss.nvms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import liss.nvms.abstracts.UserAbstract;
import liss.nvms.composite.StockPK;
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
@Entity(name="Stock")
@Table(name = "stocks")
@JsonIgnoreProperties( ignoreUnknown = true)
public class StockEntity extends UserAbstract{
	
   @Column(name = "article_ref")
    @JsonProperty(value = "article_ref")
    private String code;


    @Column(name = "quantity", columnDefinition="INT default '0'")
    @JsonProperty(value = "quantity")
    private Long quantity = 0L;

    @Column(name = "limit_quantity", columnDefinition="INT default '0'")
    @JsonProperty(value = "limit_quantity")
    private Long limitQuantity = 0L;

    @Column(name = "price", columnDefinition="Decimal(10,2) default '0.00'")
    @JsonProperty(value = "price")
    private Double price = 0.0;

    @ManyToOne
    @JsonProperty(value = "product")
    private ProductEntity product;
    
    @ManyToOne
    @JsonProperty(value = "format")
    private FormatEntity format;
    
 
    
    
    
}
