# Guía del repositorio RentifyV2

Este documento describe la organización general del repositorio RentifyV2 y el uso de Git y GitHub durante el desarrollo del proyecto.

## Organización del repositorio

El repositorio se organizó separando el código fuente, los scripts de base de datos y la documentación del proyecto.

## Estructura principal

```text
RentifyV2/
├── src/
├── database/
├── docs/
├── README.md
├── .gitignore
└── pom.xml
```

## Código fuente

El código fuente del sistema se encuentra dentro de la carpeta `src`.

La aplicación está desarrollada en JavaFX y organizada principalmente por paquetes:

```text
com.rentify.controller
com.rentify.dao
com.rentify.model
com.rentify.util
```

## Scripts de base de datos

Los scripts se organizaron dentro de la carpeta `database`, separando cada gestor utilizado:

```text
database/
├── postgresql/
├── mysql/
└── mariadb/
```

## Documentación

La documentación del proyecto se encuentra dentro de la carpeta `docs`.

Dentro de esta carpeta se agregaron documentos relacionados con los módulos principales del sistema, como:

- Usuarios y roles.
- Inmuebles.
- Imágenes de inmuebles.
- Solicitudes de arrendamiento.
- Arrendamientos.
- Contratos.
- Pagos.
- Incidencias.
- Contactos.
- Reportes.
- Quejas y sugerencias.
- Soporte multibase de datos.

## Ramas principales

Durante el desarrollo se utilizaron ramas para separar avances y mantener un mejor control de versiones.

Las ramas principales fueron:

- `main`: versión principal y estable del proyecto.
- `develop`: rama de desarrollo donde se integraron los avances antes de pasarlos a `main`.

## Ramas de funcionalidad y documentación

También se utilizaron ramas específicas para documentar o trabajar partes del sistema:

- `feature/database-scripts`
- `feature/documentacion`
- `feature/modulo-inmuebles`
- `feature/imagenes-inmuebles`
- `feature/solicitudes-arrendamiento`
- `feature/arrendamientos`
- `feature/contratos`
- `feature/pagos`
- `feature/incidencias`
- `feature/contactos`
- `feature/usuarios-roles`
- `feature/reportes`
- `feature/quejas-sugerencias`
- `feature/multibase-datos`
- `feature/documentacion-final`

## Flujo de trabajo utilizado

El flujo de trabajo con Git y GitHub fue el siguiente:

1. Se actualizó la rama `develop`.
2. Se creó una rama nueva a partir de `develop`.
3. Se realizaron cambios relacionados con un módulo específico.
4. Se hizo commit con un mensaje descriptivo.
5. Se subió la rama al repositorio remoto en GitHub.
6. Se creó un Pull Request hacia `develop`.
7. Se integró el cambio mediante merge.
8. Se actualizó nuevamente la rama `develop` local.

## Ejemplo de comandos utilizados

```bash
git checkout develop
git pull
git checkout -b feature/nombre-modulo
git status
git add .
git commit -m "Mensaje descriptivo del cambio"
git push -u origin feature/nombre-modulo
```

Después de subir la rama, el cambio se integró mediante Pull Request en GitHub.

## Importancia del control de versiones

El uso de Git permitió llevar un historial de cambios del proyecto, organizar el trabajo por módulos y mantener evidencia del avance realizado.

GitHub permitió visualizar ramas, commits y Pull Requests, lo cual sirve como respaldo del proceso de desarrollo y como evidencia para la documentación académica del proyecto.

## Estado final

Al finalizar la integración de los módulos documentados, la rama `develop` contiene los avances organizados del proyecto. Posteriormente, estos cambios se integran a `main` para dejar una versión estable del repositorio.