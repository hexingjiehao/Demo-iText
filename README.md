# Demo-iText
学习iText core和pd ffhtml的demo代码

#
PDF文档的分类：
1. PDF/A: 应用于电子文档的长期归档。是PDF的一种变型.可持续，无论怎样创建该文件。它屏蔽了一些如Javascript，音频、视频等不适合的功能
2. PDF/X：应用与图形内容交换
3. PDF/E：应用于工程文档的交互式交换

#
# 参考书籍：《iText 7 Jump-Start Tutorial Java.pdf》
   1. 本质：类似与html的tag标签，itext core使用一些构建块在pdf文档上渲染指定的内容
   2. 基础构建块：   
        2.1 示例：
        ```
            OutputStream fos = new FileOutputStream("dest.pdf");
            PdfWriter writer = new PdfWriter(fos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            document.add(new Paragraph("Hello World!"));
            document.close();
        ```
        2.2 设置添加内容的字体
        ```
            PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
            document.add(new Paragraph("iText is:").setFont(font)); 
        ```
        ```
            //解决中文字符无法显示的问题
            PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H",true);
            document.add(new Paragraph("这是中文").setFont(font));
        ```
        2.3 设置类似于\<li>的标签的集合
        ```
           List list = new List().setSymbolIndent(12).setListSymbol("\u2022");
           list.add(new ListItem("good"))
               .add(new ListItem("news"));
        ```
        2.4 设置图像
        ```
            Image fox = new Image(ImageDataFactory.create("古剑奇谭壁纸.jpg"));
            fox.setWidth(45);
            Paragraph p = new Paragraph("this is a iamge").add(fox);
            document.add(p);
        ```    
        2.5 使用大量数据创建表格：核心是逐个Cell进行渲染
        ```
            Table table = new Table(new float[]{4, 1, 3}).useAllAvailableWidth();
            BufferedReader br = new BufferedReader(new FileReader("data.csv"));
            String line = br.readLine();
            StringTokenizer tokenizer = new StringTokenizer(line, ";");
            while (tokenizer.hasMoreTokens()) {
                table.addHeaderCell(
                    new Cell().add(
                        new Paragraph(tokenizer.nextToken())));
            } 
            while ((line = br.readLine()) != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, ";");
                while (tokenizer.hasMoreTokens()) {
                    table.addCell(
                        new Cell().add(
                                new Paragraph(tokenizer.nextToken())));
                } 
            }
            br.close();
            document.add(table);
        ```    
   3. 使用底层操作渲染pdf   
        3.1 示例：
        ```
            OutputStream fos = new FileOutputStream("dest.pdf");
            PdfWriter writer = new PdfWriter(fos);
            PdfDocument pdf = new PdfDocument(writer);
            PageSize ps = PageSize.A4.rotate();
            PdfPage page = pdf.addNewPage(ps);
            PdfCanvas canvas = new PdfCanvas(page);
            
            // Draw the line,wait you add
            
            pdf.close();
        ```
        3.2 在画布上进行操作
        ```
            //变换坐标轴的中心，使之定位于旋转过后的A4纸的中心
            canvas.concatMatrix(1, 0, 0, 1, ps.getWidth() / 2, ps.getHeight() / 2);
            
            //设置画笔的颜色
            Color blueColor = new DeviceCmyk(1.f, 0.156f, 0.f, 0.118f);
            canvas.setLineWidth(0.5f).setStrokeColor(blueColor);
            
            //设置画布的背景色
            canvas.rectangle(0, 0, ps.getWidth(), ps.getHeight())
                  .setColor(ColorConstants.BLACK, true)
                  .fill();
            
            //画线操作
            canvas.moveTo(-(ps.getWidth() / 2 - 15), 0)
                    .lineTo(ps.getWidth() / 2 - 15, 0)
                    .stroke();
            
            //画虚线--在画线基础上间隔多少距离截断一下
            canvas.setLineWidth(2)
                  .setStrokeColor(greenColor)
                  .setLineDash(10, 10, 8)
                  .moveTo(-(ps.getWidth() / 2 - 15), -(ps.getHeight() / 2 - 15))
                  .lineTo(ps.getWidth() / 2 - 15, ps.getHeight() / 2 - 15)
                  .stroke();         
                  
            //添加一行文字
            canvas.newlineShowText("hello");
                           
        ```
   4. 使用渲染器和事件监听句柄   
        4.1 使用文档渲染器将整个页面布局成3列，逐列填充内容等
        ```
            OutputStream fos = new FileOutputStream("render.pdf");
            PdfWriter writer = new PdfWriter(fos);
            PdfDocument pdf = new PdfDocument(writer);
            PageSize ps = PageSize.A5;
            Document document = new Document(pdf, ps);
    
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
            document.add(...)
        ```
        4.2 使用块渲染器
        ```
            //在上文渲染表格cell时候添加块渲染器
            Cell cell = new Cell().add(new Paragraph(tokenizer.nextToken())); 
            cell.setNextRenderer(new RoundedCornersCellRenderer(cell));
        ```
        ```
            //继承块渲染器类
            public class RoundedCornersCellRenderer extends CellRenderer {
            
                public RoundedCornersCellRenderer(Cell modelElement) {
                    super(modelElement);
                }
            
                @Override
                public void drawBorder(DrawContext drawContext) {
                    PdfCanvas canvas = drawContext.getCanvas();
                    //画布上的各种操作
                    ......
                    
                    super.drawBorder(drawContext);
                }
            }
        ```
        4.3 使用事件句柄添加页眉页脚和水印
        ```
            PdfDocument pdf = new PdfDocument(writer);
            pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new MyEventHandler());
            Document document = new Document(pdf);
        ```
        ```
            //实现事件句柄接口
            public class MyEventHandler implements IEventHandler {
                @Override
                public void handleEvent(Event event) {
                    PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
                    PdfDocument pdfDoc = docEvent.getDocument();
            
                    PdfPage page = docEvent.getPage();
                    int pageNumber = pdfDoc.getPageNumber(page);
                    Rectangle pageSize = page.getPageSize();
                    PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);
            
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
            
                    // 添加水印--重新生成一个画布，然后在指定位置添加文字
                    Canvas canvas = new Canvas(pdfCanvas, pdfDoc, page.getPageSize());
                    canvas.showTextAligned(new Paragraph("CONFIDENTIAL"), 298, 421, pdfDoc.getPageNumber(page),
                            TextAlignment.CENTER, VerticalAlignment.MIDDLE, 45);
                    pdfCanvas.release();
                }
            }
        ```
   5. 和pdf文档进行交互   
        5.1 注释：类型有文本注释，链接注释，行注释，标记注释：
        ```
            PdfAnnotation ann = new PdfTextAnnotation(new Rectangle(120, 800, 0, 0))
                        .setColor(ColorConstants.GREEN)
                        .setTitle(new PdfString("iText"))
                        .setContents("this is a text annotation");
            pdf.getFirstPage().addAnnotation(ann);
        ```
        5.2 表单的交互：在pdf上创建一个类似于html的form标签。可以填写数据等
        ```
            //创建交互式表单
            PdfAcroForm form = PdfAcroForm.getAcroForm(document.getPdfDocument(), true);
            
            //文本域
            PdfTextFormField nameField = PdfTextFormField.createText(document.getPdfDocument(),
                    new Rectangle(99, 753, 425, 15), "name", "");
            form.addField(nameField);
    
            //单选按钮
            PdfButtonFormField group = PdfFormField.createRadioGroup(document.getPdfDocument(), "language", "");
            PdfFormField.createRadioButton(document.getPdfDocument(), new Rectangle(130, 728, 15, 15), group, "English");
            PdfFormField.createRadioButton(document.getPdfDocument(), new Rectangle(200, 728, 15, 15), group, "French");
            form.addField(group);
    
            //复选按钮
            for (int i = 0; i < 3; i++) {
                PdfButtonFormField checkField = PdfFormField.createCheckBox(document.getPdfDocument(), new Rectangle(119 + i * 69, 701, 15, 15),
                        "experience".concat(String.valueOf(i + 1)), "Off", PdfFormField.TYPE_CHECK);
                form.addField(checkField);
            }
    
            //下拉框
            String[] options = {"Any", "6.30 am - 2.30 pm", "1.30 pm - 9.30 pm"};
            PdfChoiceFormField choiceField = PdfFormField.createComboBox(document.getPdfDocument(), new Rectangle(163, 676, 115, 15),
                    "shift", "Any", options);
            form.addField(choiceField);
    
    
            //富文本框
            PdfTextFormField infoField = PdfTextFormField.createMultilineText(document.getPdfDocument(),
                    new Rectangle(158, 625, 366, 40), "info", "");
            form.addField(infoField);
    
            //提交按钮
            PdfButtonFormField button = PdfFormField.createPushButton(document.getPdfDocument(),
                    new Rectangle(479, 594, 45, 15), "reset", "RESET");
            button.setAction(PdfAction.createResetForm(new String[]{"name", "language", "experience1", "experience2", "experience3", "shift", "info"}, 0));
            form.addField(button);
    
            //扁平化操作--创建的pdf不能够修改内容
            form.flattenFields();
        ```    
        5.3 对有交互式表单的pdf文档，进行内容填充
        ```
            PdfReader reader = new PdfReader("src.pdf");
            PdfWriter writer = new PdfWriter("dest.pdf");
            PdfDocument pdf = new PdfDocument(reader, writer);
            
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
            Map<String, PdfFormField> fields = form.getFormFields();
            fields.get("name").setValue("James Bond");
            fields.get("language").setValue("English");
            //扁平化操作
            form.flattenFields();
            pdf.close();
        ```   
   6. 操作现有的pdf文档：可以添加注释，内容，修改表单属性，添加页眉页脚和水印，改变页面大小      
        6.1 示例：
        ``` 
            PdfReader reader = new PdfReader("src.pdf");
            PdfWriter writer = new PdfWriter("dest.pdf");
            PdfDocument pdfDoc = new PdfDocument(reader, writer); 
            
            // add content,wait you add.重用以上的代码
            
            pdfDoc.close();
        ``` 
   7. 将多个pdf文档进行合并成一个pdf   
        7.1 使用缩放的方式：
        ``` 
             PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
             PdfDocument sourcePdf = new PdfDocument(new PdfReader(SRC));
    
             //原始页面
             PdfPage origPage = sourcePdf.getPage(1);
             Rectangle orig = origPage.getPageSize();
             PdfFormXObject pageCopy = origPage.copyAsFormXObject(pdf);
    
             //n个小页面
             PageSize nUpPageSize = PageSize.A4.rotate();
             PdfPage page = pdf.addNewPage(nUpPageSize);
             PdfCanvas canvas = new PdfCanvas(page);
    
             //缩放页面
             AffineTransform transformationMatrix = AffineTransform.getScaleInstance(
                 nUpPageSize.getWidth() / orig.getWidth() / 2f,
                 nUpPageSize.getHeight() / orig.getHeight() / 2f);
             canvas.concatMatrix(transformationMatrix);
    
             //将小页面存放到大页面上
             canvas.addXObject(pageCopy, 0, orig.getHeight());
             canvas.addXObject(pageCopy, orig.getWidth(), orig.getHeight());
             canvas.addXObject(pageCopy, 0, 0);
             canvas.addXObject(pageCopy, orig.getWidth(), 0);
             pdf.close();
             sourcePdf.close();
        ``` 
        7.2 使用PdfMerger：
        ``` 
            PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
            PdfMerger merger = new PdfMerger(pdf);
            PdfDocument firstSourcePdf = new PdfDocument(new PdfReader(SRC1));
            merger.addPages(firstSourcePdf, 1, firstSourcePdf.getNumberOfPages());

            PdfDocument secondSourcePdf = new PdfDocument(new PdfReader(SRC2));
            merger.addPages(secondSourcePdf, 1, secondSourcePdf.getNumberOfPages());
            merger.merge();
            firstSourcePdf.close();
            secondSourcePdf.close();
            pdf.close();
        ``` 
   8. 创建特殊类型pdf文档   
        8.1  PDF/A文档：
        ``` 
            PdfADocument pdf = new PdfADocument(new PdfWriter(dest),
                                                PdfAConformanceLevel.PDF_A_1B,
                                                new PdfOutputIntent("Custom", 
                                                                    "", 
                                                                    "http://www.color.org",
                                                                    "sRGB IEC61966-2.1", 
                                                                    new FileInputStream("sRGB_CS_profile.icm")));
            Document document = new Document(pdf);
            //其他渲染pdf文档操作
            ......
            document.close();
        ```     
