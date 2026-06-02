package com.rentify.model;

import java.math.BigDecimal;

public class DashboardResumen {

    private int inmueblesRegistrados;
    private int inmueblesDisponibles;
    private int inmueblesOcupados;

    private int solicitudesEnviadas;
    private int solicitudesPendientes;

    private int arrendamientosActivos;

    private int contratosGenerados;
    private int contratosFirmados;
    private int contratosPorFirmar;

    private int pagosPendientes;
    private int pagosVencidos;

    private BigDecimal montoPagosPendientes = BigDecimal.ZERO;
    private BigDecimal montoPagosVencidos = BigDecimal.ZERO;

    private int incidenciasAbiertas;

    public int getInmueblesRegistrados() {
        return inmueblesRegistrados;
    }

    public void setInmueblesRegistrados(int inmueblesRegistrados) {
        this.inmueblesRegistrados = inmueblesRegistrados;
    }

    public int getInmueblesDisponibles() {
        return inmueblesDisponibles;
    }

    public void setInmueblesDisponibles(int inmueblesDisponibles) {
        this.inmueblesDisponibles = inmueblesDisponibles;
    }

    public int getInmueblesOcupados() {
        return inmueblesOcupados;
    }

    public void setInmueblesOcupados(int inmueblesOcupados) {
        this.inmueblesOcupados = inmueblesOcupados;
    }

    public int getSolicitudesEnviadas() {
        return solicitudesEnviadas;
    }

    public void setSolicitudesEnviadas(int solicitudesEnviadas) {
        this.solicitudesEnviadas = solicitudesEnviadas;
    }

    public int getSolicitudesPendientes() {
        return solicitudesPendientes;
    }

    public void setSolicitudesPendientes(int solicitudesPendientes) {
        this.solicitudesPendientes = solicitudesPendientes;
    }

    public int getArrendamientosActivos() {
        return arrendamientosActivos;
    }

    public void setArrendamientosActivos(int arrendamientosActivos) {
        this.arrendamientosActivos = arrendamientosActivos;
    }

    public int getContratosGenerados() {
        return contratosGenerados;
    }

    public void setContratosGenerados(int contratosGenerados) {
        this.contratosGenerados = contratosGenerados;
    }

    public int getContratosFirmados() {
        return contratosFirmados;
    }

    public void setContratosFirmados(int contratosFirmados) {
        this.contratosFirmados = contratosFirmados;
    }

    public int getContratosPorFirmar() {
        return contratosPorFirmar;
    }

    public void setContratosPorFirmar(int contratosPorFirmar) {
        this.contratosPorFirmar = contratosPorFirmar;
    }

    public int getPagosPendientes() {
        return pagosPendientes;
    }

    public void setPagosPendientes(int pagosPendientes) {
        this.pagosPendientes = pagosPendientes;
    }

    public int getPagosVencidos() {
        return pagosVencidos;
    }

    public void setPagosVencidos(int pagosVencidos) {
        this.pagosVencidos = pagosVencidos;
    }

    public BigDecimal getMontoPagosPendientes() {
        return montoPagosPendientes;
    }

    public void setMontoPagosPendientes(BigDecimal montoPagosPendientes) {
        this.montoPagosPendientes = montoPagosPendientes;
    }

    public BigDecimal getMontoPagosVencidos() {
        return montoPagosVencidos;
    }

    public void setMontoPagosVencidos(BigDecimal montoPagosVencidos) {
        this.montoPagosVencidos = montoPagosVencidos;
    }

    public int getIncidenciasAbiertas() {
        return incidenciasAbiertas;
    }

    public void setIncidenciasAbiertas(int incidenciasAbiertas) {
        this.incidenciasAbiertas = incidenciasAbiertas;
    }
}