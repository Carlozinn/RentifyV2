# Scripts PostgreSQL

En esta carpeta se almacenan los scripts relacionados con la base de datos PostgreSQL utilizada por RentifyV2.


-- =========================================================
-- POSTGRESQL - RENTIFY
-- BASE DE DATOS + TABLAS + ÍNDICES + CATÁLOGOS
-- =========================================================

CREATE DATABASE "RENTIFY";

\connect "RENTIFY";

-- =========================================================
-- TABLAS CATÁLOGO
-- =========================================================

CREATE TABLE rol (
id_rol INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
nombre_rol VARCHAR(50) NOT NULL,
descripcion VARCHAR(150),
CONSTRAINT uq_rol_nombre UNIQUE (nombre_rol),
CONSTRAINT chk_rol_nombre_no_vacio
CHECK (LENGTH(TRIM(nombre_rol)) > 0)
);

CREATE TABLE estado_usuario (
id_estado_usuario INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
nombre_estado VARCHAR(30) NOT NULL,
CONSTRAINT uq_estado_usuario_nombre UNIQUE (nombre_estado),
CONSTRAINT chk_estado_usuario_nombre_no_vacio
CHECK (LENGTH(TRIM(nombre_estado)) > 0)
);

CREATE TABLE tipo_contacto (
id_tipo_contacto INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
nombre_tipo VARCHAR(50) NOT NULL,
CONSTRAINT uq_tipo_contacto_nombre UNIQUE (nombre_tipo),
CONSTRAINT chk_tipo_contacto_nombre_no_vacio
CHECK (LENGTH(TRIM(nombre_tipo)) > 0)
);

CREATE TABLE tipo_inmueble (
id_tipo_inmueble INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
nombre_tipo VARCHAR(50) NOT NULL,
descripcion VARCHAR(150),
CONSTRAINT uq_tipo_inmueble_nombre UNIQUE (nombre_tipo),
CONSTRAINT chk_tipo_inmueble_nombre_no_vacio
CHECK (LENGTH(TRIM(nombre_tipo)) > 0)
);

CREATE TABLE estado_inmueble (
id_estado_inmueble INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
nombre_estado VARCHAR(30) NOT NULL,
CONSTRAINT uq_estado_inmueble_nombre UNIQUE (nombre_estado),
CONSTRAINT chk_estado_inmueble_nombre_no_vacio
CHECK (LENGTH(TRIM(nombre_estado)) > 0)
);

CREATE TABLE estado_solicitud (
id_estado_solicitud INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
nombre_estado VARCHAR(30) NOT NULL,
CONSTRAINT uq_estado_solicitud_nombre UNIQUE (nombre_estado),
CONSTRAINT chk_estado_solicitud_nombre_no_vacio
CHECK (LENGTH(TRIM(nombre_estado)) > 0)
);

CREATE TABLE estado_arrendamiento (
id_estado_arrendamiento INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
nombre_estado VARCHAR(30) NOT NULL,
CONSTRAINT uq_estado_arrendamiento_nombre UNIQUE (nombre_estado),
CONSTRAINT chk_estado_arrendamiento_nombre_no_vacio
CHECK (LENGTH(TRIM(nombre_estado)) > 0)
);

CREATE TABLE estado_contrato (
id_estado_contrato INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
nombre_estado VARCHAR(20) NOT NULL,
CONSTRAINT uq_estado_contrato_nombre UNIQUE (nombre_estado),
CONSTRAINT chk_estado_contrato_nombre_no_vacio
CHECK (LENGTH(TRIM(nombre_estado)) > 0)
);

CREATE TABLE metodo_pago (
id_metodo_pago INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
nombre_metodo VARCHAR(40) NOT NULL,
CONSTRAINT uq_metodo_pago_nombre UNIQUE (nombre_metodo),
CONSTRAINT chk_metodo_pago_nombre_no_vacio
CHECK (LENGTH(TRIM(nombre_metodo)) > 0)
);

