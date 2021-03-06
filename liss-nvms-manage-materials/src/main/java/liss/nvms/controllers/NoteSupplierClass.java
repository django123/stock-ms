package liss.nvms.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteSupplierClass {

    private Long boxLayer = 0L;
    private Long quantityLayer = 0L;
    private Long quantityBoxe = 0L;
    private Long bottleQuantity = 0L;
    private Double price = 0.0;
    private String designation;
    private Double total = 0.0;
}
