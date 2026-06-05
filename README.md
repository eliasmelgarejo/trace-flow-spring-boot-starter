## Licencia

Este proyecto está bajo la licencia **Creative Commons Atribución-NoComercial-SinDerivadas 4.0 Internacional (CC BY-NC-ND 4.0)**.

Puedes usar esta librería libremente, pero NO puedes:
- Usarla con fines comerciales
- Modificarla y distribuir la versión modificada
- Eliminar mi atribución como autor

**Sin garantías**: Esta librería se proporciona "TAL CUAL", sin garantías de ningún tipo.

Para más detalles, consulta el archivo [LICENSE](LICENSE).

[![License: CC BY-NC-ND 4.0](https://img.shields.io/badge/License-CC%20BY--NC--ND%204.0-lightgrey.svg)](https://creativecommons.org/licenses/by-nc-nd/4.0/)


# TraceFlow Spring Boot Starter

`trace-flow-spring-boot-starter` es una librería *Plug & Play* diseñada para proveer observabilidad y trazabilidad distribuida automática en arquitecturas de microservicios.

Esta librería se encarga de interceptar las peticiones HTTP entrantes, extraer el identificador de cliente (`JTI` - JWT ID) desde un token JWT de autenticación, e inyectarlo dinámicamente en el **Baggage de OpenTelemetry** y en las trazas generadas por **Micrometer Tracing**. 

Su principal beneficio es que permite a la plataforma analítica (ClickHouse + Grafana) medir el rendimiento y las tasas de error **por cliente o tenant**, propagando automáticamente el contexto a través de todos los microservicios *downstream* sin que el desarrollador tenga que modificar ni una sola línea de lógica de negocio.

---

## 🚀 Requisitos Previos

- **Java:** 25 o superior.
- **Spring Boot:** 4.0.0 o superior.
- **OpenTelemetry Collector:** El entorno donde corre la aplicación debe contar con un Collector de OTEL disponible (la configuración de exportación se hace vía variables de entorno estándar de OTEL).

---

## 📦 Instalación

Añade la siguiente dependencia en el archivo `pom.xml` de tu microservicio:

```xml
<dependency>
    <groupId>com.traceflow</groupId>
    <artifactId>trace-flow-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

Al ser un *Spring Boot Starter* con mecanismo de autoconfiguración (`AutoConfiguration.imports`), la librería se activará automáticamente con solo estar presente en el *classpath*.

---

## ⚙️ Configuración

El comportamiento de la librería puede ser ajustado mediante las siguientes propiedades en tu archivo `application.yml` o `application.properties`:

| Propiedad | Tipo | Valor por Defecto | Descripción |
| :--- | :--- | :--- | :--- |
| `traceflow.enabled` | Boolean | `true` | Permite habilitar o deshabilitar globalmente el filtro de trazabilidad de JTI. Ideal para entornos locales donde no se desee observabilidad. |
| `traceflow.auth-header` | String | `Authorization` | El nombre del encabezado HTTP del cual se debe intentar extraer el JWT o el JTI. |

### Ejemplo de `application.yml`

```yaml
traceflow:
  enabled: true
  auth-header: "Authorization"

# (Las configuraciones de OpenTelemetry nativas también aplican)
management:
  tracing:
    sampling:
      probability: 1.0
```

También debes configurar el exportador de OpenTelemetry usando variables de entorno o propiedades de Spring para que Micrometer sepa a dónde enviar las trazas:
```env
OTEL_SERVICE_NAME=mi-microservicio
OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4318
OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf
```

---

## 🧠 ¿Cómo Funciona?

1. **Intercepción HTTP:** El filtro autoconfigurado (`JtiBaggageFilter`) intercepta toda petición entrante al microservicio.
2. **Extracción del JTI:**
   - Si la cabecera `Authorization` contiene un JWT válido firmado, extrae el claim `jti`.
   - Si la cabecera contiene un texto plano (útil para pruebas locales como `Bearer jti-1234`), lo asume como el JTI.
   - Si la petición es *downstream* (originada desde otro microservicio), extrae el JTI que ya viene propagado en la cabecera estándar `baggage` del W3C.
3. **Inyección en OpenTelemetry:** El JTI recuperado se inyecta en el objeto `Span` activo como la etiqueta o atributo `jti`.
4. **Propagación Automática:** Se inserta el JTI en el contexto del **Baggage**. Cualquier cliente HTTP configurado en Spring Boot (como `RestTemplate` o `WebClient`) leerá este Baggage y lo enviará en sus futuras cabeceras HTTP hacia otros servicios.

---

## 💻 Ejemplo de Uso

¡No tienes que hacer nada en tu código!

```java
@RestController
public class MiControlador {

    private final RestTemplate restTemplate;

    public MiControlador(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/api/recurso")
    public String procesarRecurso() {
        // La librería ya registró el JTI en esta traza.
        
        // Al usar RestTemplate, el contexto de la traza y el JTI 
        // viajarán automáticamente hacia el otro microservicio.
        return restTemplate.getForObject("http://otro-servicio/api/data", String.class);
    }
}
```

---

## 🛠 Arquitectura y Dependencias Internas

El starter abstrae e integra de forma limpia las siguientes tecnologías subyacentes:
*   `spring-boot-starter-web`: Para exponer la inyección de Filtros de Servlets (`OncePerRequestFilter`).
*   `micrometer-tracing-bridge-otel`: Puente oficial de Spring Boot 4 para manejar las trazas (Spans) bajo el estándar de OpenTelemetry.
*   `opentelemetry-exporter-otlp`: Para la exportación de telemetría por red.
*   `nimbus-jose-jwt`: Para parsear de manera segura y eficiente los JWT sin requerir un servidor de validación OAuth activo.

---

## 📝 Licencia

Este proyecto es de uso interno exclusivo para la plataforma analítica TraceFlow.