CREATE TABLE estado_pago (
id_estado_pago INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
nombre_estado VARCHAR(20) NOT NULL,
CONSTRAINT uq_estado_pago_nombre UNIQUE (nombre_estado),
CONSTRAINT chk_estado_pago_nombre_no_vacio
CHECK (LENGTH(TRIM(nombre_estado)) > 0)
);

CREATE TABLE estado_incidencia (
id_estado_incidencia INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
nombre_estado VARCHAR(30) NOT NULL,
CONSTRAINT uq_estado_incidencia_nombre UNIQUE (nombre_estado),
CONSTRAINT chk_estado_incidencia_nombre_no_vacio
CHECK (LENGTH(TRIM(nombre_estado)) > 0)
);

CREATE TABLE prioridad_incidencia (
id_prioridad_incidencia INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
nombre_prioridad VARCHAR(20) NOT NULL,
CONSTRAINT uq_prioridad_incidencia_nombre UNIQUE (nombre_prioridad),
CONSTRAINT chk_prioridad_incidencia_nombre_no_vacio
CHECK (LENGTH(TRIM(nombre_prioridad)) > 0)
);

-- =========================================================
-- TABLAS PRINCIPALES
-- =========================================================

CREATE TABLE usuario (
id_usuario INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
nombre VARCHAR(80) NOT NULL,
apellido_paterno VARCHAR(80) NOT NULL,
apellido_materno VARCHAR(80),
username VARCHAR(50) NOT NULL,
password_hash VARCHAR(255) NOT NULL,
fecha_nacimiento DATE,
fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
id_rol INTEGER NOT NULL,
id_estado_usuario INTEGER NOT NULL,

    CONSTRAINT uq_usuario_username UNIQUE (username),

    CONSTRAINT chk_usuario_nombre_no_vacio
        CHECK (LENGTH(TRIM(nombre)) > 0),

    CONSTRAINT chk_usuario_apellido_paterno_no_vacio
        CHECK (LENGTH(TRIM(apellido_paterno)) > 0),

    CONSTRAINT chk_usuario_apellido_materno_no_vacio
        CHECK (apellido_materno IS NULL OR LENGTH(TRIM(apellido_materno)) > 0),

    CONSTRAINT chk_usuario_username_no_vacio
        CHECK (LENGTH(TRIM(username)) > 0),

    CONSTRAINT chk_usuario_password_no_vacio
        CHECK (LENGTH(TRIM(password_hash)) > 0),

    CONSTRAINT chk_usuario_fecha_nacimiento_valida
        CHECK (fecha_nacimiento IS NULL OR fecha_nacimiento <= CURRENT_DATE),

    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol) REFERENCES rol(id_rol),

    CONSTRAINT fk_usuario_estado
        FOREIGN KEY (id_estado_usuario) REFERENCES estado_usuario(id_estado_usuario)
);

