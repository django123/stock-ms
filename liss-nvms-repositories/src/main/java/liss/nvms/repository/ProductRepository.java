package liss.nvms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import liss.nvms.model.ProductEntity;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, String> {
	
	/** get **/
	ProductEntity findByCode(String code);
	
	@Query("SELECT p FROM Product p WHERE id =:id")
	public ProductEntity getProductById(@Param("id") String id);
	
	/** get All products **/    
	@Query("SELECT p FROM Product p WHERE isDeleted = false AND UPPER(name) like CONCAT('%', UPPER(:name), '%')"
			+ " ORDER BY name ASC")
	public Page<ProductEntity> getProducts(@Param("name") String name, Pageable page);
	
	@Query("SELECT p FROM Product p WHERE isDeleted = false AND UPPER(name) = UPPER(:name) ")
	public ProductEntity findByName(@Param("name") String name);

	
	
	
}
