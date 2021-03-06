package liss.nvms.date;

import java.util.Calendar;
import java.util.Date;

public class GenerateReference {
	
	final static int INIT_YEAR_APP = 2021;
	final static String INIT_INVOICE = "F";
	final static String INIT_LIVRAISON = "L";
	
	public static String _fGenerateRef(int nbreFacture, String initial ) {
		
		String compl="";
		int countCurrentYear =  Calendar.getInstance().get(Calendar.YEAR) - INIT_YEAR_APP + 1 ;
		
		if(countCurrentYear < 10) compl += "-0"+countCurrentYear;
		else  compl += "-"+countCurrentYear;
		
		if(nbreFacture < 10) compl += "-00000"+nbreFacture;
		else if(nbreFacture >= 10 || nbreFacture < 100) compl += "-0000"+nbreFacture;
		else if(nbreFacture >= 100 || nbreFacture < 1000)compl += "-000"+nbreFacture;
		else if(nbreFacture >= 1000 || nbreFacture < 10000)compl += "-00"+nbreFacture;
		else if(nbreFacture >= 10000 || nbreFacture < 100000) compl += "-0"+nbreFacture;
		else compl += "-"+nbreFacture;
		
		return (initial.equals(INIT_INVOICE) ? INIT_INVOICE + compl : INIT_LIVRAISON  + compl);
	}
	
	
	
public static String _fGenerateRefArticle(int nbreProduit) {
		
		String compl="";
		
		if(nbreProduit < 10) compl += "00000"+nbreProduit;
		else if(nbreProduit >= 10 || nbreProduit < 100) compl += "0000"+nbreProduit;
		else if(nbreProduit >= 100 || nbreProduit < 1000)compl += "000"+nbreProduit;
		else if(nbreProduit >= 1000 || nbreProduit < 10000)compl += "00"+nbreProduit;
		else if(nbreProduit >= 10000 || nbreProduit < 100000) compl += "0"+nbreProduit;
		else compl += ""+nbreProduit;
		
		return compl;
	}
	
	
}
