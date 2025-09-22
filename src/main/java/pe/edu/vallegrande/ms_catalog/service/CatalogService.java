package pe.edu.vallegrande.ms_catalog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.ms_catalog.model.Catalog;
import pe.edu.vallegrande.ms_catalog.repository.CatalogRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CatalogService {
    
    private final CatalogRepository catalogRepository;
    
    public Flux<Catalog> getAllCatalogs() {
        return catalogRepository.findAll();
    }
    
    public Mono<Catalog> getCatalogById(String id) {
        return catalogRepository.findById(id);
    }
    
    public Mono<Catalog> createCatalog(Catalog catalog) {
        catalog.setActive(true);
        return catalogRepository.save(catalog);
    }
    
    public Mono<Catalog> updateCatalog(String id, Catalog catalog) {
        return catalogRepository.findById(id)
                .flatMap(existingCatalog -> {
                    existingCatalog.setName(catalog.getName());
                    existingCatalog.setPrice(catalog.getPrice());
                    existingCatalog.setQuantity(catalog.getQuantity());
                    return catalogRepository.save(existingCatalog);
                });
    }
    
    public Mono<Void> deleteCatalog(String id) {
        return catalogRepository.deleteById(id);
    }
    
    // Método principal del documento: Aplicar descuento dinámico
    public Mono<Double> applyDynamicDiscount(String catalogId, Double percentage, Integer stockThreshold) {
        return catalogRepository.findById(catalogId)
                .filter(catalog -> catalog.getQuantity() > stockThreshold)
                .map(catalog -> {
                    double originalPrice = catalog.getPrice();
                    double discountAmount = originalPrice * (percentage / 100);
                    double discountedPrice = originalPrice - discountAmount;
                    
                    // Actualizar precio con descuento
                    catalog.setPrice(discountedPrice);
                    catalogRepository.save(catalog).subscribe();
                    
                    return discountedPrice;
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Catálogo no encontrado o stock insuficiente")));
    }
    
    public Flux<Catalog> getCatalogsWithStock(Integer threshold) {
        return catalogRepository.findByQuantityGreaterThan(threshold);
    }
}