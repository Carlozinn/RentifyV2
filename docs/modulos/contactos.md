# Módulo de contactos

El módulo de contactos permite registrar y administrar medios de contacto asociados a los usuarios dentro del sistema RentifyV2.

## Objetivo del módulo

El objetivo de este módulo es permitir que los usuarios puedan tener información de contacto registrada dentro del sistema, facilitando la comunicación entre arrendadores y arrendatarios.

## Funcionalidades principales

- Registro de contactos.
- Consulta de contactos registrados.
- Actualización de información de contacto.
- Eliminación de contactos.
- Validación de formato de correo electrónico.
- Identificación de contacto principal.
- Confirmación antes de eliminar un contacto marcado como principal.
- Asociación del contacto con un usuario.

## Información manejada

El módulo contempla datos como:

- Identificador del contacto.
- Usuario relacionado.
- Medio de contacto.
- Valor del contacto, como correo o teléfono.
- Indicador de contacto principal.
- Estado del contacto.

## Clases relacionadas

Algunas clases relacionadas con este módulo son:

- ContactosController
- ContactoFormController
- ContactoDAO
- ContactoTabla
- Usuario

## Flujo general

1. El usuario ingresa al módulo de contactos.
2. El sistema muestra los contactos registrados.
3. El usuario puede registrar un nuevo contacto.
4. El sistema valida los datos ingresados.
5. Si el contacto es válido, se guarda en la base de datos.
6. El usuario puede actualizar o eliminar contactos existentes.
7. Si se intenta eliminar un contacto principal, el sistema solicita confirmación.

## Relación con otros módulos

Este módulo se relaciona principalmente con:

- Usuarios.
- Arrendadores.
- Arrendatarios.
- Solicitudes de arrendamiento.
- Contratos.

## Importancia dentro del sistema

Este módulo es importante porque permite mantener información de comunicación entre los usuarios del sistema. Además, ayuda a que el proceso de arrendamiento tenga datos de contacto organizados y validados.

## Relación con los requisitos del proyecto

Este módulo se relaciona principalmente con los siguientes requisitos funcionales:

- RF-05 Administrar contactos.
- RF-06 Administrar medios de contacto.