package liss.nvms.manage;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import liss.nvms.abstracts.TimeStampEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data 
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="Permission")
@Table(name="permissions")

@JsonIgnoreProperties(ignoreUnknown = true)
public class PermissionEntity extends TimeStampEntity {
	
	@Column(name="code", unique = true)
	@JsonProperty(value = "code", required = true )
    private String code; 
	
	@Column(name="description")
	@JsonProperty(value = "description", required = false )
	private String description;
}
