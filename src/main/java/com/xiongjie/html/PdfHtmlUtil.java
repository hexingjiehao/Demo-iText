package com.xiongjie.html;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.attach.ITagWorker;
import com.itextpdf.html2pdf.attach.ProcessorContext;
import com.itextpdf.html2pdf.attach.impl.DefaultTagWorkerFactory;
import com.itextpdf.html2pdf.attach.impl.OutlineHandler;
import com.itextpdf.html2pdf.attach.impl.tags.SpanTagWorker;
import com.itextpdf.html2pdf.css.apply.ICssApplier;
import com.itextpdf.html2pdf.css.apply.impl.DefaultCssApplierFactory;
import com.itextpdf.html2pdf.html.TagConstants;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.pdfa.PdfADocument;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.media.MediaType;
import com.itextpdf.styledxmlparser.node.IElementNode;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class PdfHtmlUtil {

    public static final String BASEURI = "src/main/resources/";

    public static void main(String[] args) throws Exception {
        stringHtmlToPdf();
        htmlFileToPdf();
        stream2Stream();
        stream2Writer();
        stream2Document();

        html2ItextObj();
        html2Element();

        //某些css对浏览器渲染无效，但是对转化为pdf有效！！！

        oldCssFontToPdf();
        inlineCssFontToPdf();
        internalCssFontToPdf();
        externalCssFontToPdf();

        absolutePositionToPdf();

        pageToPdf();
        pageBreakToPdf();

        layoutToPdf();
        deskLayoutToPdf();
        tabletLayoutToPdf();
        smartphoneLayoutToPdf();

        xmlToHtmlToPdf();

        addPagefooterToPdf();
        largeHtmlToOnePdf();
        bookMarkToOnePdf();

        htmlToPdfA2B();
        htmlToPdfA2A();
        htmlToPdfA3A();

        tagWorkerFactoryToPdf();
        customtagsToPdf();
        customCssToPdf();

        standardType1ToPdf();
        itextWithFontToPdf();

        systemFontToPdf();
        webFontToPdf();
        addExtraFontToPdf();
        addExtraFontDirToPdf();

        encodeToPdf();
        internationalizationToPdf();
        chineseGarbledToPdf();
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

//******************对html文件转化为不同的布局格式******************

    public static void layoutToPdf() throws Exception {
        String html = "layout.html";
        String htmlLoc = BASEURI + html;

        HtmlConverter.convertToPdf(new File(htmlLoc), new File("layoutToPdf.pdf"));
    }

    public static void deskLayoutToPdf() throws Exception {
        String html = "layout.html";
        String htmlLoc = BASEURI + html;

        PdfWriter writer = new PdfWriter("deskLayoutToPdf.pdf");
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setTagged();
        PageSize pageSize = PageSize.A4.rotate();
        pdf.setDefaultPageSize(pageSize);

        ConverterProperties properties = new ConverterProperties();
        properties.setBaseUri(BASEURI);

        MediaDeviceDescription mediaDeviceDescription = new MediaDeviceDescription(MediaType.SCREEN);
        mediaDeviceDescription.setWidth(pageSize.getWidth());
        properties.setMediaDeviceDescription(mediaDeviceDescription);

        HtmlConverter.convertToPdf(new FileInputStream(htmlLoc), pdf, properties);
    }

    public static void tabletLayoutToPdf() throws Exception {
        String html = "layout.html";
        String htmlLoc = BASEURI + html;

        PdfWriter writer = new PdfWriter("tabletLayoutToPdf.pdf");
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setTagged();
        PageSize pageSize = new PageSize(575, 1500);
        pdf.setDefaultPageSize(pageSize);

        ConverterProperties properties = new ConverterProperties();
        properties.setBaseUri(BASEURI);

        MediaDeviceDescription mediaDeviceDescription = new MediaDeviceDescription(MediaType.SCREEN);
        mediaDeviceDescription.setWidth(pageSize.getWidth());
        properties.setMediaDeviceDescription(mediaDeviceDescription);

        HtmlConverter.convertToPdf(new FileInputStream(htmlLoc), pdf, properties);
    }

    public static void smartphoneLayoutToPdf() throws Exception {
        String html = "layout.html";
        String htmlLoc = BASEURI + html;

        PdfWriter writer = new PdfWriter("smartphoneLayoutToPdf.pdf");
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setTagged();
        PageSize pageSize = new PageSize(440, 2000);
        pdf.setDefaultPageSize(pageSize);

        ConverterProperties properties = new ConverterProperties();
        properties.setBaseUri(BASEURI);

        MediaDeviceDescription mediaDeviceDescription = new MediaDeviceDescription(MediaType.SCREEN);
        mediaDeviceDescription.setWidth(pageSize.getWidth());
        properties.setMediaDeviceDescription(mediaDeviceDescription);

        HtmlConverter.convertToPdf(new FileInputStream(htmlLoc), pdf, properties);
    }

//******************将xml文件转化为html,然后将对html文件转化为pdf******************

    public static void xmlToHtmlToPdf() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(baos);
        StreamSource xml = new StreamSource(new File(BASEURI+"movie.xml"));
        StreamSource xsl = new StreamSource(new File(BASEURI+"movie.xsl"));
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(xsl);
        transformer.transform(xml, new StreamResult(writer)); writer.flush();
        writer.close();
        byte[] res=baos.toByteArray();

        HtmlConverter.convertToPdf(new ByteArrayInputStream(res), new FileOutputStream("xmlToHtmlToPdf.pdf"));
    }

//******************使用事件监听操作*********************************

    public static void addPagefooterToPdf() throws Exception {
        String html = "htmlFileToPdf.html";
        String htmlLoc = BASEURI + html;

        PdfWriter writer = new PdfWriter("addPagefooterToPdf.pdf");
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setTagged();

        IEventHandler handler = new BackgroundListener(pdf, "hello.pdf");
        pdf.addEventHandler(PdfDocumentEvent.START_PAGE, handler);

        HtmlConverter.convertToPdf(new FileInputStream(htmlLoc), pdf);
    }

    public static void largeHtmlToOnePdf() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(baos);
        StreamSource xml = new StreamSource(new File(BASEURI+"movie.xml"));
        StreamSource xsl = new StreamSource(new File(BASEURI+"movie.xsl"));
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(xsl);
        transformer.transform(xml, new StreamResult(writer)); writer.flush();
        writer.close();
        byte[] res=baos.toByteArray();


        PdfWriter pwriter = new PdfWriter("largeHtmlToOnePdf.pdf");
        PdfDocument pdf = new PdfDocument(pwriter);
        pdf.setDefaultPageSize(new PageSize(595, 14400));

        Document document = HtmlConverter.convertToDocument(new ByteArrayInputStream(res), pdf,null);
        EndPosition endPosition = new EndPosition();
        LineSeparator separator = new LineSeparator(endPosition);
        document.add(separator);
        document.getRenderer().close();

        PdfPage page = pdf.getPage(1);
        float y = endPosition.getY() - 36;
        page.setMediaBox(new Rectangle(0, y, 595, 14400 - y));
        document.close();
    }

    public static void bookMarkToOnePdf() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(baos);
        StreamSource xml = new StreamSource(new File(BASEURI+"movie.xml"));
        StreamSource xsl = new StreamSource(new File(BASEURI+"movie_overview.xsl"));
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(xsl);
        transformer.transform(xml, new StreamResult(writer)); writer.flush();
        writer.close();
        byte[] res=baos.toByteArray();

        ConverterProperties properties = new ConverterProperties();
        properties.setBaseUri(BASEURI);
        OutlineHandler outlineHandler = OutlineHandler.createStandardHandler();
        properties.setOutlineHandler(outlineHandler);
        HtmlConverter.convertToPdf(new ByteArrayInputStream(res), new FileOutputStream("bookMarkToOnePdf.pdf"), properties);
    }


