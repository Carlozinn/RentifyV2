# Módulo de usuarios y roles

El módulo de usuarios y roles permite administrar las personas que acceden al sistema RentifyV2 y controlar las funciones disponibles para cada tipo de usuario.

## Objetivo del módulo

El objetivo de este módulo es permitir el registro, consulta y administración de usuarios dentro del sistema, asignando a cada uno un rol específico para definir sus permisos y funciones.

## Roles del sistema

El sistema contempla tres roles principales:

* Administrador.
* Arrendador.
* Arrendatario.

## Funcionalidades principales

* Registro de usuarios.
* Consulta de usuarios registrados.
* Administración de datos del usuario.
* Asignación de rol.
* Control de acceso según el tipo de usuario.
* Inicio de sesión.
* Identificación del usuario activo mediante la sesión.
* Separación de funciones por perfil de usuario.

## Información manejada

El módulo contempla datos como:

* Identificador del usuario.
* Nombre.
* Apellidos.
* Nombre de usuario.
* Contraseña protegida.
* Fecha de nacimiento.
* Rol asignado.
* Estado del usuario.

## Clases relacionadas

Algunas clases relacionadas con este módulo son:

* Usuario.
* Rol.
* UsuarioDAO.
* LoginController.
* AdministradorController.
* ArrendadorController.
* ArrendatarioController.
* Sesion.

## Flujo general

1. El usuario ingresa sus credenciales en la pantalla de inicio de sesión.
2. El sistema valida la información contra la base de datos.
3. Si las credenciales son correctas, se guarda la información del usuario activo en la sesión.
4. El sistema identifica el rol del usuario.
5. Según el rol, se muestra la vista correspondiente.
6. El usuario accede únicamente a las funciones relacionadas con su perfil.

## Relación con otros módulos

Este módulo se relaciona con prácticamente todo el sistema, ya que los usuarios participan en distintos procesos:

* Los arrendadores registran inmuebles.
* Los arrendatarios realizan solicitudes de renta.
* Los administradores gestionan información general del sistema.
* Los usuarios pueden tener contactos asociados.
* Los usuarios participan en contratos, pagos e incidencias.

## Importancia dentro del sistema

Este módulo es importante porque permite controlar quién accede al sistema y qué acciones puede realizar. Además, ayuda a mantener organizada la información de los usuarios y permite separar las funciones según el rol correspondiente.

## Relación con los requisitos del proyecto

Este módulo se relaciona principalmente con los siguientes requisitos funcionales:

* RF-03 Administrar arrendadores.
* RF-04 Administrar arrendatarios.
* RF-05 Administrar contactos.

También se relaciona con requisitos no funcionales de seguridad, ya que permite controlar el acceso al sistema mediante autenticación y roles.
