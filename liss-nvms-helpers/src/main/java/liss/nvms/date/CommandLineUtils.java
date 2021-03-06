package liss.nvms.date;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CommandLineUtils implements Serializable{
	
	@JsonProperty("description")
    private String description;
    	
    @JsonProperty("quantity")
    private Long quantity;
    	
    @JsonProperty("product")
    private String product;
    
}
