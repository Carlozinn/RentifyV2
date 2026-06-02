package com.rentify.dao;

import com.rentify.database.DatabaseConnection;
import com.rentify.database.MultiDatabaseExecutor;
import com.rentify.model.ContratoTabla;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ContratoDAO {

    public boolean insertarContrato(String folioContrato, String archivoPdf, int idArrendamiento) {
        String sql = """
                INSERT INTO contrato (
                    folio_contrato,
                    archivo_pdf,
                    id_estado_contrato,
                    id_arrendamiento
                )
                VALUES (?, ?, ?, ?)
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setString(1, folioContrato);

                if (archivoPdf == null || archivoPdf.trim().isEmpty()) {
                    statement.setNull(2, Types.VARCHAR);
                } else {
                    statement.setString(2, archivoPdf.trim());
                }

                statement.setInt(3, 1); // Generado
                statement.setInt(4, idArrendamiento);
            });

        } catch (SQLException e) {
            System.out.println("Error al insertar contrato en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<ContratoTabla> listarContratosComoArrendador(int idUsuarioArrendador) {
        List<ContratoTabla> contratos = new ArrayList<>();

        String sql = """
                SELECT c.id_contrato,
                       c.folio_contrato,
                       i.titulo AS inmueble,
                       u.nombre || ' ' || u.apellido_paterno AS contraparte,
                       c.fecha_generacion,
                       c.fecha_firma,
                       ec.nombre_estado,
                       c.archivo_pdf
                FROM contrato c
                INNER JOIN arrendamiento a ON c.id_arrendamiento = a.id_arrendamiento
                INNER JOIN inmueble i ON a.id_inmueble = i.id_inmueble
                INNER JOIN usuario u ON a.id_usuario_arrendatario = u.id_usuario
                INNER JOIN estado_contrato ec ON c.id_estado_contrato = ec.id_estado_contrato
                WHERE a.id_usuario_arrendador = ?
                ORDER BY c.id_contrato DESC
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idUsuarioArrendador);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    contratos.add(mapearContratoTabla(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar contratos como arrendador: " + e.getMessage());
            e.printStackTrace();
        }

        return contratos;
    }

    public List<ContratoTabla> listarContratosComoArrendatario(int idUsuarioArrendatario) {
        List<ContratoTabla> contratos = new ArrayList<>();

        String sql = """
                SELECT c.id_contrato,
                       c.folio_contrato,
                       i.titulo AS inmueble,
                       u.nombre || ' ' || u.apellido_paterno AS contraparte,
                       c.fecha_generacion,
                       c.fecha_firma,
                       ec.nombre_estado,
                       c.archivo_pdf
                FROM contrato c
                INNER JOIN arrendamiento a ON c.id_arrendamiento = a.id_arrendamiento
                INNER JOIN inmueble i ON a.id_inmueble = i.id_inmueble
                INNER JOIN usuario u ON a.id_usuario_arrendador = u.id_usuario
                INNER JOIN estado_contrato ec ON c.id_estado_contrato = ec.id_estado_contrato
                WHERE a.id_usuario_arrendatario = ?
                ORDER BY c.id_contrato DESC
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idUsuarioArrendatario);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    contratos.add(mapearContratoTabla(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar contratos como arrendatario: " + e.getMessage());
            e.printStackTrace();
        }

        return contratos;
    }

    public boolean existeContratoParaArrendamiento(int idArrendamiento) {
        String sql = """
                SELECT 1
                FROM contrato
                WHERE id_arrendamiento = ?
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idArrendamiento);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("Error al verificar contrato existente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean firmarContrato(int idContrato) {
        String sql = """
                UPDATE contrato
                SET id_estado_contrato = ?,
                    fecha_firma = CURRENT_TIMESTAMP
                WHERE id_contrato = ?
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setInt(1, 2); // Firmado
                statement.setInt(2, idContrato);
            });

        } catch (SQLException e) {
            System.out.println("Error al firmar contrato en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelarContrato(int idContrato) {
        String sql = """
                UPDATE contrato
                SET id_estado_contrato = ?
                WHERE id_contrato = ?
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setInt(1, 3); // Cancelado
                statement.setInt(2, idContrato);
            });

        } catch (SQLException e) {
            System.out.println("Error al cancelar contrato en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarArchivoPdf(int idContrato, String archivoPdf) {
        String sql = """
                UPDATE contrato
                SET archivo_pdf = ?
                WHERE id_contrato = ?
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                if (archivoPdf != null && !archivoPdf.isBlank()) {
                    statement.setString(1, archivoPdf);
                } else {
                    statement.setNull(1, Types.VARCHAR);
                }

                statement.setInt(2, idContrato);
            });

        } catch (SQLException e) {
            System.out.println("Error al actualizar archivo PDF del contrato en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int obtenerUltimoIdContratoPorArrendamiento(int idArrendamiento) {
        String sql = """
                SELECT id_contrato
                FROM contrato
                WHERE id_arrendamiento = ?
                ORDER BY id_contrato DESC
                LIMIT 1
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idArrendamiento);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id_contrato");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener último contrato por arrendamiento: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public boolean contratoFirmadoParaArrendamiento(int idArrendamiento) {
        String sql = """
                SELECT 1
                FROM contrato
                WHERE id_arrendamiento = ?
                  AND id_estado_contrato = 2
                LIMIT 1
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idArrendamiento);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            System.out.println("Error al verificar contrato firmado del arrendamiento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private ContratoTabla mapearContratoTabla(ResultSet rs) throws SQLException {
        return new ContratoTabla(
                rs.getInt("id_contrato"),
                rs.getString("folio_contrato"),
                rs.getString("inmueble"),
                rs.getString("contraparte"),
                rs.getTimestamp("fecha_generacion").toString(),
                rs.getTimestamp("fecha_firma") != null ? rs.getTimestamp("fecha_firma").toString() : "",
                rs.getString("nombre_estado"),
                rs.getString("archivo_pdf") != null ? rs.getString("archivo_pdf") : ""
        );
    }
}