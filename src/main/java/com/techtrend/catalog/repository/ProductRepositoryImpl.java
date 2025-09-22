package com.techtrend.catalog.repository;

import com.techtrend.catalog.model.Product;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación del repositorio de productos usando datos mock en memoria
 * 
 * Simula un repositorio de datos para desarrollo y pruebas.
 * En producción, se reemplazaría por implementaciones con BD real.
 * 
 * @author TechTrend Development Team
 */
@Repository
public class ProductRepositoryImpl implements ProductRepository {
    
    private final Map<String, Product> products;
    
    public ProductRepositoryImpl() {
        this.products = new HashMap<>();
        initializeMockData();
    }
    
    private void initializeMockData() {
        // 🖥️ Computadoras
        products.put("1", new Product("1", "Laptop Ryzen 7", new BigDecimal("9999.99"), 50));
        products.put("2", new Product("2", "Desktop Intel i7", new BigDecimal("7999.99"), 25));
        products.put("3", new Product("3", "MacBook Pro M2", new BigDecimal("15999.99"), 15));
        
        // 🎮 Gaming
        products.put("4", new Product("4", "PlayStation 5", new BigDecimal("4999.99"), 30));
        products.put("5", new Product("5", "Xbox Series X", new BigDecimal("4799.99"), 20));
        products.put("6", new Product("6", "Nintendo Switch", new BigDecimal("2999.99"), 40));
        
        // 📱 Móviles
        products.put("7", new Product("7", "iPhone 15 Pro", new BigDecimal("12999.99"), 35));
        products.put("8", new Product("8", "Samsung Galaxy S24", new BigDecimal("8999.99"), 45));
        products.put("9", new Product("9", "Google Pixel 8", new BigDecimal("6999.99"), 25));
        
        // ⌚ Wearables
        products.put("10", new Product("10", "Apple Watch Series 9", new BigDecimal("3999.99"), 60));
        products.put("11", new Product("11", "Samsung Galaxy Watch", new BigDecimal("2499.99"), 40));
        
        // 🎧 Audio
        products.put("12", new Product("12", "AirPods Pro", new BigDecimal("2299.99"), 80));
        products.put("13", new Product("13", "Sony WH-1000XM5", new BigDecimal("1999.99"), 55));
        
        // 🖱️ Periféricos
        products.put("14", new Product("14", "Mouse Gaming", new BigDecimal("299.99"), 100));
        products.put("15", new Product("15", "Teclado Mecánico", new BigDecimal("599.99"), 0)); // Sin stock
        products.put("16", new Product("16", "Monitor 4K", new BigDecimal("3999.99"), 0)); // Sin stock
    }
    
    @Override
    public Flux<Product> findAll() {
        return Flux.fromIterable(products.values());
    }
    
    @Override
    public Mono<Product> findById(String id) {
        return Mono.justOrEmpty(products.get(id));
    }
    
    @Override
    public Mono<Product> save(Product product) {
        if (product.getId() == null) {
            // Generar ID automático para nuevos productos
            String newId = String.valueOf(products.size() + 1);
            product.setId(newId);
        }
        products.put(product.getId(), product);
        return Mono.just(product);
    }
    
    @Override
    public Mono<Boolean> deleteById(String id) {
        boolean existed = products.containsKey(id);
        products.remove(id);
        return Mono.just(existed);
    }
    
    @Override
    public Mono<Boolean> existsById(String id) {
        return Mono.just(products.containsKey(id));
    }
    
    @Override
    public Flux<Product> findByQuantityGreaterThan(Integer minStock) {
        return Flux.fromIterable(products.values())
                .filter(product -> product.getQuantity() > minStock);
    }
    
    @Override
    public Flux<Product> findByNameContainingIgnoreCase(String name) {
        return Flux.fromIterable(products.values())
                .filter(product -> product.getName().toLowerCase()
                        .contains(name.toLowerCase()));
    }
}