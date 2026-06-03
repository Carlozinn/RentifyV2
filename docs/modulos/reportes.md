# Módulo de reportes

El módulo de reportes permite consultar información resumida del sistema RentifyV2, con el objetivo de apoyar la revisión de datos importantes relacionados con inmuebles, solicitudes, arrendamientos, contratos, pagos e incidencias.

## Objetivo del módulo

El objetivo de este módulo es presentar información organizada que ayude a los usuarios del sistema a consultar datos relevantes de manera más clara. Los reportes pueden apoyar principalmente al administrador y al arrendador para revisar el comportamiento general del sistema.

## Funcionalidades principales

* Consulta de información general del sistema.
* Revisión de inmuebles registrados.
* Consulta de solicitudes de arrendamiento.
* Revisión de arrendamientos activos.
* Consulta de contratos generados.
* Consulta de pagos registrados.
* Consulta de incidencias reportadas.
* Apoyo para la toma de decisiones dentro del sistema.

## Información manejada

El módulo puede contemplar datos como:

* Total de inmuebles registrados.
* Inmuebles disponibles.
* Solicitudes realizadas.
* Solicitudes aceptadas o rechazadas.
* Arrendamientos activos.
* Contratos generados.
* Pagos registrados.
* Incidencias pendientes o resueltas.

## Clases relacionadas

Algunas clases que pueden relacionarse con este módulo son:

* ReportesController
* InmuebleDAO
* SolicitudArrendamientoDAO
* ContratoDAO
* IncidenciaDAO
* PagoDAO

## Relación con otros módulos

Este módulo se relaciona con distintas partes del sistema, ya que los reportes se generan a partir de información registrada en otros módulos:

* Usuarios.
* Inmuebles.
* Solicitudes de arrendamiento.
* Arrendamientos.
* Contratos.
* Pagos.
* Incidencias.

## Importancia dentro del sistema

Este módulo es importante porque permite consultar información resumida y organizada. Además, ayuda a tener una mejor visión del funcionamiento del sistema y de los procesos principales relacionados con la renta de inmuebles.

## Relación con los requisitos del proyecto

Este módulo complementa la administración general del sistema, ya que permite consultar información relevante sobre los procesos de renta, contratos, pagos e incidencias.
