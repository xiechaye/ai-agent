package cn.edu.xmu.aiagent.tools;



import cn.edu.xmu.aiagent.utils.OssUtils;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

                // 使用内置中文字体
                PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
                document.setFont(font);

                // 创建段落
                Paragraph paragraph = new Paragraph(content);

                // 添加段落并关闭文档
                document.add(paragraph);
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

