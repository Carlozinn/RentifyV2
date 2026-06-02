# RentifyV2

RentifyV2 es un sistema de gestión de rentas de inmuebles desarrollado en JavaFX.  
El sistema permite administrar usuarios, inmuebles, imágenes, solicitudes de renta, arrendamientos, contratos, pagos, incidencias, contactos y reportes.

## Objetivo del proyecto

El objetivo de RentifyV2 es facilitar la administración del proceso de renta de propiedades, permitiendo que arrendadores y arrendatarios interactúen dentro de un sistema centralizado.

## Tecnologías utilizadas

- Java
- JavaFX
- FXML
- JDBC
- PostgreSQL
- MySQL
- MariaDB
- IntelliJ IDEA
- Git y GitHub

## Roles del sistema

El sistema contempla tres tipos principales de usuario:

- Administrador
- Arrendador
- Arrendatario

## Módulos principales

- Usuarios
- Inmuebles
- Imágenes de inmuebles
- Solicitudes de renta
- Arrendamientos
- Contratos
- Pagos
- Incidencias
- Contactos
- Reportes

## Bases de datos soportadas

RentifyV2 cuenta con soporte para trabajar con distintos gestores de bases de datos:

- PostgreSQL
- MySQL
- MariaDB

## Estructura general del proyecto

```text
src/
 └── main/
     ├── java/
     │   └── com/rentify/
     │       ├── controller/
     │       ├── dao/
     │       ├── model/
     │       └── util/
     └── resources/
         └── fxml/