#
# 参考书籍：《iText 7 - Converting HTML to PDF with pdfHTML.pdf》
   1. 本质：将纯静态的html+css的组合文档转化为pdf文档
   2. hello入门：   
        2.1 将html转化为pdf：
        ```
           //将字符串类型html转化为pdf
           String html = "<h1>Test</h1><p>Hello World</p>";
           HtmlConverter.convertToPdf(html, new FileOutputStream("stringHtmlToPdf.pdf"));
        ```
        ```
            //将html文件转化为pdf
            String html = "htmlFileToPdf.html";
            String htmlLoc = BASEURI + html;
            HtmlConverter.convertToPdf(new File(htmlLoc), new File("htmlFileToPdf.pdf"));
        ```
        ```
            //以stream2Stream的形式将html文件转化为pdf
            HtmlConverter.convertToPdf(new FileInputStream(htmlLoc), new FileOutputStream("stream2Stream.pdf"));
        ```
        ```
            //以stream2Writer的形式将html文件转化为pdf
            PdfWriter writer = new PdfWriter("stream2Writer.pdf", new WriterProperties().setFullCompressionMode(true));
            HtmlConverter.convertToPdf(new FileInputStream(htmlLoc), writer);
        ```
        ```
            //以stream2Document的形式将html文件转化为pdf
            PdfWriter writer = new PdfWriter("stream2Document.pdf");
            PdfDocument pdf = new PdfDocument(writer);
            pdf.setTagged();
            HtmlConverter.convertToPdf(new FileInputStream(htmlLoc), pdf);
        ```
        2.2 将html转化为iText对象
        ```
            //转化为Document对象
            String html = "htmlFileToPdf.html";
            String htmlLoc = BASEURI + html;
    
            PdfWriter writer = new PdfWriter("html2ItextObj.pdf");
            PdfDocument pdf = new PdfDocument(writer);
            pdf.setTagged();
    
            Document document = HtmlConverter.convertToDocument(new FileInputStream(htmlLoc), pdf,null);
            document.add(new Paragraph("Goodbye!"));
            document.close();
        ```
        ```
            //转化为Element对象
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
        ```
   3. Css样式在pdf中的渲染    
        3.1 示例：后台这边基本不改，主要是前端的css的修改。pdf可以渲染的css的种类有Old-fashioned HTML，inline css,internal css,external css
        ```
            String html = "fontCss.html";
            String htmlLoc = BASEURI + html;
            HtmlConverter.convertToPdf(new File(htmlLoc), new File("oldCssToPdf.pdf"));
        ```
        3.2 分页操作： 
        
        ```
            //使用@page：在html中使用。
            //浏览器端无效，在pdf渲染可以在右下角添加当前页数
            <style>
                @page { @bottom-right {
                    content: "Page " counter(page) " of " counter(pages); }
                }
            </style>
        ```    
        ```
            //使用page-break-after,在html中使用
            //浏览器端无效，在pdf渲染可以强制div后的内容进行分页
            <div style="page-break-after: always; width: 320pt;">
        ```
   4. 将html中的布局格式转化到pdf   
        4.1 示例： 后台这边基本不改，主要是前端的css的修改。根据设备的类型调整pdf页面的大小，渲染符合条件的文档。核心是@media only screen
        ```
        //这是css布局，不同的页面设置不同的布局方式
        /*Desktop*/
        @media only screen and (min-width: 768px ) {
            .col-1 {width:24.9%;}
            .col-2 {width: 33.32%;}
            .col-3 {width: 49%;}
            .col-4 {width: 99%;}
            p{
                font-size: 12pt;
            }
            h1{
                font-size: 20pt;
            }
            h2{
                font-size:16pt
            }
        }
        ......
        ```
        ```
            //后台代码
            String html = "layout.html";
            String htmlLoc = BASEURI + html;
    
            PdfWriter writer = new PdfWriter("deskLayoutToPdf.pdf");
            PdfDocument pdf = new PdfDocument(writer);
            pdf.setTagged();
            PageSize pageSize = PageSize.A4.rotate();
            pdf.setDefaultPageSize(pageSize);
    
            ConverterProperties properties = new ConverterProperties();
            properties.setBaseUri(BASEURI);
    
            //核心，对应css样式中的 @media only screen
            MediaDeviceDescription mediaDeviceDescription = new MediaDeviceDescription(MediaType.SCREEN);
            mediaDeviceDescription.setWidth(pageSize.getWidth());
            properties.setMediaDeviceDescription(mediaDeviceDescription);
    
            HtmlConverter.convertToPdf(new FileInputStream(htmlLoc), pdf, properties);
        ```
        ```
            //渲染成桌面应用布局
            PageSize pageSize = PageSize.A4.rotate();
        ```
        ```
            //渲染成平板布局
            PageSize pageSize = new PageSize(575, 1500);
        ```
        ```
            //渲染成智能手机布局
            PageSize pageSize = new PageSize(440, 2000);
        ```
   5. 使用pdfhtml创建报表   
        5.1 将xml数据文件转化为html,然后将对html文件转化为pdf
        ```
            //重点是前端的xml和xsl文件
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(baos);
            StreamSource xml = new StreamSource(new File(BASEURI+"movie.xml"));
            StreamSource xsl = new StreamSource(new File(BASEURI+"movie.xsl"));
            
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(xsl);
            transformer.transform(xml, new StreamResult(writer)); 
            writer.flush();
            writer.close();
            byte[] res=baos.toByteArray();
    
            HtmlConverter.convertToPdf(new ByteArrayInputStream(res), new FileOutputStream("xmlToHtmlToPdf.pdf"));
        ```
        5.2 使用事件监听添加水印
        ```
            //添加事件监听
            IEventHandler handler = new BackgroundListener(pdf, "hello.pdf");
            pdf.addEventHandler(PdfDocumentEvent.START_PAGE, handler);
            HtmlConverter.convertToPdf(new FileInputStream(htmlLoc), pdf);
        ```
        ```
            //实现事件监听接口
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
            
        ```
        5.3 将页面高度大于1页的html存放到1页的pdf中
        ```
            PdfWriter pwriter = new PdfWriter("largeHtmlToOnePdf.pdf");
            PdfDocument pdf = new PdfDocument(pwriter);
            pdf.setDefaultPageSize(new PageSize(595, 14400));
    
            Document document = HtmlConverter.convertToDocument(new ByteArrayInputStream(res), pdf,null);
            EndPosition endPosition = new EndPosition();
            LineSeparator separator = new LineSeparator(endPosition);
            document.add(separator);
            document.getRenderer().close();
    
            //调整单页pdf的高度
            PdfPage page = pdf.getPage(1);
            float y = endPosition.getY() - 36;
            page.setMediaBox(new Rectangle(0, y, 595, 14400 - y));
            document.close();
        ```
        5.4 创建特殊类型pdf文档：
        ```
            //注意：需要事先在html中添加字符集，否则报错字体没嵌入
            //比如：<body style=""font-family: FreeSans"></body>
            PdfWriter writer = new PdfWriter("htmlToPdfA2B.pdf");
            PdfADocument pdf = new PdfADocument(writer,
                    PdfAConformanceLevel.PDF_A_2B,
                    new PdfOutputIntent("Custom",
                            "",
                            "http://www.color.org",
                            "sRGB IEC61966-2.1",
                            new FileInputStream("src/main/resources/sRGB_CS_profile.icm")));
            HtmlConverter.convertToPdf(new FileInputStream(htmlLoc), pdf);
        ```
   6. 使用自定义的tag标签   
        6.1 示例：浏览器无法解析该标签，但是pdf可以  
        ```
            //html添加一个自定义<name>标签
            <name>this is define name tag</name>
        ```
        ```
            //后台操作
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
                    }
                    return null;
                }
            });
            HtmlConverter.convertToPdf(new File(htmlLoc), new File("customtagsToPdf.pdf"), converterProperties); 
        ```
   7. 使用自定义的Css样式   
        7.1 示例： 浏览器无法解析该样式，但是pdf可以
        ```
            //html添加一个自定义样式kleur: groen
            <div style="kleur: groen;"></div>
        ```
        ```
            //后台操作
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
        ```
        ```
            //实现样式接口
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
        ```
   8. 在pdfhtml中使用字体   
        8.1 示例： 后台基本不变，前端htm可以使用14种标准字体，itext附带的12中常规字体，以及系统字体
        ```
            String html = "font_standardtype1.html";
            String htmlLoc = BASEURI + html;
            HtmlConverter.convertToPdf(new File(htmlLoc), new File("standardType1ToPdf.pdf"));
        ```    
        8.2 Web开放字体的使用
        ```
            //当前端需要的字体本机没有时，通过@font-face自动下载，在html页面设置
            @font-face {
                font-family: "SourceSerifPro-Regular";
                src: url("fonts/SourceSerifPro-Regular.otf.woff") format("woff");
            }
            .regular {
                font-family: "SourceSerifPro-Regular";
            }
            <td class="regular">lalalalalalal</td>
        ```
        8.3 使用指定字体渲染pdf中指定部分，和html呼应
        ```
            //指定单个字体    
            FontProvider fontProvider = new DefaultFontProvider();
            FontProgram fontProgram = FontProgramFactory.createFont(BASEURI+"font/Cardo-Regular.ttf");
            fontProvider.addFont(fontProgram);
            ConverterProperties properties = new ConverterProperties();
            properties.setFontProvider(fontProvider);
            HtmlConverter.convertToPdf(new File(htmlLoc), new File("addExtraFontToPdf.pdf"), properties);
        ```
        ```
            //指定字体目录 
            FontProvider fontProvider = new DefaultFontProvider();
            fontProvider.addDirectory(BASEURI+"/font/");
            ConverterProperties properties = new ConverterProperties();
            properties.setFontProvider(fontProvider);
            HtmlConverter.convertToPdf(new File(htmlLoc), new File("addExtraFontDirToPdf.pdf"), properties);            
        ```
        8.4  国际化操作：也是添加对应的字体即可
        ```
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
        ```        
        8.4  解决中文无法显示问题--添加NotoSansCJKsc-Regular.otf字体
        ```
             String html = "chineseGarble.html";
            String htmlLoc = BASEURI + html;
    
            ConverterProperties properties = new ConverterProperties();
            FontProvider fontProvider = new DefaultFontProvider();
            FontProgram fontProgram = FontProgramFactory.createFont(BASEURI+"font/NotoSansCJKsc-Regular.otf");
            fontProvider.addFont(fontProgram);
    
            properties.setFontProvider(fontProvider);
            HtmlConverter.convertToPdf(new File(htmlLoc), new File("chineseGarbledToPdf.pdf"), properties);
        ```
   9. 注意事项         
        9.1 如何将ASP或JSP页面转换为PDF?   
            答：对html的抽象，需要开发者自行将其创建为html。pdfHtml只能解析htm和css   
        9.2 如何将多个HTML文件解析为一个PDF?   
            答：第一种：将每个HTML转换为内存中的一个单独的PDF文件。使用pdfmerge将这些文件合并为单个PDF。可能存在中间有大量空格
        ```
            public void createPdf(String baseUri, String[] src, String dest) throws IOException {
                ConverterProperties properties = new ConverterProperties();
                properties.setBaseUri(baseUri);
                PdfWriter writer = new PdfWriter(dest);
                PdfDocument pdf = new PdfDocument(writer);
                PdfMerger merger = new PdfMerger(pdf);
                for (String html : src) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    PdfDocument temp = new PdfDocument(new PdfWriter(baos));
                    HtmlConverter.convertToPdf(new FileInputStream(html), temp, properties);

                    temp = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())));
                    merger.merge(temp, 1, temp.getNumberOfPages());
                    temp.close();
                }
                pdf.close();
            }
        ```
                第二种: (推荐--页面更加紧凑) 将不同的HTML文件解析为一系列iText元素。我们将所有这些元素添加到一个PDF文档中。
        ```     
            public void createPdf(String baseUri, String[] src, String dest) throws IOException {
                ConverterProperties properties = new ConverterProperties();
                properties.setBaseUri(baseUri);
                PdfWriter writer = new PdfWriter(dest);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);
                for (String html : src) {
                    List<IElement> elements =
                    HtmlConverter.convertToElements(new FileInputStream(html), properties);
                    for (IElement element : elements) {
                        document.add((IBlockElement)element);
                    }
                }
                document.close();
            }
        ```
        9.3 可以从URL生成PDF而不是从磁盘上的文件?   
            答： 
        ```
            createPdf(new URL("https://stackoverflow.com/help/on-topic"), DEST);
            
            public void createPdf(URL url, String dest) throws IOException {
                HtmlConverter.convertToPdf(url.openStream(), new FileOutputStream(dest));
            }
        ```
        