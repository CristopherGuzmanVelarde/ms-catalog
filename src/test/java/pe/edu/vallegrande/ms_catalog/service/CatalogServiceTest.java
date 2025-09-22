package pe.edu.vallegrande.ms_catalog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.ms_catalog.model.Catalog;
import pe.edu.vallegrande.ms_catalog.repository.CatalogRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CatalogService Tests")
class CatalogServiceTest {

    @Mock
    private CatalogRepository catalogRepository;

    @InjectMocks
    private CatalogService catalogService;

    private Catalog testCatalog;
    private Catalog catalogStock100;
    private Catalog catalogStock200;

    @BeforeEach
    void setUp() {
        testCatalog = Catalog.builder()
                .id("1")
                .name("Laptop Ryzen 7")
                .price(2599.99)
                .quantity(50)
                .active(true)
                .build();
                
        catalogStock100 = Catalog.builder()
                .id("2")
                .name("Mouse Inalámbrico")
                .price(45.99)
                .quantity(100)
                .active(true)
                .build();
                
        catalogStock200 = Catalog.builder()
                .id("3")
                .name("Monitor 4K")
                .price(899.99)
                .quantity(200)
                .active(true)
                .build();
    }

    @Nested
    @DisplayName("Dynamic Discount Tests - Main Use Case")
    class DynamicDiscountTests {

