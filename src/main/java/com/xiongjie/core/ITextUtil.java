package com.xiongjie.core;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfChoiceFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ITextUtil {

    public static void main(String[] args) throws Exception {
        hello();
        font();
        list();
//        image();
//        table();
//        axes();
//        textState();
//        render();
//        block();
//        event();
//        annotation();
//        linkAnnotation();
//        form();
    }

    /**
     * 创建pdf
     *
     * @throws Exception
     */
    public static void hello() throws Exception {
        OutputStream fos = new FileOutputStream("hello.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("Hello World!"));
        document.close();
    }

    /**
     * 不支持中文？？？
     *
     * @throws Exception
     */
    public static void font() throws Exception {
        OutputStream fos = new FileOutputStream("font.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
        document.add(new Paragraph("iText的字体设置good").setFont(font));
        document.close();
    }

    public static void list() throws Exception {
        OutputStream fos = new FileOutputStream("list.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        List list = new List().setSymbolIndent(12).setListSymbol("\u2022");
        list.add(new ListItem("good"))
                .add(new ListItem("news"));
        document.add(list);
        document.close();
    }

    /**
     * 图片的格式可能溢出
     *
     * @throws Exception
     */
    public static void image() throws Exception {
        OutputStream fos = new FileOutputStream("image.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        Image fox = new Image(ImageDataFactory.create("古剑奇谭壁纸.jpg"));
        Paragraph p = new Paragraph("this is a iamge")
                .add(fox);
        document.add(p);
        document.close();
    }

    /**
     * 表格
     *
     * @throws Exception
     */
    public static void table() throws Exception {
        OutputStream fos = new FileOutputStream("table.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4.rotate());
        document.setMargins(20, 20, 20, 20);

        Table table = new Table(new float[]{4, 1, 3}).useAllAvailableWidth();

        BufferedReader br = new BufferedReader(new FileReader("data.csv"));
        String line = br.readLine();
        PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
        PdfFont bold = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
        process(table, line, bold, true);
        while ((line = br.readLine()) != null) {
            process(table, line, font, false);
        }
        br.close();
        document.add(table);
        document.close();
    }

    //---------------------华丽的分割线---------------------------
    public static void process(Table table, String line, PdfFont font, boolean isHeader) {
        StringTokenizer tokenizer = new StringTokenizer(line, ";");
        while (tokenizer.hasMoreTokens()) {
            if (isHeader) {
                table.addHeaderCell(
                        new Cell().add(
                                new Paragraph(tokenizer.nextToken()).setFont(font)));
            } else {
                table.addCell(
                        new Cell().add(
                                new Paragraph(tokenizer.nextToken()).setFont(font)));
            }
        }
    }

    /**
     * 画坐标轴和网格线，注意每次描绘，都重新设置画笔颜色
     *
     * @throws Exception
     */
    public static void axes() throws Exception {
        OutputStream fos = new FileOutputStream("axes.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdf = new PdfDocument(writer);
        PageSize ps = PageSize.A4.rotate();
        PdfPage page = pdf.addNewPage(ps);
        PdfCanvas canvas = new PdfCanvas(page);

        //移动坐标中心
        canvas.concatMatrix(1, 0, 0, 1, ps.getWidth() / 2, ps.getHeight() / 2);

        //颜色
        Color grayColor = new DeviceCmyk(0.f, 0.f, 0.f, 0.875f);
        Color greenColor = new DeviceCmyk(1.f, 0.f, 1.f, 0.176f);
        Color blueColor = new DeviceCmyk(1.f, 0.156f, 0.f, 0.118f);

        //设置线的颜色
        canvas.setLineWidth(0.5f).setStrokeColor(blueColor);

        //画网格线
        for (int i = -((int) ps.getHeight() / 2 - 57); i < ((int) ps.getHeight() / 2 - 56); i += 40) {
            canvas.moveTo(-(ps.getWidth() / 2 - 15), i)
                    .lineTo(ps.getWidth() / 2 - 15, i);
        }
        for (int j = -((int) ps.getWidth() / 2 - 61); j < ((int) ps.getWidth() / 2 - 60); j += 40) {
            canvas.moveTo(j, -(ps.getHeight() / 2 - 15))
                    .lineTo(j, ps.getHeight() / 2 - 15);
        }
        canvas.stroke();

        //画线
        canvas.setLineWidth(3).setStrokeColor(grayColor);
        canvas.moveTo(-(ps.getWidth() / 2 - 15), 0)
                .lineTo(ps.getWidth() / 2 - 15, 0)
                .stroke();
        canvas.setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.ROUND)
                .moveTo(ps.getWidth() / 2 - 25, -10)
                .lineTo(ps.getWidth() / 2 - 15, 0)
                .lineTo(ps.getWidth() / 2 - 25, 10).stroke()
                .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER);

        canvas.moveTo(0, -(ps.getHeight() / 2 - 15))
                .lineTo(0, ps.getHeight() / 2 - 15)
                .stroke();
        canvas.saveState()
                .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.ROUND)
                .moveTo(-10, ps.getHeight() / 2 - 25)
                .lineTo(0, ps.getHeight() / 2 - 15)
                .lineTo(10, ps.getHeight() / 2 - 25).stroke()
                .restoreState();
        //画刻度
        for (int i = -((int) ps.getWidth() / 2 - 61);
             i < ((int) ps.getWidth() / 2 - 60); i += 40) {
            canvas.moveTo(i, 5).lineTo(i, -5);
        }
        for (int j = -((int) ps.getHeight() / 2 - 57);
             j < ((int) ps.getHeight() / 2 - 56); j += 40) {
            canvas.moveTo(5, j).lineTo(-5, j);
        }
        canvas.stroke();

        //画虚斜线
        canvas.setLineWidth(2).setStrokeColor(greenColor)
                .setLineDash(10, 10, 8)
                .moveTo(-(ps.getWidth() / 2 - 15), -(ps.getHeight() / 2 - 15))
                .lineTo(ps.getWidth() / 2 - 15, ps.getHeight() / 2 - 15).stroke();
        canvas.stroke();

        pdf.close();
    }

    /**
     * 精细的文本描绘
     *
     * @throws Exception
     */
    public static void textState() throws Exception {
        OutputStream fos = new FileOutputStream("textState.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdf = new PdfDocument(writer);
        PageSize ps = PageSize.A4.rotate();
        PdfPage page = pdf.addNewPage(ps);
        PdfCanvas canvas = new PdfCanvas(page);

        //背景色
        canvas.rectangle(0, 0, ps.getWidth(), ps.getHeight())
                .setColor(ColorConstants.BLACK, true)
                .fill();

        ArrayList<String> text = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            text.add(" hello");
        }

        int maxStringWidth = 0;
        for (String fragment : text) {
            if (fragment.length() > maxStringWidth)
                maxStringWidth = fragment.length();
        }

        canvas.concatMatrix(1, 0, 0, 1, 0, ps.getHeight());
        Color yellowColor = new DeviceCmyk(0.f, 0.0537f, 0.769f, 0.051f);
        float lineHeight = 5;
        float yOffset = -40;
        canvas.beginText()
                .setFontAndSize(PdfFontFactory.createFont(FontConstants.COURIER_BOLD), 1)
                .setColor(yellowColor, true);

        for (int j = 0; j < text.size(); j++) {
            String line = text.get(j);
            float xOffset = ps.getWidth() / 2 - 45 - 8 * j;
            float fontSizeCoeff = 6 + j;
            float lineSpacing = (lineHeight + j) * j / 1.5f;
            int stringWidth = line.length();

            for (int i = 0; i < stringWidth; i++) {
                float angle = (maxStringWidth / 2 - i) / 2f;
                float charXOffset = (4 + (float) j / 2) * i;
                canvas.setTextMatrix(fontSizeCoeff, 0, angle, fontSizeCoeff / 1.5f,
                        xOffset + charXOffset, yOffset - lineSpacing)
                        .showText(String.valueOf(line.charAt(i)));
            }
        }
        canvas.endText();
        pdf.close();
    }

    /**
     * 文档渲染器
     *
     * @throws Exception
     */
    public static void render() throws Exception {
        OutputStream fos = new FileOutputStream("render.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdf = new PdfDocument(writer);
        PageSize ps = PageSize.A5;
        Document document = new Document(pdf, ps);

        //设置列参数
        float offSet = 36;
        float columnWidth = (ps.getWidth() - offSet * 2 + 10) / 3;
        float columnHeight = ps.getHeight() - offSet * 2;

        //设置列的区域
        Rectangle[] columns = {
                new Rectangle(offSet - 5, offSet, columnWidth, columnHeight),
                new Rectangle(offSet + columnWidth, offSet, columnWidth, columnHeight),
                new Rectangle(offSet + columnWidth * 2 + 5, offSet, columnWidth, columnHeight)
        };
        document.setRenderer(new ColumnDocumentRenderer(document, columns));

        //添加内容
        Image inst = new Image(ImageDataFactory.create("古剑奇谭壁纸.jpg")).setWidth(columnWidth);
        String articleInstagram = new String(Files.readAllBytes(Paths.get("data.csv")), StandardCharsets.UTF_8);
        addArticle(document,
                "this is title",
                "this is author", inst, articleInstagram);
        document.close();
    }

    //------------------------华丽的分割线------------------------
    public static void addArticle(Document doc, String title, String author, Image img, String text) throws IOException {
        Paragraph p1 = new Paragraph(title)
                .setFont(PdfFontFactory.createFont(StandardFonts.TIMES_BOLD))
                .setFontSize(14);
        doc.add(p1);
        doc.add(img);
        Paragraph p2 = new Paragraph()
                .setFont(PdfFontFactory.createFont(StandardFonts.TIMES_BOLD))
                .setFontSize(7)
                .setFontColor(ColorConstants.GRAY)
                .add(author);
        doc.add(p2);
        Paragraph p3 = new Paragraph()
                .setFont(PdfFontFactory.createFont(StandardFonts.TIMES_BOLD))
                .setFontSize(10)
                .add(text);
        doc.add(p3);
    }


    public static void block() throws Exception {
        OutputStream fos = new FileOutputStream("block.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdf = new PdfDocument(writer);
        PageSize ps = new PageSize(842, 680);
        Document document = new Document(pdf, ps);

        PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
        PdfFont bold = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
        Table table = new Table(new float[]{1.5f, 7, 2});
        table.setTextAlignment(TextAlignment.CENTER)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("data.csv"), StandardCharsets.UTF_8));
        String line = br.readLine();
        process2(table, line, bold, true);
        while ((line = br.readLine()) != null) {
            process2(table, line, font, false);
        }
        br.close();
        document.add(table);

        document.close();
    }

    //------------------------华丽的分割线------------------------
    public static void process2(Table table, String line, PdfFont font, boolean isHeader) {
        Color greenColor = new DeviceCmyk(0.78f, 0, 0.81f, 0.21f);
        Color yellowColor = new DeviceCmyk(0, 0, 0.76f, 0.01f);
        Color redColor = new DeviceCmyk(0, 0.76f, 0.86f, 0.01f);
        Color blueColor = new DeviceCmyk(0.28f, 0.11f, 0, 0);

        StringTokenizer tokenizer = new StringTokenizer(line, ";");
        int columnNumber = 0;
        while (tokenizer.hasMoreTokens()) {
            if (isHeader) {
                Cell cell = new Cell().add(new Paragraph(tokenizer.nextToken()));
                //使用自定义渲染器
                cell.setNextRenderer(new RoundedCornersCellRenderer(cell));
                cell.setPadding(5).setBorder(null);
                table.addHeaderCell(cell);
            } else {
                columnNumber++;
                Cell cell = new Cell().add(new Paragraph(tokenizer.nextToken()));
                cell.setFont(font).setBorder(new SolidBorder(ColorConstants.BLACK, 0.5f));
                switch (columnNumber) {
                    case 1:
                        cell.setBackgroundColor(greenColor);
                        break;
                    case 2:
                        cell.setBackgroundColor(yellowColor);
                        break;
                    case 3:
                        cell.setBackgroundColor(redColor);
                        break;
                    default:
                        cell.setBackgroundColor(blueColor);
                        break;
                }
                table.addCell(cell);
            }
        }
    }


    public static void event() throws Exception {
        OutputStream fos = new FileOutputStream("event.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdf = new PdfDocument(writer);
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new MyEventHandler());
        Document document = new Document(pdf);

        document.add(new Paragraph("Hello World!"));
        document.close();
    }

    //文本注释这里有点问题
    public static void annotation() throws Exception {
        PdfDocument pdf = new PdfDocument(new PdfWriter("annotation.pdf"));
        Document document = new Document(pdf);
        document.add(new Paragraph("Hello World!"));

        PdfAnnotation ann = new PdfTextAnnotation(new Rectangle(20, 800, 0, 0))
                .setOpen(true)
                .setColor(ColorConstants.GREEN)
                .setTitle(new PdfString("iText"))
                .setContents("With iText,you can truly take your documentation needs to the next level.");
        pdf.getFirstPage().addAnnotation(ann);
        document.close();
    }

    public static void linkAnnotation() throws Exception {
        PdfDocument pdf = new PdfDocument(new PdfWriter("linkAnnotation.pdf"));
        Document document = new Document(pdf);

        PdfLinkAnnotation annotation = new PdfLinkAnnotation(new Rectangle(0, 0))
                .setAction(PdfAction.createURI("http://itextpdf.com/"));
        Link link = new Link("here", annotation);
        Paragraph p = new Paragraph("The example of link annotation. Click ")
                .add(link.setUnderline())
                .add(" to learn more...");
        document.add(p);
        document.close();
    }

    public static void form() throws Exception {
        PdfDocument pdf = new PdfDocument(new PdfWriter("form.pdf"));
        Document document = new Document(pdf);

        Paragraph title = new Paragraph("Application for employment")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(16);
        document.add(title);
        document.add(new Paragraph("Full name:").setFontSize(12));
        document.add(new Paragraph("Native language:      English         French       German        Russian        Spanish").setFontSize(12));
        document.add(new Paragraph("Experience in:       cooking        driving           software development").setFontSize(12));
        document.add(new Paragraph("Preferred working shift:").setFontSize(12));
        document.add(new Paragraph("Additional information:").setFontSize(12));


        PdfAcroForm form = PdfAcroForm.getAcroForm(document.getPdfDocument(), true);


        PdfTextFormField nameField = PdfTextFormField.createText(document.getPdfDocument(),
                new Rectangle(99, 753, 425, 15), "name", "");
        form.addField(nameField);


        PdfButtonFormField group = PdfFormField.createRadioGroup(document.getPdfDocument(), "language", "");
        PdfFormField.createRadioButton(document.getPdfDocument(), new Rectangle(130, 728, 15, 15), group, "English");
        PdfFormField.createRadioButton(document.getPdfDocument(), new Rectangle(200, 728, 15, 15), group, "French");
        PdfFormField.createRadioButton(document.getPdfDocument(), new Rectangle(260, 728, 15, 15), group, "German");
        PdfFormField.createRadioButton(document.getPdfDocument(), new Rectangle(330, 728, 15, 15), group, "Russian");
        PdfFormField.createRadioButton(document.getPdfDocument(), new Rectangle(400, 728, 15, 15), group, "Spanish");
        form.addField(group);


        for (int i = 0; i < 3; i++) {
            PdfButtonFormField checkField = PdfFormField.createCheckBox(document.getPdfDocument(), new Rectangle(119 + i * 69, 701, 15, 15),
                    "experience".concat(String.valueOf(i + 1)), "Off", PdfFormField.TYPE_CHECK);
            form.addField(checkField);
        }


        String[] options = {"Any", "6.30 am - 2.30 pm", "1.30 pm - 9.30 pm"};
        PdfChoiceFormField choiceField = PdfFormField.createComboBox(document.getPdfDocument(), new Rectangle(163, 676, 115, 15),
                "shift", "Any", options);
        form.addField(choiceField);


        PdfTextFormField infoField = PdfTextFormField.createMultilineText(document.getPdfDocument(),
                new Rectangle(158, 625, 366, 40), "info", "");
        form.addField(infoField);


        PdfButtonFormField button = PdfFormField.createPushButton(document.getPdfDocument(),
                new Rectangle(479, 594, 45, 15), "reset", "RESET");
        button.setAction(PdfAction.createResetForm(new String[]{"name", "language", "experience1", "experience2", "experience3", "shift", "info"}, 0));
        form.addField(button);

        //扁平化
        form.flattenFields();
        document.close();
    }
}
