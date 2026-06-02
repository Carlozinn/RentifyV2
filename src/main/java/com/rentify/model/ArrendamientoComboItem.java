package com.rentify.model;

public class ArrendamientoComboItem {

    private int idArrendamiento;
    private String descripcion;

    public ArrendamientoComboItem(int idArrendamiento, String descripcion) {
        this.idArrendamiento = idArrendamiento;
        this.descripcion = descripcion;
    }

    public int getIdArrendamiento() {
        return idArrendamiento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}