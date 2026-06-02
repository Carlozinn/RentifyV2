package com.rentify.database;

import com.rentify.config.TipoBaseDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MultiDatabaseExecutor {

    private static final List<TipoBaseDatos> BASES_DATOS = List.of(
            TipoBaseDatos.POSTGRESQL,
            TipoBaseDatos.MARIADB,
            TipoBaseDatos.MYSQL
    );

    private MultiDatabaseExecutor() {
    }

    public static boolean ejecutarActualizacion(String sql, ParametrosSQL parametros) throws SQLException {
        for (TipoBaseDatos tipoBaseDatos : BASES_DATOS) {
            try (Connection conexion = DatabaseConnectionManager.getConnection(tipoBaseDatos);
                 PreparedStatement statement = conexion.prepareStatement(sql)) {

                parametros.aplicar(statement);
                statement.executeUpdate();

            } catch (SQLException e) {
                System.err.println("Error ejecutando en " + tipoBaseDatos + ": " + e.getMessage());
                throw e;
            }
        }

        return true;
    }

    public static boolean ejecutarTransaccion(OperacionTransaccional operacion) throws SQLException {
        for (TipoBaseDatos tipoBaseDatos : BASES_DATOS) {
            Connection conexion = null;

            try {
                conexion = DatabaseConnectionManager.getConnection(tipoBaseDatos);
                conexion.setAutoCommit(false);

                operacion.ejecutar(conexion);

                conexion.commit();

            } catch (SQLException e) {
                if (conexion != null) {
                    try {
                        conexion.rollback();
                    } catch (SQLException rollbackException) {
                        rollbackException.printStackTrace();
                    }
                }

                System.err.println("Error en transacción de " + tipoBaseDatos + ": " + e.getMessage());
                throw e;

            } finally {
                if (conexion != null) {
                    try {
                        conexion.setAutoCommit(true);
                        conexion.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return true;
    }

    @FunctionalInterface
    public interface OperacionTransaccional {
        void ejecutar(Connection conexion) throws SQLException;
    }
}