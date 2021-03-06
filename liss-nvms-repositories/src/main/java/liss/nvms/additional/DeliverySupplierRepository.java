package liss.nvms.additional;

import java.util.Date;

import liss.nvms.model.FormatEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import liss.nvms.Flivraison.DeliverySupplierEntity;


public interface DeliverySupplierRepository extends JpaRepository<DeliverySupplierEntity, String>{
	
	
	/** les notes avec code commande**/
	@Query("SELECT note FROM DeliverySupplier note WHERE isDeleted = false AND command.code = :code ")
	public Page<DeliverySupplierEntity> getAllDeliverySupplierByCmdeCode(Pageable page, @Param("code") String cmdCode );
	
	/** les notes de approvisionnements sur une periode **/
	@Query("SELECT note FROM DeliverySupplier note WHERE isDeleted = false AND createdAt BETWEEN :date1 AND :date2 "
			+ " ORDER BY createdAt DESC")
	public Page<DeliverySupplierEntity> getAllDeliverySupplierByCreatedDate(Pageable page, @Param("date1") Date date1, @Param("date2") Date date2 );
	
	/** les notes de approvisionnement d'un Suppliers **/
	@Query("SELECT note FROM DeliverySupplier note WHERE isDeleted = false AND command.supplier.id = :Supplier")
	public Page<DeliverySupplierEntity> getAllNoteDeliverySupplier(Pageable page, @Param("Supplier") String Supplier );
	
	
	/** notes de approvisionnement sur une periode pour fournisseur**/
	@Query("SELECT note FROM DeliverySupplier note WHERE isDeleted = false AND command.supplier.id = :Supplier AND createdAt BETWEEN :date1 AND :date2 "
			+ " ORDER BY createdAt DESC")
	public Page<DeliverySupplierEntity> getAllDeliverySupplierByCreatedDateSupplier(Pageable page, @Param("Supplier") String SupplierID , @Param("date1") Date date1, @Param("date2") Date date2 );
	
	
	/** get All note approvisionnement Supplier **/    
	@Query("SELECT note FROM DeliverySupplier note WHERE isDeleted = false"
			+ " ORDER BY createdAt DESC")
	public Page<DeliverySupplierEntity> getAllDeliverySupplier(Pageable page);
	
	
	/** get All note approvisionnement Supplier **/    
	@Query("SELECT note FROM DeliverySupplier note WHERE id = :reference")
	public DeliverySupplierEntity getAllDeliverySupplierByRef(@Param("reference") String reference);

	/** get deliverySupplier **/
	@Query("SELECT note FROM DeliverySupplier note WHERE id = :id")
	public DeliverySupplierEntity getDeliverySupplierById(@Param("id") String id);
	
}
