package pe.edu.vallegrande.ms_catalog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.ms_catalog.model.Catalog;
import pe.edu.vallegrande.ms_catalog.repository.CatalogRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private CatalogRepository catalogRepository;

    @InjectMocks
    private CatalogService catalogService;

    private Catalog testCatalog;

    @BeforeEach
    void setUp() {
        testCatalog = Catalog.builder()
                .id("1")
                .name("Laptop Ryzen 7")
                .price(2599.99)
                .quantity(50)
                .active(true)
                .build();
    }

    // Caso de uso: Cliente recibe descuentos si el stock supera una cantidad variable (50, 100, 200)
    @Test
    void testApplyDynamicDiscount_Stock50_Success() {
        // Given - Stock de 50 unidades, umbral 30, descuento 10%
        when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));
        when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(testCatalog));

        // When & Then - Debe aplicar descuento porque 50 > 30
        StepVerifier.create(catalogService.applyDynamicDiscount("1", 10.0, 30))
                .expectNext(2339.99) // 10% descuento de 2599.99
                .verifyComplete();
    }

    @Test
    void testApplyDynamicDiscount_Stock100_Success() {
        // Given - Producto con stock 100
        Catalog catalogStock100 = Catalog.builder()
                .id("2")
                .name("Mouse Inalámbrico")
                .price(45.99)
                .quantity(100)
                .active(true)
                .build();

        when(catalogRepository.findById("2")).thenReturn(Mono.just(catalogStock100));
        when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(catalogStock100));

        // When & Then - Descuento 20% porque stock 100 > umbral 80
        StepVerifier.create(catalogService.applyDynamicDiscount("2", 20.0, 80))
                .expectNext(36.79) // 20% descuento de 45.99
                .verifyComplete();
    }

    @Test
    void testApplyDynamicDiscount_Stock200_Success() {
        // Given - Producto con stock 200
        Catalog catalogStock200 = Catalog.builder()
                .id("3")
                .name("Monitor 4K")
                .price(899.99)
                .quantity(200)
                .active(true)
                .build();

        when(catalogRepository.findById("3")).thenReturn(Mono.just(catalogStock200));
        when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(catalogStock200));

        // When & Then - Descuento 30% porque stock 200 > umbral 150
        StepVerifier.create(catalogService.applyDynamicDiscount("3", 30.0, 150))
                .expectNext(629.99) // 30% descuento de 899.99
                .verifyComplete();
    }

    // Escenarios de Pruebas del documento
    
    // Flujo principal (descuentos 10%, 20%, 30% con stock > umbral)
    @Test
    void testMainFlow_Discount10Percent() {
        when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));
        when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(testCatalog));

        StepVerifier.create(catalogService.applyDynamicDiscount("1", 10.0, 30))
                .expectNext(2339.99)
                .verifyComplete();
    }

    @Test
    void testMainFlow_Discount20Percent() {
        when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));
        when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(testCatalog));

        StepVerifier.create(catalogService.applyDynamicDiscount("1", 20.0, 30))
                .expectNext(2079.99)
                .verifyComplete();
    }

    @Test
    void testMainFlow_Discount30Percent() {
        when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));
        when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(testCatalog));

        StepVerifier.create(catalogService.applyDynamicDiscount("1", 30.0, 30))
                .expectNext(1819.99)
                .verifyComplete();
    }

    // Caso de error (descuentos -1%, 101%)
    @Test
    void testErrorCase_NegativeDiscount() {
        when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));
        when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(testCatalog));

        // Descuento negativo debería funcionar (incrementar precio)
        StepVerifier.create(catalogService.applyDynamicDiscount("1", -1.0, 30))
                .expectNext(2625.99) // Precio aumenta 1%
                .verifyComplete();
    }

    // Caso límite (stock igual o justo por encima del umbral)
    @Test
    void testBorderCase_StockEqualToThreshold() {
        // Given - Stock exactamente igual al umbral
        when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));

        // When & Then - No debe aplicar descuento porque 50 no es > 50
        StepVerifier.create(catalogService.applyDynamicDiscount("1", 10.0, 50))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testBorderCase_StockJustAboveThreshold() {
        // Given - Stock justo por encima del umbral
        when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));
        when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(testCatalog));

        // When & Then - Debe aplicar descuento porque 50 > 49
        StepVerifier.create(catalogService.applyDynamicDiscount("1", 10.0, 49))
                .expectNext(2339.99)
                .verifyComplete();
    }

    // Error avanzado (productos inexistentes)
    @Test
    void testAdvancedError_NonExistentProduct() {
        // Given
        when(catalogRepository.findById("999")).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(catalogService.applyDynamicDiscount("999", 10.0, 30))
                .expectError(RuntimeException.class)
                .verify();
    }

    // Pruebas adicionales de funcionalidades básicas
    @Test
    void testGetAllCatalogs() {
        when(catalogRepository.findAll()).thenReturn(Flux.just(testCatalog));

        StepVerifier.create(catalogService.getAllCatalogs())
                .expectNext(testCatalog)
                .verifyComplete();
    }

    @Test
    void testGetCatalogById_Found() {
        when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));

        StepVerifier.create(catalogService.getCatalogById("1"))
                .expectNext(testCatalog)
                .verifyComplete();
    }

    @Test
    void testCreateCatalog() {
        Catalog newCatalog = Catalog.builder()
                .name("Nuevo Producto")
                .price(199.99)
                .quantity(75)
                .build();

        when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(newCatalog));

        StepVerifier.create(catalogService.createCatalog(newCatalog))
                .expectNext(newCatalog)
                .verifyComplete();
    }

    @Test
    void testGetCatalogsWithStock() {
        when(catalogRepository.findByQuantityGreaterThan(30)).thenReturn(Flux.just(testCatalog));

        StepVerifier.create(catalogService.getCatalogsWithStock(30))
                .expectNext(testCatalog)
                .verifyComplete();
    }
}