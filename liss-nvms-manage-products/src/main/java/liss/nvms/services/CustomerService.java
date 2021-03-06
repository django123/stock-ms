package liss.nvms.services;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import liss.nvms.httpException.HttpServiceExceptionHandle;
import liss.nvms.model.CustomerEntity;
import liss.nvms.model.SupplierEntity;
import liss.nvms.repository.CustomerRepository;
import liss.nvms.utils.HttpErrorCodes;

@Service
@Transactional(rollbackFor = {HttpServiceExceptionHandle.class, SQLException.class }, noRollbackFor = EntityNotFoundException.class)
public class CustomerService {
	
	@Autowired CustomerRepository customerRep;
	
	/** aficher la liste des customers **/
	public  Map<String, Object> getAllCustomer(String name, int page, int limit) {
		
		try {
			Map<String, Object> results = new HashMap<>();
			Pageable paging = PageRequest.of( page, limit);
			Page<CustomerEntity> stockPage = null;
			if(name == null) stockPage = customerRep.getAllCustomer(paging);
			else  stockPage = customerRep.getAllCustomerByPram(paging, name);
			
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
	
/** ajouter un client **/	
  public CustomerEntity addCustomer(CustomerEntity customer) {
		
		try {
			if(customer == null) throw new HttpServiceExceptionHandle("le customer envoye est vide", HttpErrorCodes.INTERNAL_SERVER_ERROR);
			if(customerRep.getCustomerByCode(customer.getCode()) != null)throw new HttpServiceExceptionHandle("Ce Code de client existe deja", HttpErrorCodes.CONFLITS);
			if(customerRep.findByName(customer.getName()) != null ) throw new HttpServiceExceptionHandle("Ce nom de client existe deja", HttpErrorCodes.CONFLITS);
			if(customerRep.findByTva(customer.getTva()) != null) throw new HttpServiceExceptionHandle("Cette tva existe deja", HttpErrorCodes.CONFLITS);
			 return customerRep.save(customer);
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
  }
  
  /** modiier un client **/
  public CustomerEntity updateCustomer(CustomerEntity newCustomer) {
		
		try {
			
			if(newCustomer.getId() == null) new HttpServiceExceptionHandle("Client introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			
			CustomerEntity customer = customerRep.getCustomerById(newCustomer.getId());
			if(customer == null) new HttpServiceExceptionHandle("Client introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			if (customerRep.findByName(newCustomer.getName()) == null) {
				customer.setName(newCustomer.getName());
			}
			else {
				throw new HttpServiceExceptionHandle("Ce nom de fornisseur existe deja", HttpErrorCodes.CONFLITS);
			}
			
			if (customerRep.findByName(newCustomer.getTva()) == null) {
				customer.setTva(newCustomer.getTva());
			}
			else {
				throw new HttpServiceExceptionHandle("Cette existe deja", HttpErrorCodes.CONFLITS);
			}
			customer.setAdress(newCustomer.getAdress());
			customer.setCity(newCustomer.getCity());
			customer.setContactPerson(newCustomer.getContactPerson());
			customer.setCountry(newCustomer.getCountry());
			customer.setEmail(newCustomer.getEmail());
			customer.setFax(newCustomer.getFax());
			customer.setPhone(newCustomer.getPhone());
			customer.setPostalCode(newCustomer.getPostalCode());
			
			return customerRep.save(customer);
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
  }
	
  /**  suppression d'un client**/

  public boolean deleteCustomer(String reference){
  	try {
  		CustomerEntity customer = customerRep.getCustomerById(reference);
		if(customer == null) throw new HttpServiceExceptionHandle("Client introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
		if(customer.getIsDeleted() == true) customer.setIsDeleted(false);
		else customer.setIsDeleted(true);
		customerRep.save(customer);
		return true;
	}catch (HttpServiceExceptionHandle e) {
		HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
		throw new HttpServiceExceptionHandle(e.getMessage(),code);
	}
  }
	

}
