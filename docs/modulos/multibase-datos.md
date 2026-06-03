# Soporte multibase de datos

El proyecto RentifyV2 cuenta con soporte para trabajar con distintos gestores de bases de datos, principalmente PostgreSQL, MySQL y MariaDB.

## Objetivo

El objetivo de esta parte del proyecto es permitir que el sistema pueda adaptarse a diferentes gestores de base de datos, manteniendo la misma lógica general de funcionamiento.

## Gestores considerados

- PostgreSQL
- MySQL
- MariaDB

## Importancia dentro del proyecto

El soporte para varios gestores de base de datos permite que RentifyV2 sea más flexible. Esto facilita que el sistema pueda ejecutarse en diferentes entornos, dependiendo del gestor disponible o solicitado.

## Organización de scripts

Los scripts de base de datos se organizaron dentro de la carpeta `database`, separando cada gestor en su propia carpeta:

```text
database/
├── postgresql/
├── mysql/
└── mariadb/
´´´´ 
