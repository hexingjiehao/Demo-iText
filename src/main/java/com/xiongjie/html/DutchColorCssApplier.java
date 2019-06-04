package com.xiongjie.html;

import com.itextpdf.html2pdf.attach.ITagWorker;
import com.itextpdf.html2pdf.attach.ProcessorContext;
import com.itextpdf.html2pdf.css.CssConstants;
import com.itextpdf.html2pdf.css.apply.impl.BlockCssApplier;
import com.itextpdf.styledxmlparser.node.IStylesContainer;

import java.util.HashMap;
import java.util.Map;

public class DutchColorCssApplier extends BlockCssApplier {

    public static final Map<String, String> KLEUR = new HashMap<String, String>();
    static {
        KLEUR.put("groen", "green");
    }


    @Override
    public void apply(ProcessorContext context, IStylesContainer stylesContainer, ITagWorker tagWorker){
        Map<String, String> cssStyles = stylesContainer.getStyles();

        if(cssStyles.containsKey("kleur")){
            cssStyles.put(CssConstants.COLOR,KLEUR.get(cssStyles.get("kleur")));
            stylesContainer.setStyles(cssStyles);
        }

        super.apply(context, stylesContainer,tagWorker);
    }

}
