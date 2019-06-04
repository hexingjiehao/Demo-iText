package com.xiongjie.html;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.colors.ColorConstants;

import java.io.IOException;

public class BackgroundListener implements IEventHandler {

    PdfXObject stationery;

    public BackgroundListener(PdfDocument pdf, String src) throws IOException {
        PdfDocument template = new PdfDocument(new PdfReader(src));
        PdfPage page = template.getPage(1);
        stationery = page.copyAsFormXObject(pdf);
        template.close();
    }

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdf = docEvent.getDocument();

        PdfPage page = docEvent.getPage();
        PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);
        pdfCanvas.addXObject(stationery, 0, 0);

        Rectangle rect = new Rectangle(36, 32, 36, 64);
        Canvas canvas = new Canvas(pdfCanvas, pdf, rect);
        canvas.add(new Paragraph(String.valueOf(pdf.getNumberOfPages()))
                                        .setFontSize(48)
                                        .setFontColor(ColorConstants.RED));
        canvas.close();
    }
}
