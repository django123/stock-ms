package liss.nvms.manage;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@Entity(name="User")
@Table(name="users")

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserEntity extends TimeStampEntity {
	
	@Column(name="lock", columnDefinition="boolean default false")
	@JsonProperty(value = "isLock", required = false )
    private Boolean isLock = false; 
	
	@Column(name="name")
	@JsonProperty(value = "name", required = true )
	private String name;
	
	@Column(name="email", unique=true)
	@JsonProperty(value = "email", required = true )
	private String email; 
	
	@Column(name="phone")
	@JsonProperty(value = "phone", required = false )
	private String phone;
	
	@Column(name="mdp")
	@JsonProperty(value = "password", required = false )
	private String password;
	
	@Column(name="cfp")
	@JsonProperty(value = "confirm", required = false )
	private String cfp;
	
	@ManyToOne(optional = true, cascade = CascadeType.ALL)
	@JoinColumn(name="role_id")
	@JsonProperty(value = "role", required = false )
	private RoleEntity role;
	
	
	@ManyToMany(cascade=CascadeType.ALL)//(fetch=FetchType.EAGER)
	@JoinTable(name="access_permissions",
	joinColumns={@JoinColumn(name="user_id")},
	inverseJoinColumns={@JoinColumn(name="permission_id")})
	@JsonProperty(value = "permissions", required = false )
	private List<PermissionEntity> permissions = new ArrayList<>();
	
	
}
