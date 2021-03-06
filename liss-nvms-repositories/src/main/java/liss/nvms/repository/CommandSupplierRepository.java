package liss.nvms.repository;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import liss.nvms.model.CommandSupplierEntity;


@Repository
public interface CommandSupplierRepository extends JpaRepository<CommandSupplierEntity, String>{


	
	/** get All command supplier **/    
	@Query("SELECT cmd FROM CommandSupplier cmd WHERE isDeleted = false ORDER BY code DESC")
	public Page<CommandSupplierEntity> getAllCommandeSupplier(Pageable page);
	
	/** commandes sur une periode **/
	@Query("SELECT cmd FROM CommandSupplier cmd WHERE isDeleted = false AND createdAt BETWEEN :date1 AND :date2 ORDER BY code DESC ")
	public Page<CommandSupplierEntity> getAllCommandeSupplierByCreatedDate(Pageable page, @Param("date1") Date date1, @Param("date2") Date date2 );
	
	/** les commandes d'un fournisseurs **/
	@Query("SELECT cmd FROM CommandSupplier cmd WHERE isDeleted = false AND supplier.id = :fournisseur ORDER BY code DESC ")
	public Page<CommandSupplierEntity> getAllCommandeSupplier(Pageable page, @Param("fournisseur") String fournisseurID );
	
	
	/** les commandes avec code commande**/
	@Query("SELECT cmd FROM CommandSupplier cmd WHERE isDeleted = false AND cmd.code LIKE CONCAT('%',:code, '%')"
			+ "ORDER BY code DESC ")
	public Page<CommandSupplierEntity> getAllCommandeSupplierByCmdeCode(Pageable page, @Param("code") String cmdCode );
	
	
	/** commandes pour un forunisseur ayant un code commande **/
	@Query("SELECT cmd FROM CommandSupplier cmd WHERE isDeleted = false AND code = :code AND supplier.id = :fournisseur")
	public Page<CommandSupplierEntity> getAllCommandeSupplierByCmdeCodeAndSupplier(Pageable page, @Param("code") String cmdCode, @Param("fournisseur") String fournisseurID);
	
	
	
	/** commandes sur une periode pour fournisseur**/
	@Query("SELECT cmd FROM CommandSupplier cmd WHERE isDeleted = false AND supplier.id = :fournisseur AND createdAt BETWEEN :date1 AND :date2 ORDER BY code DESC")
	public Page<CommandSupplierEntity> getAllCommandeSupplierByCreatedDateSupplier(Pageable page, @Param("fournisseur") String fournisseurID , @Param("date1") Date date1, @Param("date2") Date date2 );
	
	/** valider une commande fournisseur**/
	@Modifying
	@Transactional
	@Query(value = "UPDATE command_suppliers SET validated = true WHERE uuid = :commandID", nativeQuery = true)
	public int validateCommandeSupplier( @Param("commandID") String campaignID);
	
	
	/** get one command supplier **/    
	@Query("SELECT cmd FROM CommandSupplier cmd WHERE id = :id")
	public CommandSupplierEntity getCommandeSupplierById(@Param("id") String id);
	

	/** delete une commande customer**/
	@Modifying
	@Transactional
	@Query(value = "UPDATE command_suppliers SET  deleted = true WHERE uuid = :commandID", nativeQuery = true)
	public int deleteCommandeSupplier( @Param("commandID") String campaignID);
	
	
	
	
}
