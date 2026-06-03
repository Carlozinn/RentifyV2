# Módulo de arrendamientos

El módulo de arrendamientos representa el proceso en el que una solicitud aceptada se convierte en una relación formal de renta entre un arrendador y un arrendatario dentro del sistema RentifyV2.

## Objetivo del módulo

El objetivo de este módulo es registrar y administrar los arrendamientos generados a partir de solicitudes de renta aceptadas. Permite llevar el control de la propiedad rentada, las partes involucradas, las fechas del periodo de renta y el estado del arrendamiento.

## Funcionalidades principales

- Generación de un arrendamiento a partir de una solicitud aceptada.
- Registro del inmueble relacionado con el arrendamiento.
- Asociación del arrendador y arrendatario.
- Registro de fecha de inicio y fecha de finalización.
- Consulta de arrendamientos activos.
- Control del estado del arrendamiento.
- Relación con contratos, pagos e incidencias.

## Información manejada

El módulo contempla datos como:

- Identificador del arrendamiento.
- Inmueble rentado.
- Arrendador.
- Arrendatario.
- Solicitud relacionada.
- Fecha de inicio.
- Fecha de finalización.
- Estado del arrendamiento.
- Monto de renta acordado.

## Clases relacionadas

Algunas clases relacionadas con este módulo son:

- ArrendamientosController
- ArrendamientoComboItem
- SolicitudArrendamientoDAO
- ContratoFormController
- IncidenciaFormController
- PagosController

## Flujo general

1. El arrendatario realiza una solicitud sobre un inmueble disponible.
2. El arrendador revisa la solicitud recibida.
3. Si la solicitud es aceptada, el sistema puede generar un arrendamiento.
4. El arrendamiento queda asociado al inmueble, arrendador y arrendatario.
5. A partir del arrendamiento se pueden generar contratos, pagos e incidencias.
6. El sistema permite consultar los arrendamientos activos.

## Relación con otros módulos

Este módulo se relaciona principalmente con:

- Solicitudes de arrendamiento.
- Inmuebles.
- Contratos.
- Pagos.
- Incidencias.
- Usuarios.

## Importancia dentro del sistema

Este módulo es importante porque representa la unión formal entre la solicitud de renta y los procesos posteriores del sistema. A partir del arrendamiento se pueden controlar contratos, pagos e incidencias relacionadas con una propiedad rentada.

## Relación con los requisitos del proyecto

Este módulo se relaciona con el proceso de selección de propiedad para renta, la intención de renta y la generación posterior del contrato.