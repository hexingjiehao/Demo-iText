package com.xiongjie.html;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;

public class EndPosition implements ILineDrawer {

    protected float y;

    public float getY() {
        return y;
    }

    @Override
    public void draw(PdfCanvas pdfCanvas, Rectangle rectangle) {
        this.y=rectangle.getY();
    }

    @Override
    public float getLineWidth() {
        return 0;
    }

    @Override
    public void setLineWidth(float v) {

    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public void setColor(Color color) {

    }
}
