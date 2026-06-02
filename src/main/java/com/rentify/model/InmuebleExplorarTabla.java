package com.rentify.model;

public class InmuebleExplorarTabla {

    private int idInmueble;
    private String titulo;
    private String ciudad;
    private String tipo;
    private String precioRenta;
    private String arrendador;

    public InmuebleExplorarTabla(int idInmueble, String titulo, String ciudad,
                                 String tipo, String precioRenta, String arrendador) {
        this.idInmueble = idInmueble;
        this.titulo = titulo;
        this.ciudad = ciudad;
        this.tipo = tipo;
        this.precioRenta = precioRenta;
        this.arrendador = arrendador;
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

    public String getPrecioRenta() {
        return precioRenta;
    }

    public String getArrendador() {
        return arrendador;
    }
}