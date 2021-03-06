package liss.nvms.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MapStringWithValue implements Serializable{
	
	@JsonProperty("x-reference")
	String ref;
	
	@JsonProperty("x-code")
	String value;

}
