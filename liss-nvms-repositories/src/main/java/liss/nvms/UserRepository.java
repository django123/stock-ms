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

import liss.nvms.manage.UserEntity;


@Repository
public interface UserRepository extends JpaRepository<UserEntity,  String>{
	
	/** rechercher un utilisateur **/
	@Query("SELECT u FROM User u WHERE email =:email")
	public UserEntity getUserByEmail(@Param("email") String email);
	
	/** get users if have or not params **/
	@Query("SELECT u FROM User u WHERE email like concat(:param, '%') or name like concat(:param, '%')")
	public Page<UserEntity> getUsersWithParam(@Param("param") String param, Pageable page);
	
	/*** add permission to user in table (access_permissions) **/
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO access_permissions (user_id, permission_id) VALUES (:user, :permission)", nativeQuery = true)
	public int addPermissionToUser(@Param("user") String username, @Param("permission") String permission);
	
	/** remove permissions to user **/
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM access_permissions WHERE user_id = :user AND  permission_id in ( SELECT id FROM permissions WHERE code in :permission )", nativeQuery = true)
	public int removePermissionsToUser(@Param("user") String username, @Param("permission") List<String> permission);
	

	
	
	
	
}
