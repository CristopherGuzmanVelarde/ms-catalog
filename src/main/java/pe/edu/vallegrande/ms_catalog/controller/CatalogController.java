package pe.edu.vallegrande.ms_catalog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.ms_catalog.model.Catalog;
import pe.edu.vallegrande.ms_catalog.service.CatalogService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class CatalogController {
    
    private final CatalogService catalogService;
    
    @GetMapping
    public Flux<Catalog> getAllCatalogs() {
        return catalogService.getAllCatalogs();
    }
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Catalog>> getCatalogById(@PathVariable String id) {
        return catalogService.getCatalogById(id)
                .map(catalog -> ResponseEntity.ok(catalog))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Mono<ResponseEntity<Catalog>> createCatalog(@RequestBody Catalog catalog) {
        return catalogService.createCatalog(catalog)
                .map(createdCatalog -> ResponseEntity.status(HttpStatus.CREATED).body(createdCatalog));
    }
    
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Catalog>> updateCatalog(@PathVariable String id, @RequestBody Catalog catalog) {
        return catalogService.updateCatalog(id, catalog)
                .map(updatedCatalog -> ResponseEntity.ok(updatedCatalog))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCatalog(@PathVariable String id) {
        return catalogService.deleteCatalog(id)
                .map(v -> ResponseEntity.noContent().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    // Endpoint principal del documento: Descuento dinámico
    @GetMapping("/dynamic-discount")
    public Mono<ResponseEntity<Double>> applyDynamicDiscount(
            @RequestParam String catalogId,
            @RequestParam Double percentage,
            @RequestParam Integer stockThreshold) {
        return catalogService.applyDynamicDiscount(catalogId, percentage, stockThreshold)
                .map(discountedPrice -> ResponseEntity.ok(discountedPrice))
                .onErrorReturn(ResponseEntity.badRequest().build());
    }
    
    @GetMapping("/with-stock")
    public Flux<Catalog> getCatalogsWithStock(@RequestParam(defaultValue = "0") Integer threshold) {
        return catalogService.getCatalogsWithStock(threshold);
    }
}