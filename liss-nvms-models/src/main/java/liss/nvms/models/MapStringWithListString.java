package liss.nvms.models;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
/**
 * Use to assign list of values to a key
 * @author LISS SARL
 *
 */

public class MapStringWithListString implements Serializable{
		
		@JsonProperty("x-ref")
		String ref;
		
		@JsonProperty("x-list")
		List<String> strings;
}
