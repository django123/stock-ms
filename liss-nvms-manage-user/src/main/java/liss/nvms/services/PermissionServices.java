package liss.nvms.services;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import liss.nvms.PermissionRepository;
import liss.nvms.RoleRepository;
import liss.nvms.UserRepository;
import liss.nvms.manage.PermissionEntity;
import liss.nvms.manage.RoleEntity;

@Service
@Transactional(rollbackFor = Exception.class, noRollbackFor = EntityNotFoundException.class)
public class PermissionServices {
	
	@Autowired UserRepository userRep;
	@Autowired RoleRepository roleRep;
	@Autowired PermissionRepository permissionRep;
	
	
	/** liste des permissions pour un role **/
	public void permissionsOfRole (String roleCode) {
		try {
			
			RoleEntity role_bd = roleRep.findByCode(roleCode);
			if(role_bd == null) throw new RuntimeException("Ce role n'est dans le systeme !!");
			
			role_bd.getPermissions(); // retourner la liste des permissions du role
			
			System.out.println("liste des permissions du role ...");
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			throw new RuntimeException("Une erreur serveur !!");
		}
	}
	
	/** ajouter les permissions a un role ***/
	public void addPermissionsToRole (List<String> permissions, String roleCode) {
		try {
			RoleEntity role_bd = roleRep.findByCode(roleCode);
			if(role_bd == null) throw new RuntimeException("Ce role n'est dans le systeme !!");
			for (String string : permissions) {
				PermissionEntity pm = permissionRep.findByCode(string);
				if(pm != null) roleRep.addPermissionToUser(role_bd.getId(), pm.getId());
			}
			System.out.println("liste des permissions du role ...");
		} catch (Exception e) {
			System.err.println(e.getMessage());
			throw new RuntimeException("Une erreur serveur !!");
		}
	}
	
	/** remove les permissions a un role ***/
	public void removePermissionsToRole (List<String> permissions, String roleCode) {
		try {
			RoleEntity role_bd = roleRep.findByCode(roleCode);
			if(role_bd == null) throw new RuntimeException("Ce role n'est dans le systeme !!");
			roleRep.removePermissionsToRole(role_bd.getId(), permissions);
			
			
			System.out.println("liste des permissions du role ...");
		} catch (Exception e) {
			System.err.println(e.getMessage());
			throw new RuntimeException("Une erreur serveur !!");
		}
	}
	
	
	
}
