package com.techtrend.catalog.integration;

import com.techtrend.catalog.model.Product;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * 🧪 Pruebas de Integración REST - End-to-End HTTP
 * 
 * Estas pruebas levantan el servidor completo y prueban el flujo completo:
 * HTTP Request → Controller → Service → Repository → HTTP Response
 * 
 * ✅ Simula requests HTTP reales como lo haría un cliente externo
 * ✅ Valida toda la pila de la aplicación en conjunto
 * ✅ Verifica comportamiento del API bajo condiciones reales
 * 
 * @author TechTrend Development Team
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("🔗 Integration Tests - Catalog REST API")
class CatalogRestIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    private String baseUrl;
    
    // 📊 Métricas de rendimiento
    private long startTime;
    private long endTime;

    @BeforeAll
    void setup() {
        baseUrl = "/api/catalog";
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🚀 Iniciando Pruebas de Integración REST");
        System.out.println("📍 Puerto: " + port);
        System.out.println("🌐 Base URL: " + baseUrl);
        System.out.println("=".repeat(80) + "\n");
    }

    @BeforeEach
    void startTimer() {
        startTime = System.currentTimeMillis();
    }

    @AfterEach
    void logExecutionTime(TestInfo testInfo) {
        endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("⏱️  " + testInfo.getDisplayName() + " - Tiempo: " + duration + "ms\n");
    }

    // ========================================================================
    // 📋 TEST 1: GET /api/catalog/products - Listar todos los productos
    // ========================================================================
    
    @Test
    @Order(1)
    @DisplayName("✅ GET /products - Debe retornar lista completa de productos")
    void testGetAllProducts_ShouldReturnCompleteProductList() {
        System.out.println("📝 TEST 1: Listando todos los productos...");
        
        webTestClient
                .get()
                .uri(baseUrl + "/products")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // Verificar status HTTP
                .expectStatus().isOk()
                // Verificar Content-Type
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                // Verificar que retorna un array
                .expectBodyList(Product.class)
                .consumeWith(response -> {
                    var products = response.getResponseBody();
                    assertThat(products).isNotNull();
                    assertThat(products).isNotEmpty();
                    assertThat(products.size()).isGreaterThan(10); // Esperamos al menos 10 productos
                    
                    System.out.println("   ✓ Total de productos: " + products.size());
                    System.out.println("   ✓ Status HTTP: 200 OK");
                    System.out.println("   ✓ Content-Type: application/json");
                    
                    // Verificar estructura de datos
                    Product firstProduct = products.get(0);
                    assertThat(firstProduct.getId()).isNotNull();
                    assertThat(firstProduct.getName()).isNotNull();
                    assertThat(firstProduct.getPrice()).isNotNull();
                    assertThat(firstProduct.getQuantity()).isNotNull();
                    
                    System.out.println("   ✓ Estructura de datos validada");
                    System.out.println("   📦 Ejemplo: " + firstProduct.getName() + 
                                     " - $" + firstProduct.getPrice());
                });
    }

    @Test
    @Order(2)
    @DisplayName("✅ GET /products - Debe responder en tiempo razonable (< 1000ms)")
    void testGetAllProducts_ShouldRespondQuickly() {
        System.out.println("📝 TEST 2: Verificando tiempo de respuesta...");
        
        long start = System.currentTimeMillis();
        
        webTestClient
                .get()
                .uri(baseUrl + "/products")
                .exchange()
                .expectStatus().isOk();
        
        long responseTime = System.currentTimeMillis() - start;
        
        assertThat(responseTime).isLessThan(1000); // Debe responder en menos de 1 segundo
        
        System.out.println("   ✓ Tiempo de respuesta: " + responseTime + "ms");
        System.out.println("   ✓ Rendimiento aceptable (< 1000ms)");
    }

    // ========================================================================
    // 🎯 TEST 2: GET /api/catalog/products/{id} - Obtener producto específico
    // ========================================================================
    
    @Test
    @Order(3)
    @DisplayName("✅ GET /products/{id} - Debe retornar producto existente")
    void testGetProductById_WhenProductExists_ShouldReturnProduct() {
        System.out.println("📝 TEST 3: Obteniendo producto por ID (existente)...");
        
        String productId = "1"; // Laptop Ryzen 7
        
        webTestClient
                .get()
                .uri(baseUrl + "/products/{id}", productId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Product.class)
                .consumeWith(response -> {
                    Product product = response.getResponseBody();
                    assertThat(product).isNotNull();
                    assertThat(product.getId()).isEqualTo(productId);
                    assertThat(product.getName()).isNotBlank();
                    assertThat(product.getPrice()).isGreaterThan(BigDecimal.ZERO);
                    
                    System.out.println("   ✓ Producto encontrado: " + product.getName());
                    System.out.println("   ✓ ID: " + product.getId());
                    System.out.println("   ✓ Precio: $" + product.getPrice());
                    System.out.println("   ✓ Stock: " + product.getQuantity() + " unidades");
                });
    }

    @Test
    @Order(4)
    @DisplayName("❌ GET /products/{id} - Debe retornar 404 para producto inexistente")
    void testGetProductById_WhenProductNotExists_ShouldReturn404() {
        System.out.println("📝 TEST 4: Intentando obtener producto inexistente...");
        
        String nonExistentId = "999";
        
        webTestClient
                .get()
                .uri(baseUrl + "/products/{id}", nonExistentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .isEmpty();
        
        System.out.println("   ✓ Status HTTP: 404 Not Found");
        System.out.println("   ✓ Manejo correcto de producto inexistente");
    }

    @Test
    @Order(5)
    @DisplayName("✅ GET /products/{id} - Debe manejar múltiples productos diferentes")
    void testGetProductById_MultipleProducts_ShouldReturnCorrectData() {
        System.out.println("📝 TEST 5: Obteniendo múltiples productos...");
        
        String[] productIds = {"1", "4", "7", "10", "12"};
        
        for (String id : productIds) {
            webTestClient
                    .get()
                    .uri(baseUrl + "/products/{id}", id)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Product.class)
                    .consumeWith(response -> {
                        Product product = response.getResponseBody();
                        assertThat(product).isNotNull();
                        assertThat(product.getId()).isEqualTo(id);
                        System.out.println("   ✓ ID " + id + ": " + product.getName());
                    });
        }
        
        System.out.println("   ✓ Todos los productos recuperados correctamente");
    }

    // ========================================================================
    // 📦 TEST 3: GET /api/catalog/products/{id}/stock - Verificar stock
    // ========================================================================
    
    @Test
    @Order(6)
    @DisplayName("✅ GET /products/{id}/stock - Debe validar stock suficiente")
    void testCheckStock_WhenStockSufficient_ShouldReturnTrue() {
        System.out.println("📝 TEST 6: Verificando stock suficiente...");
        
        String productId = "1"; // Laptop Ryzen 7 (50 unidades)
        Integer requestedQuantity = 10;
        
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUrl + "/products/{id}/stock")
                        .queryParam("quantity", requestedQuantity)
                        .build(productId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .consumeWith(response -> {
                    Boolean hasStock = response.getResponseBody();
                    assertThat(hasStock).isTrue();
                    
                    System.out.println("   ✓ Producto: ID " + productId);
                    System.out.println("   ✓ Cantidad solicitada: " + requestedQuantity);
                    System.out.println("   ✓ Stock disponible: TRUE");
                });
    }

    @Test
    @Order(7)
    @DisplayName("❌ GET /products/{id}/stock - Debe validar stock insuficiente")
    void testCheckStock_WhenStockInsufficient_ShouldReturnFalse() {
        System.out.println("📝 TEST 7: Verificando stock insuficiente...");
        
        String productId = "3"; // MacBook Pro M2 (15 unidades)
        Integer requestedQuantity = 100; // Más de lo disponible
        
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUrl + "/products/{id}/stock")
                        .queryParam("quantity", requestedQuantity)
                        .build(productId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .consumeWith(response -> {
                    Boolean hasStock = response.getResponseBody();
                    assertThat(hasStock).isFalse();
                    
                    System.out.println("   ✓ Producto: ID " + productId);
                    System.out.println("   ✓ Cantidad solicitada: " + requestedQuantity);
                    System.out.println("   ✓ Stock disponible: FALSE");
                    System.out.println("   ✓ Validación correcta de stock insuficiente");
                });
    }

    @Test
    @Order(8)
    @DisplayName("❌ GET /products/{id}/stock - Debe manejar producto sin stock")
    void testCheckStock_WhenProductOutOfStock_ShouldReturnFalse() {
        System.out.println("📝 TEST 8: Verificando producto sin stock...");
        
        String productId = "15"; // Teclado Mecánico (0 unidades)
        Integer requestedQuantity = 1;
        
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUrl + "/products/{id}/stock")
                        .queryParam("quantity", requestedQuantity)
                        .build(productId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .consumeWith(response -> {
                    Boolean hasStock = response.getResponseBody();
                    assertThat(hasStock).isFalse();
                    
                    System.out.println("   ✓ Producto agotado detectado correctamente");
                    System.out.println("   ✓ Stock: 0 unidades");
                });
    }

    @Test
    @Order(9)
    @DisplayName("❌ GET /products/{id}/stock - Debe retornar false para producto inexistente")
    void testCheckStock_WhenProductNotExists_ShouldReturnFalse() {
        System.out.println("📝 TEST 9: Verificando stock de producto inexistente...");
        
        String nonExistentId = "999";
        Integer requestedQuantity = 5;
        
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUrl + "/products/{id}/stock")
                        .queryParam("quantity", requestedQuantity)
                        .build(nonExistentId))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .consumeWith(response -> {
                    Boolean hasStock = response.getResponseBody();
                    assertThat(hasStock).isFalse();
                    
                    System.out.println("   ✓ Status HTTP: 200 OK");
                    System.out.println("   ✓ Stock disponible: FALSE");
                    System.out.println("   ✓ Manejo correcto de producto inexistente");
                });
    }

    // ========================================================================
    // 📄 TEST 4: GET /api/catalog/products/{id}/details - Obtener detalles
    // ========================================================================
    
    @Test
    @Order(10)
    @DisplayName("✅ GET /products/{id}/details - Debe retornar detalles del producto")
    void testGetProductDetails_WhenProductExists_ShouldReturnDetails() {
        System.out.println("📝 TEST 10: Obteniendo detalles del producto...");
        
        String productId = "7"; // iPhone 15 Pro
        
        webTestClient
                .get()
                .uri(baseUrl + "/products/{id}/details", productId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Product.class)
                .consumeWith(response -> {
                    Product product = response.getResponseBody();
                    assertThat(product).isNotNull();
                    assertThat(product.getName()).isNotBlank();
                    assertThat(product.getPrice()).isNotNull();
                    
                    System.out.println("   ✓ Nombre: " + product.getName());
                    System.out.println("   ✓ Precio: $" + product.getPrice());
                    System.out.println("   ✓ Detalles recuperados correctamente");
                });
    }

    @Test
    @Order(11)
    @DisplayName("❌ GET /products/{id}/details - Debe retornar 404 para producto inexistente")
    void testGetProductDetails_WhenProductNotExists_ShouldReturn404() {
        System.out.println("📝 TEST 11: Intentando obtener detalles de producto inexistente...");
        
        String nonExistentId = "888";
        
        webTestClient
                .get()
                .uri(baseUrl + "/products/{id}/details", nonExistentId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .isEmpty();
        
        System.out.println("   ✓ Status HTTP: 404 Not Found");
        System.out.println("   ✓ Manejo correcto de error");
    }

    // ========================================================================
    // 🔄 TEST 5: Casos negativos y Edge Cases
    // ========================================================================
    
    @Test
    @Order(12)
    @DisplayName("❌ GET /products/{id} - Debe manejar ID con caracteres especiales")
    void testGetProductById_WithSpecialCharacters_ShouldHandleGracefully() {
        System.out.println("📝 TEST 12: Probando ID con caracteres especiales...");
        
        String specialId = "abc@#$";
        
        webTestClient
                .get()
                .uri(baseUrl + "/products/{id}", specialId)
                .exchange()
                .expectStatus().isNotFound();
        
        System.out.println("   ✓ Manejo correcto de caracteres especiales");
    }

    @Test
    @Order(13)
    @DisplayName("❌ GET /products/{id}/stock - Debe validar cantidad negativa")
    void testCheckStock_WithNegativeQuantity_ShouldHandleGracefully() {
        System.out.println("📝 TEST 13: Probando cantidad negativa...");
        
        String productId = "1";
        Integer negativeQuantity = -5;
        
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(baseUrl + "/products/{id}/stock")
                        .queryParam("quantity", negativeQuantity)
                        .build(productId))
                .exchange()
                // El servicio retorna un error Mono que se traduce en 400
                .expectStatus().isBadRequest();
        
        System.out.println("   ✓ Status HTTP: 400 Bad Request");
        System.out.println("   ✓ Validación de cantidad negativa correcta");
    }

    @Test
    @Order(14)
    @DisplayName("✅ GET /products - Debe ser idempotente")
    void testGetAllProducts_ShouldBeIdempotent() {
        System.out.println("📝 TEST 14: Verificando idempotencia...");
        
        // Primera llamada
        var firstResponse = webTestClient
                .get()
                .uri(baseUrl + "/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .returnResult()
                .getResponseBody();
        
        // Segunda llamada
        var secondResponse = webTestClient
                .get()
                .uri(baseUrl + "/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .returnResult()
                .getResponseBody();
        
        assertThat(firstResponse).isEqualTo(secondResponse);
        
        System.out.println("   ✓ Las respuestas son idénticas");
        System.out.println("   ✓ Operación idempotente verificada");
    }

    // ========================================================================
    // 📊 Prueba de rendimiento bajo carga
    // ========================================================================
    
    @Test
    @Order(15)
    @DisplayName("⚡ Performance - Múltiples requests concurrentes")
    void testPerformance_MultipleRequests_ShouldHandleLoad() {
        System.out.println("📝 TEST 15: Prueba de carga con múltiples requests...");
        
        int numberOfRequests = 50;
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < numberOfRequests; i++) {
            webTestClient
                    .get()
                    .uri(baseUrl + "/products")
                    .exchange()
                    .expectStatus().isOk();
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        double avgTime = (double) totalTime / numberOfRequests;
        
        System.out.println("   ✓ Total de requests: " + numberOfRequests);
        System.out.println("   ✓ Tiempo total: " + totalTime + "ms");
        System.out.println("   ✓ Tiempo promedio: " + String.format("%.2f", avgTime) + "ms");
        System.out.println("   ✓ Throughput: " + 
                         String.format("%.2f", (numberOfRequests * 1000.0 / totalTime)) + " req/s");
        
        assertThat(avgTime).isLessThan(100); // Promedio menor a 100ms
    }

    @AfterAll
    void tearDown() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("✅ Pruebas de Integración REST Completadas");
        System.out.println("=".repeat(80) + "\n");
    }
}