//******************使用pdfHtml创建PDF/A文档*********************************

    /**
     * 需要设置body的字体集
     * 比如：<body style=""font-family: FreeSans"></body>
     * @throws Exception
     */
    public static void htmlToPdfA2B() throws Exception {
        String html = "htmlFileToPdf.html";
        String htmlLoc = BASEURI + html;

        PdfWriter writer = new PdfWriter("htmlToPdfA2B.pdf");
        PdfADocument pdf = new PdfADocument(writer,
                PdfAConformanceLevel.PDF_A_2B,
                new PdfOutputIntent("Custom",
                        "",
                        "http://www.color.org",
                        "sRGB IEC61966-2.1",
                        new FileInputStream("src/main/resources/sRGB_CS_profile.icm")));
        HtmlConverter.convertToPdf(new FileInputStream(htmlLoc), pdf);
    }

    /**
     * 需要设置body的字体集
     * @throws Exception
     */
    public static void htmlToPdfA2A() throws Exception {
        String html = "htmlFileToPdf.html";
        String htmlLoc = BASEURI + html;

        PdfWriter writer = new PdfWriter("htmlToPdfA2A.pdf");
        PdfADocument pdf = new PdfADocument(writer,
                PdfAConformanceLevel.PDF_A_2A,
                new PdfOutputIntent("Custom",
                        "",
                        "http://www.color.org",
                        "sRGB IEC61966-2.1",
                        new FileInputStream("src/main/resources/sRGB_CS_profile.icm")));
        pdf.setTagged();
        HtmlConverter.convertToPdf(new FileInputStream(htmlLoc), pdf);
    }

    /**
     * 需要设置body的字体集
     * @throws Exception
     */
    public static void htmlToPdfA3A() throws Exception {
        String html = "htmlFileToPdf.html";
        String htmlLoc = BASEURI + html;

        PdfWriter writer = new PdfWriter("htmlToPdfA3A.pdf");
        PdfADocument pdf = new PdfADocument(writer,
                PdfAConformanceLevel.PDF_A_3A,
                new PdfOutputIntent("Custom",
                        "",
                        "http://www.color.org",
                        "sRGB IEC61966-2.1",
                        new FileInputStream("src/main/resources/sRGB_CS_profile.icm")));
        pdf.setTagged();

        pdf.addFileAttachment("Movie info",
                PdfFileSpec.createEmbeddedFileSpec(pdf,
                Files.readAllBytes(Paths.get(BASEURI+"movie.xml")),
                        "Movie info",
                        "movie.xml",
                        PdfName.ApplicationXml,
                        new PdfDictionary(),
                        PdfName.Data));

        HtmlConverter.convertToPdf(new FileInputStream(htmlLoc), pdf);
    }


