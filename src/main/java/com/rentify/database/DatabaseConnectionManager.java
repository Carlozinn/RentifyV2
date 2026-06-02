package com.rentify.database;

import com.rentify.config.DatabaseConfig;
import com.rentify.config.TipoBaseDatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {

    public static Connection getConnection(TipoBaseDatos tipoBaseDatos) throws SQLException {
        DatabaseConfig config = obtenerConfiguracion(tipoBaseDatos);

        return DriverManager.getConnection(
                config.getUrl(),
                config.getUsuario(),
                config.getPassword()
        );
    }

    private static DatabaseConfig obtenerConfiguracion(TipoBaseDatos tipoBaseDatos) {
        return switch (tipoBaseDatos) {
            case POSTGRESQL -> new DatabaseConfig(
                    "jdbc:postgresql://localhost:5432/RENTIFY",
                    "Rentify",
                    "userRentify"
            );

            case MARIADB -> new DatabaseConfig(
                    "jdbc:mariadb://localhost:3306/Rentify",
                    "rentify_user",
                    "Rentify123"
            );

            case MYSQL -> new DatabaseConfig(
                    "jdbc:mysql://localhost:3307/Rentify?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                    "rentify_user",
                    "Rentify123"
            );
        };
    }
}