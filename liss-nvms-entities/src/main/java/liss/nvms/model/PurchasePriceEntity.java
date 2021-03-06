package liss.nvms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import liss.nvms.abstracts.UserAbstract;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "PurchasePrice")
@Table(name="purchase_prices")

@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchasePriceEntity extends UserAbstract {

	 @Column(name = "price")
    @JsonProperty(value = "price", required = false)
    private Double price;
	
    @Column(name = "date")
    @JsonProperty(value = "date", required = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToOne
    @JsonProperty(value = "stock", required = false)
    @JsonBackReference
    private StockEntity stock;

}
