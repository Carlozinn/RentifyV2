package com.rentify.model;

public class IncidenciaTabla {

    private int idIncidencia;
    private String titulo;
    private String arrendamiento;
    private String reporta;
    private String fechaReporte;
    private String prioridad;
    private String estado;

    public IncidenciaTabla(int idIncidencia, String titulo, String arrendamiento,
                           String reporta, String fechaReporte,
                           String prioridad, String estado) {
        this.idIncidencia = idIncidencia;
        this.titulo = titulo;
        this.arrendamiento = arrendamiento;
        this.reporta = reporta;
        this.fechaReporte = fechaReporte;
        this.prioridad = prioridad;
        this.estado = estado;
    }

    public int getIdIncidencia() {
        return idIncidencia;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getArrendamiento() {
        return arrendamiento;
    }

    public String getReporta() {
        return reporta;
    }

    public String getFechaReporte() {
        return fechaReporte;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public String getEstado() {
        return estado;
    }
}