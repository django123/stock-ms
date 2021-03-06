package liss.nvms.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import liss.nvms.PermissionRepository;
import liss.nvms.RoleRepository;
import liss.nvms.UserRepository;
import liss.nvms.httpException.HttpServiceExceptionHandle;
import liss.nvms.manage.PermissionEntity;
import liss.nvms.manage.RoleEntity;
import liss.nvms.manage.UserEntity;
import liss.nvms.models.MapStringWithValue;
import liss.nvms.utils.HttpErrorCodes;

@Service
@Transactional(rollbackFor = HttpServiceExceptionHandle.class, noRollbackFor = EntityNotFoundException.class)
public class UserService {
	
	@Autowired UserRepository userRep;
	@Autowired RoleRepository roleRep;
	@Autowired PermissionRepository permissionRep;
	
	@Autowired private PasswordEncoder bcryptEncoder;
	
	/*** Enregistrer un utilisateur **/
	public UserEntity savedUser(UserEntity dto) {
		try {
			
			System.err.println(dto);			
			UserEntity userEnt = dto;
			if(userRep.getUserByEmail(dto.getEmail()) != null) throw new HttpServiceExceptionHandle("e-mail existe deja !!", HttpErrorCodes.BAD_FORMAT_DATA);
			userEnt.setCfp(dto.getPassword());
			userEnt.setPassword(bcryptEncoder.encode(dto.getPassword()));
			userEnt = userRep.save(userEnt);
			return userEnt;
		} catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
	}
	
	/** modifier un utilisateur **/
	public UserEntity updatedUser(UserEntity userEntity) {
		try {
			
			if(userEntity.getEmail() == null) throw new HttpServiceExceptionHandle("Impossible de retrouver cette utilisateur !!", HttpErrorCodes.BAD_FORMAT_DATA);
			UserEntity userEntity_bd = userRep.getUserByEmail(userEntity.getEmail());
			userEntity_bd.setName( userEntity.getName() );
			userEntity_bd.setPhone(userEntity.getPhone());
			userEntity_bd.setCfp(bcryptEncoder.encode(userEntity.getPassword()));
			userEntity_bd.setPassword(bcryptEncoder.encode(userEntity.getPassword()));
			
			userEntity_bd = userRep.save(userEntity_bd);
			
			return userEntity_bd;
			
		} catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
	}
	
	/** rechercher un utilisateur **/
	public UserEntity searchUser(String email) {
		try {
			return userRep.getUserByEmail(email);
		} catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
	}
	
	/** afficher les utilisateurs **/
	public Map<String, Object> getUsers(String param, int page, int limit) {
		try {
			Pageable paging = PageRequest.of(page, limit);
			Page<UserEntity> pageUsers = null; 
			
			if(param == null) pageUsers = userRep.findAll(paging);
			else pageUsers = userRep.getUsersWithParam(param, paging);
	
			  Map<String, Object> results = new HashMap<>();
			  results.put("data", pageUsers.getContent());
			  results.put("currentPage", pageUsers.getNumber());
			  results.put("totalItems", pageUsers.getTotalElements());
			  results.put("totalPages", pageUsers.getTotalPages());
			 return results;
		} catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
	}
	
	
	/*** attribution d'un role a un user **/
	public Boolean addRoleTouser(MapStringWithValue data) {
		try {
			
			UserEntity user_bd = userRep.getUserByEmail(data.getRef());
			if(user_bd == null) throw new HttpServiceExceptionHandle("Cette user n'est dans le systeme !!",HttpErrorCodes.BAD_FORMAT_DATA);
			
			RoleEntity role_bd = roleRep.findByCode(data.getValue());
			if(role_bd == null) throw new HttpServiceExceptionHandle("Ce role n'est dans le systeme !!", HttpErrorCodes.BAD_FORMAT_DATA);
			
			for (PermissionEntity permission : role_bd.getPermissions()) userRep.addPermissionToUser(user_bd.getId(), permission.getId());
			// throw new HttpServiceExceptionHandle("Une erreur serveur !!"); le decommenté pour voir si la transaction est bien positionné
			user_bd.setRole(role_bd);
			userRep.save(user_bd);
			return true;
		} catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
	}
	
	
	/** remove role to user **/
	public Boolean removeRoleTouser(MapStringWithValue data) {
		
		try {
			
			RoleEntity role_bd = roleRep.findByCode(data.getRef());
			if(role_bd == null) throw new HttpServiceExceptionHandle("Ce role n'est dans le systeme !!", HttpErrorCodes.NO_CONTENT_DATA);
			List<String> permissions = role_bd.getPermissions().parallelStream().map(item -> item.getCode()).collect(Collectors.toList());
			removePermissionsToUser(data.getRef(), permissions);
			return true;
		} catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
		
	}
	
	
	/** retirer des permissions a un utilisateur **/
	public Boolean removePermissionsToUser(String username, List<String> permissions){
		try {
			UserEntity user_bd = userRep.getUserByEmail(username);
			if(user_bd == null) throw new HttpServiceExceptionHandle("Cette user n'est dans le systeme !!", HttpErrorCodes.NO_CONTENT_DATA);
			 int count = userRep.removePermissionsToUser(username, permissions);
			 
			 if(count == 0)  throw new HttpServiceExceptionHandle("Aucun retrait effectué !!", HttpErrorCodes.BAD_FORMAT_DATA);
			 
			 return true;
		} catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}
	
	
	/** attribution des permissions a un utilisateur **/
	public Boolean attributionPermissionsToUser(String username, List<String> permissions){
		try {
			UserEntity user_bd = userRep.getUserByEmail(username);
			if(user_bd == null) throw new HttpServiceExceptionHandle("Cette user n'est dans le systeme !!", HttpErrorCodes.NO_CONTENT_DATA);
			 
			/*** liste des permission du role dont l'utilisateur dispose **/
			 List<PermissionEntity> reelPermissions = user_bd.getRole().getPermissions();
			 
			 for (String permission : permissions) {
				 /** il permet de verifie si la permission encours est dans la liste réel des permisions du role dons le user dispose**/ 
				 PermissionEntity pm = reelPermissions.parallelStream().filter(item -> item.getCode() == permission).limit(1).collect(Collectors.toList()).get(0);
				 if (pm != null) userRep.addPermissionToUser(user_bd.getId(), pm.getId());
			 }
			 
			 System.out.println("Attribution de permission ...");
			 return true;
		} catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}
	
	
	/** listes permissions of user **/
	public Map<String, Object> permissionsOfUser(MapStringWithValue data, int page, int limit){
		try {
			
			UserEntity user_bd = userRep.getUserByEmail(data.getRef());
			if(user_bd == null) throw new HttpServiceExceptionHandle("Cette user n'est dans le systeme !!", HttpErrorCodes.NO_CONTENT_DATA );
			
			  Pageable paging = PageRequest.of(page, limit);
			  Page<PermissionEntity> pagePermissions = null;
			  
			 if(data.getValue() == null || data.getValue().isEmpty()) pagePermissions = permissionRep.permissionsOfUser(user_bd.getId(), paging);
			 else pagePermissions = permissionRep.searchPermissionsOfUser(user_bd.getId(), data.getValue(), paging);
			  
			  Map<String, Object> results = new HashMap<>();
			  results.put("data", pagePermissions.getContent());
			  results.put("currentPage", pagePermissions.getNumber());
			  results.put("totalItems", pagePermissions.getTotalElements());
			  results.put("totalPages", pagePermissions.getTotalPages());
			 
			 System.out.println("liste des permissions utilisateur ...");
			 return results;
		} catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}
	
	
	

}
