package com.rentify.model;

public class PagoTabla {

    private int idPago;
    private String inmueble;
    private String contraparte;
    private String fechaVencimiento;
    private String fechaPago;
    private String periodo;
    private String monto;
    private String metodoPago;
    private String referenciaPago;
    private String comprobante;
    private String estado;

    public PagoTabla(int idPago, String inmueble, String contraparte,
                     String fechaVencimiento, String fechaPago,
                     String periodo, String monto,
                     String metodoPago, String referenciaPago,
                     String comprobante, String estado) {
        this.idPago = idPago;
        this.inmueble = inmueble;
        this.contraparte = contraparte;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaPago = fechaPago;
        this.periodo = periodo;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.referenciaPago = referenciaPago;
        this.comprobante = comprobante;
        this.estado = estado;
    }

    public int getIdPago() {
        return idPago;
    }

    public String getInmueble() {
        return inmueble;
    }

    public String getContraparte() {
        return contraparte;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public String getPeriodo() {
        return periodo;
    }

    public String getMonto() {
        return monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public String getReferenciaPago() {
        return referenciaPago;
    }

    public String getComprobante() {
        return comprobante;
    }

    public String getEstado() {
        return estado;
    }
}