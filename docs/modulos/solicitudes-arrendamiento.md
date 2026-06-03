# Módulo de solicitudes de arrendamiento

El módulo de solicitudes de arrendamiento permite que un arrendatario manifieste su interés por rentar un inmueble disponible dentro del sistema RentifyV2.

## Objetivo del módulo

El objetivo de este módulo es registrar la intención de renta de un arrendatario sobre una propiedad específica. A partir de esta solicitud, el arrendador puede revisar la información y decidir si acepta o rechaza la petición.

## Funcionalidades principales

* Consulta de inmuebles disponibles.
* Selección de un inmueble para renta.
* Registro de una solicitud de arrendamiento.
* Captura de mensaje o comentario del arrendatario.
* Consulta de solicitudes realizadas por el arrendatario.
* Consulta de solicitudes recibidas por el arrendador.
* Cambio de estado de la solicitud.
* Relación de la solicitud con un inmueble y un usuario arrendatario.

## Información manejada

El módulo contempla datos como:

* Identificador de la solicitud.
* Inmueble solicitado.
* Usuario arrendatario.
* Mensaje de la solicitud.
* Fecha de registro.
* Estado de la solicitud.

## Clases relacionadas

Algunas clases relacionadas con este módulo son:

* SolicitudArrendamientoDAO
* SolicitudTabla
* MisSolicitudesController
* SolicitudesArrendadorController
* ExplorarInmueblesController
* InmuebleDetalleController

## Flujo general

1. El arrendatario consulta los inmuebles disponibles.
2. Selecciona una propiedad de interés.
3. El sistema muestra el detalle del inmueble.
4. El arrendatario registra una solicitud de renta.
5. El arrendador consulta las solicitudes recibidas.
6. El arrendador acepta o rechaza la solicitud.
7. Si la solicitud es aceptada, puede continuar el proceso de arrendamiento.

## Relación con otros módulos

Este módulo se relaciona principalmente con:

* Inmuebles.
* Arrendatarios.
* Arrendadores.
* Arrendamientos.
* Contratos.

## Importancia dentro del sistema

Este módulo es importante porque representa el inicio formal del proceso de renta. Permite conectar al arrendatario con el arrendador mediante una solicitud registrada dentro del sistema.

## Relación con los requisitos del proyecto

Este módulo se relaciona principalmente con los siguientes requisitos funcionales:

* RF-16 Consultar propiedades disponibles.
* RF-17 Consultar detalle de propiedad.
* RF-18 Seleccionar propiedad para renta.
* RF-19 Registrar intención de renta.
