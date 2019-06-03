package com.xiongjie.html;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class PdfHtmlUtil {

    public static final String BASEURI = "src/main/resources/";

    public static void main(String[] args) throws Exception {
//        stringHtmlToPdf();
//        htmlFileToPdf();
//        stream2Stream();
//        stream2Writer();
        stream2Document();
    }

    public static void stringHtmlToPdf() throws Exception {
        String html ="<h1>Test</h1><p>Hello World</p>";
        HtmlConverter.convertToPdf(html, new FileOutputStream("stringHtmlToPdf.pdf"));
    }

    public static void htmlFileToPdf() throws Exception {
        String html ="htmlFileToPdf.html";
        String htmlLoc=BASEURI+html;

        HtmlConverter.convertToPdf(new File(htmlLoc), new File("htmlFileToPdf.pdf"));
    }

    public static void stream2Stream() throws Exception {
        String html ="htmlFileToPdf.html";
        String htmlLoc=BASEURI+html;
        HtmlConverter.convertToPdf(new FileInputStream(htmlLoc), new FileOutputStream("stream2Stream.pdf"));
    }

    public static void stream2Writer() throws Exception {
        String html ="htmlFileToPdf.html";
        String htmlLoc=BASEURI+html;

        PdfWriter writer = new PdfWriter("stream2Writer.pdf",new WriterProperties().setFullCompressionMode(true));
        HtmlConverter.convertToPdf(new FileInputStream(htmlLoc), writer);
    }

    public static void stream2Document() throws Exception {
        String html ="htmlFileToPdf.html";
        String htmlLoc=BASEURI+html;

        PdfWriter writer = new PdfWriter("stream2Document.pdf");
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setTagged();

        HtmlConverter.convertToPdf(new FileInputStream(htmlLoc), pdf);
    }
}
