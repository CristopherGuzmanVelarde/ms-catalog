package com.techtrend.catalog.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}