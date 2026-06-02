package com.rentify.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ArrendamientoContratoData {

    private int idArrendamiento;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal montoMensual;
    private BigDecimal depositoGarantia;
    private Integer diaPago;
    private String observaciones;
    private String tituloInmueble;
    private String direccionInmueble;
    private String nombreArrendador;
    private String nombreArrendatario;

    public int getIdArrendamiento() {
        return idArrendamiento;
    }

    public void setIdArrendamiento(int idArrendamiento) {
        this.idArrendamiento = idArrendamiento;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public BigDecimal getMontoMensual() {
        return montoMensual;
    }

    public void setMontoMensual(BigDecimal montoMensual) {
        this.montoMensual = montoMensual;
    }

    public BigDecimal getDepositoGarantia() {
        return depositoGarantia;
    }

    public void setDepositoGarantia(BigDecimal depositoGarantia) {
        this.depositoGarantia = depositoGarantia;
    }

    public Integer getDiaPago() {
        return diaPago;
    }

    public void setDiaPago(Integer diaPago) {
        this.diaPago = diaPago;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getTituloInmueble() {
        return tituloInmueble;
    }

    public void setTituloInmueble(String tituloInmueble) {
        this.tituloInmueble = tituloInmueble;
    }

    public String getDireccionInmueble() {
        return direccionInmueble;
    }

    public void setDireccionInmueble(String direccionInmueble) {
        this.direccionInmueble = direccionInmueble;
    }

    public String getNombreArrendador() {
        return nombreArrendador;
    }

    public void setNombreArrendador(String nombreArrendador) {
        this.nombreArrendador = nombreArrendador;
    }

    public String getNombreArrendatario() {
        return nombreArrendatario;
    }

    public void setNombreArrendatario(String nombreArrendatario) {
        this.nombreArrendatario = nombreArrendatario;
    }
}