CREATE TABLE contacto (
id_contacto INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
valor_contacto VARCHAR(120) NOT NULL,
principal BOOLEAN NOT NULL DEFAULT FALSE,
verificado BOOLEAN NOT NULL DEFAULT FALSE,
id_tipo_contacto INTEGER NOT NULL,
id_usuario INTEGER NOT NULL,

    CONSTRAINT uq_contacto_usuario_valor UNIQUE (id_usuario, valor_contacto),

    CONSTRAINT chk_contacto_valor_no_vacio
        CHECK (LENGTH(TRIM(valor_contacto)) > 0),

    CONSTRAINT fk_contacto_tipo
        FOREIGN KEY (id_tipo_contacto) REFERENCES tipo_contacto(id_tipo_contacto),

    CONSTRAINT fk_contacto_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE inmueble (
id_inmueble INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
titulo VARCHAR(100) NOT NULL,
descripcion TEXT,
calle VARCHAR(100) NOT NULL,
numero_exterior VARCHAR(20) NOT NULL,
numero_interior VARCHAR(20),
colonia VARCHAR(80) NOT NULL,
ciudad VARCHAR(80) NOT NULL,
estado_provincia VARCHAR(80) NOT NULL,
codigo_postal VARCHAR(10) NOT NULL,
precio_renta NUMERIC(10,2) NOT NULL,
superficie_m2 NUMERIC(10,2),
habitaciones INTEGER,
banos NUMERIC(3,1),
estacionamientos INTEGER,
mascotas_permitidas BOOLEAN NOT NULL DEFAULT FALSE,
fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
id_usuario_arrendador INTEGER NOT NULL,
id_tipo_inmueble INTEGER NOT NULL,
id_estado_inmueble INTEGER NOT NULL,

    CONSTRAINT chk_inmueble_titulo_no_vacio
        CHECK (LENGTH(TRIM(titulo)) > 0),

    CONSTRAINT chk_inmueble_descripcion_no_vacia
        CHECK (descripcion IS NULL OR LENGTH(TRIM(descripcion)) > 0),

    CONSTRAINT chk_inmueble_calle_no_vacia
        CHECK (LENGTH(TRIM(calle)) > 0),

    CONSTRAINT chk_inmueble_numero_exterior_no_vacio
        CHECK (LENGTH(TRIM(numero_exterior)) > 0),

    CONSTRAINT chk_inmueble_numero_interior_no_vacio
        CHECK (numero_interior IS NULL OR LENGTH(TRIM(numero_interior)) > 0),

    CONSTRAINT chk_inmueble_colonia_no_vacia
        CHECK (LENGTH(TRIM(colonia)) > 0),

    CONSTRAINT chk_inmueble_ciudad_no_vacia
        CHECK (LENGTH(TRIM(ciudad)) > 0),

    CONSTRAINT chk_inmueble_estado_provincia_no_vacio
        CHECK (LENGTH(TRIM(estado_provincia)) > 0),

    CONSTRAINT chk_inmueble_codigo_postal_no_vacio
        CHECK (LENGTH(TRIM(codigo_postal)) > 0),

    CONSTRAINT chk_inmueble_precio_renta
        CHECK (precio_renta > 0),

    CONSTRAINT chk_inmueble_superficie
        CHECK (superficie_m2 IS NULL OR superficie_m2 >= 0),

    CONSTRAINT chk_inmueble_habitaciones
        CHECK (habitaciones IS NULL OR habitaciones >= 0),

    CONSTRAINT chk_inmueble_banos
        CHECK (banos IS NULL OR banos >= 0),

    CONSTRAINT chk_inmueble_estacionamientos
        CHECK (estacionamientos IS NULL OR estacionamientos >= 0),

    CONSTRAINT fk_inmueble_usuario_arrendador
        FOREIGN KEY (id_usuario_arrendador) REFERENCES usuario(id_usuario),

    CONSTRAINT fk_inmueble_tipo
        FOREIGN KEY (id_tipo_inmueble) REFERENCES tipo_inmueble(id_tipo_inmueble),

    CONSTRAINT fk_inmueble_estado
        FOREIGN KEY (id_estado_inmueble) REFERENCES estado_inmueble(id_estado_inmueble)
);

CREATE TABLE imagen_inmueble (
id_imagen INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
url_imagen VARCHAR(255) NOT NULL,
descripcion VARCHAR(150),
es_principal BOOLEAN NOT NULL DEFAULT FALSE,
orden_visualizacion INTEGER,
id_inmueble INTEGER NOT NULL,

    CONSTRAINT chk_imagen_url_no_vacia
        CHECK (LENGTH(TRIM(url_imagen)) > 0),

    CONSTRAINT chk_imagen_descripcion_no_vacia
        CHECK (descripcion IS NULL OR LENGTH(TRIM(descripcion)) > 0),

    CONSTRAINT chk_imagen_orden_visualizacion
        CHECK (orden_visualizacion IS NULL OR orden_visualizacion >= 1),

    CONSTRAINT fk_imagen_inmueble
        FOREIGN KEY (id_inmueble) REFERENCES inmueble(id_inmueble)
);

CREATE TABLE solicitud_arrendamiento (
id_solicitud INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
fecha_solicitud TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
mensaje VARCHAR(255),
fecha_respuesta TIMESTAMP,
id_usuario_arrendatario INTEGER NOT NULL,
id_inmueble INTEGER NOT NULL,
id_estado_solicitud INTEGER NOT NULL,

    CONSTRAINT chk_solicitud_mensaje_no_vacio
        CHECK (mensaje IS NULL OR LENGTH(TRIM(mensaje)) > 0),

    CONSTRAINT chk_solicitud_fechas
        CHECK (fecha_respuesta IS NULL OR fecha_respuesta >= fecha_solicitud),

    CONSTRAINT fk_solicitud_usuario_arrendatario
        FOREIGN KEY (id_usuario_arrendatario) REFERENCES usuario(id_usuario),

    CONSTRAINT fk_solicitud_inmueble
        FOREIGN KEY (id_inmueble) REFERENCES inmueble(id_inmueble),

    CONSTRAINT fk_solicitud_estado
        FOREIGN KEY (id_estado_solicitud) REFERENCES estado_solicitud(id_estado_solicitud)
);

CREATE TABLE arrendamiento (
id_arrendamiento INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
fecha_inicio DATE NOT NULL,
fecha_fin DATE,
monto_mensual NUMERIC(10,2) NOT NULL,
deposito_garantia NUMERIC(10,2),
dia_pago INTEGER,
observaciones VARCHAR(255),
id_inmueble INTEGER NOT NULL,
id_usuario_arrendador INTEGER NOT NULL,
id_usuario_arrendatario INTEGER NOT NULL,
id_estado_arrendamiento INTEGER NOT NULL,
id_solicitud INTEGER,

    CONSTRAINT chk_arrendamiento_fechas
        CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio),

    CONSTRAINT chk_arrendamiento_monto_mensual
        CHECK (monto_mensual > 0),

    CONSTRAINT chk_arrendamiento_deposito_garantia
        CHECK (deposito_garantia IS NULL OR deposito_garantia >= 0),

    CONSTRAINT chk_arrendamiento_dia_pago
        CHECK (dia_pago IS NULL OR dia_pago BETWEEN 1 AND 31),

    CONSTRAINT chk_arrendamiento_observaciones_no_vacias
        CHECK (observaciones IS NULL OR LENGTH(TRIM(observaciones)) > 0),

    CONSTRAINT fk_arrendamiento_inmueble
        FOREIGN KEY (id_inmueble) REFERENCES inmueble(id_inmueble),

    CONSTRAINT fk_arrendamiento_usuario_arrendador
        FOREIGN KEY (id_usuario_arrendador) REFERENCES usuario(id_usuario),

    CONSTRAINT fk_arrendamiento_usuario_arrendatario
        FOREIGN KEY (id_usuario_arrendatario) REFERENCES usuario(id_usuario),

    CONSTRAINT fk_arrendamiento_estado
        FOREIGN KEY (id_estado_arrendamiento) REFERENCES estado_arrendamiento(id_estado_arrendamiento),

    CONSTRAINT fk_arrendamiento_solicitud
        FOREIGN KEY (id_solicitud) REFERENCES solicitud_arrendamiento(id_solicitud)
);

