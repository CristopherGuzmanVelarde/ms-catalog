package com.techtrend.catalog.service;

import com.techtrend.catalog.model.Product;
import com.techtrend.catalog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementación del servicio de catálogo usando ProductRepository
 * 
 * Implementa la lógica de negocio del catálogo utilizando el patrón Repository
 * para el acceso a datos. Maneja validaciones y reglas de negocio.
 * 
 * @author TechTrend Development Team
 */
@Service
public class CatalogServiceImpl implements CatalogService {
    
    private final ProductRepository productRepository;
    
    @Autowired
    public CatalogServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public Flux<Product> getAllProducts() {
        return productRepository.findAll()
                .filter(Product::isAvailable);
    }
    
    @Override
    public Mono<Product> getProductById(String id) {
        return productRepository.findById(id);
    }
    
    @Override
    public Mono<Boolean> checkStock(String productId, Integer requestedQuantity) {
        if (requestedQuantity == null || requestedQuantity <= 0) {
            return Mono.error(new IllegalArgumentException("La cantidad debe ser mayor a 0"));
        }
        
        return productRepository.findById(productId)
                .map(product -> product.hasStock(requestedQuantity))
                .defaultIfEmpty(false);
    }
    
    @Override
    public Mono<Product> getProductDetails(String productId) {
        return productRepository.findById(productId);
    }
}