package liss.nvms.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
/**
 * Use to assign value  to a key
 * @author LISS SARL
 *
 */
public class MapStringWithValue implements Serializable{
	
	@JsonProperty("x-ref")
	String ref;
	
	@JsonProperty("x-value")
	String value;
	

}
