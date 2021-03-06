package liss.nvms.services;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import liss.nvms.model.CustomerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import liss.nvms.httpException.HttpServiceExceptionHandle;

import liss.nvms.model.SupplierEntity;
import liss.nvms.repository.SupplierRepository;
import liss.nvms.utils.HttpErrorCodes;

@Service
@Transactional(rollbackFor = {HttpServiceExceptionHandle.class, SQLException.class }, noRollbackFor = EntityNotFoundException.class)
public class SupplierService {
	
	@Autowired SupplierRepository supplierRep;
	
	/** aficher la liste des stocks **/
	public  Map<String, Object> getAllSupplier(String name, int page, int limit) {
		
		try {
			Map<String, Object> results = new HashMap<>();
			Pageable paging = PageRequest.of( page, limit);
			Page<SupplierEntity> stockPage = null;
			if(name == null) stockPage = supplierRep.getAllSupplier(paging);
			else  stockPage = supplierRep.getAllSupplier(paging, name);
			
			 results.put("data", stockPage.getContent());
			 results.put("currentPage", stockPage.getNumber());
			 results.put("totalItems", stockPage.getTotalElements());
			 results.put("totalPages", stockPage.getTotalPages());
			
			 return results;

		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}
	
	
	/** ajouter un fournisseur **/
	public  SupplierEntity addSupplier(SupplierEntity supplier) {
		
		try {
			 if(supplier == null) throw new HttpServiceExceptionHandle("le format envoye est vide", HttpErrorCodes.INTERNAL_SERVER_ERROR);
			 if(supplierRep.findByCode(supplier.getCode()) != null)throw new HttpServiceExceptionHandle("Ce Code du fournisseur existe deja", HttpErrorCodes.CONFLITS);
			 if(supplierRep.findByName(supplier.getName()) != null) throw new HttpServiceExceptionHandle("Ce nom de fornisseur existe deja", HttpErrorCodes.CONFLITS);
			 if(supplierRep.findByName(supplier.getTva()) != null) throw new HttpServiceExceptionHandle("Cette tva existe deja", HttpErrorCodes.CONFLITS);
			return supplierRep.save(supplier);
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}
	
	
	/** modifier  un fournisseur **/
	public  SupplierEntity updateSupplier(SupplierEntity supplier) {
		
		try {
			if(supplier.getId() == null) throw new HttpServiceExceptionHandle("Fournisseur introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			SupplierEntity supplier2 = supplierRep.getSupplier(supplier.getId());
			if(supplier2 == null) new HttpServiceExceptionHandle("Fournisseur introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			if (supplierRep.findByName(supplier.getName()) == null) {
				supplier2.setName(supplier.getName());
			}
			else {
				throw new HttpServiceExceptionHandle("Ce nom de fornisseur existe deja", HttpErrorCodes.CONFLITS);
			}
			
			if (supplierRep.findByName(supplier.getTva()) == null) {
				supplier2.setTva(supplier.getTva());
			}
			else {
				throw new HttpServiceExceptionHandle("Cette existe deja", HttpErrorCodes.CONFLITS);
			}
			
			supplier2.setAdress(supplier.getAdress());
			supplier2.setCity(supplier.getCity());
			supplier2.setContact_person(supplier.getContact_person());
			supplier2.setCountry(supplier.getCountry());
			supplier2.setEmail(supplier.getEmail());
			supplier2.setPostal_code(supplier.getPostal_code());
			supplier2.setFax(supplier.getFax());
			supplier2.setPhone(supplier.getPhone());
			return supplierRep.save(supplier2);
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}


	/**  suppression d'un supplier**/

	public boolean deleteSupplier(String reference){
		try {
			SupplierEntity supplier = supplierRep.getSupplier(reference);
			if(supplier == null) throw new HttpServiceExceptionHandle("Supplier introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			if(supplier.getIsDeleted() == true) supplier.setIsDeleted(false);
			else supplier.setIsDeleted(true);
			supplierRep.save(supplier);
			return true;
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
	}
	
	
}
