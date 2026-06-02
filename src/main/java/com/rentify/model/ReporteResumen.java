package com.rentify.model;

public class ReporteResumen {

    private String categoria;
    private String concepto;
    private int total;

    public ReporteResumen(String categoria, String concepto, int total) {
        this.categoria = categoria;
        this.concepto = concepto;
        this.total = total;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getConcepto() {
        return concepto;
    }

    public int getTotal() {
        return total;
    }
}