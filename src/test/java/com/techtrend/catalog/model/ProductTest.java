package com.techtrend.catalog.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🏷️ Pruebas unitarias para la entidad Product
 * 
 * Verifica la lógica de negocio de la entidad de dominio.
 * Prueba validaciones de stock y disponibilidad.
 * 
 * @author TechTrend Development Team
 */
@DisplayName("🏷️ Product Entity - Lógica de Dominio")
class ProductTest {

    @Test
    @DisplayName("✅ Producto con stock debe estar disponible (10 unidades)")
    void productWithStockShouldBeAvailable() {
        System.out.println("🏷️ Probando disponibilidad de producto con stock: 10 unidades");
        
        // Given
        Product product = new Product("1", "Laptop", new BigDecimal("1000"), 10);

        // When & Then
        assertTrue(product.isAvailable());
        System.out.println("✅ Test exitoso: Producto con 10 unidades está disponible");
    }

    @Test
    @DisplayName("❌ Producto sin stock no debe estar disponible (0 unidades)")
    void productWithoutStockShouldNotBeAvailable() {
        System.out.println("🏷️ Probando disponibilidad de producto sin stock: 0 unidades");
        
        // Given
        Product product = new Product("1", "Laptop", new BigDecimal("1000"), 0);

        // When & Then
        assertFalse(product.isAvailable());
        System.out.println("✅ Test exitoso: Producto con 0 unidades no está disponible");
    }

    @Test
    @DisplayName("✅ Debe tener stock suficiente cuando cantidad solicitada ≤ disponible")
    void shouldHaveSufficientStockWhenRequestedQuantityIsLessOrEqual() {
        System.out.println("🏷️ Probando validación de stock suficiente (50 unidades disponibles)");
        
        // Given
        Product product = new Product("1", "Laptop", new BigDecimal("1000"), 50);

        // When & Then
        assertTrue(product.hasStock(10));
        assertTrue(product.hasStock(50));
        
        System.out.println("✅ Test exitoso: Stock suficiente para 10 y 50 unidades");
    }

    @Test
    @DisplayName("❌ No debe tener stock suficiente cuando cantidad solicitada > disponible")
    void shouldNotHaveSufficientStockWhenRequestedQuantityIsGreater() {
        System.out.println("🏷️ Probando validación de stock insuficiente (60 > 50 disponibles)");
        
        // Given
        Product product = new Product("1", "Laptop", new BigDecimal("1000"), 50);

        // When & Then
        assertFalse(product.hasStock(60));
        
        System.out.println("✅ Test exitoso: Stock insuficiente para 60 unidades (solo 50 disponibles)");
    }
    
    // ============ PRUEBAS PARAMETRIZADAS ============
    
    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 25, 50, 100})
    @DisplayName("🧪 [PARAMETRIZADA 1] Productos con stock positivo deben estar disponibles")
    void productsWithPositiveStockShouldBeAvailable(int stock) {
        System.out.println("🧪 Probando disponibilidad con stock: " + stock + " unidades");
        
        // Given
        Product product = new Product("test", "Producto Test", new BigDecimal("100"), stock);
        
        // When & Then
        assertTrue(product.isAvailable(), "Producto con " + stock + " unidades debe estar disponible");
        System.out.println("✅ Producto con " + stock + " unidades confirmado como disponible");
    }
    
    @ParameterizedTest
    @CsvSource({
        "10, 5, true",
        "10, 10, true", 
        "10, 15, false",
        "50, 25, true",
        "50, 50, true",
        "50, 75, false",
        "100, 1, true",
        "100, 150, false"
    })
    @DisplayName("🧪 [PARAMETRIZADA 2] Validación de stock con diferentes combinaciones disponible/solicitado")
    void stockValidationWithDifferentCombinations(int available, int requested, boolean expectedResult) {
        System.out.println("🧪 Probando: " + available + " disponibles vs " + requested + " solicitadas = " + expectedResult);
        
        // Given
        Product product = new Product("test", "Producto Test", new BigDecimal("100"), available);
        
        // When
        boolean hasStock = product.hasStock(requested);
        
        // Then
        assertEquals(expectedResult, hasStock, 
            "Con " + available + " unidades, solicitar " + requested + " debería ser " + expectedResult);
        System.out.println("✅ Resultado correcto: " + hasStock);
    }
}