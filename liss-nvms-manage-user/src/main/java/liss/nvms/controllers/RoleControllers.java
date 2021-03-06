package liss.nvms.controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import liss.nvms.httpException.HttpServiceExceptionHandle;
import liss.nvms.models.MapStringWithListString;
import liss.nvms.models.MapStringWithValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


import liss.nvms.manage.RoleEntity;

import liss.nvms.utils.HttpErrorCodes;
import liss.nvms.utils.HttpErrorMessage;
import liss.nvms.services.RoleServices;

public class RoleControllers {
	
	@Autowired RoleServices roleService;
	
	/** add role **/
	@PostMapping
	public ResponseEntity<?> saveRole(HttpServletRequest request,  @RequestBody RoleEntity roleEntity) {
		try{
			if(roleEntity == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			
			return new ResponseEntity<>(roleService.saveRole(roleEntity), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	/** update role **/
	@PutMapping
	public ResponseEntity<?> updateRole(HttpServletRequest request,  @RequestBody RoleEntity roleEntity) {
		try{
			if(roleEntity == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			return new ResponseEntity<>(roleService.updateRole(roleEntity), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	
	
	/** get all role **/
	@GetMapping
	public ResponseEntity<?> getRoles(HttpServletRequest request, @RequestParam(value = "query", required = false) String query,
									@RequestParam(value = "page", required = false, defaultValue = "0") int page,
									@RequestParam(value = "limit", required = false, defaultValue = "100") int limit) {
		try{
			return new ResponseEntity<>(roleService.getRoles(query, page, limit), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	
	
	/** set permissions to role **/
	@PutMapping("set-permission")
	public ResponseEntity<?> setRolePermissions(HttpServletRequest request,  @RequestBody MapStringWithListString data) {
		try{
			if(data == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			return new ResponseEntity<>(roleService.attributionPermissionsToRole(data), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	
	/** remove permissions to role **/
	@PutMapping("remove-permission")
	public ResponseEntity<?> removeRolePermissions(HttpServletRequest request, @RequestBody MapStringWithListString data) {
		try{
			if(data == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			return new ResponseEntity<>(roleService.removePermissionsToRole(data), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	
	/** list permission of role **/
	@PutMapping("permission")
	public ResponseEntity<?> getRolePermission(HttpServletRequest request, @RequestBody MapStringWithValue data,
									@RequestParam(value = "page", required = false, defaultValue = "0") int page,
									@RequestParam(value = "limit", required = false, defaultValue = "100") int limit) {
		try{
			 if(data == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			
			return new ResponseEntity<>(roleService.getRolePermission(data, page, limit), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	

}
