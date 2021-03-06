package liss.nvms.manage;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Entity(name="Role")
@Table(name="roles")

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleEntity extends TimeStampEntity {
	
	@Column(name="code", unique = true)
	@JsonProperty(value = "code", required = true )
    private String code; 
	
	@Column(name="description")
	@JsonProperty(value = "description", required = false )
	private String description;
	
	
	
	@ManyToMany(cascade=CascadeType.ALL)//(fetch=FetchType.EAGER)
	@JoinTable(name="roles_permissions",
	joinColumns={@JoinColumn(name="role_id")},
	inverseJoinColumns={@JoinColumn(name="permission_id")})
	@JsonProperty(value = "permissions", required = false )
	private List<PermissionEntity> permissions = new ArrayList<>();
	
	
}
