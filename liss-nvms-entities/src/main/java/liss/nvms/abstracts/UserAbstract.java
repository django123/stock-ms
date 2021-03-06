package liss.nvms.abstracts;

import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class UserAbstract extends TimeStampEntity{
	
//	@ManyToOne
//	@JoinColumn(name= "created_by")
//	@JsonProperty(value = "created_by", required = false )
//	private UserEntity createdBy;
//	
//	
//	@ManyToOne
//	@JoinColumn(name= "updated_by")
//	@JsonProperty(value = "updated_by", required = false )
//	private UserEntity updatedBy;
}
