package com.rentify.model;

public class SolicitudTabla {

    private int idSolicitud;
    private String inmueble;
    private String arrendatario;
    private String mensaje;
    private String fechaSolicitud;
    private String estado;

    public SolicitudTabla(int idSolicitud, String inmueble, String arrendatario,
                          String mensaje, String fechaSolicitud, String estado) {
        this.idSolicitud = idSolicitud;
        this.inmueble = inmueble;
        this.arrendatario = arrendatario;
        this.mensaje = mensaje;
        this.fechaSolicitud = fechaSolicitud;
        this.estado = estado;
    }

    public int getIdSolicitud() {
        return idSolicitud;
    }

    public String getInmueble() {
        return inmueble;
    }

    public String getArrendatario() {
        return arrendatario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    public String getEstado() {
        return estado;
    }
}