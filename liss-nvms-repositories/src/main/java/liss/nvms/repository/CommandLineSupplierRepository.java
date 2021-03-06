package liss.nvms.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import liss.nvms.model.CommandLineSupplierEntity;
import liss.nvms.model.CommandSupplierEntity;


@Repository
public interface CommandLineSupplierRepository extends JpaRepository<CommandLineSupplierEntity, String> {

	/** get one command line supplier **/    
	@Query("SELECT line FROM CommandLineSupplier line WHERE id = :id")
	public CommandLineSupplierEntity getCommandeLineSupplierById(@Param("id") String id);
	
	
	/** delete une commande line customer**/
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM command_line_suppliers WHERE uuid = :commandID", nativeQuery = true)
	public int deleteCommandeLineSupplier( @Param("commandID") String campaignID);
	
}
