package liss.nvms.services;


import liss.nvms.model.SupplierEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;

import liss.nvms.httpException.HttpServiceExceptionHandle;
import liss.nvms.model.FormatEntity;
import liss.nvms.repository.FormatRepository;
import liss.nvms.utils.HttpErrorCodes;




@Service
@Transactional(rollbackFor = {HttpServiceExceptionHandle.class, SQLException.class }, noRollbackFor = EntityNotFoundException.class)
public class FormatService {

	@Autowired FormatRepository formatRep;
	
	public List<FormatEntity> getAllFormats(){
		
		try {
			
			return formatRep.getFormats();

		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
	}
	
	public FormatEntity addFormat(FormatEntity format){
		
		try {
				if(format == null) throw new HttpServiceExceptionHandle("le format envoye est vide", HttpErrorCodes.INTERNAL_SERVER_ERROR);
				if(formatRep.findByName(format.getName()) != null) throw new HttpServiceExceptionHandle("Ce nom de format existe deja", HttpErrorCodes.CONFLITS);
			
			return formatRep.save(format);

		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
	}

	/**  suppression d'un format**/

	public boolean deleteFormat(String reference){
		try {
			FormatEntity format = formatRep.getFormatById(reference);
			if(format == null) throw new HttpServiceExceptionHandle("Format introuvable",HttpErrorCodes.INTERNAL_SERVER_ERROR);
			if(format.getIsDeleted() == true) format.setIsDeleted(false);
			else format.setIsDeleted(true);
			formatRep.save(format);
			return true;
		}catch (HttpServiceExceptionHandle e) {
			HttpErrorCodes code = (e.getErrorCode() != null ? HttpErrorCodes.fromId(e.getErrorCode()) : HttpErrorCodes.INTERNAL_SERVER_ERROR);
			throw new HttpServiceExceptionHandle(e.getMessage(),code);
		}
	}
	
	
}
