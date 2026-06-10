# ⚡ VoltRoom

<div align="center">

### Plataforma Inteligente para la Gestión de Edificios y Consumos Energéticos

Sistema web desarrollado con **Spring Boot**, **Angular** y **PostgreSQL** para la administración de inmuebles, usuarios, roles y monitoreo de consumos en edificios, condominios y residenciales.

</div>

---

## 📖 Descripción

VoltRoom es una plataforma moderna diseñada para optimizar la gestión de propiedades y el control de consumos energéticos. Permite administrar usuarios, inmuebles, lecturas de consumo y reportes, proporcionando una solución escalable, segura y eficiente para administradores y propietarios.

El proyecto implementa una arquitectura cliente-servidor utilizando tecnologías actuales del ecosistema Java y Angular, siguiendo buenas prácticas de desarrollo, seguridad y despliegue continuo.

---

## 🎯 Objetivos

- Centralizar la gestión de inmuebles y usuarios.
- Registrar y monitorear consumos energéticos.
- Facilitar la administración mediante roles y permisos.
- Generar información confiable para la toma de decisiones.
- Garantizar seguridad mediante autenticación JWT.
- Automatizar procesos de integración continua (CI/CD).

---

## 🚀 Tecnologías Utilizadas

### Backend

- Java 21
- Spring Boot 3.3
- Spring Security
- Spring Data JPA
- Hibernate
- JWT Authentication
- Maven
- Lombok
- Swagger / OpenAPI

### Frontend

- Angular
- TypeScript
- HTML5
- CSS3
- Bootstrap

### Base de Datos

- PostgreSQL
- H2 Database (Pruebas)

### DevOps

- Git
- GitHub
- GitHub Actions
- CI/CD

---

## 🏗 Arquitectura del Proyecto

```text
VOLTROOM
│
├── voltroom-backend
│   ├── config
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── enums
│   ├── repository
│   ├── security
│   ├── service
│   └── exception
│
├── voltroom-frontend
│   ├── src
│   ├── assets
│   ├── app
│   └── environments
│
└── .github
    └── workflows
        └── ci-cd.yml
```

---

## 🔐 Seguridad

El sistema incorpora mecanismos modernos de seguridad:

- Autenticación mediante JWT.
- Control de acceso basado en roles.
- Encriptación de contraseñas con BCrypt.
- Protección de rutas y endpoints.
- Configuración segura mediante variables de entorno.

---

## 👥 Gestión de Usuarios

- Registro de usuarios.
- Inicio de sesión seguro.
- Gestión de perfiles.
- Asignación de roles.
- Control de permisos.

---

## 🏢 Gestión de Inmuebles

- Registro de inmuebles.
- Actualización de información.
- Consulta de propiedades.
- Administración de propietarios.

---

## ⚡ Gestión de Consumos

- Registro de lecturas.
- Historial de consumos.
- Monitoreo de información energética.
- Seguimiento de registros.

---

## 📊 Reportes

- Consulta de consumos.
- Estadísticas generales.
- Información histórica.
- Reportes administrativos.

---

## ⚙️ Configuración del Backend

### Variables de Entorno

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/voltroom_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=********

JWT_SECRET=your_secret_key
JWT_EXPIRATION=3600000
```

### Ejecutar Backend

```bash
cd voltroom-backend

mvn clean install

mvn spring-boot:run
```

Servidor disponible en:

```text
http://localhost:8080
```

---

## 🎨 Configuración del Frontend

### Instalar Dependencias

```bash
cd voltroom-frontend

npm install
```

### Ejecutar Aplicación

```bash
ng serve
```

Disponible en:

```text
http://localhost:4200
```

---

## 📚 Documentación API

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI:

```text
http://localhost:8080/api-docs
```

---

## 🧪 Pruebas

### Backend

```bash
mvn test
```

### Frontend

```bash
npm test
```

---

## 🔄 Integración Continua

El proyecto utiliza GitHub Actions para automatizar:

- Compilación del Backend.
- Ejecución de pruebas.
- Construcción del Frontend.
- Validación de cambios mediante Pull Requests.

### Flujo CI/CD

```text
Push / Pull Request
         │
         ▼
 GitHub Actions
         │
 ┌───────┴────────┐
 │                │
 ▼                ▼
Backend        Frontend
 Maven          Angular
 Test           Build
 │                │
 └───────┬────────┘
         ▼
      Success
```

---

## 📌 Estado del Proyecto

| Módulo | Estado |
|---------|---------|
| Backend | ✅ |
| Frontend | ✅ |
| Seguridad JWT | ✅ |
| Swagger | ✅ |
| PostgreSQL | ✅ |
| CI/CD | ✅ |
| Testing | 🚧 |

---

## 👨‍💻 Equipo de Desarrollo

Proyecto desarrollado como parte de una iniciativa académica enfocada en la modernización de la gestión de edificios, condominios y monitoreo de consumos energéticos mediante tecnologías web modernas.

---

## 📄 Licencia

Este proyecto se distribuye bajo la licencia MIT.

```text
MIT License © 2026 VoltRoom
```

---

<div align="center">

### ⚡ VoltRoom - Smart Building Management Platform

Desarrollado con Spring Boot, React y PostgreSQL.

</div>