package com.rentify.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Inmueble {

    private int idInmueble;
    private String titulo;
    private String descripcion;
    private String calle;
    private String numeroExterior;
    private String numeroInterior;
    private String colonia;
    private String ciudad;
    private String estadoProvincia;
    private String codigoPostal;
    private BigDecimal precioRenta;
    private BigDecimal superficieM2;
    private Integer habitaciones;
    private BigDecimal banos;
    private Integer estacionamientos;
    private boolean mascotasPermitidas;
    private LocalDateTime fechaRegistro;
    private int idUsuarioArrendador;
    private int idTipoInmueble;
    private int idEstadoInmueble;

    public Inmueble() {
    }

    public int getIdInmueble() {
        return idInmueble;
    }

    public void setIdInmueble(int idInmueble) {
        this.idInmueble = idInmueble;
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

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumeroExterior() {
        return numeroExterior;
    }

    public void setNumeroExterior(String numeroExterior) {
        this.numeroExterior = numeroExterior;
    }

    public String getNumeroInterior() {
        return numeroInterior;
    }

    public void setNumeroInterior(String numeroInterior) {
        this.numeroInterior = numeroInterior;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEstadoProvincia() {
        return estadoProvincia;
    }

    public void setEstadoProvincia(String estadoProvincia) {
        this.estadoProvincia = estadoProvincia;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public BigDecimal getPrecioRenta() {
        return precioRenta;
    }

    public void setPrecioRenta(BigDecimal precioRenta) {
        this.precioRenta = precioRenta;
    }

    public BigDecimal getSuperficieM2() {
        return superficieM2;
    }

    public void setSuperficieM2(BigDecimal superficieM2) {
        this.superficieM2 = superficieM2;
    }

    public Integer getHabitaciones() {
        return habitaciones;
    }

    public void setHabitaciones(Integer habitaciones) {
        this.habitaciones = habitaciones;
    }

    public BigDecimal getBanos() {
        return banos;
    }

    public void setBanos(BigDecimal banos) {
        this.banos = banos;
    }

    public Integer getEstacionamientos() {
        return estacionamientos;
    }

    public void setEstacionamientos(Integer estacionamientos) {
        this.estacionamientos = estacionamientos;
    }

    public boolean isMascotasPermitidas() {
        return mascotasPermitidas;
    }

    public void setMascotasPermitidas(boolean mascotasPermitidas) {
        this.mascotasPermitidas = mascotasPermitidas;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdUsuarioArrendador() {
        return idUsuarioArrendador;
    }

    public void setIdUsuarioArrendador(int idUsuarioArrendador) {
        this.idUsuarioArrendador = idUsuarioArrendador;
    }

    public int getIdTipoInmueble() {
        return idTipoInmueble;
    }

    public void setIdTipoInmueble(int idTipoInmueble) {
        this.idTipoInmueble = idTipoInmueble;
    }

    public int getIdEstadoInmueble() {
        return idEstadoInmueble;
    }

    public void setIdEstadoInmueble(int idEstadoInmueble) {
        this.idEstadoInmueble = idEstadoInmueble;
    }
}