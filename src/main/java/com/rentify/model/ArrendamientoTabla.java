package com.rentify.model;

public class ArrendamientoTabla {

    private int idArrendamiento;
    private String inmueble;
    private String contraparte;
    private String fechaInicio;
    private String fechaFin;
    private String montoMensual;
    private String estado;

    public ArrendamientoTabla(int idArrendamiento, String inmueble, String contraparte,
                              String fechaInicio, String fechaFin, String montoMensual, String estado) {
        this.idArrendamiento = idArrendamiento;
        this.inmueble = inmueble;
        this.contraparte = contraparte;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.montoMensual = montoMensual;
        this.estado = estado;
    }

    public int getIdArrendamiento() {
        return idArrendamiento;
    }

    public String getInmueble() {
        return inmueble;
    }

    public String getContraparte() {
        return contraparte;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public String getMontoMensual() {
        return montoMensual;
    }

    public String getEstado() {
        return estado;
    }
}