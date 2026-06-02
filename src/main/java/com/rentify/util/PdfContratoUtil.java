package com.rentify.util;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PdfContratoUtil {

    private PdfContratoUtil() {
    }

    public static String generarContratoPdf(
            String folioContrato,
            String nombreArrendador,
            String nombreArrendatario,
            String tituloInmueble,
            String direccionInmueble,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            BigDecimal montoMensual,
            BigDecimal depositoGarantia,
            Integer diaPago,
            String observaciones
    ) throws IOException, DocumentException {

        File carpeta = new File("documentos/contratos");
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta de contratos.");
        }

        String nombreArchivo = folioContrato + ".pdf";
        File archivoPdf = new File(carpeta, nombreArchivo);

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(archivoPdf));
        document.open();

        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font subtituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font textoFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

        document.add(new Paragraph("CONTRATO DE ARRENDAMIENTO", tituloFont));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Folio: " + valor(folioContrato), textoFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("PARTES", subtituloFont));
        document.add(new Paragraph("Arrendador: " + valor(nombreArrendador), textoFont));
        document.add(new Paragraph("Arrendatario: " + valor(nombreArrendatario), textoFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("INMUEBLE", subtituloFont));
        document.add(new Paragraph("Título: " + valor(tituloInmueble), textoFont));
        document.add(new Paragraph("Dirección: " + valor(direccionInmueble), textoFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("CONDICIONES DEL ARRENDAMIENTO", subtituloFont));
        document.add(new Paragraph("Fecha de inicio: " + valorFecha(fechaInicio), textoFont));
        document.add(new Paragraph("Fecha de fin: " + valorFecha(fechaFin), textoFont));
        document.add(new Paragraph("Monto mensual: " + MonedaUtil.formatear(montoMensual), textoFont));
        document.add(new Paragraph("Depósito de garantía: " + MonedaUtil.formatear(depositoGarantia), textoFont));
        document.add(new Paragraph("Día de pago: " + (diaPago != null ? diaPago : "No especificado"), textoFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("OBSERVACIONES", subtituloFont));
        document.add(new Paragraph(valor(observaciones), textoFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph(
                "Este documento fue generado automáticamente por Rentify y resume la información registrada del arrendamiento.",
                textoFont
        ));

        document.close();

        return archivoPdf.getPath();
    }

    private static String valor(String texto) {
        return texto != null && !texto.isBlank() ? texto : "No especificado";
    }

    private static String valorFecha(LocalDate fecha) {
        return fecha != null ? fecha.toString() : "No especificada";
    }
}