CREATE TABLE contrato (
id_contrato INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
folio_contrato VARCHAR(30) NOT NULL,
fecha_generacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
fecha_firma TIMESTAMP,
archivo_pdf VARCHAR(255),
id_estado_contrato INTEGER NOT NULL,
id_arrendamiento INTEGER NOT NULL,

    CONSTRAINT uq_contrato_folio UNIQUE (folio_contrato),
    CONSTRAINT uq_contrato_arrendamiento UNIQUE (id_arrendamiento),

    CONSTRAINT chk_contrato_folio_no_vacio
        CHECK (LENGTH(TRIM(folio_contrato)) > 0),

    CONSTRAINT chk_contrato_archivo_pdf_no_vacio
        CHECK (archivo_pdf IS NULL OR LENGTH(TRIM(archivo_pdf)) > 0),

    CONSTRAINT chk_contrato_fechas
        CHECK (fecha_firma IS NULL OR fecha_firma >= fecha_generacion),

    CONSTRAINT fk_contrato_estado
        FOREIGN KEY (id_estado_contrato) REFERENCES estado_contrato(id_estado_contrato),

    CONSTRAINT fk_contrato_arrendamiento
        FOREIGN KEY (id_arrendamiento) REFERENCES arrendamiento(id_arrendamiento)
);

