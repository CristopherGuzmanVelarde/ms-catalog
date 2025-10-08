package com.techtrend.catalog.service;

import com.techtrend.catalog.model.Product;
import com.techtrend.catalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 🧪 Pruebas unitarias para CatalogService
 * 
 * Verifica la lógica de negocio del catálogo usando mocks del repository.
 * Cubre todos los escenarios críticos de negocio para TechTrend.
 * 
 * @author TechTrend Development Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("🛍️ Catalog Service - Lógica de Negocio")
class CatalogServiceTest {

    @Mock
    private ProductRepository productRepository;
    
    private CatalogServiceImpl catalogService;

    @BeforeEach
    @DisplayName("🔧 Configuración inicial del servicio")
    void setUp() {
        // Asegurar inicialización de mocks
        MockitoAnnotations.openMocks(this);
        
        // Crear instancia del servicio manualmente con el mock
        catalogService = new CatalogServiceImpl(productRepository);
        
        // Verificar que los mocks estén correctamente inicializados
        System.out.println("✅ CatalogService inicializado con mocks del repositorio");
        System.out.println("🔧 ProductRepository mock: " + (productRepository != null ? "OK" : "NULL"));
        System.out.println("🔧 CatalogService instance: " + (catalogService != null ? "OK" : "NULL"));
        
        // Verificar que la inyección funcionó
        assertNotNull(productRepository, "ProductRepository mock should not be null");
        assertNotNull(catalogService, "CatalogService instance should not be null");
    }

    @Test
    @DisplayName("📦 Debe retornar todos los productos disponibles usando repository")
    void shouldReturnAllAvailableProducts() {
        System.out.println("🔍 Probando listado de productos disponibles...");
        
        // Given
        Product product1 = new Product("1", "Laptop Ryzen 7", new BigDecimal("9999.99"), 50);
        Product product2 = new Product("2", "Mouse Gaming", new BigDecimal("299.99"), 100);
        Product productSinStock = new Product("3", "Producto Agotado", new BigDecimal("199.99"), 0);
        
        when(productRepository.findAll()).thenReturn(Flux.just(product1, product2, productSinStock));
        
        // When
        Flux<Product> products = catalogService.getAllProducts();

        // Then - Solo deben retornarse los productos disponibles (con stock > 0)
        StepVerifier.create(products)
                .expectNext(product1)
                .expectNext(product2)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
                
        System.out.println("✅ Test exitoso: Se retornaron solo productos disponibles");
        
        // Verificar que el mock fue llamado
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("🔍 Debe retornar producto existente por ID usando repository")
    void shouldReturnExistingProductById() {
        System.out.println("🔍 Probando búsqueda de producto por ID: 1");
        
        // Given
        Product expectedProduct = new Product("1", "Laptop Ryzen 7", new BigDecimal("9999.99"), 50);
        when(productRepository.findById("1")).thenReturn(Mono.just(expectedProduct));
        
        // When
        Mono<Product> product = catalogService.getProductById("1");

        // Then
        StepVerifier.create(product)
                .expectNext(expectedProduct)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
                
        System.out.println("✅ Producto encontrado: " + expectedProduct.getName() + " - $" + expectedProduct.getPrice());
        
        // Verificar que el mock fue llamado
        verify(productRepository, times(1)).findById("1");
    }

    @Test
    @DisplayName("❌ Debe retornar vacío para producto no existente usando repository")
    void shouldReturnEmptyForNonExistentProduct() {
        System.out.println("🔍 Probando búsqueda de producto inexistente ID: 999");
        
        // Given
        when(productRepository.findById("999")).thenReturn(Mono.empty());
        
        // When
        Mono<Product> product = catalogService.getProductById("999");

        // Then
        StepVerifier.create(product)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
                
        System.out.println("✅ Test exitoso: Producto inexistente retorna vacío");
        
        // Verificar que el mock fue llamado
        verify(productRepository, times(1)).findById("999");
    }

    @Test
    @DisplayName("✅ Stock suficiente debe retornar true usando repository")
    void shouldReturnTrueWhenStockIsSufficient() {
        System.out.println("📊 Probando verificación de stock suficiente: 10 unidades de 50 disponibles");
        
        // Given
        Product productWithStock = new Product("1", "Laptop Ryzen 7", new BigDecimal("9999.99"), 50);
        when(productRepository.findById("1")).thenReturn(Mono.just(productWithStock));
        
        // When
        Mono<Boolean> result = catalogService.checkStock("1", 10);

        // Then
        StepVerifier.create(result)
                .expectNext(true)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
                
        System.out.println("✅ Test exitoso: Stock suficiente confirmado");
        
        // Verificar que el mock fue llamado
        verify(productRepository, times(1)).findById("1");
    }

    @Test
    @DisplayName("❌ Stock insuficiente debe retornar false usando repository")
    void shouldReturnFalseWhenStockIsInsufficient() {
        System.out.println("📊 Probando verificación de stock insuficiente: 60 unidades de 50 disponibles");
        
        // Given
        Product productWithLimitedStock = new Product("1", "Laptop Ryzen 7", new BigDecimal("9999.99"), 50);
        when(productRepository.findById("1")).thenReturn(Mono.just(productWithLimitedStock));
        
        // When
        Mono<Boolean> result = catalogService.checkStock("1", 60);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
                
        System.out.println("✅ Test exitoso: Stock insuficiente detectado correctamente");
        
        // Verificar que el mock fue llamado
        verify(productRepository, times(1)).findById("1");
    }

    @Test
    @DisplayName("🚫 Cantidad negativa debe lanzar excepción IllegalArgumentException")
    void shouldThrowExceptionForNegativeQuantity() {
        System.out.println("⚠️ Probando validación de cantidad negativa: -1");
        
        // When (no necesita mock porque la validación ocurre antes de llamar al repository)
        Mono<Boolean> result = catalogService.checkStock("1", -1);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("La cantidad debe ser mayor a 0"))
                .verify(Duration.ofSeconds(5));
                
        System.out.println("✅ Test exitoso: Excepción lanzada para cantidad negativa");
        
        // Verificar que el repository NO fue llamado debido a la validación temprana
        verifyNoInteractions(productRepository);
    }

