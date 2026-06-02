package com.rentify.dao;

import com.rentify.database.DatabaseConnection;
import com.rentify.database.MultiDatabaseExecutor;
import com.rentify.model.ImagenInmueble;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ImagenInmuebleDAO {

    public boolean insertarImagen(ImagenInmueble imagen) {
        String sql = """
                INSERT INTO imagen_inmueble
                (
                    url_imagen,
                    descripcion,
                    es_principal,
                    orden_visualizacion,
                    id_inmueble
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setString(1, imagen.getUrlImagen());
                statement.setString(2, imagen.getDescripcion());
                statement.setBoolean(3, imagen.isEsPrincipal());

                if (imagen.getOrdenVisualizacion() != null) {
                    statement.setInt(4, imagen.getOrdenVisualizacion());
                } else {
                    statement.setNull(4, Types.INTEGER);
                }

                statement.setInt(5, imagen.getIdInmueble());
            });

        } catch (SQLException e) {
            System.out.println("Error insertarImagen en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean guardarImagen(ImagenInmueble imagen) {
        return insertarImagen(imagen);
    }

    public List<ImagenInmueble> listarPorInmueble(int idInmueble) {
        List<ImagenInmueble> lista = new ArrayList<>();

        String sql = """
                SELECT 
                    id_imagen,
                    url_imagen,
                    descripcion,
                    es_principal,
                    orden_visualizacion,
                    id_inmueble
                FROM imagen_inmueble
                WHERE id_inmueble = ?
                ORDER BY 
                    es_principal DESC,
                    orden_visualizacion ASC NULLS LAST,
                    id_imagen ASC
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idInmueble);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearImagen(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error listarPorInmueble: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    public ImagenInmueble obtenerImagenPrincipal(int idInmueble) {
        String sql = """
                SELECT 
                    id_imagen,
                    url_imagen,
                    descripcion,
                    es_principal,
                    orden_visualizacion,
                    id_inmueble
                FROM imagen_inmueble
                WHERE id_inmueble = ?
                ORDER BY 
                    es_principal DESC,
                    orden_visualizacion ASC NULLS LAST,
                    id_imagen ASC
                LIMIT 1
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idInmueble);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapearImagen(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error obtenerImagenPrincipal: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public boolean marcarComoPrincipal(int idImagen, int idInmueble) {
        String sqlQuitarPrincipal = """
                UPDATE imagen_inmueble
                SET es_principal = FALSE
                WHERE id_inmueble = ?
                """;

        String sqlMarcarPrincipal = """
                UPDATE imagen_inmueble
                SET es_principal = TRUE
                WHERE id_imagen = ?
                AND id_inmueble = ?
                """;

        try {
            return MultiDatabaseExecutor.ejecutarTransaccion(conexion -> {
                try (PreparedStatement quitarPrincipal = conexion.prepareStatement(sqlQuitarPrincipal);
                     PreparedStatement marcarPrincipal = conexion.prepareStatement(sqlMarcarPrincipal)) {

                    quitarPrincipal.setInt(1, idInmueble);
                    quitarPrincipal.executeUpdate();

                    marcarPrincipal.setInt(1, idImagen);
                    marcarPrincipal.setInt(2, idInmueble);

                    int filasActualizadas = marcarPrincipal.executeUpdate();

                    if (filasActualizadas == 0) {
                        throw new SQLException("No se encontró la imagen para marcarla como principal.");
                    }
                }
            });

        } catch (SQLException e) {
            System.out.println("Error marcarComoPrincipal en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarImagen(int idImagen) {
        String sql = """
                DELETE FROM imagen_inmueble
                WHERE id_imagen = ?
                """;

        try {
            return MultiDatabaseExecutor.ejecutarActualizacion(sql, statement -> {
                statement.setInt(1, idImagen);
            });

        } catch (SQLException e) {
            System.out.println("Error eliminarImagen en las bases sincronizadas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int contarPorInmueble(int idInmueble) {
        String sql = """
                SELECT COUNT(*) AS total
                FROM imagen_inmueble
                WHERE id_inmueble = ?
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idInmueble);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error contarPorInmueble: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    public int obtenerSiguienteOrden(int idInmueble) {
        String sql = """
                SELECT COALESCE(MAX(orden_visualizacion), 0) + 1 AS siguiente_orden
                FROM imagen_inmueble
                WHERE id_inmueble = ?
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement statement = conexion.prepareStatement(sql)) {

            statement.setInt(1, idInmueble);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("siguiente_orden");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error obtenerSiguienteOrden: " + e.getMessage());
            e.printStackTrace();
        }

        return 1;
    }

    private ImagenInmueble mapearImagen(ResultSet rs) throws SQLException {
        ImagenInmueble img = new ImagenInmueble();

        img.setIdImagen(rs.getInt("id_imagen"));
        img.setUrlImagen(rs.getString("url_imagen"));
        img.setDescripcion(rs.getString("descripcion"));
        img.setEsPrincipal(rs.getBoolean("es_principal"));

        int orden = rs.getInt("orden_visualizacion");
        img.setOrdenVisualizacion(rs.wasNull() ? null : orden);

        img.setIdInmueble(rs.getInt("id_inmueble"));

        return img;
    }
}