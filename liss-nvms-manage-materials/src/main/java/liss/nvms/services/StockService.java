package liss.nvms.services;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import liss.nvms.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import liss.nvms.composite.StockPK;
import liss.nvms.date.GenerateReference;
import liss.nvms.httpException.HttpServiceExceptionHandle;
import liss.nvms.repository.FormatRepository;
import liss.nvms.repository.ProductRepository;
import liss.nvms.repository.PurchasePriceRepository;
import liss.nvms.repository.StockRepository;
import liss.nvms.utils.HttpErrorCodes;

@Service
@Transactional(rollbackFor = {HttpServiceExceptionHandle.class, SQLException.class }, noRollbackFor = EntityNotFoundException.class)
public class StockService {
	
	@Autowired private ProductRepository prodRep;
	@Autowired private FormatRepository formatRep;
	@Autowired private StockRepository stockRep;
	@Autowired private PurchasePriceRepository purchasePriceRep;
	
	
	
	/** afficher les format de produit pour les MVTs E/S **/
	public  Map<String, Object> productForEvents(String name, int page, int limit) {
		
		try {
			Map<String, Object> results = new HashMap<>();
			Pageable paging = PageRequest.of( page, limit);
			if(name == null) name = "";
			Page<StockEntity> stockPage = stockRep.getProductForEvents(name,paging);
			
			 results.put("data", stockPage.getContent().stream().map(item ->{
				 Map<String, Object> param = new HashMap<>();
				 param.put("reference", item.getId());
				 param.put("name", item.getProduct().getName()+" - "+ item.getFormat().getName());
				 param.put("product", item.getProduct().getName());
				 param.put("format", item.getFormat().getName());
				 return param;
			 }).collect(Collectors.toList()));
			 results.put("currentPage", stockPage.getNumber());
			 results.put("totalItems", stockPage.getTotalElements());
			 results.put("totalPages", stockPage.getTotalPages());
			
			 return results;

		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}
	
	
	/** afiicher la liste des stocks **/
	public  Map<String, Object> getAllStock(String name, int page, int limit) {
		
		try {
			Map<String, Object> results = new HashMap<>();
			Pageable paging = PageRequest.of( page, limit);
			if(name == null) name  ="";
			Page<StockEntity> stockPage = stockRep.getStocks(paging, name);
			
			 results.put("data", stockPage.getContent());
			 results.put("currentPage", stockPage.getNumber());
			 results.put("totalItems", stockPage.getTotalElements());
			 results.put("totalPages", stockPage.getTotalPages());
			
			 return results;

		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}
	
	
	/** ajouter un stock **/
	public StockEntity addStock(StockEntity stockEntity) {
		try {
			
			
			if(stockEntity.getProduct() == null || stockEntity.getProduct().getId() == null ) throw new HttpServiceExceptionHandle("Produit introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			
			if(stockEntity.getFormat() == null || stockEntity.getFormat().getId() == null) throw new HttpServiceExceptionHandle("Format introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			
			StockEntity checkStockEntity = stockRep.getStockbyProductFormat(stockEntity.getProduct().getId(), stockEntity.getFormat().getId());
			
			if(checkStockEntity !=  null) throw new HttpServiceExceptionHandle("Format produit déja existant",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			
			int cnt = stockRep.countStockProduct();
			//String code = GenerateReference._fGenerateRefArticle(cnt+1);
			//stockEntity.setCode(code);
			return stockRep.save(stockEntity);

			
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}

		
	}
	
	
	/** modifier un stock **/
	public StockEntity updateStock(StockEntity stockEntity) {
		try {
			
			if(stockEntity.getId() == null) throw new HttpServiceExceptionHandle("Produiten stock introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			
			if(stockEntity.getProduct() == null || stockEntity.getProduct().getId() == null ) throw new HttpServiceExceptionHandle("Produit introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			if(stockEntity.getFormat() == null || stockEntity.getFormat().getId() == null) throw new HttpServiceExceptionHandle("Format introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			
			ProductEntity product = prodRep.getProductById(stockEntity.getProduct().getId());
			if(product == null) throw new HttpServiceExceptionHandle("Produit introuvable en BD",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			FormatEntity format = formatRep.getFormatById(stockEntity.getFormat().getId());
			if(format == null) throw new HttpServiceExceptionHandle("format introuvable en BD",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			
			StockEntity	stock = stockRep.getStockById(stockEntity.getId());
			if(stock == null) throw new HttpServiceExceptionHandle("Format produit introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			
			stock.setProduct(product); stock.setFormat(format);
			stock.setPrice(stockEntity.getPrice());
			stock.setLimitQuantity(stockEntity.getLimitQuantity());
			stock.setQuantity(stockEntity.getQuantity());
			stock.setCode(stockEntity.getCode());
			
			if(stockEntity.getPrice().compareTo(stock.getPrice()) != 0 ) purchasePriceRep.save( new PurchasePriceEntity(stock.getPrice(), new Date(), stock));
			return stockRep.save(stock);
			
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
		
	}

	/** supprimer stock **/
	public boolean deleteStock(String reference) {
		try {
			StockEntity stock = stockRep.getStockById(reference);
			if(stock == null) throw new HttpServiceExceptionHandle("stock introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			if(stock.getIsDeleted() == true) stock.setIsDeleted(false);
			else stock.setIsDeleted(true);
			stockRep.save(stock);
			return true;
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
	}
	
	
	/** update code of stock -- **/
	
	public Boolean updateStockCode(String codeArticle, String newCode) {
		
		try {
			    StockEntity stock  = stockRep.getStockById(codeArticle);
			    if(stock == null)throw new HttpServiceExceptionHandle("code does not exist !!!");
			    stock.setCode(newCode);
			    stockRep.save(stock);
				return true;
		} catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
	}
	
	
}
