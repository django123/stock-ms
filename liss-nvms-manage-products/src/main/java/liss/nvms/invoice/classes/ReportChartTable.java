package liss.nvms.invoice.classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class ReportChartTable {
	private String customer;
	private String impaye;
    private String paye;
    private String total;
}
