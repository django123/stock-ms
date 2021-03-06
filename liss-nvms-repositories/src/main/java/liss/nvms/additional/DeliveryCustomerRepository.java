package liss.nvms.additional;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import liss.nvms.Clivraison.DeliveryCustomerEntity;
import liss.nvms.model.CommandCustomerEntity;

@Repository
public interface DeliveryCustomerRepository extends JpaRepository<DeliveryCustomerEntity, String>{

	/** les notes avec code commande**/
	@Query("SELECT note FROM DeliveryCustomer note WHERE isDeleted = false AND command.code = :code ")
	public Page<DeliveryCustomerEntity> getAllDeliveryCustomerByCmdeCode(Pageable page, @Param("code") String cmdCode );
	
	/** les notes de livraisons sur une periode **/
	@Query("SELECT note FROM DeliveryCustomer note WHERE isDeleted = false AND createdAt BETWEEN :date1 AND :date2 ")
	public Page<DeliveryCustomerEntity> getAllDeliveryCustomerByCreatedDate(Pageable page, @Param("date1") Date date1, @Param("date2") Date date2 );
	
	/** les notes de livraison d'un customers **/
	@Query("SELECT note FROM DeliveryCustomer note WHERE isDeleted = false AND command.customer.id = :customer")
	public Page<DeliveryCustomerEntity> getAllNoteDeliveryCustomer(Pageable page, @Param("customer") String customer );
	
	
	/** notes de livraison sur une periode pour fournisseur**/
	@Query("SELECT note FROM DeliveryCustomer note WHERE isDeleted = false AND command.customer.id = :customer AND createdAt BETWEEN :date1 AND :date2 ")
	public Page<DeliveryCustomerEntity> getAllDeliveryCustomerByCreatedDateCustomer(Pageable page, @Param("customer") String customerID , @Param("date1") Date date1, @Param("date2") Date date2 );
	
	
	/** get All note livraison customer **/    
	@Query("SELECT note FROM DeliveryCustomer note WHERE isDeleted = false")
	public Page<DeliveryCustomerEntity> getAllDeliveryCustomer(Pageable page);
	
	
	/** get one delivery note of Customer **/    
	@Query("SELECT note FROM DeliveryCustomer note WHERE id = :id")
	public DeliveryCustomerEntity getDeliveryCustomerById(@Param("id") String id);
	
	/** valider une note de livraison customer**/
	@Modifying
	@Transactional
	@Query(value = "UPDATE customer_delivery SET  validated = true WHERE uuid = :commandID", nativeQuery = true)
	public int validateDeliveryCustomer( @Param("commandID") String campaignID);
	
}
