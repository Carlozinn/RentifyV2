package com.rentify.model;

public class IncidenciaDetalle {

    private int idIncidencia;
    private String titulo;
    private String descripcion;
    private String arrendamiento;
    private String reporta;
    private String fechaReporte;
    private String fechaCierre;
    private String solucion;
    private String prioridad;
    private String estado;

    public int getIdIncidencia() {
        return idIncidencia;
    }

    public void setIdIncidencia(int idIncidencia) {
        this.idIncidencia = idIncidencia;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getArrendamiento() {
        return arrendamiento;
    }

    public void setArrendamiento(String arrendamiento) {
        this.arrendamiento = arrendamiento;
    }

    public String getReporta() {
        return reporta;
    }

    public void setReporta(String reporta) {
        this.reporta = reporta;
    }

    public String getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(String fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    public String getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(String fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getSolucion() {
        return solucion;
    }

    public void setSolucion(String solucion) {
        this.solucion = solucion;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}