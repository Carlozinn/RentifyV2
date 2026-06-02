package com.rentify.config;

public class DatabaseConfig {

    private final String url;
    private final String usuario;
    private final String password;

    public DatabaseConfig(String url, String usuario, String password) {
        this.url = url;
        this.usuario = usuario;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPassword() {
        return password;
    }
}