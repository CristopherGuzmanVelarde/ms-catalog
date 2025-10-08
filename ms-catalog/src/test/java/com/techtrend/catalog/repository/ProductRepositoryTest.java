package com.techtrend.catalog.repository;

import com.techtrend.catalog.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;



/**
 * 🗄️ Pruebas unitarias para ProductRepository
 * 
 * Verifica las operaciones CRUD y consultas específicas del repositorio.
 * Prueba la persistencia y recuperación de datos de productos.
 * 
 * @author TechTrend Development Team
 */
@DisplayName("🗄️ Product Repository - Gestión de Datos")
class ProductRepositoryTest {

    private ProductRepository productRepository;

    @BeforeEach
    @DisplayName("🔧 Configuración inicial del repositorio")
    void setUp() {
        productRepository = new ProductRepositoryImpl();
        System.out.println("✅ ProductRepository inicializado con 16 productos mock");
    }

    @Test
    @DisplayName("📦 Debe retornar todos los productos almacenados (16 productos)")
    void shouldReturnAllStoredProducts() {
        System.out.println("🔍 Probando obtención de todos los productos...");
        
        // When
        Flux<Product> products = productRepository.findAll();

        // Then
        StepVerifier.create(products)
                .expectNextCount(16) // 16 productos totales en el repositorio
                .verifyComplete();
                
        System.out.println("✅ Test exitoso: Se retornaron 16 productos del repositorio");
    }

