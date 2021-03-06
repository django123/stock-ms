package liss.nvms.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import liss.nvms.model.FormatEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import liss.nvms.model.CommandCustomerEntity;
import liss.nvms.model.CustomerEntity;

@Repository
public interface CommandCustomerRepository extends JpaRepository<CommandCustomerEntity, String>{
	
	
	@Query("SELECT COUNT(cmd) FROM CommandCustomer cmd")
	public int countCustomerCommand();
	
	/** get All command customer **/    
	@Query("SELECT cmd FROM CommandCustomer cmd WHERE isDeleted = false ORDER BY code DESC")
	public Page<CommandCustomerEntity> getAllCommandeCustomer(Pageable page);
	
	/** commandes sur une periode **/
	@Query("SELECT cmd FROM CommandCustomer cmd WHERE isDeleted = false AND createdAt BETWEEN :date1 AND :date2 ORDER BY code DESC")
	public Page<CommandCustomerEntity> getAllCommandeCustomerByCreatedDate(Pageable page, @Param("date1") Date date1, @Param("date2") Date date2 );
	
	/** les commandes d'un customers **/
	@Query("SELECT cmd FROM CommandCustomer cmd WHERE isDeleted = false AND customer.id = :customer"
			+ " ORDER BY code DESC")
	public Page<CommandCustomerEntity> getAllCommandeCustomer(Pageable page, @Param("customer") String customer );

	/** les commandes avec code commande**/
	@Query("SELECT cmd FROM CommandCustomer cmd WHERE isDeleted = false AND cmd.code LIKE CONCAT('%',:code, '%')"
			+ " ORDER BY code DESC")
	public Page<CommandCustomerEntity> getAllCommandeCustomerByCmdeCode(Pageable page, @Param("code") String cmdCode );
	
	/** commandes pour un customer ayant un code commande **/
	@Query("SELECT cmd FROM CommandCustomer cmd WHERE isDeleted = false AND code = :code AND customer.id = :customer ORDER BY createdAt DESC")
	public Page<CommandCustomerEntity> getAllCommandeCustomerByCmdeCodeAndCustomer(Pageable page, @Param("code") String cmdCode, @Param("customer") String customer);
	
	/** commandes sur une periode pour fournisseur**/
	@Query("SELECT cmd FROM CommandCustomer cmd WHERE isDeleted = false AND customer.id = :customer AND createdAt BETWEEN :date1 AND :date2 "
			+ " ORDER BY code DESC")
	public Page<CommandCustomerEntity> getAllCommandeCustomerByCreatedDateCustomer(Pageable page, @Param("customer") String customerID , @Param("date1") Date date1, @Param("date2") Date date2 );
	
	@Query("SELECT cmd FROM CommandCustomer cmd WHERE isDeleted = false AND customer.id = :customer AND createdAt BETWEEN :date1 AND :date2 ORDER BY createdAt DESC")
	public List<CommandCustomerEntity> getListCommandeCustomerByCreatedDateCustomer(@Param("customer") String customerID , @Param("date1") Date date1, @Param("date2") Date date2 );
	
	/** commandes sur une periode pour customer**/
	@Query("SELECT DISTINCT cmd.customer FROM CommandCustomer cmd WHERE cmd.isDeleted = false AND cmd.createdAt BETWEEN :date1 AND :date2")
	public List<CustomerEntity> getCustomerWhoHaveCmd(@Param("date1") Date date1, @Param("date2") Date date2 );
	
	/** get one command Customer **/    
	@Query("SELECT cmd FROM CommandCustomer cmd WHERE id = :id")
	public CommandCustomerEntity getCommandeCustomerById(@Param("id") String id);
	
	/** get one command Customer **/    
	@Query("SELECT cmd FROM CommandCustomer cmd WHERE isDeleted = false AND code like CONCAT('%', :param) AND id NOT IN ( SELECT invoice.commandCustomer.id FROM InvoiceCustomer invoice WHERE isDeleted = false )")
	public List<CommandCustomerEntity> getCmdWhoNotHaveInvoice(@Param("param") String param);
	
	/** valider une commande customer**/
	@Modifying
	@Transactional
	@Query(value = "UPDATE command_customer SET  validated = true WHERE uuid = :commandID", nativeQuery = true)
	public int validateCommandeCustomer( @Param("commandID") String campaignID);
	
	/** invalider une commande customer**/
	@Modifying
	@Transactional
	@Query(value = "UPDATE command_customer SET  validated = false WHERE uuid = :commandID", nativeQuery = true)
	public int inValidateCommandeCustomer( @Param("commandID") String campaignID);
		
	/** delete une commande customer**/
	@Modifying
	@Transactional
	@Query(value = "UPDATE command_customer SET  deleted = true WHERE uuid = :commandID", nativeQuery = true)
	public int deleteCommandeCustomer( @Param("commandID") String campaignID);

	/** get format **/
	@Query("SELECT cmd FROM CommandCustomer cmd WHERE id = :id")
	public CommandCustomerEntity getCommandCustomerById(@Param("id") String id);
	
}
