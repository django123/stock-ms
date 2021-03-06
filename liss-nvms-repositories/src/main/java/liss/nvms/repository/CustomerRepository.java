package liss.nvms.repository;

import liss.nvms.model.InvoiceCustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import liss.nvms.model.CustomerEntity;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, String>{
		
	/** get All Customer **/    
	@Query("SELECT c FROM Customer c WHERE isDeleted = false"
			+ " ORDER BY name ASC")
	public Page<CustomerEntity> getAllCustomer(Pageable page);
	
	/** get All Customer by param**/    
	@Query("SELECT c FROM Customer c WHERE isDeleted = false AND UPPER(name) LIKE CONCAT(UPPER(:param), '%') ORDER BY name ASC")
	public Page<CustomerEntity> getAllCustomerByPram(Pageable page, @Param("param") String name);
	
	
	/** get one customer **/    
	@Query("SELECT c FROM Customer c  WHERE isDeleted = false AND code = :code")
	public CustomerEntity getCustomerByCode(@Param("code") String code);
	
	/** get one customer **/    
	@Query("SELECT c FROM Customer c WHERE isDeleted = false AND  id = :id")
	public CustomerEntity getCustomerById(@Param("id") String id);
	
	@Query("SELECT c FROM Customer c WHERE isDeleted = false AND  UPPER(name) = UPPER(:name)")
	public CustomerEntity findByName(@Param("name") String name);
	
	@Query("SELECT c FROM Customer c WHERE isDeleted = false AND  UPPER(tva) = UPPER(:tva)")
	public CustomerEntity findByTva(@Param("tva") String tva);


}
