# Módulo de imágenes de inmuebles

El módulo de imágenes de inmuebles permite asociar fotografías a las propiedades registradas dentro del sistema RentifyV2.

## Objetivo del módulo

El objetivo de este módulo es permitir que los inmuebles cuenten con imágenes que ayuden a mostrar mejor la propiedad al usuario. Esto facilita que el arrendatario pueda visualizar el inmueble antes de realizar una solicitud de renta.

## Funcionalidades principales

* Selección de imágenes desde el equipo.
* Guardado físico de la imagen dentro del proyecto.
* Registro de la ruta de la imagen en la base de datos.
* Asociación de imágenes con un inmueble específico.
* Indicación de una imagen principal para el inmueble.
* Visualización de la imagen principal en el detalle del inmueble.
* Organización de imágenes mediante un orden de visualización.

## Información manejada

El módulo contempla datos como:

* Identificador de la imagen.
* Ruta o URL de la imagen.
* Descripción de la imagen.
* Indicador de imagen principal.
* Orden de visualización.
* Identificador del inmueble relacionado.

## Clases relacionadas

Algunas clases relacionadas con este módulo son:

* ImagenInmueble
* ImagenInmuebleDAO
* InmuebleDetalleController
* InmuebleFormController
* InmueblesController

## Relación con otros módulos

Este módulo se relaciona principalmente con el módulo de inmuebles, ya que cada imagen pertenece a una propiedad registrada. También se relaciona con la consulta de detalle del inmueble, porque ahí se muestra la imagen principal.

## Importancia dentro del sistema

Este módulo mejora la presentación de las propiedades dentro de RentifyV2. Las imágenes permiten que los usuarios puedan identificar visualmente los inmuebles disponibles, haciendo más clara la consulta de propiedades.

## Relación con los requisitos del proyecto

Este módulo se relaciona principalmente con el siguiente requisito funcional:

* RF-12 Gestionar imágenes de propiedades.
