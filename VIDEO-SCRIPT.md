# 🎬 Guión para Video - Microservicio de Catálogo TechTrend

## 📋 Estructura del Video (Duración estimada: 8-12 minutos)

---

## 🎯 **PARTE 1: Introducción y Contexto** (1-2 minutos)

### **Slide de Apertura**
```
🛍️ Microservicio de Catálogo - TechTrend
Implementación con Spring Boot WebFlux y Testing Completo
```

### **Guión de Narración:**
> "Hola, en este video vamos a explorar un microservicio de catálogo completo para una plataforma e-commerce llamada TechTrend. Este proyecto implementa las mejores prácticas de desarrollo con Spring Boot, programación reactiva y testing exhaustivo."

### **Puntos Clave a Mencionar:**
- ✅ Microservicio para gestión de inventario
- ✅ Arquitectura hexagonal (Ports & Adapters)
- ✅ Programación reactiva con WebFlux
- ✅ 95 pruebas automatizadas con 100% de éxito

---

## 🏗️ **PARTE 2: Arquitectura y Tecnologías** (2-3 minutos)

### **Mostrar en Pantalla:**
```
📁 Estructura del Proyecto
src/
├── main/java/com/techtrend/catalog/
│   ├── model/Product.java          # 🏷️ Entidad de dominio
│   ├── service/CatalogService.java # 🔌 Puerto (Interface)
│   ├── service/CatalogServiceImpl.java # 💼 Lógica de negocio
│   └── controller/CatalogController.java # 🌐 REST Endpoints
└── test/ # 🧪 95 pruebas unitarias e integración
```

### **Guión de Narración:**
> "La arquitectura sigue el patrón hexagonal, separando claramente las responsabilidades. Tenemos la entidad Product como dominio, el servicio que implementa la lógica de negocio, y el controlador que expone los endpoints REST."

### **Tecnologías a Destacar:**
- ☕ **Java 17** (LTS)
- 🍃 **Spring Boot 3.2.0**
- ⚡ **WebFlux** (Programación Reactiva)
- 🧪 **JUnit 5 + Mockito**
- 📊 **JaCoCo** (Cobertura de código)
- 🔍 **SonarQube** (Análisis de calidad)

---

## 🔌 **PARTE 3: Endpoints y Funcionalidades** (2-3 minutos)

### **Mostrar API Endpoints:**
```
🌐 API REST Endpoints:

GET /api/catalog/products
📦 Lista todos los productos disponibles

GET /api/catalog/products/{id}
🔍 Obtiene un producto específico por ID

GET /api/catalog/products/{id}/stock?quantity={n}
📊 Verifica si hay stock suficiente

GET /api/catalog/products/{id}/details
📋 Obtiene detalles completos del producto
```

### **Demo en Vivo:**
```bash
# Mostrar estos comandos ejecutándose
curl http://localhost:8080/api/catalog/products
curl http://localhost:8080/api/catalog/products/1
curl "http://localhost:8080/api/catalog/products/1/stock?quantity=10"
```

### **Guión de Narración:**
> "El microservicio expone 4 endpoints principales. Vamos a ver cómo funcionan en tiempo real. Aquí podemos listar todos los productos disponibles, buscar uno específico, y lo más importante, verificar si tenemos stock suficiente para una compra."

---

## 🧪 **PARTE 4: Escenarios de Prueba** (3-4 minutos)

### **Mostrar Ejecución de Tests:**
```bash
mvn clean test
```

### **Destacar Resultados:**
```
🎯 Resultados de Testing:
✅ Tests run: 95
✅ Failures: 0  
✅ Errors: 0
✅ Skipped: 0
✅ BUILD SUCCESS
```

### **Escenarios Específicos a Demostrar:**

#### **🟢 Escenario 1: Flujo Principal**
```bash
# Mostrar estos comandos
curl "http://localhost:8080/api/catalog/products/1/stock?quantity=1"  # → true
curl "http://localhost:8080/api/catalog/products/1/stock?quantity=2"  # → true  
curl "http://localhost:8080/api/catalog/products/1/stock?quantity=3"  # → true
```

#### **🔴 Escenario 2: Casos de Error**
```bash
curl "http://localhost:8080/api/catalog/products/1/stock?quantity=60"   # → false
curl "http://localhost:8080/api/catalog/products/1/stock?quantity=100"  # → false
curl "http://localhost:8080/api/catalog/products/5/stock?quantity=1"    # → false (agotado)
```

#### **🟡 Escenario 3: Casos Límite**
```bash
curl "http://localhost:8080/api/catalog/products/1/stock?quantity=0"   # → 400 Bad Request
curl "http://localhost:8080/api/catalog/products/1/stock?quantity=50"  # → true (límite exacto)
```

#### **🟠 Escenario 4: Errores Avanzados**
```bash
curl "http://localhost:8080/api/catalog/products/1/stock?quantity=-1"   # → 400 Bad Request
curl "http://localhost:8080/api/catalog/products/999/stock?quantity=1"  # → false (no existe)
curl "http://localhost:8080/api/catalog/products/abc/stock?quantity=1"  # → false (ID inválido)
```

### **Guión de Narración:**
> "Ahora vamos a probar los escenarios críticos. Primero el flujo principal donde todo funciona correctamente. Luego casos de error cuando no hay stock suficiente. También casos límite como cantidad cero o stock exacto. Y finalmente errores avanzados con datos inválidos."

