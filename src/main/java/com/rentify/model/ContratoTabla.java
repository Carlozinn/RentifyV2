package com.rentify.model;

public class ContratoTabla {

    private int idContrato;
    private String folioContrato;
    private String inmueble;
    private String contraparte;
    private String fechaGeneracion;
    private String fechaFirma;
    private String estado;
    private String archivoPdf;

    public ContratoTabla(int idContrato, String folioContrato, String inmueble,
                         String contraparte, String fechaGeneracion,
                         String fechaFirma, String estado, String archivoPdf) {
        this.idContrato = idContrato;
        this.folioContrato = folioContrato;
        this.inmueble = inmueble;
        this.contraparte = contraparte;
        this.fechaGeneracion = fechaGeneracion;
        this.fechaFirma = fechaFirma;
        this.estado = estado;
        this.archivoPdf = archivoPdf;
    }

    public int getIdContrato() {
        return idContrato;
    }

    public String getFolioContrato() {
        return folioContrato;
    }

    public String getInmueble() {
        return inmueble;
    }

    public String getContraparte() {
        return contraparte;
    }

    public String getFechaGeneracion() {
        return fechaGeneracion;
    }

    public String getFechaFirma() {
        return fechaFirma;
    }

    public String getEstado() {
        return estado;
    }

    public String getArchivoPdf() {
        return archivoPdf;
    }
}