package cn.edu.xmu.aiagent.tools;



import cn.edu.xmu.aiagent.utils.OssUtils;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PDF生成工具类
 *
 * @author Suave
 */
public class PDFGenerationTool {

    @Tool(description = "Generate a PDF file with given content", returnDirect = true)
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content) {

        // 文件名简单合法性处理
        fileName = fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
        fileName = String.format("%s/%s", System.currentTimeMillis(), fileName);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // 创建 PdfWriter 和 PdfDocument 对象，写入到 ByteArrayOutputStream
            try (PdfWriter writer = new PdfWriter(baos);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {

                // 使用本地中文字体，保证兼容性
                String fontPath = Paths.get("src/main/resources/static/NotoSansSC-Regular.ttf").toAbsolutePath().toString();
                PdfFont font = PdfFontFactory.createFont(fontPath, "Identity-H", pdf);
                document.setFont(font);

                // 处理内容，识别Markdown图片语法并插入图片，其余文本插入段落
                Pattern pattern = Pattern.compile("!\\[.*?\\]\\((.*?)\\)");
                Matcher matcher = pattern.matcher(content);
                int lastEnd = 0;
                while (matcher.find()) {
                    // 插入前面的文本
                    if (matcher.start() > lastEnd) {
                        String text = content.substring(lastEnd, matcher.start());
                        if (!text.trim().isEmpty()) {
                            document.add(new Paragraph(text));
                        }
                    }
                    // 插入图片
                    String imageUrl = matcher.group(1);
                    try {
                        Image img = new Image(ImageDataFactory.create(imageUrl));
                        document.add(img);
                    } catch (Exception e) {
                        document.add(new Paragraph("[图片加载失败] " + imageUrl));
                    }
                    lastEnd = matcher.end();
                }
                // 插入最后剩余的文本
                if (lastEnd < content.length()) {
                    String text = content.substring(lastEnd);
                    if (!text.trim().isEmpty()) {
                        document.add(new Paragraph(text));
                    }
                }
            }

            // 将 PDF 内容作为字节数组获取
            byte[] pdfBytes = baos.toByteArray();

            // 上传到 OSS
            String ossUrl = OssUtils.uploadImage(pdfBytes, fileName);

            if (ossUrl != null) {
                return "PDF generated and uploaded successfully to OSS: " + ossUrl;
            } else {
                return "Error uploading PDF to OSS: OssUtils.uploadImage returned null.";
            }

        } catch (IOException e) {
            return "Error generating PDF: " + e.getMessage();
        }
    }
}