CREATE TABLE pago (
id_pago INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
fecha_vencimiento DATE NOT NULL,
fecha_pago TIMESTAMP,
periodo_anio INTEGER NOT NULL,
periodo_mes INTEGER NOT NULL,
monto NUMERIC(10,2) NOT NULL,
referencia_pago VARCHAR(80),
comprobante VARCHAR(255),
id_arrendamiento INTEGER NOT NULL,
id_metodo_pago INTEGER,
id_estado_pago INTEGER NOT NULL,

    CONSTRAINT uq_pago_arrendamiento_periodo
        UNIQUE (id_arrendamiento, periodo_anio, periodo_mes),

    CONSTRAINT chk_pago_periodo_anio
        CHECK (periodo_anio BETWEEN 2020 AND 2100),

    CONSTRAINT chk_pago_periodo_mes
        CHECK (periodo_mes BETWEEN 1 AND 12),

    CONSTRAINT chk_pago_monto
        CHECK (monto > 0),

    CONSTRAINT chk_pago_referencia_no_vacia
        CHECK (referencia_pago IS NULL OR LENGTH(TRIM(referencia_pago)) > 0),

    CONSTRAINT chk_pago_comprobante_no_vacio
        CHECK (comprobante IS NULL OR LENGTH(TRIM(comprobante)) > 0),

    CONSTRAINT fk_pago_arrendamiento
        FOREIGN KEY (id_arrendamiento) REFERENCES arrendamiento(id_arrendamiento),

    CONSTRAINT fk_pago_metodo
        FOREIGN KEY (id_metodo_pago) REFERENCES metodo_pago(id_metodo_pago),

    CONSTRAINT fk_pago_estado
        FOREIGN KEY (id_estado_pago) REFERENCES estado_pago(id_estado_pago)
);

CREATE TABLE incidencia (
id_incidencia INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
titulo VARCHAR(100) NOT NULL,
descripcion TEXT NOT NULL,
fecha_reporte TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
fecha_cierre TIMESTAMP,
solucion VARCHAR(255),
id_arrendamiento INTEGER NOT NULL,
id_usuario_reporta INTEGER NOT NULL,
id_estado_incidencia INTEGER NOT NULL,
id_prioridad_incidencia INTEGER NOT NULL,

    CONSTRAINT chk_incidencia_titulo_no_vacio
        CHECK (LENGTH(TRIM(titulo)) > 0),

    CONSTRAINT chk_incidencia_descripcion_no_vacia
        CHECK (LENGTH(TRIM(descripcion)) > 0),

    CONSTRAINT chk_incidencia_solucion_no_vacia
        CHECK (solucion IS NULL OR LENGTH(TRIM(solucion)) > 0),

    CONSTRAINT chk_incidencia_fechas
        CHECK (fecha_cierre IS NULL OR fecha_cierre >= fecha_reporte),

    CONSTRAINT fk_incidencia_arrendamiento
        FOREIGN KEY (id_arrendamiento) REFERENCES arrendamiento(id_arrendamiento),

    CONSTRAINT fk_incidencia_usuario_reporta
        FOREIGN KEY (id_usuario_reporta) REFERENCES usuario(id_usuario),

    CONSTRAINT fk_incidencia_estado
        FOREIGN KEY (id_estado_incidencia) REFERENCES estado_incidencia(id_estado_incidencia),

    CONSTRAINT fk_incidencia_prioridad
        FOREIGN KEY (id_prioridad_incidencia) REFERENCES prioridad_incidencia(id_prioridad_incidencia)
);

-- =========================================================
-- ÍNDICES
-- =========================================================

CREATE INDEX idx_usuario_id_rol ON usuario(id_rol);
CREATE INDEX idx_usuario_id_estado ON usuario(id_estado_usuario);

CREATE INDEX idx_contacto_id_usuario ON contacto(id_usuario);
CREATE INDEX idx_contacto_id_tipo_contacto ON contacto(id_tipo_contacto);

