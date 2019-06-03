package com.xiongjie.core;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;

public class RoundedCornersCellRenderer extends CellRenderer {

    public RoundedCornersCellRenderer(Cell modelElement) {
        super(modelElement);
    }

    @Override
    public void drawBorder(DrawContext drawContext) {
        Rectangle rectangle = getOccupiedAreaBBox();
        float llx = rectangle.getX() + 1;
        float lly = rectangle.getY() + 1;
        float urx = rectangle.getX() + getOccupiedAreaBBox().getWidth() - 1;
        float ury = rectangle.getY() + getOccupiedAreaBBox().getHeight() - 1;
        PdfCanvas canvas = drawContext.getCanvas();
        float r = 4;
        float b = 0.4477f;
        canvas.moveTo(llx, lly)
                .lineTo(urx, lly)
                .lineTo(urx, ury - r)
                .curveTo(urx, ury - r * b, urx - r * b, ury, urx - r, ury)
                .lineTo(llx + r, ury)
                .curveTo(llx + r * b, ury, llx, ury - r * b, llx, ury - r)
                .lineTo(llx, lly)
                .stroke();
        super.drawBorder(drawContext);
    }

}
