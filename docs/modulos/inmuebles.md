# Módulo de inmuebles

El módulo de inmuebles es una de las partes principales del sistema RentifyV2, ya que permite registrar, consultar, actualizar y administrar las propiedades disponibles para renta.

## Objetivo del módulo

El objetivo de este módulo es permitir que el arrendador pueda gestionar los inmuebles que desea poner en renta dentro del sistema. También permite que los arrendatarios puedan consultar la información de las propiedades disponibles.

## Funcionalidades principales

- Registro de inmuebles.
- Consulta de inmuebles registrados.
- Actualización de información de inmuebles.
- Eliminación de inmuebles.
- Consulta de inmuebles disponibles.
- Visualización del detalle de una propiedad.
- Asociación del inmueble con un arrendador.
- Registro de información básica como título, descripción, dirección, precio, superficie y características principales.

## Información manejada

El módulo contempla datos como:

- Título del inmueble.
- Descripción.
- Calle, número exterior, número interior y colonia.
- Ciudad, estado o provincia y código postal.
- Precio de renta.
- Superficie en metros cuadrados.
- Número de habitaciones.
- Número de baños.
- Número de estacionamientos.
- Indicación sobre mascotas permitidas.
- Estado del inmueble.
- Tipo de inmueble.
- Arrendador propietario del registro.

## Clases relacionadas

Algunas clases relacionadas con este módulo son:

- Inmueble
- InmuebleDAO
- InmueblesController
- InmuebleDetalleController
- InmuebleFormController

## Relación con otros módulos

El módulo de inmuebles se relaciona con otros módulos del sistema, como:

- Imágenes de inmuebles.
- Solicitudes de arrendamiento.
- Contratos.
- Arrendamientos.
- Incidencias.

## Importancia dentro del sistema

Este módulo es fundamental porque representa la base del funcionamiento de RentifyV2. A partir del registro de inmuebles se pueden generar solicitudes de renta, contratos y arrendamientos. Además, permite organizar la información de las propiedades de forma clara para arrendadores y arrendatarios.

## Relación con los requisitos del proyecto

Este módulo se relaciona principalmente con los siguientes requisitos funcionales:

- RF-08 Registrar propiedades.
- RF-09 Consultar propiedades.
- RF-10 Actualizar propiedades.
- RF-11 Eliminar propiedades.
- RF-16 Consultar propiedades disponibles.
- RF-17 Consultar detalle de propiedad.