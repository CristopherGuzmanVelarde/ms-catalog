# 🧪 Pruebas de Integración REST - Catalog Microservice

## 📋 Índice

1. [Descripción General](#-descripción-general)
2. [Ejecución Rápida](#-ejecución-rápida)
3. [Endpoints de Prueba](#-endpoints-de-prueba)
4. [Casos de Prueba](#-casos-de-prueba)
5. [Pruebas con Postman](#-pruebas-con-postman)
6. [Troubleshooting](#-troubleshooting)

---

## 📖 Descripción General

Las **Pruebas de Integración REST** validan el comportamiento completo del microservicio desde la perspectiva de un cliente HTTP real (Postman, frontend, otro microservicio).

### 🎯 Qué Validan

✅ **Flujo Completo End-to-End:**
```
Cliente HTTP → Controller → Service → Repository → Response JSON
```

✅ **Aspectos Técnicos:**
- Códigos de estado HTTP (200, 404, 400)
- Estructura y formato de respuestas JSON
- Manejo de errores y validaciones
- Tiempos de respuesta y throughput

### 📊 Cobertura

| Tipo | Tests | % |
|------|-------|---|
| ✅ Happy Path (casos exitosos) | 7 | 47% |
| ❌ Error Handling (manejo errores) | 6 | 40% |
| ⚡ Performance (rendimiento) | 2 | 13% |
| **TOTAL** | **15** | **100%** |

---

## ⚡ Ejecución Rápida

### Paso 1: Iniciar el Servidor

```bash
# Iniciar el servidor
mvn spring-boot:run

# Esperar mensaje: "Netty started on port 8080"
```

### Paso 2: Ejecutar las Pruebas

**Opción A: Maven Command Line**
```bash
# Ejecutar solo las pruebas de integración
mvn test -Dtest=CatalogRestIntegrationTest

# Sin JaCoCo (si hay problemas de compatibilidad)
mvn test -Dtest=CatalogRestIntegrationTest -Djacoco.skip=true

# Con output detallado
mvn test -Dtest=CatalogRestIntegrationTest -X
```

**Opción B: IDE (IntelliJ / Eclipse / VS Code)**
1. Abrir: `src/test/java/com/techtrend/catalog/integration/CatalogRestIntegrationTest.java`
2. Click derecho → **Run Test**
3. Ver resultados en la consola

### Resultado Esperado

```
================================================================================
🚀 Iniciando Pruebas de Integración REST
📍 Puerto: 52291
🌐 Base URL: /api/catalog
================================================================================

📝 TEST 1: Listando todos los productos...
   ✓ Total de productos: 14
   ✓ Status HTTP: 200 OK
⏱️  ✅ GET /products - Tiempo: 245ms

...

================================================================================
✅ Pruebas de Integración REST Completadas
================================================================================

[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 🌐 Endpoints de Prueba

### Tabla Completa de Endpoints para Postman

> ⚠️ **Base URL:** `http://localhost:8080/api/catalog`

| # | Método | Endpoint | Descripción | Parámetros | Status Esperado |
|---|--------|----------|-------------|------------|-----------------|
| 1 | GET | `/products` | Listar todos los productos disponibles | - | 200 OK |
| 2 | GET | `/products/1` | Obtener producto existente | - | 200 OK |
| 3 | GET | `/products/999` | Obtener producto inexistente | - | 404 Not Found |
| 4 | GET | `/products/1/stock?quantity=10` | Verificar stock suficiente | `quantity=10` | 200 OK |
| 5 | GET | `/products/3/stock?quantity=100` | Verificar stock insuficiente | `quantity=100` | 200 OK |
| 6 | GET | `/products/1/stock?quantity=-5` | Validar cantidad negativa | `quantity=-5` | 400 Bad Request |
| 7 | GET | `/products/5/stock?quantity=1` | Producto sin stock | `quantity=1` | 200 OK |
| 8 | GET | `/products/7/details` | Obtener detalles (existente) | - | 200 OK |
| 9 | GET | `/products/999/details` | Obtener detalles (inexistente) | - | 404 Not Found |
| 10 | GET | `/products/@#$` | ID con caracteres especiales | - | 404 Not Found |

### Headers Recomendados

```http
Accept: application/json
Content-Type: application/json
```

---

## 📝 Casos de Prueba

### Resumen de Tests

| ID | Endpoint | Caso | Status | Validación |
|----|----------|------|--------|------------|
| TEST 1 | `GET /products` | Lista completa | 200 | Array con >10 productos |
| TEST 2 | `GET /products` | Tiempo respuesta | 200 | < 1000ms |
| TEST 3 | `GET /products/{id}` | Producto existente | 200 | Datos completos |
| TEST 4 | `GET /products/{id}` | Producto inexistente | 404 | Body vacío |
| TEST 5 | `GET /products/{id}` | Múltiples productos | 200 | 5 IDs diferentes |
| TEST 6 | `GET /products/{id}/stock` | Stock suficiente | 200 | Response: true |
| TEST 7 | `GET /products/{id}/stock` | Stock insuficiente | 200 | Response: false |
| TEST 8 | `GET /products/{id}/stock` | Sin stock | 200 | Response: false |
| TEST 9 | `GET /products/{id}/stock` | Producto inexistente | 200 | Response: false |
| TEST 10 | `GET /products/{id}/details` | Detalles existentes | 200 | Info completa |
| TEST 11 | `GET /products/{id}/details` | Detalles inexistentes | 404 | Body vacío |
| TEST 12 | `GET /products/{id}` | Caracteres especiales | 404 | Manejo graceful |
| TEST 13 | `GET /products/{id}/stock` | Cantidad negativa | 400 | Validación input |
| TEST 14 | `GET /products` | Idempotencia | 200 | Múltiples calls iguales |
| TEST 15 | `GET /products` | Carga (50 requests) | 200 | Throughput medido |

---

## 🔧 Pruebas con Postman

### 1️⃣ Listar Todos los Productos

**Request:**
```http
GET http://localhost:8080/api/catalog/products
Accept: application/json
```

**Response Esperado (200 OK):**
```json
[
  {
    "id": "1",
    "name": "Laptop Ryzen 7",
    "price": 9999.99,
    "quantity": 50
  },
  {
    "id": "2",
    "name": "Mouse Gaming",
    "price": 299.99,
    "quantity": 100
  }
]
```

---

### 2️⃣ Obtener Producto por ID

**Request (Existente):**
```http
GET http://localhost:8080/api/catalog/products/1
Accept: application/json
```

**Response (200 OK):**
```json
{
  "id": "1",
  "name": "Laptop Ryzen 7",
  "price": 9999.99,
  "quantity": 50
}
```

**Request (Inexistente):**
```http
GET http://localhost:8080/api/catalog/products/999
Accept: application/json
```

**Response (404 Not Found):**
```
HTTP/1.1 404 Not Found
(empty body)
```

---

### 3️⃣ Verificar Stock

**Request (Stock Suficiente):**
```http
GET http://localhost:8080/api/catalog/products/1/stock?quantity=10
Accept: application/json
```

**Response (200 OK):**
```json
true
```

**Request (Stock Insuficiente):**
```http
GET http://localhost:8080/api/catalog/products/3/stock?quantity=100
Accept: application/json
```

**Response (200 OK):**
```json
false
```

**Request (Cantidad Inválida):**
```http
GET http://localhost:8080/api/catalog/products/1/stock?quantity=-5
Accept: application/json
```

**Response (400 Bad Request):**
```json
{
  "timestamp": "2025-10-08T20:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "La cantidad debe ser mayor a 0"
}
```

---

### 4️⃣ Obtener Detalles del Producto

**Request (Existente):**
```http
GET http://localhost:8080/api/catalog/products/7/details
Accept: application/json
```

**Response (200 OK):**
```json
{
  "id": "7",
  "name": "SSD 1TB",
  "price": 899.99,
  "quantity": 30
}
```

**Request (Inexistente):**
```http
GET http://localhost:8080/api/catalog/products/999/details
Accept: application/json
```

**Response (404 Not Found):**
```
HTTP/1.1 404 Not Found
(empty body)
```

---

### 📦 Variables de Postman (Opcional)

Crea una colección con estas variables para facilitar las pruebas:

```json
{
  "name": "Catalog Microservice",
  "variables": [
    {
      "key": "base_url",
      "value": "http://localhost:8080/api/catalog"
    },
    {
      "key": "product_id_valid",
      "value": "1"
    },
    {
      "key": "product_id_invalid",
      "value": "999"
    },
    {
      "key": "quantity_valid",
      "value": "10"
    },
    {
      "key": "quantity_invalid",
      "value": "-5"
    }
  ]
}
```

Luego usa: `{{base_url}}/products/{{product_id_valid}}`

---

## 🐛 Troubleshooting

### Problema 1: Postman Retorna HTML en vez de JSON

**Síntomas:**
- Response muestra página HTML
- Status 403 Forbidden o error de página

**Causa:** Servidor no está corriendo o puerto incorrecto

**Solución:**
```bash
# 1. Verificar si el servidor está corriendo
tasklist | findstr java

# 2. Iniciar el servidor
mvn spring-boot:run

# 3. Esperar mensaje: "Netty started on port 8080"

# 4. Verificar health check
curl http://localhost:8080/actuator/health
```

---

### Problema 2: Puerto 8080 ya está en uso

**Error:**
```
Web server failed to start. Port 8080 was already in use.
```

**Solución Windows:**
```bash
# Encontrar proceso usando el puerto
netstat -ano | findstr :8080

# Matar el proceso (reemplaza 1234 con el PID real)
taskkill /F /PID 1234
```

**Solución Linux/Mac:**
```bash
# Encontrar proceso
lsof -i :8080

# Matar el proceso
kill -9 <PID>
```

---

### Problema 3: Tests Fallan con Timeout

**Error:**
```
java.util.concurrent.TimeoutException
```

**Causa:** Servidor tarda en iniciar

**Solución:**
```bash
# Aumentar memoria JVM
set MAVEN_OPTS=-Xmx1024m
mvn test -Dtest=CatalogRestIntegrationTest

# O aumentar timeout
mvn test -Dtest=CatalogRestIntegrationTest -Dmaven.test.timeout=60000
```

---

### Problema 4: JaCoCo Incompatibilidad con Java 25

**Error:**
```
Unsupported class file major version 69
```

**Solución:**
```bash
# Ejecutar sin JaCoCo
mvn test -Dtest=CatalogRestIntegrationTest -Djacoco.skip=true
```

---

### Problema 5: Postman No Encuentra el Servidor

**Checklist de Verificación:**

1. ✅ **¿Servidor corriendo?**
   ```bash
   mvn spring-boot:run
   ```

2. ✅ **¿Puerto correcto (8080)?**
   ```
   http://localhost:8080/api/catalog/products
   ```

3. ✅ **¿Header Accept configurado?**
   ```
   Accept: application/json
   ```

4. ✅ **¿Firewall bloqueando?**
   - Desactiva temporalmente el firewall para probar

5. ✅ **¿Proxy en Postman?**
   - Settings → Proxy → Desactivar

---

## ✅ Checklist Final

Antes de iniciar las pruebas en Postman:

- [ ] `mvn clean compile` - Proyecto compila sin errores
- [ ] `mvn spring-boot:run` - Servidor inicia en puerto 8080
- [ ] Ver mensaje: "Netty started on port 8080"
- [ ] Verificar health: `http://localhost:8080/actuator/health`
- [ ] Postman configurado con URLs usando puerto **8080**
- [ ] Header `Accept: application/json` configurado

---

## 📚 Recursos Adicionales

### Documentación Relacionada
- [README.md](README.md) - Documentación general del proyecto
- [TEST-RESULTS-ANALYSIS.md](TEST-RESULTS-ANALYSIS.md) - Análisis de resultados y optimización
- [pom.xml](pom.xml) - Configuración Maven y dependencias

### Documentación Externa
- [Spring WebTestClient](https://docs.spring.io/spring-framework/reference/testing/webtestclient.html)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [Postman Documentation](https://learning.postman.com/docs/getting-started/introduction/)

---

**📅 Última actualización:** Octubre 2025  
**👨‍💻 Autor:** TechTrend Development Team  
**📊 Versión:** 3.0.0 (Limpia y Consolidada)

---

**🚀 ¡Microservicio validado y listo para testing!**
