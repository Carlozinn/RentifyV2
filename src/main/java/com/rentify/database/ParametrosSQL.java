package com.rentify.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface ParametrosSQL {
    void aplicar(PreparedStatement stmt) throws SQLException;
}