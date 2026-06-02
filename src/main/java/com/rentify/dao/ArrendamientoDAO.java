package com.rentify.dao;

import com.rentify.database.DatabaseConnection;
import com.rentify.database.MultiDatabaseExecutor;
import com.rentify.model.ArrendamientoContratoData;
import com.rentify.model.ArrendamientoTabla;
import com.rentify.util.MonedaUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ArrendamientoDAO {

    public boolean insertarArrendamiento(
            Date fechaInicio,
            Date fechaFin,
            BigDecimal montoMensual,
            BigDecimal depositoGarantia,
            Integer diaPago,
            String observaciones,
            int idInmueble,
            int idUsuarioArrendador,
            int idUsuarioArrendatario,
            int idSolicitud
    ) {
        String sql = """
                INSERT INTO arrendamiento (
                    fecha_inicio,
                    fecha_fin,
                    monto_mensual,
                    deposito_garantia,
                    dia_pago,
                    observaciones,
                    id_inmueble,
                    id_usuario_arrendador,
                    id_usuario_arrendatario,
                    id_estado_arrendamiento,
                    id_solicitud
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setDate(1, fechaInicio);

                if (fechaFin != null) {
                    statement.setDate(2, fechaFin);
                } else {
                    statement.setNull(2, Types.DATE);
                }

                statement.setBigDecimal(3, montoMensual);

                if (depositoGarantia != null) {
                    statement.setBigDecimal(4, depositoGarantia);
                } else {
                    statement.setNull(4, Types.NUMERIC);
                }

                if (diaPago != null) {
                    statement.setInt(5, diaPago);
                } else {
                    statement.setNull(5, Types.INTEGER);
                }

                if (observaciones == null || observaciones.trim().isEmpty()) {
                    statement.setNull(6, Types.VARCHAR);
                } else {
                    statement.setString(6, observaciones.trim());
                }

                statement.setInt(7, idInmueble);
                statement.setInt(8, idUsuarioArrendador);
                statement.setInt(9, idUsuarioArrendatario);
                statement.setInt(10, 1); // Activo
                statement.setInt(11, idSolicitud);
            });

        } catch (SQLException e) {
            System.out.println("Error al insertar arrendamiento en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<ArrendamientoTabla> listarArrendamientosComoArrendador(int idUsuarioArrendador) {
        List<ArrendamientoTabla> arrendamientos = new ArrayList<>();

        String sql = """
                SELECT a.id_arrendamiento,
                       i.titulo AS inmueble,
                       u.nombre || ' ' || u.apellido_paterno AS contraparte,
                       a.fecha_inicio,
                       a.fecha_fin,
                       a.monto_mensual,
                       ea.nombre_estado
                FROM arrendamiento a
                INNER JOIN inmueble i ON a.id_inmueble = i.id_inmueble
                INNER JOIN usuario u ON a.id_usuario_arrendatario = u.id_usuario
                INNER JOIN estado_arrendamiento ea ON a.id_estado_arrendamiento = ea.id_estado_arrendamiento
                WHERE a.id_usuario_arrendador = ?
                ORDER BY a.id_arrendamiento DESC
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idUsuarioArrendador);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ArrendamientoTabla arrendamiento = new ArrendamientoTabla(
                            resultSet.getInt("id_arrendamiento"),
                            resultSet.getString("inmueble"),
                            resultSet.getString("contraparte"),
                            resultSet.getDate("fecha_inicio").toString(),
                            resultSet.getDate("fecha_fin") != null ? resultSet.getDate("fecha_fin").toString() : "",
                            MonedaUtil.formatear(resultSet.getBigDecimal("monto_mensual")),
                            resultSet.getString("nombre_estado")
                    );

                    arrendamientos.add(arrendamiento);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar arrendamientos como arrendador: " + e.getMessage());
            e.printStackTrace();
        }

        return arrendamientos;
    }

    public List<ArrendamientoTabla> listarArrendamientosComoArrendatario(int idUsuarioArrendatario) {
        List<ArrendamientoTabla> arrendamientos = new ArrayList<>();

        String sql = """
                SELECT a.id_arrendamiento,
                       i.titulo AS inmueble,
                       u.nombre || ' ' || u.apellido_paterno AS contraparte,
                       a.fecha_inicio,
                       a.fecha_fin,
                       a.monto_mensual,
                       ea.nombre_estado
                FROM arrendamiento a
                INNER JOIN inmueble i ON a.id_inmueble = i.id_inmueble
                INNER JOIN usuario u ON a.id_usuario_arrendador = u.id_usuario
                INNER JOIN estado_arrendamiento ea ON a.id_estado_arrendamiento = ea.id_estado_arrendamiento
                WHERE a.id_usuario_arrendatario = ?
                ORDER BY a.id_arrendamiento DESC
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idUsuarioArrendatario);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ArrendamientoTabla arrendamiento = new ArrendamientoTabla(
                            resultSet.getInt("id_arrendamiento"),
                            resultSet.getString("inmueble"),
                            resultSet.getString("contraparte"),
                            resultSet.getDate("fecha_inicio").toString(),
                            resultSet.getDate("fecha_fin") != null ? resultSet.getDate("fecha_fin").toString() : "",
                            MonedaUtil.formatear(resultSet.getBigDecimal("monto_mensual")),
                            resultSet.getString("nombre_estado")
                    );

                    arrendamientos.add(arrendamiento);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar arrendamientos como arrendatario: " + e.getMessage());
            e.printStackTrace();
        }

        return arrendamientos;
    }

    public BigDecimal obtenerMontoMensualPorId(int idArrendamiento) {
        String sql = """
                SELECT monto_mensual
                FROM arrendamiento
                WHERE id_arrendamiento = ?
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idArrendamiento);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal("monto_mensual");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener monto mensual del arrendamiento: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public boolean actualizarEstadoArrendamiento(int idArrendamiento, int idEstadoArrendamiento) {
        String sql = """
                UPDATE arrendamiento
                SET id_estado_arrendamiento = ?
                WHERE id_arrendamiento = ?
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setInt(1, idEstadoArrendamiento);
                statement.setInt(2, idArrendamiento);
            });

        } catch (SQLException e) {
            System.out.println("Error al actualizar estado del arrendamiento en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int obtenerIdInmueblePorArrendamiento(int idArrendamiento) {
        String sql = """
                SELECT id_inmueble
                FROM arrendamiento
                WHERE id_arrendamiento = ?
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idArrendamiento);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id_inmueble");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener inmueble del arrendamiento: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public ArrendamientoContratoData obtenerDatosContrato(int idArrendamiento) {
        String sql = """
                SELECT a.id_arrendamiento,
                       a.fecha_inicio,
                       a.fecha_fin,
                       a.monto_mensual,
                       a.deposito_garantia,
                       a.dia_pago,
                       a.observaciones,
                       i.titulo AS inmueble_titulo,
                       i.calle,
                       i.numero_exterior,
                       i.numero_interior,
                       i.colonia,
                       i.ciudad,
                       i.estado_provincia,
                       ua.nombre || ' ' || ua.apellido_paterno AS arrendador_nombre,
                       ut.nombre || ' ' || ut.apellido_paterno AS arrendatario_nombre
                FROM arrendamiento a
                INNER JOIN inmueble i ON a.id_inmueble = i.id_inmueble
                INNER JOIN usuario ua ON a.id_usuario_arrendador = ua.id_usuario
                INNER JOIN usuario ut ON a.id_usuario_arrendatario = ut.id_usuario
                WHERE a.id_arrendamiento = ?
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idArrendamiento);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    ArrendamientoContratoData data = new ArrendamientoContratoData();

                    data.setIdArrendamiento(rs.getInt("id_arrendamiento"));

                    Date fechaInicio = rs.getDate("fecha_inicio");
                    if (fechaInicio != null) {
                        data.setFechaInicio(fechaInicio.toLocalDate());
                    }

                    Date fechaFin = rs.getDate("fecha_fin");
                    if (fechaFin != null) {
                        data.setFechaFin(fechaFin.toLocalDate());
                    }

                    data.setMontoMensual(rs.getBigDecimal("monto_mensual"));
                    data.setDepositoGarantia(rs.getBigDecimal("deposito_garantia"));

                    int diaPago = rs.getInt("dia_pago");
                    data.setDiaPago(rs.wasNull() ? null : diaPago);

                    data.setObservaciones(rs.getString("observaciones"));
                    data.setTituloInmueble(rs.getString("inmueble_titulo"));

                    String direccion = rs.getString("calle") + " #" + rs.getString("numero_exterior");
                    String numeroInterior = rs.getString("numero_interior");

                    if (numeroInterior != null && !numeroInterior.isBlank()) {
                        direccion += ", Int. " + numeroInterior;
                    }

                    direccion += ", Col. " + rs.getString("colonia");
                    direccion += ", " + rs.getString("ciudad");
                    direccion += ", " + rs.getString("estado_provincia");

                    data.setDireccionInmueble(direccion);
                    data.setNombreArrendador(rs.getString("arrendador_nombre"));
                    data.setNombreArrendatario(rs.getString("arrendatario_nombre"));

                    return data;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener datos de contrato: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public boolean existeArrendamientoActivoPorInmueble(int idInmueble) {
        String sql = """
                SELECT 1
                FROM arrendamiento
                WHERE id_inmueble = ?
                  AND id_estado_arrendamiento = 1
                LIMIT 1
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idInmueble);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            System.out.println("Error al verificar arrendamiento activo del inmueble: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public java.time.LocalDate[] obtenerFechasArrendamiento(int idArrendamiento) {
        String sql = """
                SELECT fecha_inicio, fecha_fin
                FROM arrendamiento
                WHERE id_arrendamiento = ?
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idArrendamiento);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Date fechaInicio = rs.getDate("fecha_inicio");
                    Date fechaFin = rs.getDate("fecha_fin");

                    return new java.time.LocalDate[]{
                            fechaInicio != null ? fechaInicio.toLocalDate() : null,
                            fechaFin != null ? fechaFin.toLocalDate() : null
                    };
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener fechas del arrendamiento: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}