package liss.nvms.date;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OrderLineUtils implements Serializable{

	@JsonProperty("description")
    private String description;
    	
    @JsonProperty("quantity")
    private Long quantity;
    	
    @JsonProperty("material")
    private String material;
   	
}
