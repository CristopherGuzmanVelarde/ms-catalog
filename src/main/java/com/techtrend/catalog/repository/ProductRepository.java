package com.techtrend.catalog.repository;

import com.techtrend.catalog.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repositorio para la gestión de datos de productos
 * 
 * Define las operaciones de persistencia y consulta de productos.
 * Implementa el patrón Repository para desacoplar el acceso a datos.
 * 
 * @author TechTrend Development Team
 */
public interface ProductRepository {
    
    /**
     * Obtiene todos los productos almacenados
     * @return Flux de todos los productos
     */
    Flux<Product> findAll();
    
    /**
     * Busca un producto por su identificador único
     * @param id Identificador del producto
     * @return Mono con el producto encontrado o vacío
     */
    Mono<Product> findById(String id);
    
    /**
     * Guarda o actualiza un producto
     * @param product Producto a guardar
     * @return Mono con el producto guardado
     */
    Mono<Product> save(Product product);
    
    /**
     * Elimina un producto por su identificador
     * @param id Identificador del producto a eliminar
     * @return Mono con indicación de éxito
     */
    Mono<Boolean> deleteById(String id);
    
    /**
     * Verifica si existe un producto con el ID especificado
     * @param id Identificador del producto
     * @return Mono con true si existe, false en caso contrario
     */
    Mono<Boolean> existsById(String id);
    
    /**
     * Busca productos que tengan stock mayor al especificado
     * @param minStock Stock mínimo
     * @return Flux de productos con stock suficiente
     */
    Flux<Product> findByQuantityGreaterThan(Integer minStock);
    
    /**
     * Busca productos por nombre (parcial, case insensitive)
     * @param name Nombre o parte del nombre del producto
     * @return Flux de productos que coinciden
     */
    Flux<Product> findByNameContainingIgnoreCase(String name);
}