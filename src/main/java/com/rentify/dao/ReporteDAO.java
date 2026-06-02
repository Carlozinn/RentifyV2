package com.rentify.dao;

import com.rentify.database.DatabaseConnection;
import com.rentify.model.ReporteResumen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    public List<ReporteResumen> obtenerResumenGeneral() {
        List<ReporteResumen> resumen = new ArrayList<>();

        resumen.addAll(obtenerUsuariosPorRol());
        resumen.addAll(obtenerUsuariosPorEstado());
        resumen.addAll(obtenerInmueblesPorEstado());
        resumen.addAll(obtenerSolicitudesPorEstado());
        resumen.addAll(obtenerArrendamientosPorEstado());
        resumen.addAll(obtenerPagosPorEstado());
        resumen.addAll(obtenerIncidenciasPorEstado());

        return resumen;
    }

    private List<ReporteResumen> obtenerUsuariosPorRol() {
        String sql = """
                SELECT 'Usuarios por rol' AS categoria,
                       r.nombre_rol AS concepto,
                       COUNT(*) AS total
                FROM usuario u
                INNER JOIN rol r ON u.id_rol = r.id_rol
                GROUP BY r.nombre_rol
                ORDER BY r.nombre_rol
                """;
        return ejecutarConsulta(sql);
    }

    private List<ReporteResumen> obtenerUsuariosPorEstado() {
        String sql = """
                SELECT 'Usuarios por estado' AS categoria,
                       e.nombre_estado AS concepto,
                       COUNT(*) AS total
                FROM usuario u
                INNER JOIN estado_usuario e ON u.id_estado_usuario = e.id_estado_usuario
                GROUP BY e.nombre_estado
                ORDER BY e.nombre_estado
                """;
        return ejecutarConsulta(sql);
    }

    private List<ReporteResumen> obtenerInmueblesPorEstado() {
        String sql = """
                SELECT 'Inmuebles por estado' AS categoria,
                       e.nombre_estado AS concepto,
                       COUNT(*) AS total
                FROM inmueble i
                INNER JOIN estado_inmueble e ON i.id_estado_inmueble = e.id_estado_inmueble
                GROUP BY e.nombre_estado
                ORDER BY e.nombre_estado
                """;
        return ejecutarConsulta(sql);
    }

    private List<ReporteResumen> obtenerSolicitudesPorEstado() {
        String sql = """
                SELECT 'Solicitudes por estado' AS categoria,
                       e.nombre_estado AS concepto,
                       COUNT(*) AS total
                FROM solicitud_arrendamiento s
                INNER JOIN estado_solicitud e ON s.id_estado_solicitud = e.id_estado_solicitud
                GROUP BY e.nombre_estado
                ORDER BY e.nombre_estado
                """;
        return ejecutarConsulta(sql);
    }

    private List<ReporteResumen> obtenerArrendamientosPorEstado() {
        String sql = """
                SELECT 'Arrendamientos por estado' AS categoria,
                       e.nombre_estado AS concepto,
                       COUNT(*) AS total
                FROM arrendamiento a
                INNER JOIN estado_arrendamiento e ON a.id_estado_arrendamiento = e.id_estado_arrendamiento
                GROUP BY e.nombre_estado
                ORDER BY e.nombre_estado
                """;
        return ejecutarConsulta(sql);
    }

    private List<ReporteResumen> obtenerPagosPorEstado() {
        String sql = """
                SELECT 'Pagos por estado' AS categoria,
                       e.nombre_estado AS concepto,
                       COUNT(*) AS total
                FROM pago p
                INNER JOIN estado_pago e ON p.id_estado_pago = e.id_estado_pago
                GROUP BY e.nombre_estado
                ORDER BY e.nombre_estado
                """;
        return ejecutarConsulta(sql);
    }

    private List<ReporteResumen> obtenerIncidenciasPorEstado() {
        String sql = """
                SELECT 'Incidencias por estado' AS categoria,
                       e.nombre_estado AS concepto,
                       COUNT(*) AS total
                FROM incidencia i
                INNER JOIN estado_incidencia e ON i.id_estado_incidencia = e.id_estado_incidencia
                GROUP BY e.nombre_estado
                ORDER BY e.nombre_estado
                """;
        return ejecutarConsulta(sql);
    }

    private List<ReporteResumen> ejecutarConsulta(String sql) {
        List<ReporteResumen> lista = new ArrayList<>();

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                lista.add(new ReporteResumen(
                        rs.getString("categoria"),
                        rs.getString("concepto"),
                        rs.getInt("total")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error al ejecutar reporte: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }
}