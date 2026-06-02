package com.rentify.dao;

import com.rentify.database.DatabaseConnection;
import com.rentify.database.MultiDatabaseExecutor;
import com.rentify.model.ArrendamientoComboItem;
import com.rentify.model.IncidenciaDetalle;
import com.rentify.model.IncidenciaTabla;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IncidenciaDAO {

    public List<IncidenciaTabla> listarIncidenciasComoArrendador(int idUsuarioArrendador) {
        List<IncidenciaTabla> incidencias = new ArrayList<>();

        String sql = """
                SELECT i.id_incidencia,
                       i.titulo,
                       inm.titulo AS arrendamiento,
                       u.nombre || ' ' || u.apellido_paterno AS reporta,
                       i.fecha_reporte,
                       p.nombre_prioridad,
                       e.nombre_estado
                FROM incidencia i
                INNER JOIN arrendamiento a ON i.id_arrendamiento = a.id_arrendamiento
                INNER JOIN inmueble inm ON a.id_inmueble = inm.id_inmueble
                INNER JOIN usuario u ON i.id_usuario_reporta = u.id_usuario
                INNER JOIN prioridad_incidencia p ON i.id_prioridad_incidencia = p.id_prioridad_incidencia
                INNER JOIN estado_incidencia e ON i.id_estado_incidencia = e.id_estado_incidencia
                WHERE a.id_usuario_arrendador = ?
                ORDER BY i.id_incidencia DESC
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idUsuarioArrendador);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    incidencias.add(mapearIncidenciaTabla(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar incidencias como arrendador: " + e.getMessage());
            e.printStackTrace();
        }

        return incidencias;
    }

    public List<IncidenciaTabla> listarIncidenciasComoArrendatario(int idUsuarioArrendatario) {
        List<IncidenciaTabla> incidencias = new ArrayList<>();

        String sql = """
                SELECT i.id_incidencia,
                       i.titulo,
                       inm.titulo AS arrendamiento,
                       u.nombre || ' ' || u.apellido_paterno AS reporta,
                       i.fecha_reporte,
                       p.nombre_prioridad,
                       e.nombre_estado
                FROM incidencia i
                INNER JOIN arrendamiento a ON i.id_arrendamiento = a.id_arrendamiento
                INNER JOIN inmueble inm ON a.id_inmueble = inm.id_inmueble
                INNER JOIN usuario u ON i.id_usuario_reporta = u.id_usuario
                INNER JOIN prioridad_incidencia p ON i.id_prioridad_incidencia = p.id_prioridad_incidencia
                INNER JOIN estado_incidencia e ON i.id_estado_incidencia = e.id_estado_incidencia
                WHERE a.id_usuario_arrendatario = ?
                ORDER BY i.id_incidencia DESC
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idUsuarioArrendatario);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    incidencias.add(mapearIncidenciaTabla(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar incidencias como arrendatario: " + e.getMessage());
            e.printStackTrace();
        }

        return incidencias;
    }

    public List<ArrendamientoComboItem> listarArrendamientosActivosDeArrendatario(int idUsuarioArrendatario) {
        List<ArrendamientoComboItem> items = new ArrayList<>();

        String sql = """
                SELECT a.id_arrendamiento,
                       inm.titulo || ' - ' || a.fecha_inicio AS descripcion
                FROM arrendamiento a
                INNER JOIN inmueble inm ON a.id_inmueble = inm.id_inmueble
                INNER JOIN estado_arrendamiento ea ON a.id_estado_arrendamiento = ea.id_estado_arrendamiento
                WHERE a.id_usuario_arrendatario = ?
                  AND ea.nombre_estado = 'Activo'
                ORDER BY a.id_arrendamiento DESC
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idUsuarioArrendatario);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    items.add(new ArrendamientoComboItem(
                            rs.getInt("id_arrendamiento"),
                            rs.getString("descripcion")
                    ));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar arrendamientos activos del arrendatario: " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }

    public boolean insertarIncidencia(String titulo, String descripcion,
                                      int idArrendamiento, int idUsuarioReporta,
                                      int idPrioridadIncidencia) {
        String sql = """
                INSERT INTO incidencia (
                    titulo,
                    descripcion,
                    id_arrendamiento,
                    id_usuario_reporta,
                    id_estado_incidencia,
                    id_prioridad_incidencia
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setString(1, titulo);
                statement.setString(2, descripcion);
                statement.setInt(3, idArrendamiento);
                statement.setInt(4, idUsuarioReporta);
                statement.setInt(5, 1); // Abierta
                statement.setInt(6, idPrioridadIncidencia);
            });

        } catch (SQLException e) {
            System.out.println("Error al insertar incidencia en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarEstadoIncidencia(int idIncidencia, int idEstadoIncidencia) {
        String sql = """
                UPDATE incidencia
                SET id_estado_incidencia = ?
                WHERE id_incidencia = ?
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setInt(1, idEstadoIncidencia);
                statement.setInt(2, idIncidencia);
            });

        } catch (SQLException e) {
            System.out.println("Error al actualizar estado de incidencia en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean resolverIncidencia(int idIncidencia, String solucion) {
        String sql = """
                UPDATE incidencia
                SET solucion = ?,
                    fecha_cierre = CURRENT_TIMESTAMP,
                    id_estado_incidencia = ?
                WHERE id_incidencia = ?
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setString(1, solucion);
                statement.setInt(2, 3); // Resuelta
                statement.setInt(3, idIncidencia);
            });

        } catch (SQLException e) {
            System.out.println("Error al resolver incidencia en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public IncidenciaDetalle buscarDetallePorId(int idIncidencia) {
        String sql = """
                SELECT i.id_incidencia,
                       i.titulo,
                       i.descripcion,
                       inm.titulo AS arrendamiento,
                       u.nombre || ' ' || u.apellido_paterno AS reporta,
                       i.fecha_reporte,
                       i.fecha_cierre,
                       i.solucion,
                       p.nombre_prioridad,
                       e.nombre_estado
                FROM incidencia i
                INNER JOIN arrendamiento a ON i.id_arrendamiento = a.id_arrendamiento
                INNER JOIN inmueble inm ON a.id_inmueble = inm.id_inmueble
                INNER JOIN usuario u ON i.id_usuario_reporta = u.id_usuario
                INNER JOIN prioridad_incidencia p ON i.id_prioridad_incidencia = p.id_prioridad_incidencia
                INNER JOIN estado_incidencia e ON i.id_estado_incidencia = e.id_estado_incidencia
                WHERE i.id_incidencia = ?
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idIncidencia);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    IncidenciaDetalle detalle = new IncidenciaDetalle();

                    detalle.setIdIncidencia(rs.getInt("id_incidencia"));
                    detalle.setTitulo(rs.getString("titulo"));
                    detalle.setDescripcion(rs.getString("descripcion"));
                    detalle.setArrendamiento(rs.getString("arrendamiento"));
                    detalle.setReporta(rs.getString("reporta"));
                    detalle.setFechaReporte(
                            rs.getTimestamp("fecha_reporte") != null
                                    ? rs.getTimestamp("fecha_reporte").toString()
                                    : "No especificada"
                    );
                    detalle.setFechaCierre(
                            rs.getTimestamp("fecha_cierre") != null
                                    ? rs.getTimestamp("fecha_cierre").toString()
                                    : "Sin cierre"
                    );
                    detalle.setSolucion(
                            rs.getString("solucion") != null
                                    ? rs.getString("solucion")
                                    : "Sin solución registrada"
                    );
                    detalle.setPrioridad(rs.getString("nombre_prioridad"));
                    detalle.setEstado(rs.getString("nombre_estado"));

                    return detalle;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar detalle de incidencia: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private IncidenciaTabla mapearIncidenciaTabla(ResultSet rs) throws SQLException {
        return new IncidenciaTabla(
                rs.getInt("id_incidencia"),
                rs.getString("titulo"),
                rs.getString("arrendamiento"),
                rs.getString("reporta"),
                rs.getTimestamp("fecha_reporte").toString(),
                rs.getString("nombre_prioridad"),
                rs.getString("nombre_estado")
        );
    }
}