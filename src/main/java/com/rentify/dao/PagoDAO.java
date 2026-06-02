package com.rentify.dao;

import com.rentify.database.DatabaseConnection;
import com.rentify.database.MultiDatabaseExecutor;
import com.rentify.model.PagoTabla;
import com.rentify.util.MonedaUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    public List<PagoTabla> listarPagosComoArrendador(int idUsuarioArrendador) {
        List<PagoTabla> pagos = new ArrayList<>();

        String sql = """
                SELECT p.id_pago,
                       i.titulo AS inmueble,
                       u.nombre || ' ' || u.apellido_paterno AS contraparte,
                       p.fecha_vencimiento,
                       p.fecha_pago,
                       p.periodo_mes,
                       p.periodo_anio,
                       p.monto,
                       p.referencia_pago,
                       p.comprobante,
                       mp.nombre_metodo,
                       ep.nombre_estado
                FROM pago p
                INNER JOIN arrendamiento a ON p.id_arrendamiento = a.id_arrendamiento
                INNER JOIN inmueble i ON a.id_inmueble = i.id_inmueble
                INNER JOIN usuario u ON a.id_usuario_arrendatario = u.id_usuario
                LEFT JOIN metodo_pago mp ON p.id_metodo_pago = mp.id_metodo_pago
                INNER JOIN estado_pago ep ON p.id_estado_pago = ep.id_estado_pago
                WHERE a.id_usuario_arrendador = ?
                ORDER BY p.id_pago DESC
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idUsuarioArrendador);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    pagos.add(mapearPagoTabla(resultSet));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar pagos como arrendador: " + e.getMessage());
            e.printStackTrace();
        }

        return pagos;
    }

    public List<PagoTabla> listarPagosComoArrendatario(int idUsuarioArrendatario) {
        List<PagoTabla> pagos = new ArrayList<>();

        String sql = """
                SELECT p.id_pago,
                       i.titulo AS inmueble,
                       u.nombre || ' ' || u.apellido_paterno AS contraparte,
                       p.fecha_vencimiento,
                       p.fecha_pago,
                       p.periodo_mes,
                       p.periodo_anio,
                       p.monto,
                       p.referencia_pago,
                       p.comprobante,
                       mp.nombre_metodo,
                       ep.nombre_estado
                FROM pago p
                INNER JOIN arrendamiento a ON p.id_arrendamiento = a.id_arrendamiento
                INNER JOIN inmueble i ON a.id_inmueble = i.id_inmueble
                INNER JOIN usuario u ON a.id_usuario_arrendador = u.id_usuario
                LEFT JOIN metodo_pago mp ON p.id_metodo_pago = mp.id_metodo_pago
                INNER JOIN estado_pago ep ON p.id_estado_pago = ep.id_estado_pago
                WHERE a.id_usuario_arrendatario = ?
                ORDER BY p.id_pago DESC
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idUsuarioArrendatario);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    pagos.add(mapearPagoTabla(resultSet));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar pagos como arrendatario: " + e.getMessage());
            e.printStackTrace();
        }

        return pagos;
    }

    public boolean existePagoDelPeriodo(int idArrendamiento, int periodoAnio, int periodoMes) {
        String sql = """
                SELECT 1
                FROM pago
                WHERE id_arrendamiento = ?
                  AND periodo_anio = ?
                  AND periodo_mes = ?
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idArrendamiento);
            statement.setInt(2, periodoAnio);
            statement.setInt(3, periodoMes);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            System.out.println("Error al verificar pago duplicado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertarPago(
            int idArrendamiento,
            Date fechaVencimiento,
            Integer periodoAnio,
            Integer periodoMes,
            BigDecimal monto,
            Integer idMetodoPago,
            Integer idEstadoPago,
            Timestamp fechaPago,
            String referenciaPago,
            String comprobante
    ) {
        String sql = """
                INSERT INTO pago (
                    fecha_vencimiento,
                    fecha_pago,
                    periodo_anio,
                    periodo_mes,
                    monto,
                    referencia_pago,
                    comprobante,
                    id_arrendamiento,
                    id_metodo_pago,
                    id_estado_pago
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setDate(1, fechaVencimiento);

                if (fechaPago != null) {
                    statement.setTimestamp(2, fechaPago);
                } else {
                    statement.setNull(2, Types.TIMESTAMP);
                }

                statement.setInt(3, periodoAnio);
                statement.setInt(4, periodoMes);
                statement.setBigDecimal(5, monto);

                if (referenciaPago != null && !referenciaPago.isBlank()) {
                    statement.setString(6, referenciaPago.trim());
                } else {
                    statement.setNull(6, Types.VARCHAR);
                }

                if (comprobante != null && !comprobante.isBlank()) {
                    statement.setString(7, comprobante.trim());
                } else {
                    statement.setNull(7, Types.VARCHAR);
                }

                statement.setInt(8, idArrendamiento);

                if (idMetodoPago != null) {
                    statement.setInt(9, idMetodoPago);
                } else {
                    statement.setNull(9, Types.INTEGER);
                }

                statement.setInt(10, idEstadoPago);
            });

        } catch (SQLException e) {
            System.out.println("Error al insertar pago en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean pagarPago(int idPago, int idMetodoPago) {
        String sql = """
                UPDATE pago
                SET fecha_pago = CURRENT_TIMESTAMP,
                    id_metodo_pago = ?,
                    id_estado_pago = ?
                WHERE id_pago = ?
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setInt(1, idMetodoPago);
                statement.setInt(2, 2); // Pagado
                statement.setInt(3, idPago);
            });

        } catch (SQLException e) {
            System.out.println("Error al pagar pago en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int contarPagosPorArrendamiento(int idArrendamiento) {
        String sql = """
                SELECT COUNT(*)
                FROM pago
                WHERE id_arrendamiento = ?
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idArrendamiento);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al contar pagos del arrendamiento: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public Date obtenerUltimaFechaVencimientoPorArrendamiento(int idArrendamiento) {
        String sql = """
                SELECT MAX(fecha_vencimiento) AS ultima_fecha
                FROM pago
                WHERE id_arrendamiento = ?
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idArrendamiento);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDate("ultima_fecha");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener última fecha de vencimiento: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public int actualizarPagosVencidos() {
        String sql = """
                UPDATE pago
                SET id_estado_pago = (
                    SELECT id_estado_pago
                    FROM estado_pago
                    WHERE nombre_estado = 'Vencido'
                )
                WHERE id_estado_pago = (
                    SELECT id_estado_pago
                    FROM estado_pago
                    WHERE nombre_estado = 'Pendiente'
                )
                AND fecha_pago IS NULL
                AND fecha_vencimiento < CURRENT_DATE
                """;

        try {
            boolean actualizado = MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                // No tiene parámetros.
            });

            return actualizado ? 1 : 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar pagos vencidos en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    private PagoTabla mapearPagoTabla(ResultSet resultSet) throws SQLException {
        return new PagoTabla(
                resultSet.getInt("id_pago"),
                resultSet.getString("inmueble"),
                resultSet.getString("contraparte"),
                resultSet.getDate("fecha_vencimiento").toString(),
                resultSet.getTimestamp("fecha_pago") != null ? resultSet.getTimestamp("fecha_pago").toString() : "",
                resultSet.getInt("periodo_mes") + "/" + resultSet.getInt("periodo_anio"),
                MonedaUtil.formatear(resultSet.getBigDecimal("monto")),
                resultSet.getString("nombre_metodo") != null ? resultSet.getString("nombre_metodo") : "",
                resultSet.getString("referencia_pago") != null ? resultSet.getString("referencia_pago") : "",
                resultSet.getString("comprobante") != null ? resultSet.getString("comprobante") : "",
                resultSet.getString("nombre_estado")
        );
    }

}