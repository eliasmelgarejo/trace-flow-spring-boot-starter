# Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto se adhiere a [Semantic Versioning](https://semver.org/spec/v2.0.0.html) (Versionado Semántico).

---

## [1.0.2] - 2026-06-05

### Changed
- pom properties cambiado a 17 para retrocompatibilidad con Spring Boot 3

---

## [1.0.0] - 2026-06-04

### Agregado (Added)
- **Lanzamiento Inicial del Starter:** Creación del componente `trace-flow-spring-boot-starter` compatible con Java 25 y Spring Boot 4.0.0.
- **Autoconfiguración Inteligente:** Implementación de `TraceFlowAutoConfiguration` que carga la librería automáticamente sin que el desarrollador modifique código de configuración (mecanismo Plug & Play).
- **Intercepción y Propagación de JTI:** Creación de `JtiBaggageFilter` para capturar el `jti` desde JWTs (vía `nimbus-jose-jwt`) o texto plano y propagarlo automáticamente al contexto de OpenTelemetry mediante *W3C Baggage*.
- **Integración Nativa con Micrometer:** Configuración de compatibilidad con `micrometer-tracing-bridge-otel` para garantizar que las trazas se inserten y viajen usando el estándar de observabilidad de Spring Boot.
- **Propiedades de Entorno Personalizables:** Soporte para deshabilitar el rastreo dinámicamente (`traceflow.enabled`) o elegir de qué cabecera HTTP extraer los datos (`traceflow.auth-header`).
- **Distribución Segura:** El proyecto se preparó en su archivo `pom.xml` para ser publicado en repositorios privados utilizando GitHub Packages.
