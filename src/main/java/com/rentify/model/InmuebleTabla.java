package com.rentify.model;

public class InmuebleTabla {

    private int idInmueble;
    private String titulo;
    private String ciudad;
    private String tipo;
    private String estado;
    private String precioRenta;

    public InmuebleTabla(int idInmueble, String titulo, String ciudad, String tipo, String estado, String precioRenta) {
        this.idInmueble = idInmueble;
        this.titulo = titulo;
        this.ciudad = ciudad;
        this.tipo = tipo;
        this.estado = estado;
        this.precioRenta = precioRenta;
    }

    public int getIdInmueble() {
        return idInmueble;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getTipo() {
        return tipo;
    }

    public String getEstado() {
        return estado;
    }

    public String getPrecioRenta() {
        return precioRenta;
    }
}