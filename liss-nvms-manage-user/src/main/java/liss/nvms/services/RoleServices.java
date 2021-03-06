package liss.nvms.services;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import liss.nvms.PermissionRepository;
import liss.nvms.RoleRepository;
import liss.nvms.httpException.HttpServiceExceptionHandle;
import liss.nvms.manage.PermissionEntity;
import liss.nvms.manage.RoleEntity;
import liss.nvms.models.MapStringWithListString;
import liss.nvms.models.MapStringWithValue;
import liss.nvms.utils.HttpErrorCodes;

@Service
@Transactional(rollbackFor = HttpServiceExceptionHandle.class, noRollbackFor = EntityNotFoundException.class)
public class RoleServices {
	
	@Autowired RoleRepository roleRep;
	@Autowired PermissionRepository permissionRep;
	
	
	/** save role **/
	public RoleEntity saveRole(RoleEntity roleEntity) {
		
		try {
			roleEntity = roleRep.save(roleEntity);
			return roleEntity;
		 } catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}
	
	/** update role **/
	public RoleEntity updateRole(RoleEntity roleEntity) {
		
		try {
			
			RoleEntity roleBd = roleRep.findByCode(roleEntity.getCode());
			if(roleBd == null) throw new HttpServiceExceptionHandle("Cette user n'est dans le systeme !!", HttpErrorCodes.NO_CONTENT_DATA);
			
			roleBd.setDescription(roleEntity.getDescription());
	
			roleEntity = roleRep.save(roleBd);
			return roleEntity;
		 } catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}
	
	
	/** get Roles **/
	public Map<String, Object> getRoles(String param, int page, int limit) {
		
		try {
			
			Pageable paging = PageRequest.of(page, limit);
			Page<RoleEntity> pageRoles = null; 
			if(param == null) roleRep.findAll(paging);
			else roleRep.getRolesWithParam(param, paging);
			
			  Map<String, Object> results = new HashMap<>();
			  results.put("data", pageRoles.getContent());
			  results.put("currentPage", pageRoles.getNumber());
			  results.put("totalItems", pageRoles.getTotalElements());
			  results.put("totalPages", pageRoles.getTotalPages());
			  
			 return results;
		 } catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}
	
	/** attribution des permissions a un role **/
	public Boolean attributionPermissionsToRole(MapStringWithListString data){
		try {
			RoleEntity roleBd = roleRep.findByCode(data.getRef());
			if(roleBd == null) throw new HttpServiceExceptionHandle("Cette user n'est dans le systeme !!", HttpErrorCodes.NO_CONTENT_DATA);
			 
			 for (String permission : data.getStrings()) {
				 PermissionEntity pm = permissionRep.findByCode(permission);
				 if (pm != null) roleRep.addPermissionToUser(roleBd.getId(), pm.getId());
			 }
			 
			 System.out.println("Attribution de permission ...");
			 return true;
		} catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}
	
	
	
	/** remove des permissions a un role **/
	public Boolean removePermissionsToRole(MapStringWithListString data){
		try {
			RoleEntity roleBd = roleRep.findByCode(data.getRef());
			if(roleBd == null) throw new HttpServiceExceptionHandle("Cette user n'est dans le systeme !!", HttpErrorCodes.NO_CONTENT_DATA);
			 
			 int count = roleRep.removePermissionsToRole(roleBd.getId(), data.getStrings());
			 
			 if(count == 0) throw new HttpServiceExceptionHandle("Aucun retrait effectué !!", HttpErrorCodes.BAD_FORMAT_DATA);
			 
			 System.out.println("Attribution de permission ...");
			 return true;
		} catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}
	
	
	/** liste le  permissions a un role **/
   public Map<String, Object> getRolePermission(MapStringWithValue data, int page, int limit) {
		
		try {
				
			  RoleEntity roleBd = roleRep.findByCode(data.getRef());
			  if(roleBd == null) throw new HttpServiceExceptionHandle("Cette user n'est dans le systeme !!", HttpErrorCodes.NO_CONTENT_DATA);
			  
			  Pageable paging = PageRequest.of(page, limit);
			  Page<PermissionEntity> pagePermissions = null;
			  if(data.getValue() == null || data.getValue().isEmpty()) pagePermissions = permissionRep.permissionsOfRole(roleBd.getId(), paging);
			  else pagePermissions = permissionRep.searchPermissionsOfRole(roleBd.getId(), data.getValue(), paging);
			  
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