    @Test
    @DisplayName("🔍 Producto no existente debe retornar false usando repository")
    void shouldReturnFalseForNonExistentProductStock() {
        System.out.println("📊 Probando verificación de stock para producto inexistente ID: 999");
        
        // Given
        when(productRepository.findById("999")).thenReturn(Mono.empty());
        
        // When
        Mono<Boolean> result = catalogService.checkStock("999", 1);

        // Then
        StepVerifier.create(result)
                .expectNext(false)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
                
        System.out.println("✅ Test exitoso: Stock de producto inexistente retorna false");
        
        // Verificar que el mock fue llamado
        verify(productRepository, times(1)).findById("999");
    }

    @Test
    @DisplayName("📋 Debe retornar detalles correctos del producto usando repository")
    void shouldReturnCorrectProductDetails() {
        System.out.println("📋 Probando obtención de detalles del producto ID: 1");
        
        // Given
        Product expectedProduct = new Product("1", "Laptop Ryzen 7", new BigDecimal("9999.99"), 50);
        when(productRepository.findById("1")).thenReturn(Mono.just(expectedProduct));
        
        // When
        Mono<Product> product = catalogService.getProductDetails("1");

        // Then
        StepVerifier.create(product)
                .expectNext(expectedProduct)
                .expectComplete()
                .verify(Duration.ofSeconds(5));
                
        System.out.println("✅ Detalles correctos: " + expectedProduct.getName() + " - $" + expectedProduct.getPrice() + " (" + expectedProduct.getQuantity() + " unidades)");
        
        // Verificar que el mock fue llamado
        verify(productRepository, times(1)).findById("1");
    }
}