---

## 📊 **PARTE 5: Datos Mock y Lógica de Negocio** (1-2 minutos)

### **Mostrar Catálogo de Productos:**
```
📦 Catálogo TechTrend (15 productos):

🟢 DISPONIBLES:
• Laptop Ryzen 7 - $9,999.99 (50 unidades)
• Mouse Gaming - $299.99 (100 unidades)  
• Teclado Mecánico - $599.99 (25 unidades)
• Monitor 4K - $1,299.99 (15 unidades)
• ... (9 productos más)

🔴 AGOTADOS:
• Auriculares Bluetooth - $199.99 (0 unidades)
• Tablet Android 10" - $1,899.99 (0 unidades)

📈 Estadísticas:
• Total: 15 productos
• Disponibles: 13 (86.7%)
• Stock total: 470 unidades
• Valor inventario: ~$15,000,000
```

### **Guión de Narración:**
> "El sistema maneja un catálogo de 15 productos tecnológicos, desde laptops hasta componentes de PC. Solo los productos con stock mayor a cero aparecen en el catálogo público, implementando correctamente la lógica de negocio."

---

## 🔧 **PARTE 6: Herramientas de Desarrollo** (1 minuto)

### **Mostrar Herramientas:**
```
🛠️ Herramientas Utilizadas:

🤖 Kiro IDE:
• Exploración de archivos
• Ejecución de comandos  
• Análisis de código
• Generación de reportes

🔧 Maven:
• Gestión de dependencias
• Compilación automática
• Ejecución de tests
• Reportes de cobertura

📊 JaCoCo + SonarQube:
• Análisis de calidad de código
• Cobertura de pruebas
• Métricas de mantenibilidad
```

### **Guión de Narración:**
> "Para el desarrollo utilicé Kiro como IDE principal, que me permitió explorar el proyecto, ejecutar comandos y generar reportes automáticamente. Maven gestiona las dependencias y el build, mientras que JaCoCo y SonarQube aseguran la calidad del código."

---

## 🏆 **PARTE 7: Resultados y Conclusiones** (1 minuto)

### **Mostrar Métricas Finales:**
```
🎯 RESULTADOS FINALES:

✅ Estado: LISTO PARA PRODUCCIÓN
✅ Tests: 95/95 exitosos (100%)
✅ Cobertura: Completa en todas las clases
✅ Arquitectura: Hexagonal bien implementada
✅ Validaciones: Robustas y completas
✅ Documentación: Detallada y profesional

🚀 Casos de Uso Empresariales:
• Cliente verifica disponibilidad ✅
• Prevención de sobreventa ✅  
• Integración con otros microservicios ✅
• Experiencia de usuario fluida ✅
```

### **Guión de Narración:**
> "En conclusión, hemos implementado un microservicio robusto y completo. Con 95 pruebas exitosas, arquitectura sólida y validaciones exhaustivas, este sistema está listo para un entorno de producción real."

---

## 📝 **CONSEJOS PARA LA GRABACIÓN**

### **🎥 Aspectos Técnicos:**
- **Resolución**: 1080p mínimo
- **Duración por sección**: No más de 3 minutos
- **Transiciones**: Suaves entre secciones
- **Audio**: Claro y sin ruido de fondo

### **💡 Tips de Presentación:**
- **Ritmo**: Pausas entre conceptos importantes
- **Énfasis**: Destacar los números (95 tests, 100% éxito)
- **Demostraciones**: Mostrar comandos ejecutándose en tiempo real
- **Visuales**: Usar colores para diferenciar estados (✅❌⚠️)

### **🔍 Puntos Clave a Enfatizar:**
1. **Arquitectura profesional** (hexagonal + reactiva)
2. **Testing exhaustivo** (95 pruebas, todos los escenarios)
3. **Validaciones robustas** (casos límite y errores)
4. **Listo para producción** (calidad empresarial)
5. **Herramientas modernas** (Kiro, Spring Boot 3.2, Java 17)

---

## 📋 **CHECKLIST PRE-GRABACIÓN**

### **Preparación del Entorno:**
- [ ] Aplicación corriendo en `localhost:8080`
- [ ] Terminal con comandos curl preparados
- [ ] IDE abierto con código visible
- [ ] Reportes de testing generados
- [ ] Archivos README y TESTING-RESULTS abiertos

### **Comandos a Tener Listos:**
```bash
# Iniciar aplicación
mvn spring-boot:run

# Ejecutar tests
mvn clean test

# Comandos de demostración
curl http://localhost:8080/api/catalog/products
curl "http://localhost:8080/api/catalog/products/1/stock?quantity=10"
curl "http://localhost:8080/api/catalog/products/1/stock?quantity=60"
curl "http://localhost:8080/api/catalog/products/1/stock?quantity=-1"
```

### **Archivos a Mostrar:**
- [ ] `pom.xml` (dependencias)
- [ ] `Product.java` (entidad)
- [ ] `CatalogController.java` (endpoints)
- [ ] `CatalogServiceImpl.java` (lógica)
- [ ] `README.md` (documentación)
- [ ] `TESTING-RESULTS.md` (resultados)

---

**¡Éxito en tu grabación! 🎬✨**