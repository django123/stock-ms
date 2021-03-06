package liss.nvms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import liss.nvms.model.FormatEntity;
import liss.nvms.model.ProductEntity;

@Repository
public interface FormatRepository extends JpaRepository<FormatEntity, String> {
	
	/** get format **/
	@Query("SELECT f FROM Format f WHERE id = :id")
	public FormatEntity getFormatById(@Param("id") String id);
	
	/** get formats **/
	@Query("SELECT f FROM Format f WHERE isDeleted = false"
			+ " ORDER BY name ASC")
	public List<FormatEntity> getFormats();
	
	@Query("SELECT f FROM Format f WHERE UPPER(name) =UPPER(:name)")
	public FormatEntity findByName(@Param("name") String name);
}
