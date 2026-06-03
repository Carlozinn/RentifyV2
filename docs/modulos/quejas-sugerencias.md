# Módulo de quejas y sugerencias

El módulo de quejas y sugerencias permite registrar comentarios, inconformidades o recomendaciones relacionadas con el uso del sistema RentifyV2.

## Objetivo del módulo

El objetivo de este módulo es permitir que los usuarios puedan enviar reportes generales sobre el sistema, sugerencias de mejora o quejas relacionadas con el servicio. Esto ayuda a mantener un canal de comunicación entre los usuarios y la administración del sistema.

## Funcionalidades principales

- Registro de quejas.
- Registro de sugerencias.
- Consulta de reportes enviados.
- Clasificación del tipo de reporte.
- Registro de fecha del reporte.
- Asociación del reporte con un usuario.
- Consulta por parte del administrador.
- Actualización del estado del reporte.

## Información manejada

El módulo puede contemplar datos como:

- Identificador del reporte.
- Usuario que registra la queja o sugerencia.
- Tipo de reporte.
- Descripción.
- Fecha de registro.
- Estado del reporte.

## Clases relacionadas

Algunas clases que pueden relacionarse con este módulo son:

- QuejasSugerenciasController
- QuejaSugerenciaDAO
- QuejaSugerencia
- Usuario

## Flujo general

1. El usuario ingresa al módulo de quejas y sugerencias.
2. Selecciona el tipo de reporte.
3. Escribe la descripción de la queja o sugerencia.
4. El sistema guarda el reporte.
5. El administrador consulta los reportes registrados.
6. El administrador puede revisar o actualizar el estado del reporte.

## Relación con otros módulos

Este módulo se relaciona principalmente con:

- Usuarios.
- Administrador.
- Sesión.

## Importancia dentro del sistema

Este módulo es importante porque permite recibir retroalimentación de los usuarios. Además, ayuda a detectar problemas o áreas de mejora dentro del sistema RentifyV2.

## Relación con los requisitos del proyecto

Este módulo se relaciona principalmente con el siguiente requisito funcional:

- RF-07 Gestionar quejas y sugerencias.