//******************使用pdfHtml渲染自定义标签和css*********************************

    public static void tagWorkerFactoryToPdf() throws Exception {
        String html = "htmlFileToPdf.html";
        String htmlLoc = BASEURI + html;

        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setTagWorkerFactory(new DefaultTagWorkerFactory() {

            @Override
            public ITagWorker getCustomTagWorker(IElementNode tag, ProcessorContext context) {
                if ("h1".equalsIgnoreCase(tag.name()) ) {
                    System.out.println("找到h1标签");

                    //这里将h1标签按照span标签进行渲染
                    return new SpanTagWorker(tag, context);

                }
                return null;
            }
        });
        HtmlConverter.convertToPdf(new File(htmlLoc), new File("tagWorkerFactoryToPdf.pdf"), converterProperties);
    }

    public static void customtagsToPdf() throws Exception {
        String html = "customTag.html";
        String htmlLoc = BASEURI + html;

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setTagWorkerFactory(new DefaultTagWorkerFactory() {

            @Override
            public ITagWorker getCustomTagWorker( IElementNode tag, ProcessorContext context) {
                if ("name".equalsIgnoreCase(tag.name()) ) {
                    return new SpanTagWorker(tag, context){

                        @Override
                        public boolean processContent(String content, ProcessorContext context) {
                            return super.processContent("Bruno Lowagie", context);
                        }
                    };
                }else if ("date".equalsIgnoreCase(tag.name()) ) {
                    return new SpanTagWorker(tag, context) {

                        @Override
                        public boolean processContent(String content, ProcessorContext context) {
                            return super.processContent(sdf.format(new Date()), context);
                        }
                    };
                }
                return null;
            }
        });
        HtmlConverter.convertToPdf(new File(htmlLoc), new File("customtagsToPdf.pdf"), converterProperties);
    }


    public static void customCssToPdf() throws Exception {
        String html = "customCss.html";
        String htmlLoc = BASEURI + html;

        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setCssApplierFactory(new DefaultCssApplierFactory() {

            ICssApplier dutchCssColor = new DutchColorCssApplier();
            @Override
            public ICssApplier getCustomCssApplier(IElementNode tag) {
                if( tag.name().equals(TagConstants.DIV) ){
                    return dutchCssColor;
                }
                return null;
            }
        });
        HtmlConverter.convertToPdf(new File(htmlLoc), new File("customCssToPdf.pdf"), converterProperties);
    }

