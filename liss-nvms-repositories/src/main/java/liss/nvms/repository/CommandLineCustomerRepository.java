package liss.nvms.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import liss.nvms.model.CommandLineCustomerEntity;


@Repository
public interface CommandLineCustomerRepository extends JpaRepository<CommandLineCustomerEntity, String>{
	
	/** get one command line customer **/
	@Query("SELECT line FROM CommandLineCustomer line WHERE id = :id")
	public CommandLineCustomerEntity getCommandeLineCustomerById(@Param("id") String id);
	
	/** delete une commande line customer**/
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM command_line_customers WHERE uuid = :commandID", nativeQuery = true)
	public int deleteCommandeLineCustomer( @Param("commandID") String campaignID);
	
}
