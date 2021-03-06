package liss.nvms.services;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import liss.nvms.Flivraison.DeliverySupplierEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import liss.nvms.httpException.HttpServiceExceptionHandle;
import liss.nvms.model.ProductEntity;
import liss.nvms.repository.ProductRepository;
import liss.nvms.utils.HttpErrorCodes;

@Service
@Transactional(rollbackFor = {HttpServiceExceptionHandle.class, SQLException.class }, noRollbackFor = EntityNotFoundException.class)
public class ProductService {

	@Autowired ProductRepository productRep;
	
	
	/*** rechercher un produit en fonction de son nom **/
	public Map<String, Object>  getAllProduct(String name, int page, int limit){
		try {
			Map<String, Object> results = new HashMap<>();
			Pageable paging = PageRequest.of( page, limit);
			if(name == null) name = "";
			Page<ProductEntity> productsPage = productRep.getProducts(name, paging);
			
			 results.put("data", productsPage.getContent());
			 results.put("currentPage", productsPage.getNumber());
			 results.put("totalItems", productsPage.getTotalElements());
			 results.put("totalPages", productsPage.getTotalPages());
			
			return results;

		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
	}
	
	/** ajouter un produit **/
	public ProductEntity addProduct (ProductEntity product) {
		try {
			
			if(product == null) throw new HttpServiceExceptionHandle("le produit envoye est vide", HttpErrorCodes.INTERNAL_SERVER_ERROR);
			if(productRep.findByName(product.getName()) != null) throw new HttpServiceExceptionHandle("Ce nom de produit existe deja", HttpErrorCodes.CONFLITS);
			String code = "#NVP"+System.currentTimeMillis();
			product.setCode(code);
			return productRep.save(product);
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}
	
	/** modifier un produit **/
	public ProductEntity updateProduct (ProductEntity product) {
		try {
			  
			if (product.getId() == null) new HttpServiceExceptionHandle("produit introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR); 
			ProductEntity productEntity = productRep.getProductById(product.getId());
			if(productRep.findByName(product.getName()) == null) {
				productEntity.setName(product.getName());
			}
			else {
				throw new HttpServiceExceptionHandle("Ce nom de produit existe deja", HttpErrorCodes.CONFLITS);
			}
			productEntity.setCode(product.getCode());
			productEntity.setDescription(product.getDescription());
			return productRep.save(productEntity);
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}

	/**  suppression d'un produit**/

	public boolean deleteProduct(String reference){
		try {
			ProductEntity product = productRep.getProductById(reference);
			if(product == null) throw new HttpServiceExceptionHandle("Note introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			if(product.getIsDeleted() == true) product.setIsDeleted(false);
			else product.setIsDeleted(true);
			productRep.save(product);
			return true;
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
	}
	
}