    @Test
    @DisplayName("🔍 Debe encontrar producto existente por ID (Laptop Ryzen 7)")
    void shouldFindExistingProductById() {
        System.out.println("🔍 Probando búsqueda de producto por ID: 1");
        
        // When
        Mono<Product> product = productRepository.findById("1");

        // Then
        StepVerifier.create(product)
                .expectNextMatches(p -> {
                    boolean isValid = "1".equals(p.getId()) && "Laptop Ryzen 7".equals(p.getName());
                    if (isValid) {
                        System.out.println("✅ Producto encontrado: " + p.getName() + " - $" + p.getPrice());
                    }
                    return isValid;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("❌ Debe retornar vacío para producto no existente (ID: 999)")
    void shouldReturnEmptyForNonExistentProduct() {
        System.out.println("🔍 Probando búsqueda de producto inexistente ID: 999");
        
        // When
        Mono<Product> product = productRepository.findById("999");

        // Then
        StepVerifier.create(product)
                .verifyComplete();
                
        System.out.println("✅ Test exitoso: Producto inexistente retorna vacío");
    }

    @Test
    @DisplayName("💾 Debe guardar nuevo producto correctamente")
    void shouldSaveNewProductCorrectly() {
        System.out.println("💾 Probando guardado de nuevo producto...");
        
        // Given
        Product newProduct = new Product(null, "Nuevo Producto Test", new BigDecimal("999.99"), 10);

        // When
        Mono<Product> savedProduct = productRepository.save(newProduct);

        // Then
        StepVerifier.create(savedProduct)
                .expectNextMatches(p -> {
                    boolean isValid = p.getId() != null && 
                                    "Nuevo Producto Test".equals(p.getName()) &&
                                    new BigDecimal("999.99").equals(p.getPrice()) &&
                                    p.getQuantity() == 10;
                    if (isValid) {
                        System.out.println("✅ Producto guardado con ID: " + p.getId());
                    }
                    return isValid;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("🔄 Debe actualizar producto existente correctamente")
    void shouldUpdateExistingProductCorrectly() {
        System.out.println("🔄 Probando actualización de producto existente ID: 1");
        
        // Given - Actualizar el producto existente ID "1"
        Product updatedProduct = new Product("1", "Laptop Ryzen 7 ACTUALIZADA", new BigDecimal("10999.99"), 45);

        // When
        Mono<Product> savedProduct = productRepository.save(updatedProduct);

        // Then
        StepVerifier.create(savedProduct)
                .expectNextMatches(p -> {
                    boolean isValid = "1".equals(p.getId()) && 
                                    "Laptop Ryzen 7 ACTUALIZADA".equals(p.getName()) &&
                                    new BigDecimal("10999.99").equals(p.getPrice()) &&
                                    p.getQuantity() == 45;
                    if (isValid) {
                        System.out.println("✅ Producto actualizado: " + p.getName() + " - $" + p.getPrice());
                    }
                    return isValid;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("🗑️ Debe eliminar producto existente correctamente")
    void shouldDeleteExistingProductCorrectly() {
        System.out.println("🗑️ Probando eliminación de producto existente ID: 2");
        
        // When
        Mono<Boolean> deleteResult = productRepository.deleteById("2");

        // Then
        StepVerifier.create(deleteResult)
                .expectNext(true)
                .verifyComplete();
                
        // Verificar que ya no existe
        StepVerifier.create(productRepository.findById("2"))
                .verifyComplete();
                
        System.out.println("✅ Test exitoso: Producto eliminado correctamente");
    }

    @Test
    @DisplayName("❌ Debe retornar false al eliminar producto no existente")
    void shouldReturnFalseWhenDeletingNonExistentProduct() {
        System.out.println("🗑️ Probando eliminación de producto inexistente ID: 999");
        
        // When
        Mono<Boolean> deleteResult = productRepository.deleteById("999");

        // Then
        StepVerifier.create(deleteResult)
                .expectNext(false)
                .verifyComplete();
                
        System.out.println("✅ Test exitoso: Eliminación de producto inexistente retorna false");
    }

    @Test
    @DisplayName("✅ Debe confirmar existencia de producto por ID")
    void shouldConfirmProductExistence() {
        System.out.println("🔍 Probando verificación de existencia del producto ID: 1");
        
        // When
        Mono<Boolean> exists = productRepository.existsById("1");

        // Then
        StepVerifier.create(exists)
                .expectNext(true)
                .verifyComplete();
                
        System.out.println("✅ Test exitoso: Producto existe confirmado");
    }

    @Test
    @DisplayName("❌ Debe confirmar no existencia de producto inexistente")
    void shouldConfirmProductNonExistence() {
        System.out.println("🔍 Probando verificación de no existencia del producto ID: 999");
        
        // When
        Mono<Boolean> exists = productRepository.existsById("999");

        // Then
        StepVerifier.create(exists)
                .expectNext(false)
                .verifyComplete();
                
        System.out.println("✅ Test exitoso: Producto no existe confirmado");
    }

    @Test
    @DisplayName("📊 Debe encontrar productos con stock mayor al especificado")
    void shouldFindProductsWithSufficientStock() {
        System.out.println("📊 Probando búsqueda de productos con stock > 40 unidades");
        
        // When - Buscar productos con más de 40 unidades
        Flux<Product> products = productRepository.findByQuantityGreaterThan(40);

        // Then - Deberían ser varios productos que tienen más de 40 unidades
        StepVerifier.create(products)
                .expectNextMatches(p -> p.getQuantity() > 40)
                .expectNextMatches(p -> p.getQuantity() > 40)
                .expectNextMatches(p -> p.getQuantity() > 40)
                .thenCancel()
                .verify();
                
        System.out.println("✅ Test exitoso: Se encontraron productos con stock > 40");
    }

    @Test
    @DisplayName("💾 Debe guardar producto con ID existente y sin ID (ambas ramas)")
    void shouldSaveProductWithAndWithoutId() {
        // Guardar producto con ID existente (actualiza)
        Product update = new Product("1", "Update", new BigDecimal("1.0"), 1);
        StepVerifier.create(productRepository.save(update))
            .expectNext(update)
            .verifyComplete();
        // Guardar producto sin ID (crea nuevo)
        Product nuevo = new Product(null, "Nuevo", new BigDecimal("2.0"), 2);
        StepVerifier.create(productRepository.save(nuevo))
            .expectNextMatches(p -> p.getId() != null && p.getName().equals("Nuevo"))
            .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 10, 50, 100})
    @DisplayName("📊 [PARAMETRIZADA] Buscar productos con stock mayor a diferentes valores")
    void shouldFindByQuantityGreaterThanVarious(int minStock) {
        StepVerifier.create(productRepository.findByQuantityGreaterThan(minStock))
            .expectNextCount(
                productRepository.findAll().filter(p -> p.getQuantity() > minStock).count().block()
            )
            .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(strings = {"LAPTOP", "laptop", "Lap", "Pro", "Gaming", "NoExiste"})
    @DisplayName("🔤 [PARAMETRIZADA] Buscar productos por nombre parcial (case insensitive)")
    void shouldFindByNameContainingIgnoreCaseVarious(String name) {
        StepVerifier.create(productRepository.findByNameContainingIgnoreCase(name))
            .expectNextCount(
                productRepository.findAll().filter(p -> p.getName().toLowerCase().contains(name.toLowerCase())).count().block()
            )
            .verifyComplete();
    }

    @Test
    @DisplayName("🗑️ Eliminar todos los productos y verificar existencia")
    void shouldDeleteAllAndCheckExistence() {
        // Eliminar todos
        productRepository.findAll().toIterable().forEach(p -> productRepository.deleteById(p.getId()).block());
        // Verificar que no existe ninguno
        StepVerifier.create(productRepository.findAll())
            .verifyComplete();
        StepVerifier.create(productRepository.existsById("1"))
            .expectNext(false)
            .verifyComplete();
    }
}