//******************使用pdfHtml加载需要的字体*********************************

    public static void standardType1ToPdf() throws Exception {
        String html = "font_standardtype1.html";
        String htmlLoc = BASEURI + html;

        HtmlConverter.convertToPdf(new File(htmlLoc), new File("standardType1ToPdf.pdf"));
    }

    public static void itextWithFontToPdf() throws Exception {
        String html = "font_itextwithfont.html";
        String htmlLoc = BASEURI + html;

        HtmlConverter.convertToPdf(new File(htmlLoc), new File("itextWithFontToPdf.pdf"));
    }

    public static void systemFontToPdf() throws Exception {
        String html = "font_system.html";
        String htmlLoc = BASEURI + html;

        ConverterProperties properties = new ConverterProperties();
        properties.setFontProvider(new DefaultFontProvider(true, true, true));

        HtmlConverter.convertToPdf(new File(htmlLoc), new File("systemFontToPdf.pdf"),properties);
    }

    //使用@font-face,将自动下载该字体
    public static void webFontToPdf() throws Exception {
        String html = "font_web.html";
        String htmlLoc = BASEURI + html;

        HtmlConverter.convertToPdf(new File(htmlLoc), new File("webFontToPdf.pdf"));
    }

    public static void addExtraFontToPdf() throws Exception {
        String html = "font_extra.html";
        String htmlLoc = BASEURI + html;

        FontProvider fontProvider = new DefaultFontProvider();
        FontProgram fontProgram = FontProgramFactory.createFont(BASEURI+"font/Cardo-Regular.ttf");
        fontProvider.addFont(fontProgram);

        ConverterProperties properties = new ConverterProperties();
        properties.setFontProvider(fontProvider);

        HtmlConverter.convertToPdf(new File(htmlLoc), new File("addExtraFontToPdf.pdf"), properties);
    }

    public static void addExtraFontDirToPdf() throws Exception {
        String html = "font_extra.html";
        String htmlLoc = BASEURI + html;

        FontProvider fontProvider = new DefaultFontProvider();
        fontProvider.addDirectory(BASEURI+"/font/");

        ConverterProperties properties = new ConverterProperties();
        properties.setFontProvider(fontProvider);

        HtmlConverter.convertToPdf(new File(htmlLoc), new File("addExtraFontDirToPdf.pdf"), properties);
    }


//******************使用pdfHtml创建适当的编码*********************************

    public static void encodeToPdf() throws IOException {
        String html = "htmlFileToPdf.html";
        String htmlLoc = BASEURI + html;

        ConverterProperties properties = new ConverterProperties();
        FontProvider fontProvider = new DefaultFontProvider(false, false, false);
        FontProgram fontProgram = FontProgramFactory.createFont(BASEURI+"/font/Cardo-Regular.ttf");
        fontProvider.addFont(fontProgram, "Winansi"); //取值有Identity-H，Winansi
        properties.setFontProvider(fontProvider);
        HtmlConverter.convertToPdf(new File(htmlLoc), new File("encodeToPdf.pdf"), properties);
    }

//******************使用pdfHtml创建国际化的pdf*********************************

    public static void internationalizationToPdf() throws IOException {
        String html = "i18n.html";
        String htmlLoc = BASEURI + html;

        ConverterProperties properties = new ConverterProperties();
        FontProvider fontProvider = new DefaultFontProvider(false, false, false);

        String[] fonts = {
                "src/main/resources/font/NotoSans-Regular.ttf",
                "src/main/resources/font/NotoSans-Bold.ttf",
                "src/main/resources/font/NotoSansCJKsc-Regular.otf",
                "src/main/resources/font/NotoNaskhArabic-Regular.ttf",
                "src/main/resources/font/NotoSansHebrew-Regular.ttf"
        };

        for (String font : fonts) {
            FontProgram fontProgram = FontProgramFactory.createFont(font);
            fontProvider.addFont(fontProgram);
        }
        properties.setFontProvider(fontProvider);
        HtmlConverter.convertToPdf(new File(htmlLoc), new File("internationalizationToPdf.pdf"), properties);
    }

    public static void chineseGarbledToPdf() throws IOException {
        String html = "chineseGarble.html";
        String htmlLoc = BASEURI + html;

        ConverterProperties properties = new ConverterProperties();
        FontProvider fontProvider = new DefaultFontProvider();
        FontProgram fontProgram = FontProgramFactory.createFont(BASEURI+"font/NotoSansCJKsc-Regular.otf");
        fontProvider.addFont(fontProgram);

        properties.setFontProvider(fontProvider);
        HtmlConverter.convertToPdf(new File(htmlLoc), new File("chineseGarbledToPdf.pdf"), properties);
    }

}




















