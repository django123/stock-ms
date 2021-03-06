package liss.nvms.date;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	
	public static String formatDateStrg(Date date) {
		if(date == null) return "00/00/0000";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/YYYY");
		return simpleDateFormat.format(date);
	}
	

}