CREATE INDEX idx_inmueble_id_usuario_arrendador ON inmueble(id_usuario_arrendador);
CREATE INDEX idx_inmueble_id_tipo_inmueble ON inmueble(id_tipo_inmueble);
CREATE INDEX idx_inmueble_id_estado_inmueble ON inmueble(id_estado_inmueble);
CREATE INDEX idx_inmueble_ciudad ON inmueble(ciudad);
CREATE INDEX idx_inmueble_estado_provincia ON inmueble(estado_provincia);
CREATE INDEX idx_inmueble_codigo_postal ON inmueble(codigo_postal);

CREATE INDEX idx_imagen_inmueble_id_inmueble ON imagen_inmueble(id_inmueble);

CREATE INDEX idx_solicitud_id_usuario_arrendatario ON solicitud_arrendamiento(id_usuario_arrendatario);
CREATE INDEX idx_solicitud_id_inmueble ON solicitud_arrendamiento(id_inmueble);
CREATE INDEX idx_solicitud_id_estado_solicitud ON solicitud_arrendamiento(id_estado_solicitud);

CREATE INDEX idx_arrendamiento_id_inmueble ON arrendamiento(id_inmueble);
CREATE INDEX idx_arrendamiento_id_usuario_arrendador ON arrendamiento(id_usuario_arrendador);
CREATE INDEX idx_arrendamiento_id_usuario_arrendatario ON arrendamiento(id_usuario_arrendatario);
CREATE INDEX idx_arrendamiento_id_estado_arrendamiento ON arrendamiento(id_estado_arrendamiento);

CREATE INDEX idx_contrato_id_estado_contrato ON contrato(id_estado_contrato);

CREATE INDEX idx_pago_id_arrendamiento ON pago(id_arrendamiento);
CREATE INDEX idx_pago_id_metodo_pago ON pago(id_metodo_pago);
CREATE INDEX idx_pago_id_estado_pago ON pago(id_estado_pago);
CREATE INDEX idx_pago_fecha_vencimiento ON pago(fecha_vencimiento);

CREATE INDEX idx_incidencia_id_arrendamiento ON incidencia(id_arrendamiento);
CREATE INDEX idx_incidencia_id_usuario_reporta ON incidencia(id_usuario_reporta);
CREATE INDEX idx_incidencia_id_estado_incidencia ON incidencia(id_estado_incidencia);
CREATE INDEX idx_incidencia_id_prioridad_incidencia ON incidencia(id_prioridad_incidencia);

-- =========================================================
-- DATOS INICIALES DE CATÁLOGO
-- =========================================================

INSERT INTO rol (nombre_rol, descripcion) VALUES
('Administrador', 'Administra el sistema'),
('Arrendador', 'Publica y gestiona inmuebles'),
('Arrendatario', 'Solicita y renta inmuebles');

INSERT INTO estado_usuario (nombre_estado) VALUES
('Activo'),
('Inactivo'),
('Suspendido');

INSERT INTO tipo_contacto (nombre_tipo) VALUES
('Correo'),
('Telefono'),
('WhatsApp');

INSERT INTO tipo_inmueble (nombre_tipo, descripcion) VALUES
('Casa', 'Vivienda independiente'),
('Departamento', 'Unidad en edificio'),
('Local', 'Espacio comercial');

INSERT INTO estado_inmueble (nombre_estado) VALUES
('Disponible'),
('Ocupado'),
('No disponible');

INSERT INTO estado_solicitud (nombre_estado) VALUES
('Pendiente'),
('Aceptada'),
('Rechazada');

INSERT INTO estado_arrendamiento (nombre_estado) VALUES
('Activo'),
('Finalizado'),
('Cancelado');

INSERT INTO estado_contrato (nombre_estado) VALUES
('Generado'),
('Firmado'),
('Cancelado');

INSERT INTO metodo_pago (nombre_metodo) VALUES
('Transferencia'),
('Efectivo'),
('Tarjeta');

INSERT INTO estado_pago (nombre_estado) VALUES
('Pendiente'),
('Pagado'),
('Vencido');

INSERT INTO estado_incidencia (nombre_estado) VALUES
('Abierta'),
('En proceso'),
('Resuelta');

INSERT INTO prioridad_incidencia (nombre_prioridad) VALUES
('Baja'),
('Media'),
('Alta');