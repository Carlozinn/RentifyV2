package com.rentify.model;

public class Contacto {

    private int idContacto;
    private String valorContacto;
    private boolean principal;
    private boolean verificado;
    private int idTipoContacto;
    private int idUsuario;

    public Contacto() {
    }

    public int getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(int idContacto) {
        this.idContacto = idContacto;
    }

    public String getValorContacto() {
        return valorContacto;
    }

    public void setValorContacto(String valorContacto) {
        this.valorContacto = valorContacto;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

    public int getIdTipoContacto() {
        return idTipoContacto;
    }

    public void setIdTipoContacto(int idTipoContacto) {
        this.idTipoContacto = idTipoContacto;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}