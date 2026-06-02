package com.rentify.dao;

import com.rentify.database.DatabaseConnection;
import com.rentify.database.MultiDatabaseExecutor;
import com.rentify.model.Inmueble;
import com.rentify.model.InmuebleExplorarTabla;
import com.rentify.model.InmuebleTabla;
import com.rentify.util.MonedaUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class InmuebleDAO {

    public List<InmuebleTabla> listarInmueblesPorArrendador(int idUsuarioArrendador) {
        List<InmuebleTabla> inmuebles = new ArrayList<>();

        String sql = """
                SELECT i.id_inmueble,
                       i.titulo,
                       i.ciudad,
                       ti.nombre_tipo,
                       ei.nombre_estado,
                       i.precio_renta
                FROM inmueble i
                INNER JOIN tipo_inmueble ti ON i.id_tipo_inmueble = ti.id_tipo_inmueble
                INNER JOIN estado_inmueble ei ON i.id_estado_inmueble = ei.id_estado_inmueble
                WHERE i.id_usuario_arrendador = ?
                ORDER BY i.id_inmueble
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idUsuarioArrendador);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    InmuebleTabla inmueble = new InmuebleTabla(
                            resultSet.getInt("id_inmueble"),
                            resultSet.getString("titulo"),
                            resultSet.getString("ciudad"),
                            resultSet.getString("nombre_tipo"),
                            resultSet.getString("nombre_estado"),
                            MonedaUtil.formatear(resultSet.getBigDecimal("precio_renta"))
                    );

                    inmuebles.add(inmueble);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar inmuebles: " + e.getMessage());
            e.printStackTrace();
        }

        return inmuebles;
    }

    public boolean insertarInmueble(Inmueble inmueble) {
        String sql = """
                INSERT INTO inmueble (
                    titulo,
                    descripcion,
                    calle,
                    numero_exterior,
                    numero_interior,
                    colonia,
                    ciudad,
                    estado_provincia,
                    codigo_postal,
                    precio_renta,
                    superficie_m2,
                    habitaciones,
                    banos,
                    estacionamientos,
                    mascotas_permitidas,
                    id_usuario_arrendador,
                    id_tipo_inmueble,
                    id_estado_inmueble
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setString(1, inmueble.getTitulo());
                statement.setString(2, inmueble.getDescripcion());
                statement.setString(3, inmueble.getCalle());
                statement.setString(4, inmueble.getNumeroExterior());
                statement.setString(5, inmueble.getNumeroInterior());
                statement.setString(6, inmueble.getColonia());
                statement.setString(7, inmueble.getCiudad());
                statement.setString(8, inmueble.getEstadoProvincia());
                statement.setString(9, inmueble.getCodigoPostal());
                statement.setBigDecimal(10, inmueble.getPrecioRenta());

                if (inmueble.getSuperficieM2() != null) {
                    statement.setBigDecimal(11, inmueble.getSuperficieM2());
                } else {
                    statement.setNull(11, Types.NUMERIC);
                }

                if (inmueble.getHabitaciones() != null) {
                    statement.setInt(12, inmueble.getHabitaciones());
                } else {
                    statement.setNull(12, Types.INTEGER);
                }

                if (inmueble.getBanos() != null) {
                    statement.setBigDecimal(13, inmueble.getBanos());
                } else {
                    statement.setNull(13, Types.NUMERIC);
                }

                if (inmueble.getEstacionamientos() != null) {
                    statement.setInt(14, inmueble.getEstacionamientos());
                } else {
                    statement.setNull(14, Types.INTEGER);
                }

                statement.setBoolean(15, inmueble.isMascotasPermitidas());
                statement.setInt(16, inmueble.getIdUsuarioArrendador());
                statement.setInt(17, inmueble.getIdTipoInmueble());
                statement.setInt(18, inmueble.getIdEstadoInmueble());
            });

        } catch (SQLException e) {
            System.out.println("Error al insertar inmueble en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Inmueble buscarPorId(int idInmueble) {
        String sql = """
                SELECT id_inmueble, titulo, descripcion, calle, numero_exterior,
                       numero_interior, colonia, ciudad, estado_provincia,
                       codigo_postal, precio_renta, superficie_m2, habitaciones,
                       banos, estacionamientos, mascotas_permitidas, fecha_registro,
                       id_usuario_arrendador, id_tipo_inmueble, id_estado_inmueble
                FROM inmueble
                WHERE id_inmueble = ?
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idInmueble);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearInmueble(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar inmueble por id: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public boolean actualizarInmueble(Inmueble inmueble) {
        String sql = """
                UPDATE inmueble
                SET titulo = ?,
                    descripcion = ?,
                    calle = ?,
                    numero_exterior = ?,
                    numero_interior = ?,
                    colonia = ?,
                    ciudad = ?,
                    estado_provincia = ?,
                    codigo_postal = ?,
                    precio_renta = ?,
                    superficie_m2 = ?,
                    habitaciones = ?,
                    banos = ?,
                    estacionamientos = ?,
                    mascotas_permitidas = ?,
                    id_tipo_inmueble = ?,
                    id_estado_inmueble = ?
                WHERE id_inmueble = ?
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setString(1, inmueble.getTitulo());
                statement.setString(2, inmueble.getDescripcion());
                statement.setString(3, inmueble.getCalle());
                statement.setString(4, inmueble.getNumeroExterior());
                statement.setString(5, inmueble.getNumeroInterior());
                statement.setString(6, inmueble.getColonia());
                statement.setString(7, inmueble.getCiudad());
                statement.setString(8, inmueble.getEstadoProvincia());
                statement.setString(9, inmueble.getCodigoPostal());
                statement.setBigDecimal(10, inmueble.getPrecioRenta());

                if (inmueble.getSuperficieM2() != null) {
                    statement.setBigDecimal(11, inmueble.getSuperficieM2());
                } else {
                    statement.setNull(11, Types.NUMERIC);
                }

                if (inmueble.getHabitaciones() != null) {
                    statement.setInt(12, inmueble.getHabitaciones());
                } else {
                    statement.setNull(12, Types.INTEGER);
                }

                if (inmueble.getBanos() != null) {
                    statement.setBigDecimal(13, inmueble.getBanos());
                } else {
                    statement.setNull(13, Types.NUMERIC);
                }

                if (inmueble.getEstacionamientos() != null) {
                    statement.setInt(14, inmueble.getEstacionamientos());
                } else {
                    statement.setNull(14, Types.INTEGER);
                }

                statement.setBoolean(15, inmueble.isMascotasPermitidas());
                statement.setInt(16, inmueble.getIdTipoInmueble());
                statement.setInt(17, inmueble.getIdEstadoInmueble());
                statement.setInt(18, inmueble.getIdInmueble());
            });

        } catch (SQLException e) {
            System.out.println("Error al actualizar inmueble en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarEstadoInmueble(int idInmueble, int idEstadoInmueble) {
        String sql = """
                UPDATE inmueble
                SET id_estado_inmueble = ?
                WHERE id_inmueble = ?
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setInt(1, idEstadoInmueble);
                statement.setInt(2, idInmueble);
            });

        } catch (SQLException e) {
            System.out.println("Error al actualizar estado del inmueble en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<InmuebleExplorarTabla> listarInmueblesDisponibles() {
        List<InmuebleExplorarTabla> inmuebles = new ArrayList<>();

        String sql = """
                SELECT i.id_inmueble,
                       i.titulo,
                       i.ciudad,
                       ti.nombre_tipo,
                       i.precio_renta,
                       u.nombre || ' ' || u.apellido_paterno AS arrendador
                FROM inmueble i
                INNER JOIN tipo_inmueble ti ON i.id_tipo_inmueble = ti.id_tipo_inmueble
                INNER JOIN usuario u ON i.id_usuario_arrendador = u.id_usuario
                INNER JOIN estado_inmueble ei ON i.id_estado_inmueble = ei.id_estado_inmueble
                WHERE ei.nombre_estado = 'Disponible'
                ORDER BY i.id_inmueble
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                InmuebleExplorarTabla inmueble = new InmuebleExplorarTabla(
                        resultSet.getInt("id_inmueble"),
                        resultSet.getString("titulo"),
                        resultSet.getString("ciudad"),
                        resultSet.getString("nombre_tipo"),
                        MonedaUtil.formatear(resultSet.getBigDecimal("precio_renta")),
                        resultSet.getString("arrendador")
                );

                inmuebles.add(inmueble);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar inmuebles disponibles: " + e.getMessage());
            e.printStackTrace();
        }

        return inmuebles;
    }

    public boolean actualizarEstadoInmueblePorSolicitud(int idInmueble, int idEstadoInmueble) {
        String sql = """
                UPDATE inmueble
                SET id_estado_inmueble = ?
                WHERE id_inmueble = ?
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setInt(1, idEstadoInmueble);
                statement.setInt(2, idInmueble);
            });

        } catch (SQLException e) {
            System.out.println("Error al actualizar estado del inmueble por solicitud en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public BigDecimal obtenerPrecioRentaPorId(int idInmueble) {
        String sql = """
                SELECT precio_renta
                FROM inmueble
                WHERE id_inmueble = ?
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idInmueble);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal("precio_renta");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener precio de renta: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public Inmueble buscarDetallePorId(int idInmueble) {
        String sql = """
                SELECT i.id_inmueble,
                       i.titulo,
                       i.descripcion,
                       i.calle,
                       i.numero_exterior,
                       i.numero_interior,
                       i.colonia,
                       i.ciudad,
                       i.estado_provincia,
                       i.codigo_postal,
                       i.precio_renta,
                       i.superficie_m2,
                       i.habitaciones,
                       i.banos,
                       i.estacionamientos,
                       i.mascotas_permitidas,
                       i.fecha_registro,
                       i.id_usuario_arrendador,
                       i.id_tipo_inmueble,
                       i.id_estado_inmueble
                FROM inmueble i
                WHERE i.id_inmueble = ?
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idInmueble);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapearInmueble(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar detalle de inmueble: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<InmuebleExplorarTabla> filtrarInmueblesDisponibles(String ciudad, Integer idTipoInmueble, BigDecimal precioMaximo) {
        List<InmuebleExplorarTabla> inmuebles = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
                SELECT i.id_inmueble,
                       i.titulo,
                       i.ciudad,
                       ti.nombre_tipo,
                       i.precio_renta,
                       u.nombre || ' ' || u.apellido_paterno AS arrendador
                FROM inmueble i
                INNER JOIN tipo_inmueble ti ON i.id_tipo_inmueble = ti.id_tipo_inmueble
                INNER JOIN usuario u ON i.id_usuario_arrendador = u.id_usuario
                INNER JOIN estado_inmueble ei ON i.id_estado_inmueble = ei.id_estado_inmueble
                WHERE ei.nombre_estado = 'Disponible'
                """);

        List<Object> parametros = new ArrayList<>();

        if (ciudad != null && !ciudad.isBlank()) {
            sql.append(" AND LOWER(i.ciudad) LIKE LOWER(?) ");
            parametros.add("%" + ciudad.trim() + "%");
        }

        if (idTipoInmueble != null) {
            sql.append(" AND i.id_tipo_inmueble = ? ");
            parametros.add(idTipoInmueble);
        }

        if (precioMaximo != null) {
            sql.append(" AND i.precio_renta <= ? ");
            parametros.add(precioMaximo);
        }

        sql.append(" ORDER BY i.id_inmueble ");

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                statement.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    InmuebleExplorarTabla inmueble = new InmuebleExplorarTabla(
                            resultSet.getInt("id_inmueble"),
                            resultSet.getString("titulo"),
                            resultSet.getString("ciudad"),
                            resultSet.getString("nombre_tipo"),
                            MonedaUtil.formatear(resultSet.getBigDecimal("precio_renta")),
                            resultSet.getString("arrendador")
                    );

                    inmuebles.add(inmueble);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al filtrar inmuebles disponibles: " + e.getMessage());
            e.printStackTrace();
        }

        return inmuebles;
    }

    public Integer obtenerIdEstadoInmueble(int idInmueble) {
        String sql = """
                SELECT id_estado_inmueble
                FROM inmueble
                WHERE id_inmueble = ?
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idInmueble);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id_estado_inmueble");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener estado del inmueble: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public boolean inmuebleDisponible(int idInmueble) {
        Integer idEstado = obtenerIdEstadoInmueble(idInmueble);
        return idEstado != null && idEstado == 1;
    }

    public boolean tieneHistorial(int idInmueble) {
        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM solicitud_arrendamiento
                    WHERE id_inmueble = ?
                )
                OR EXISTS (
                    SELECT 1
                    FROM arrendamiento
                    WHERE id_inmueble = ?
                ) AS tiene_historial
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idInmueble);
            statement.setInt(2, idInmueble);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("tiene_historial");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al validar historial del inmueble: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    public boolean eliminarInmuebleSinHistorial(int idInmueble) {
        String sqlEliminarImagenes = """
                DELETE FROM imagen_inmueble
                WHERE id_inmueble = ?
                """;

        String sqlEliminarInmueble = """
                DELETE FROM inmueble
                WHERE id_inmueble = ?
                """;

        try {
            return MultiDatabaseExecutor.ejecutarTransaccion(conexion -> {
                try (PreparedStatement eliminarImagenes = conexion.prepareStatement(sqlEliminarImagenes);
                     PreparedStatement eliminarInmueble = conexion.prepareStatement(sqlEliminarInmueble)) {

                    eliminarImagenes.setInt(1, idInmueble);
                    eliminarImagenes.executeUpdate();

                    eliminarInmueble.setInt(1, idInmueble);
                    int filasAfectadas = eliminarInmueble.executeUpdate();

                    if (filasAfectadas == 0) {
                        throw new SQLException("No se encontró el inmueble para eliminar.");
                    }
                }
            });

        } catch (SQLException e) {
            System.out.println("Error al eliminar inmueble en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Inmueble mapearInmueble(ResultSet resultSet) throws SQLException {
        Inmueble inmueble = new Inmueble();

        inmueble.setIdInmueble(resultSet.getInt("id_inmueble"));
        inmueble.setTitulo(resultSet.getString("titulo"));
        inmueble.setDescripcion(resultSet.getString("descripcion"));
        inmueble.setCalle(resultSet.getString("calle"));
        inmueble.setNumeroExterior(resultSet.getString("numero_exterior"));
        inmueble.setNumeroInterior(resultSet.getString("numero_interior"));
        inmueble.setColonia(resultSet.getString("colonia"));
        inmueble.setCiudad(resultSet.getString("ciudad"));
        inmueble.setEstadoProvincia(resultSet.getString("estado_provincia"));
        inmueble.setCodigoPostal(resultSet.getString("codigo_postal"));
        inmueble.setPrecioRenta(resultSet.getBigDecimal("precio_renta"));
        inmueble.setSuperficieM2(resultSet.getBigDecimal("superficie_m2"));

        int habitaciones = resultSet.getInt("habitaciones");
        inmueble.setHabitaciones(resultSet.wasNull() ? null : habitaciones);

        inmueble.setBanos(resultSet.getBigDecimal("banos"));

        int estacionamientos = resultSet.getInt("estacionamientos");
        inmueble.setEstacionamientos(resultSet.wasNull() ? null : estacionamientos);

        inmueble.setMascotasPermitidas(resultSet.getBoolean("mascotas_permitidas"));
        inmueble.setIdUsuarioArrendador(resultSet.getInt("id_usuario_arrendador"));
        inmueble.setIdTipoInmueble(resultSet.getInt("id_tipo_inmueble"));
        inmueble.setIdEstadoInmueble(resultSet.getInt("id_estado_inmueble"));

        return inmueble;
    }
}