package com.rentify.database;

import com.rentify.config.TipoBaseDatos;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DatabaseConnectionManager.getConnection(TipoBaseDatos.POSTGRESQL);
    }
}