package liss.nvms;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import liss.nvms.manage.PermissionEntity;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, String>{
	
	
	/** find permission **/
	//	@Query("SELECT u FROM User u WHERE code = :param OR description like CONCAT('%',:param, '%')")
	//	public Page<PermissionEntity> getPermissions(@Param("param") String param, Pageable page);
	
	public PermissionEntity findByCode(String code);
	
	/** list of permissions of user **/
	@Query(value = "SELECT u.permissions FROM User u  WHERE id = :user")
	public Page<PermissionEntity> permissionsOfUser(@Param("user") String username, Pageable page);
	
	/** search permissions in user **/
	@Query(value = "SELECT u.permissions FROM User u LEFT JOIN u.permissions p WHERE id = :user AND UPPER(p.description) LIKE CONCAT('%',:param,'%')")
	public Page<PermissionEntity> searchPermissionsOfUser(@Param("user") String username, @Param("param") String param, Pageable page);
	
	
	/** list of permissions of role **/
	@Query(value = "SELECT r.permissions FROM Role r WHERE id = :role")
	public Page<PermissionEntity> permissionsOfRole(@Param("role") String username, Pageable page);
	
	
	/** search permisons in role **/
	@Query(value = "SELECT p FROM Role r LEFT JOIN r.permissions p WHERE id = :role AND UPPER(p.description) LIKE CONCAT('%',:param,'%')")
	public Page<PermissionEntity> searchPermissionsOfRole(@Param("role") String username, @Param("param") String param, Pageable page);
	
	
	

}
