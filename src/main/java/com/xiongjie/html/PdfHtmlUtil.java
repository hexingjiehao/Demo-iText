package com.xiongjie.html;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;


public class PdfHtmlUtil {

    public static final String BASEURI = "src/main/resources/";

    public static void main(String[] args) throws Exception {
//        stringHtmlToPdf();
//        htmlFileToPdf();
//        stream2Stream();
//        stream2Writer();
//        stream2Document();

//        html2ItextObj();
//        html2Element();

        //某些css对浏览器渲染无效，但是对转化为pdf有效！！！

//        oldCssFontToPdf();
//        inlineCssFontToPdf();
//        internalCssFontToPdf();
//        externalCssFontToPdf();

//        absolutePositionToPdf();

//        pageToPdf();
//        pageBreakToPdf();
    }

//******************将html文件转化为pdf的多种方式******************

    public static void stringHtmlToPdf() throws Exception {
        String html = "<h1>Test</h1><p>Hello World</p>";
        HtmlConverter.convertToPdf(html, new FileOutputStream("stringHtmlToPdf.pdf"));
    }

    public static void htmlFileToPdf() throws Exception {
        String html = "htmlFileToPdf.html";
        String htmlLoc = BASEURI + html;

        HtmlConverter.convertToPdf(new File(htmlLoc), new File("htmlFileToPdf.pdf"));
    }

    public static void stream2Stream() throws Exception {
        String html = "htmlFileToPdf.html";
        String htmlLoc = BASEURI + html;
        HtmlConverter.convertToPdf(new FileInputStream(htmlLoc), new FileOutputStream("stream2Stream.pdf"));
    }

    public static void stream2Writer() throws Exception {
        String html = "htmlFileToPdf.html";
        String htmlLoc = BASEURI + html;

        PdfWriter writer = new PdfWriter("stream2Writer.pdf", new WriterProperties().setFullCompressionMode(true));
        HtmlConverter.convertToPdf(new FileInputStream(htmlLoc), writer);
    }

    public static void stream2Document() throws Exception {
        String html = "htmlFileToPdf.html";
        String htmlLoc = BASEURI + html;

        PdfWriter writer = new PdfWriter("stream2Document.pdf");
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setTagged();

        HtmlConverter.convertToPdf(new FileInputStream(htmlLoc), pdf);
    }

//******************将html文件转化为iText对象的多种方式******************

    public static void html2ItextObj() throws Exception {
        String html = "htmlFileToPdf.html";
        String htmlLoc = BASEURI + html;

        PdfWriter writer = new PdfWriter("html2ItextObj.pdf");
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setTagged();

        Document document = HtmlConverter.convertToDocument(new FileInputStream(htmlLoc), pdf,null);
        document.add(new Paragraph("Goodbye!"));
        document.close();
    }

    public static void html2Element() throws Exception {
        String html = "htmlFileToPdf.html";
        String htmlLoc = BASEURI + html;

        List<IElement> elements = HtmlConverter.convertToElements(new FileInputStream(htmlLoc), null);
        PdfDocument pdf = new PdfDocument(new PdfWriter("html2Element.pdf"));
        Document document = new Document(pdf);
        for (IElement element : elements) {
            document.add(new Paragraph(element.getClass().getName()));  //说明每个元素的类型
            document.add((IBlockElement)element);
        }
        document.close();
    }

//******************将html文件中Css的样式进行转化******************

    public static void oldCssFontToPdf() throws Exception {
        String html = "fontCss.html";
        String htmlLoc = BASEURI + html;

        HtmlConverter.convertToPdf(new File(htmlLoc), new File("oldCssToPdf.pdf"));
    }

    public static void inlineCssFontToPdf() throws Exception {
        String html = "fontCss.html";
        String htmlLoc = BASEURI + html;

        HtmlConverter.convertToPdf(new File(htmlLoc), new File("inlineCssToPdf.pdf"));
    }

    public static void internalCssFontToPdf() throws Exception {
        String html = "fontCss.html";
        String htmlLoc = BASEURI + html;

        HtmlConverter.convertToPdf(new File(htmlLoc), new File("internalCssFontToPdf.pdf"));
    }

    public static void externalCssFontToPdf() throws Exception {
        String html = "fontCss.html";
        String htmlLoc = BASEURI + html;

        HtmlConverter.convertToPdf(new File(htmlLoc), new File("externalCssFontToPdf.pdf"));
    }

//******************对html文件使用绝对路径******************

    public static void absolutePositionToPdf() throws Exception {
        String html = "absolutePosition.html";
        String htmlLoc = BASEURI + html;

        PdfWriter writer = new PdfWriter("absolutePositionToPdf.pdf");
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setTagged();

        HtmlConverter.convertToPdf(new FileInputStream(htmlLoc),pdf);
    }

//******************对html文件使用@page,完成pdf的分页操作******************

    public static void pageToPdf() throws Exception {
        String html = "page.html";
        String htmlLoc = BASEURI + html;

        HtmlConverter.convertToPdf(new File(htmlLoc), new File("pageToPdf.pdf"));
    }

    public static void pageBreakToPdf() throws Exception {
        String html = "page.html";
        String htmlLoc = BASEURI + html;

        HtmlConverter.convertToPdf(new File(htmlLoc), new File("pageBreakToPdf.pdf"));
    }
}
