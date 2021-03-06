package liss.nvms;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import liss.nvms.manage.RoleEntity;
import liss.nvms.manage.UserEntity;


@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, String>{
	
	public RoleEntity findByCode(String code);
	
	/** get users if have or not params **/
	@Query("SELECT r FROM Role r WHERE UPPER(code) =:param OR UPPER(description) like CONCAT('%',:param, '%') ")
	public Page<UserEntity> getRolesWithParam(@Param("param") String param, Pageable page);
	
	
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO roles_permissions (role_id, permission_id) VALUES (:role, :permission)", nativeQuery = true)
	public int addPermissionToUser(@Param("role") String roleID, @Param("permission") String permission);
	
	/** remove permissions to user **/
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM roles_permissions WHERE role_id = :role AND permission_id in ( SELECT id FROM permissions WHERE code in :permissions )", nativeQuery = true)
	public int removePermissionsToRole(@Param("role") String role, @Param("permissions") List<String> permissions);
	
	
	
}
