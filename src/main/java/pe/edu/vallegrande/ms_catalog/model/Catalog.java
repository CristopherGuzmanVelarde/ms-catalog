package pe.edu.vallegrande.ms_catalog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Catalog {
    private String id;
    private String name;
    private Double price;
    private Integer quantity;
    private Boolean active;
}