package liss.nvms.services;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import liss.nvms.model.FormatEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import liss.nvms.Clivraison.DeliveryCustomerEntity;
import liss.nvms.Flivraison.DeliverySupplierEntity;
import liss.nvms.additional.DeliverySupplierRepository;
import liss.nvms.httpException.HttpServiceExceptionHandle;
import liss.nvms.model.StockEntity;
import liss.nvms.repository.StockRepository;
import liss.nvms.utils.HttpErrorCodes;

@Service
@Transactional(rollbackFor = {HttpServiceExceptionHandle.class, SQLException.class }, noRollbackFor = EntityNotFoundException.class)
public class DeliverySupplierService {
	
	@Autowired DeliverySupplierRepository deliverySupplierRepository;
	@Autowired StockRepository stockrep;
	
   /** ajouter une approvisionnement fournisseur **/
	public DeliverySupplierEntity AddDeliverySupplier(DeliverySupplierEntity deliverySupp){
		try {
			
			String code = "AP"+System.currentTimeMillis();
			deliverySupp.setCode(code);
			deliverySupp.getDeliveryLineSuppliers().stream().map(line ->{
			 	StockEntity stock = stockrep.getStockById(line.getStock().getId());
			 	if(stock == null) throw new HttpServiceExceptionHandle("Un produit est introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			 	 Long QteStock = line.getBottleQuantity() + stock.getQuantity();
			     stock.setQuantity(QteStock);
			 	 return stockrep.save(stock);
		 }).collect(Collectors.toList());
		return deliverySupplierRepository.save(deliverySupp);
			
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
	}
	
	/** afficher les notes de approvisionnements **/
	public  Map<String, Object> getAllDeliveryNote(String code, String supplier, Date date1, Date date2, int page, int limit) {
		
		try {
			
			Map<String, Object> results = new HashMap<>();
			Pageable paging = PageRequest.of( page, limit);
	
			 Page<DeliverySupplierEntity> stockPage = null;
			 if(code != null) stockPage = deliverySupplierRepository.getAllDeliverySupplierByCmdeCode(paging, code);
			 else {
				 if(supplier == null && (date1 != null && date2 != null)) stockPage = deliverySupplierRepository.getAllDeliverySupplierByCreatedDate(paging, date1, date2);
				 else if(supplier != null && (date1 == null && date2 == null)) stockPage = deliverySupplierRepository.getAllNoteDeliverySupplier(paging, supplier);
				 else if(supplier != null && (date1 != null && date2 != null)) stockPage = deliverySupplierRepository.getAllDeliverySupplierByCreatedDateSupplier(paging, supplier,date1, date2);
				 else stockPage = deliverySupplierRepository.getAllDeliverySupplier(paging);
			 }
			 
			 results.put("data", stockPage.getContent().stream().map(item -> {
				 Map<String, Object> param = new HashMap<>();
				 param.put("reference", item.getId());
    			 param.put("supplier", (item.getCommand() != null ? item.getCommand().getSupplier().getName() : ""));
				 param.put("code", item.getCode());
				 param.put("command", item.getCommand().getCode());
				 return param;
			   }).collect(Collectors.toList())
			 );
			 results.put("currentPage", stockPage.getNumber());
			 results.put("totalItems", stockPage.getTotalElements());
			 results.put("totalPages", stockPage.getTotalPages());
			 
			 
			return results;
	
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes scode = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),scode);
		}
	
	}
	
	
	
	/** afficher les notes de approvisionnements **/
	public  Map<String, Object> getDeliveryNoteDetail(String ref) {
		try {
			Map<String, Object> results = new HashMap<>();
			DeliverySupplierEntity delivery = deliverySupplierRepository.getAllDeliverySupplierByRef(ref);
			if(delivery == null) throw new HttpServiceExceptionHandle("Imposssible de retrouver cette approvisionnement",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			results.put("data", delivery.getDeliveryLineSuppliers());
			return results;
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes scode = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),scode);
		}
	}

	/**  suppression d'une note**/

	public boolean deleteSupplierDelivery(String reference){
		try {
			DeliverySupplierEntity delivSup = deliverySupplierRepository.getDeliverySupplierById(reference);
			if(delivSup == null) throw new HttpServiceExceptionHandle("Note introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			if(delivSup.getIsDeleted() == true) delivSup.setIsDeleted(false);
			else delivSup.setIsDeleted(true);
			deliverySupplierRepository.save(delivSup);
			return true;
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
	}
	
	
}
