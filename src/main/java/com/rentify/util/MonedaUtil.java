package com.rentify.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class MonedaUtil {

    private MonedaUtil() {
    }

    public static String formatear(BigDecimal valor) {
        if (valor == null) {
            return "$0.00";
        }

        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(Locale.US);
        DecimalFormat formato = new DecimalFormat("$#,##0.00", simbolos);

        return formato.format(valor);
    }

    public static String formatear(String valor) {
        if (valor == null || valor.isBlank()) {
            return "$0.00";
        }

        try {
            return formatear(parsear(valor));
        } catch (NumberFormatException e) {
            return valor;
        }
    }

    public static BigDecimal parsear(String valor) {
        if (valor == null || valor.isBlank()) {
            return BigDecimal.ZERO;
        }

        String limpio = valor
                .replace("$", "")
                .replace(",", "")
                .trim();

        return new BigDecimal(limpio);
    }
}