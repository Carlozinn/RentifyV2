package com.rentify.model;

public class ContactoTabla {

    private int idContacto;
    private String tipoContacto;
    private String valorContacto;
    private boolean principal;
    private boolean verificado;

    public ContactoTabla(int idContacto,
                         String tipoContacto,
                         String valorContacto,
                         boolean principal,
                         boolean verificado) {
        this.idContacto = idContacto;
        this.tipoContacto = tipoContacto;
        this.valorContacto = valorContacto;
        this.principal = principal;
        this.verificado = verificado;
    }

    public int getIdContacto() {
        return idContacto;
    }

    public String getTipoContacto() {
        return tipoContacto;
    }

    public String getValorContacto() {
        return valorContacto;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public boolean isVerificado() {
        return verificado;
    }
}