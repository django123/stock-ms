package liss.nvms.invoice.classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class ReportChartDog {
	private String customer;
    private Double value = 0.0;
}
