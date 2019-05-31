package com.xiongjie;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;

import java.io.IOException;

public class MyEventHandler implements IEventHandler {
    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdfDoc = docEvent.getDocument();

        PdfPage page = docEvent.getPage();
        int pageNumber = pdfDoc.getPageNumber(page);
        Rectangle pageSize = page.getPageSize();
        PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);

        // 设置背景
        Color limeColor = new DeviceCmyk(0.208f, 0, 0.584f, 0);
        Color blueColor = new DeviceCmyk(0.445f, 0.0546f, 0, 0.0667f);
        pdfCanvas.saveState()
                .setFillColor(pageNumber % 2 == 1 ? limeColor : blueColor)
                .rectangle(pageSize.getLeft(), pageSize.getBottom(),pageSize.getWidth(), pageSize.getHeight())
                .fill()
                .restoreState();

        // 添加页眉页脚
        try {
            pdfCanvas.beginText()
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 9)
                    .moveText(pageSize.getWidth() / 2 - 60, pageSize.getTop() - 20)
                    .showText("this is truth")
                    .moveText(60, -pageSize.getTop() + 30)
                    .showText(String.valueOf(pageNumber))
                    .endText();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 添加水印--就是在指定位置添加文字
        Canvas canvas = new Canvas(pdfCanvas, pdfDoc, page.getPageSize());
        canvas.setFontColor(ColorConstants.WHITE);
        canvas.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(60));
        try {
            canvas.setProperty(Property.FONT, PdfFontFactory.createFont(StandardFonts.HELVETICA));
        } catch (IOException e) {
            e.printStackTrace();
        }
        canvas.showTextAligned(new Paragraph("CONFIDENTIAL"), 298, 421, pdfDoc.getPageNumber(page),
                TextAlignment.CENTER, VerticalAlignment.MIDDLE, 45);
        pdfCanvas.release();
    }
}
