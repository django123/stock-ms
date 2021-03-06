package liss.nvms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import liss.nvms.model.SupplierEntity;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierEntity, String>{

	
	/** get All supplier **/    
	@Query("SELECT s FROM Supplier s WHERE isDeleted = false"
			+ " ORDER BY name ASC")
	public Page<SupplierEntity> getAllSupplier(Pageable page);
	
	
	@Query("SELECT s FROM Supplier s WHERE isDeleted = false AND  UPPER(name) LIKE CONCAT(UPPER(:name), '%')"
			+ " ORDER BY name ASC")
	public Page<SupplierEntity> getAllSupplier(Pageable page, @Param("name") String name);
	
	
	/** get one supplier **/    
	@Query("SELECT s FROM Supplier s WHERE id = :id")
	public SupplierEntity getSupplier(@Param("id") String id);
	
	@Query("SELECT s FROM Supplier s WHERE isDeleted = false AND UPPER(name) = UPPER(:name)")
	public SupplierEntity findByName(@Param("name") String name);
	
	@Query("SELECT s FROM Supplier s WHERE isDeleted = false AND UPPER(tva) = UPPER(:tva)")
	public SupplierEntity findByTva(@Param("tva") String tva);
	
	
	@Query("SELECT s FROM Supplier s WHERE isDeleted = false AND UPPER(email) = UPPER(:email)")
	public SupplierEntity findByEmail(@Param("email") String email);
	
	
	@Query("SELECT s FROM Supplier s WHERE isDeleted = false AND UPPER(code) = UPPER(:code)")
	public SupplierEntity findByCode(@Param("code") String email);
	
	
}
