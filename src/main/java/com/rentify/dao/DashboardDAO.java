package com.rentify.dao;

import com.rentify.database.DatabaseConnection;
import com.rentify.model.DashboardResumen;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardDAO {

    public DashboardResumen obtenerResumenArrendador(int idArrendador) {
        DashboardResumen resumen = new DashboardResumen();

        resumen.setInmueblesRegistrados(contar("""
                SELECT COUNT(*)
                FROM inmueble
                WHERE id_usuario_arrendador = ?
                """, idArrendador));

        resumen.setInmueblesDisponibles(contar("""
                SELECT COUNT(*)
                FROM inmueble i
                INNER JOIN estado_inmueble ei
                    ON i.id_estado_inmueble = ei.id_estado_inmueble
                WHERE i.id_usuario_arrendador = ?
                  AND ei.nombre_estado = 'Disponible'
                """, idArrendador));

        resumen.setInmueblesOcupados(contar("""
                SELECT COUNT(*)
                FROM inmueble i
                INNER JOIN estado_inmueble ei
                    ON i.id_estado_inmueble = ei.id_estado_inmueble
                WHERE i.id_usuario_arrendador = ?
                  AND ei.nombre_estado = 'Ocupado'
                """, idArrendador));

        resumen.setSolicitudesPendientes(contar("""
                SELECT COUNT(*)
                FROM solicitud_arrendamiento s
                INNER JOIN inmueble i
                    ON s.id_inmueble = i.id_inmueble
                INNER JOIN estado_solicitud es
                    ON s.id_estado_solicitud = es.id_estado_solicitud
                WHERE i.id_usuario_arrendador = ?
                  AND es.nombre_estado = 'Pendiente'
                """, idArrendador));

        resumen.setArrendamientosActivos(contar("""
                SELECT COUNT(*)
                FROM arrendamiento a
                INNER JOIN estado_arrendamiento ea
                    ON a.id_estado_arrendamiento = ea.id_estado_arrendamiento
                WHERE a.id_usuario_arrendador = ?
                  AND ea.nombre_estado = 'Activo'
                """, idArrendador));

        resumen.setContratosFirmados(contar("""
                SELECT COUNT(*)
                FROM contrato c
                INNER JOIN arrendamiento a
                    ON c.id_arrendamiento = a.id_arrendamiento
                INNER JOIN estado_contrato ec
                    ON c.id_estado_contrato = ec.id_estado_contrato
                WHERE a.id_usuario_arrendador = ?
                  AND ec.nombre_estado = 'Firmado'
                """, idArrendador));

        resumen.setContratosGenerados(contar("""
                SELECT COUNT(*)
                FROM contrato c
                INNER JOIN arrendamiento a
                    ON c.id_arrendamiento = a.id_arrendamiento
                INNER JOIN estado_contrato ec
                    ON c.id_estado_contrato = ec.id_estado_contrato
                WHERE a.id_usuario_arrendador = ?
                  AND ec.nombre_estado = 'Generado'
                """, idArrendador));

        resumen.setPagosPendientes(contar("""
                SELECT COUNT(*)
                FROM pago p
                INNER JOIN arrendamiento a
                    ON p.id_arrendamiento = a.id_arrendamiento
                INNER JOIN estado_pago ep
                    ON p.id_estado_pago = ep.id_estado_pago
                WHERE a.id_usuario_arrendador = ?
                  AND ep.nombre_estado = 'Pendiente'
                """, idArrendador));

        resumen.setPagosVencidos(contar("""
                SELECT COUNT(*)
                FROM pago p
                INNER JOIN arrendamiento a
                    ON p.id_arrendamiento = a.id_arrendamiento
                INNER JOIN estado_pago ep
                    ON p.id_estado_pago = ep.id_estado_pago
                WHERE a.id_usuario_arrendador = ?
                  AND ep.nombre_estado = 'Vencido'
                """, idArrendador));

        resumen.setMontoPagosPendientes(sumar("""
                SELECT COALESCE(SUM(p.monto), 0)
                FROM pago p
                INNER JOIN arrendamiento a
                    ON p.id_arrendamiento = a.id_arrendamiento
                INNER JOIN estado_pago ep
                    ON p.id_estado_pago = ep.id_estado_pago
                WHERE a.id_usuario_arrendador = ?
                  AND ep.nombre_estado = 'Pendiente'
                """, idArrendador));

        resumen.setMontoPagosVencidos(sumar("""
                SELECT COALESCE(SUM(p.monto), 0)
                FROM pago p
                INNER JOIN arrendamiento a
                    ON p.id_arrendamiento = a.id_arrendamiento
                INNER JOIN estado_pago ep
                    ON p.id_estado_pago = ep.id_estado_pago
                WHERE a.id_usuario_arrendador = ?
                  AND ep.nombre_estado = 'Vencido'
                """, idArrendador));

        resumen.setIncidenciasAbiertas(contar("""
                SELECT COUNT(*)
                FROM incidencia inc
                INNER JOIN arrendamiento a
                    ON inc.id_arrendamiento = a.id_arrendamiento
                INNER JOIN estado_incidencia ei
                    ON inc.id_estado_incidencia = ei.id_estado_incidencia
                WHERE a.id_usuario_arrendador = ?
                  AND ei.nombre_estado = 'Abierta'
                """, idArrendador));

        return resumen;
    }

    public DashboardResumen obtenerResumenArrendatario(int idArrendatario) {
        DashboardResumen resumen = new DashboardResumen();

        resumen.setSolicitudesEnviadas(contar("""
                SELECT COUNT(*)
                FROM solicitud_arrendamiento
                WHERE id_usuario_arrendatario = ?
                """, idArrendatario));

        resumen.setSolicitudesPendientes(contar("""
                SELECT COUNT(*)
                FROM solicitud_arrendamiento s
                INNER JOIN estado_solicitud es
                    ON s.id_estado_solicitud = es.id_estado_solicitud
                WHERE s.id_usuario_arrendatario = ?
                  AND es.nombre_estado = 'Pendiente'
                """, idArrendatario));

        resumen.setArrendamientosActivos(contar("""
                SELECT COUNT(*)
                FROM arrendamiento a
                INNER JOIN estado_arrendamiento ea
                    ON a.id_estado_arrendamiento = ea.id_estado_arrendamiento
                WHERE a.id_usuario_arrendatario = ?
                  AND ea.nombre_estado = 'Activo'
                """, idArrendatario));

        resumen.setContratosPorFirmar(contar("""
                SELECT COUNT(*)
                FROM contrato c
                INNER JOIN arrendamiento a
                    ON c.id_arrendamiento = a.id_arrendamiento
                INNER JOIN estado_contrato ec
                    ON c.id_estado_contrato = ec.id_estado_contrato
                WHERE a.id_usuario_arrendatario = ?
                  AND ec.nombre_estado = 'Generado'
                """, idArrendatario));

        resumen.setPagosPendientes(contar("""
                SELECT COUNT(*)
                FROM pago p
                INNER JOIN arrendamiento a
                    ON p.id_arrendamiento = a.id_arrendamiento
                INNER JOIN estado_pago ep
                    ON p.id_estado_pago = ep.id_estado_pago
                WHERE a.id_usuario_arrendatario = ?
                  AND ep.nombre_estado = 'Pendiente'
                """, idArrendatario));

        resumen.setPagosVencidos(contar("""
                SELECT COUNT(*)
                FROM pago p
                INNER JOIN arrendamiento a
                    ON p.id_arrendamiento = a.id_arrendamiento
                INNER JOIN estado_pago ep
                    ON p.id_estado_pago = ep.id_estado_pago
                WHERE a.id_usuario_arrendatario = ?
                  AND ep.nombre_estado = 'Vencido'
                """, idArrendatario));

        resumen.setMontoPagosPendientes(sumar("""
                SELECT COALESCE(SUM(p.monto), 0)
                FROM pago p
                INNER JOIN arrendamiento a
                    ON p.id_arrendamiento = a.id_arrendamiento
                INNER JOIN estado_pago ep
                    ON p.id_estado_pago = ep.id_estado_pago
                WHERE a.id_usuario_arrendatario = ?
                  AND ep.nombre_estado = 'Pendiente'
                """, idArrendatario));

        resumen.setMontoPagosVencidos(sumar("""
                SELECT COALESCE(SUM(p.monto), 0)
                FROM pago p
                INNER JOIN arrendamiento a
                    ON p.id_arrendamiento = a.id_arrendamiento
                INNER JOIN estado_pago ep
                    ON p.id_estado_pago = ep.id_estado_pago
                WHERE a.id_usuario_arrendatario = ?
                  AND ep.nombre_estado = 'Vencido'
                """, idArrendatario));

        resumen.setIncidenciasAbiertas(contar("""
                SELECT COUNT(*)
                FROM incidencia inc
                INNER JOIN arrendamiento a
                    ON inc.id_arrendamiento = a.id_arrendamiento
                INNER JOIN estado_incidencia ei
                    ON inc.id_estado_incidencia = ei.id_estado_incidencia
                WHERE a.id_usuario_arrendatario = ?
                  AND ei.nombre_estado = 'Abierta'
                """, idArrendatario));

        return resumen;
    }

    private int contar(String sql, int idUsuario) {
        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idUsuario);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener conteo dashboard: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    private BigDecimal sumar(String sql, int idUsuario) {
        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idUsuario);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal(1);
                    return total != null ? total : BigDecimal.ZERO;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener suma dashboard: " + e.getMessage());
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }
}