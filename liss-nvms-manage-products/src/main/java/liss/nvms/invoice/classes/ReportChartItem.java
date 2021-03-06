package liss.nvms.invoice.classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class ReportChartItem {
	private String customer;
	private String category;
    private Double value = 0.0;
}
