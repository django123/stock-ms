package liss.nvms.repository;

import liss.nvms.model.CommandCustomerEntity;
import liss.nvms.model.CustomerEntity;
import liss.nvms.model.InvoiceCustomerEntity;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceCustomerEntity, String> {
	
	@Query("SELECT COUNT(invoice) FROM InvoiceCustomer invoice")
	public int countCustomerInvoice();
	
	/** get All invoice customer **/    
	@Query("SELECT invoice FROM InvoiceCustomer invoice WHERE isDeleted = false ORDER BY code DESC")
	public Page<InvoiceCustomerEntity> getAllInvoiceCustomer(Pageable page);
	
	/** invoices sur une periode **/
	@Query("SELECT invoice FROM InvoiceCustomer invoice WHERE isDeleted = false AND createdAt BETWEEN :date1 AND :date2 ORDER BY code DESC")
	public Page<InvoiceCustomerEntity> getAllInvoiceCustomerByCreatedDate(Pageable page, @Param("date1") Date date1, @Param("date2") Date date2 );
	
	/** les invoices d'un customers **/
	@Query("SELECT invoice FROM InvoiceCustomer invoice WHERE isDeleted = false AND commandCustomer.customer.id = :customer ORDER BY code DESC")
	public Page<InvoiceCustomerEntity> getAllInvoiceCustomer(Pageable page, @Param("customer") String customer );
	
	/** les invoices avec code commande**/
	@Query("SELECT invoice FROM InvoiceCustomer invoice WHERE isDeleted = false AND code = :code ORDER BY code DESC")
	public Page<InvoiceCustomerEntity> getAllInvoiceCustomerByinvoiceeCode(Pageable page, @Param("code") String invoiceCode );
	
	/** invoices pour un customer ayant un code **/
	@Query("SELECT invoice FROM InvoiceCustomer invoice WHERE isDeleted = false AND code = :code AND commandCustomer.customer.id = :customer ORDER BY code DESC")
	public Page<InvoiceCustomerEntity> getAllInvoiceCustomerByinvoiceeCodeAndCustomer(Pageable page, @Param("code") String invoiceCode, @Param("customer") String customer);
	
	/** invoices sur une periode pour customer**/
	@Query("SELECT invoice FROM InvoiceCustomer invoice WHERE isDeleted = false AND commandCustomer.customer.id = :customer AND createdAt BETWEEN :date1 AND :date2 ORDER BY code DESC")
	public Page<InvoiceCustomerEntity> getAllInvoiceCustomerByCreatedDateCustomer(Pageable page, @Param("customer") String customerID , @Param("date1") Date date1, @Param("date2") Date date2 );
	
	@Query("SELECT invoice FROM InvoiceCustomer invoice WHERE isDeleted = false AND commandCustomer.customer.id = :customer AND createdAt BETWEEN :date1 AND :date2 ORDER BY code DESC")
	public List<InvoiceCustomerEntity> getListInvoiceCustomerByCreatedDateCustomer(@Param("customer") String customerID , @Param("date1") Date date1, @Param("date2") Date date2 );
	
	/** get one command Customer **/    
	@Query("SELECT invoice FROM InvoiceCustomer invoice WHERE id = :id")
	public InvoiceCustomerEntity getInvoiceCustomerById(@Param("id") String id);
	
	
	
}