        @Test
        @DisplayName("Should apply 10% discount when stock is 50 and threshold is 30")
        void testApplyDynamicDiscount_Stock50_Success() {
            // Given
            when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));
            when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(testCatalog));

            // When & Then
            StepVerifier.create(catalogService.applyDynamicDiscount("1", 10.0, 30))
                    .expectNextMatches(price -> assertPriceEquals(price, 2339.99))
                    .verifyComplete();
                    
            verify(catalogRepository).findById("1");
            verify(catalogRepository).save(any(Catalog.class));
        }

        @Test
        @DisplayName("Should apply 20% discount when stock is 100 and threshold is 80")
        void testApplyDynamicDiscount_Stock100_Success() {
            // Given
            when(catalogRepository.findById("2")).thenReturn(Mono.just(catalogStock100));
            when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(catalogStock100));

            // When & Then
            StepVerifier.create(catalogService.applyDynamicDiscount("2", 20.0, 80))
                    .expectNextMatches(price -> assertPriceEquals(price, 36.79))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should apply 30% discount when stock is 200 and threshold is 150")
        void testApplyDynamicDiscount_Stock200_Success() {
            // Given
            when(catalogRepository.findById("3")).thenReturn(Mono.just(catalogStock200));
            when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(catalogStock200));

            // When & Then
            StepVerifier.create(catalogService.applyDynamicDiscount("3", 30.0, 150))
                    .expectNextMatches(price -> assertPriceEquals(price, 629.99))
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Main Flow Tests - Different Discount Percentages")
    class MainFlowTests {

        @Test
        @DisplayName("Main flow with 10% discount")
        void testMainFlow_Discount10Percent() {
            when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));
            when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(testCatalog));

            StepVerifier.create(catalogService.applyDynamicDiscount("1", 10.0, 30))
                    .expectNextMatches(price -> assertPriceEquals(price, 2339.99))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Main flow with 20% discount")
        void testMainFlow_Discount20Percent() {
            when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));
            when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(testCatalog));

            StepVerifier.create(catalogService.applyDynamicDiscount("1", 20.0, 30))
                    .expectNextMatches(price -> assertPriceEquals(price, 2079.99))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Main flow with 30% discount")
        void testMainFlow_Discount30Percent() {
            when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));
            when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(testCatalog));

            StepVerifier.create(catalogService.applyDynamicDiscount("1", 30.0, 30))
                    .expectNextMatches(price -> assertPriceEquals(price, 1819.99))
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Error Cases Tests")
    class ErrorCasesTests {

        @Test
        @DisplayName("Should handle negative discount (price increase)")
        void testErrorCase_NegativeDiscount() {
            when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));
            when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(testCatalog));

            // Negative discount should increase the price
            StepVerifier.create(catalogService.applyDynamicDiscount("1", -1.0, 30))
                    .expectNextMatches(price -> assertPriceEquals(price, 2625.99))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should handle 101% discount (negative price)")
        void testErrorCase_ExcessiveDiscount() {
            when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));
            when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(testCatalog));

            StepVerifier.create(catalogService.applyDynamicDiscount("1", 101.0, 30))
                    .expectNextMatches(price -> assertPriceEquals(price, -25.99))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should throw error for non-existent product")
        void testAdvancedError_NonExistentProduct() {
            // Given
            when(catalogRepository.findById("999")).thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(catalogService.applyDynamicDiscount("999", 10.0, 30))
                    .expectError(RuntimeException.class)
                    .verify();
                    
            verify(catalogRepository).findById("999");
            verify(catalogRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw error when repository throws exception")
        void testRepositoryException() {
            // Given
            when(catalogRepository.findById("1")).thenReturn(Mono.error(new RuntimeException("Database error")));

            // When & Then
            StepVerifier.create(catalogService.applyDynamicDiscount("1", 10.0, 30))
                    .expectError(RuntimeException.class)
                    .verify();
        }
    }

    @Nested
    @DisplayName("Border Cases Tests")
    class BorderCasesTests {

        @Test
        @DisplayName("Should not apply discount when stock equals threshold")
        void testBorderCase_StockEqualToThreshold() {
            // Given - stock exactly equals threshold
            when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));

            // When & Then - should not apply discount because 50 is not > 50
            StepVerifier.create(catalogService.applyDynamicDiscount("1", 10.0, 50))
                    .expectError(RuntimeException.class)
                    .verify();
                    
            verify(catalogRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should apply discount when stock is just above threshold")
        void testBorderCase_StockJustAboveThreshold() {
            // Given - stock just above threshold
            when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));
            when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(testCatalog));

            // When & Then - should apply discount because 50 > 49
            StepVerifier.create(catalogService.applyDynamicDiscount("1", 10.0, 49))
                    .expectNextMatches(price -> assertPriceEquals(price, 2339.99))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should not apply discount when stock is below threshold")
        void testBorderCase_StockBelowThreshold() {
            // Given
            when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));

            // When & Then - should not apply discount because 50 < 60
            StepVerifier.create(catalogService.applyDynamicDiscount("1", 10.0, 60))
                    .expectError(RuntimeException.class)
                    .verify();
        }

        @Test
        @DisplayName("Should handle zero discount")
        void testBorderCase_ZeroDiscount() {
            when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));
            when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(testCatalog));

            StepVerifier.create(catalogService.applyDynamicDiscount("1", 0.0, 30))
                    .expectNextMatches(price -> assertPriceEquals(price, 2599.99))
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Basic CRUD Operations Tests")
    class BasicOperationsTests {

        @Test
        @DisplayName("Should get all catalogs successfully")
        void testGetAllCatalogs() {
            when(catalogRepository.findAll()).thenReturn(Flux.just(testCatalog, catalogStock100));

            StepVerifier.create(catalogService.getAllCatalogs())
                    .expectNext(testCatalog)
                    .expectNext(catalogStock100)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should get catalog by id when found")
        void testGetCatalogById_Found() {
            when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));

            StepVerifier.create(catalogService.getCatalogById("1"))
                    .expectNext(testCatalog)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should return empty when catalog not found")
        void testGetCatalogById_NotFound() {
            when(catalogRepository.findById("999")).thenReturn(Mono.empty());

            StepVerifier.create(catalogService.getCatalogById("999"))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should create catalog successfully")
        void testCreateCatalog() {
            Catalog newCatalog = Catalog.builder()
                    .name("Nuevo Producto")
                    .price(199.99)
                    .quantity(75)
                    .build();

            when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(newCatalog));

            StepVerifier.create(catalogService.createCatalog(newCatalog))
                    .expectNextMatches(catalog -> {
                        assertThat(catalog.getActive()).isTrue();
                        return catalog.equals(newCatalog);
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should update catalog successfully")
        void testUpdateCatalog() {
            Catalog updatedData = Catalog.builder()
                    .name("Updated Product")
                    .price(299.99)
                    .quantity(80)
                    .build();

            when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));
            when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(testCatalog));

            StepVerifier.create(catalogService.updateCatalog("1", updatedData))
                    .expectNextMatches(catalog -> 
                        catalog.getName().equals("Updated Product") &&
                        catalog.getPrice().equals(299.99) &&
                        catalog.getQuantity().equals(80)
                    )
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should delete catalog successfully")
        void testDeleteCatalog() {
            when(catalogRepository.deleteById("1")).thenReturn(Mono.empty());

            StepVerifier.create(catalogService.deleteCatalog("1"))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Should get catalogs with stock above threshold")
        void testGetCatalogsWithStock() {
            when(catalogRepository.findByQuantityGreaterThan(30))
                    .thenReturn(Flux.just(testCatalog, catalogStock100));

            StepVerifier.create(catalogService.getCatalogsWithStock(30))
                    .expectNext(testCatalog)
                    .expectNext(catalogStock100)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Performance and Timeout Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Should complete discount operation within timeout")
        void testDiscountOperationTimeout() {
            when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog).delayElement(Duration.ofMillis(100)));
            when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(testCatalog));

            StepVerifier.create(catalogService.applyDynamicDiscount("1", 10.0, 30))
                    .expectNextMatches(price -> assertPriceEquals(price, 2339.99))
                    .expectComplete()
                    .verify(Duration.ofSeconds(5));
        }
    }

    @Nested
    @DisplayName("Integration-like Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should handle multiple discount operations in sequence")
        void testMultipleDiscountOperations() {
            when(catalogRepository.findById("1")).thenReturn(Mono.just(testCatalog));
            when(catalogRepository.save(any(Catalog.class))).thenReturn(Mono.just(testCatalog));

            // First discount
            StepVerifier.create(catalogService.applyDynamicDiscount("1", 10.0, 30))
                    .expectNextMatches(price -> assertPriceEquals(price, 2339.99))
                    .verifyComplete();

            // Second discount (on already discounted item)
            testCatalog.setPrice(2339.99);
            StepVerifier.create(catalogService.applyDynamicDiscount("1", 5.0, 30))
                    .expectNextMatches(price -> assertPriceEquals(price, 2222.99))
                    .verifyComplete();
        }
    }

    // Helper method for price comparison with tolerance
    private boolean assertPriceEquals(Double actual, Double expected) {
        return Math.abs(actual - expected) < 0.01;
    }
}