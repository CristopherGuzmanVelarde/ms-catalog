package pe.edu.vallegrande.ms_catalog.repository;

import pe.edu.vallegrande.ms_catalog.model.Catalog;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class CatalogRepository {
    
    private final Map<String, Catalog> catalogs = new HashMap<>();
    
    public CatalogRepository() {
        // Datos iniciales para testing
        Catalog catalog1 = Catalog.builder()
                .id("1")
                .name("Laptop Ryzen 7")
                .price(2599.99)
                .quantity(50)
                .active(true)
                .build();
        
        Catalog catalog2 = Catalog.builder()
                .id("2")
                .name("Mouse Inalámbrico")
                .price(45.99)
                .quantity(100)
                .active(true)
                .build();
        
        Catalog catalog3 = Catalog.builder()
                .id("3")
                .name("Teclado Mecánico")
                .price(129.99)
                .quantity(25)
                .active(true)
                .build();
        
        catalogs.put("1", catalog1);
        catalogs.put("2", catalog2);
        catalogs.put("3", catalog3);
    }
    
    public Flux<Catalog> findAll() {
        return Flux.fromIterable(catalogs.values())
                .filter(Catalog::getActive);
    }
    
    public Mono<Catalog> findById(String id) {
        Catalog catalog = catalogs.get(id);
        return catalog != null && catalog.getActive() 
                ? Mono.just(catalog) 
                : Mono.empty();
    }
    
    public Mono<Catalog> save(Catalog catalog) {
        if (catalog.getId() == null) {
            catalog.setId(UUID.randomUUID().toString());
        }
        catalogs.put(catalog.getId(), catalog);
        return Mono.just(catalog);
    }
    
    public Mono<Void> deleteById(String id) {
        Catalog catalog = catalogs.get(id);
        if (catalog != null) {
            catalog.setActive(false);
        }
        return Mono.empty();
    }
    
    public Flux<Catalog> findByQuantityGreaterThan(Integer threshold) {
        return Flux.fromIterable(catalogs.values())
                .filter(Catalog::getActive)
                .filter(catalog -> catalog.getQuantity() > threshold);
    }
}