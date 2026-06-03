# Módulo de incidencias

El módulo de incidencias permite registrar, consultar y dar seguimiento a problemas o reportes relacionados con un arrendamiento dentro del sistema RentifyV2.

## Objetivo del módulo

El objetivo principal de este módulo es permitir que el arrendatario pueda reportar una situación relacionada con el inmueble rentado, y que el arrendador pueda revisar dicha incidencia y registrar una solución.

## Funcionalidades principales

* Registro de incidencias por parte del arrendatario.
* Selección del arrendamiento relacionado con la incidencia.
* Captura de título, descripción y prioridad del reporte.
* Clasificación de la prioridad como baja, media o alta.
* Consulta de incidencias registradas.
* Resolución de incidencias por parte del arrendador.
* Registro de la solución aplicada.
* Actualización del estado de la incidencia.

## Clases relacionadas

Algunas clases relacionadas con este módulo son:

* IncidenciasController
* IncidenciaFormController
* IncidenciaResolverController
* IncidenciaDAO
* IncidenciaTabla
* ArrendamientoComboItem

## Flujo general

1. El arrendatario ingresa al módulo de incidencias.
2. Selecciona un arrendamiento activo.
3. Registra el título, descripción y prioridad de la incidencia.
4. El sistema guarda el reporte en la base de datos.
5. El arrendador consulta las incidencias registradas.
6. El arrendador registra una solución.
7. El sistema actualiza el estado de la incidencia.

## Importancia dentro del sistema

Este módulo es importante porque permite dar seguimiento a problemas que pueden presentarse durante el periodo de renta. Además, ayuda a mantener comunicación entre el arrendatario y el arrendador, dejando evidencia de los reportes realizados y de las soluciones aplicadas.

## Relación con los requisitos del proyecto

Este módulo se relaciona principalmente con la gestión de incidencias, quejas o reportes dentro del proceso de arrendamiento. También aporta al cumplimiento de requisitos no funcionales como la organización clara por módulos y el uso de mensajes comprensibles para el usuario.
