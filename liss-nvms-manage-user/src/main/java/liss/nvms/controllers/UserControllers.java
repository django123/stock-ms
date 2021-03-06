package liss.nvms.controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import liss.nvms.httpException.HttpServiceExceptionHandle;
import liss.nvms.manage.UserEntity;
import liss.nvms.models.MapStringWithListString;
import liss.nvms.models.MapStringWithValue;
import liss.nvms.utils.HttpErrorCodes;
import liss.nvms.utils.HttpErrorMessage;
import liss.nvms.services.UserService;


@RestController
@RequestMapping("user")
@CrossOrigin("*")
public class UserControllers {
	
	@Autowired UserService userService;
	
	/** add user **/
	@PostMapping
	public ResponseEntity<?> saveUser(HttpServletRequest request,  @RequestBody UserEntity userEntity) {
		try{
			if(userEntity == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			return new ResponseEntity<>(userService.savedUser(userEntity), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	/** update user **/
	@PutMapping
	public ResponseEntity<?> updateUser(HttpServletRequest request,  @RequestBody UserEntity userEntity) {
		try{
			if(userEntity == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			return new ResponseEntity<>(userService.updatedUser(userEntity), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	
	/** get all users **/
	@GetMapping
	public ResponseEntity<?> getUsers(HttpServletRequest request, @RequestParam(value = "query", required = false) String query,
									@RequestParam(value = "page", required = false, defaultValue = "0") int page,
									@RequestParam(value = "limit", required = false, defaultValue = "100") int limit) {
		try{
			 return new ResponseEntity<>(userService.getUsers(query, page, limit), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	/** set role of user **/
	@PutMapping("set-role")
	public ResponseEntity<?> setRoleToUser(HttpServletRequest request,  @RequestBody MapStringWithValue userRole) {
		try{
			
			if(userRole == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			
			return new ResponseEntity<>(userService.addRoleTouser(userRole), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	/** remove role to user **/
	@PutMapping("remove-role")
	public ResponseEntity<?> removeRoleToUser(HttpServletRequest request,  @RequestBody MapStringWithValue userRole) {
		try{
			
			if(userRole == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			return new ResponseEntity<>(userService.removeRoleTouser(userRole), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	
	/** remove pemission to user **/
	@PutMapping("remove-permission")
	public ResponseEntity<?> lockPermissionToUser(HttpServletRequest request,  @RequestBody MapStringWithListString userPermissionsModel) {
		try{
			
			if(userPermissionsModel == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			return new ResponseEntity<>(userService.removePermissionsToUser(userPermissionsModel.getRef(), userPermissionsModel.getStrings()), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	
	
	/** add the permission to user **/
	@PutMapping("add-permission")
	public ResponseEntity<?> addPermissionToUser(HttpServletRequest request,  @RequestBody MapStringWithListString userPermissionsModel) {
		try{
			if(userPermissionsModel == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			return new ResponseEntity<>(userService.attributionPermissionsToUser(userPermissionsModel.getRef(), userPermissionsModel.getStrings()), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}
	
	/** permissions of user **/
	@PutMapping("permission")
	public ResponseEntity<?> getUserPermissions(HttpServletRequest request, @RequestBody MapStringWithValue data,
									@RequestParam(value = "page", required = false, defaultValue = "0") int page,
									@RequestParam(value = "limit", required = false, defaultValue = "100") int limit) {
		try{
			
			 if(data == null) throw new HttpServiceExceptionHandle("Contenue vide !!", HttpErrorCodes.BAD_FORMAT_DATA);
			 return new ResponseEntity<>(userService.permissionsOfUser(data, page, limit), HttpStatus.OK);
		}catch (HttpServiceExceptionHandle e) {
			return new ResponseEntity<>(new HttpErrorMessage(new Date(), e.getMessage()), HttpStatus.valueOf(e.getErrorCode()));
		}
	}

}
