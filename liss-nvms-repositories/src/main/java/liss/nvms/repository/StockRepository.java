package liss.nvms.repository;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import liss.nvms.composite.StockPK;
import liss.nvms.model.StockEntity;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, StockPK> {

	 
	@Query("SELECT COUNT(s) FROM Stock s")
	public int countStockProduct();
	
	
	@Query("SELECT s FROM Stock s WHERE id = :id")
	public StockEntity getStockById(@Param("id") String id);
	
	/** get All stock **/    
	@Query("SELECT s FROM Stock s WHERE isDeleted = false AND UPPER(product.name) like UPPER(CONCAT('%',:param, '%'))"
			+ " ORDER BY s.product.name DESC")
	public Page<StockEntity> getStocks(Pageable page, @Param("param") String param);
	
	
	/** get stock ( produit - format ) **/
	@Query("SELECT s FROM Stock s WHERE product.id = :productID AND format.id = :formatID ")
	public StockEntity getStockbyProductFormat(@Param("productID") String productID, @Param("formatID") String formatID);
	
	
	public StockEntity findByCode(String code);
	
	
	
	/** get All format product for events **/    
	@Query("SELECT s FROM Stock s WHERE UPPER(product.name) like UPPER(CONCAT('%',:param, '%'))")
	public Page<StockEntity> getProductForEvents(@Param("param") String param, Pageable page);
	
	


	
	
}
