package cn.edu.xmu.aiagent.tools;

import java.io.File;
import cn.hutool.http.HttpUtil;
import cn.edu.xmu.aiagent.utils.OssUtils;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.core.io.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PDF生成工具类
 *
 * @author Suave
 */
public class PDFGenerationTool {

    @Tool(description = "Generate a PDF document from plain text with image URLs (avoiding Markdown syntax). Converts content to PDF and uploads to OSS storage. Returns accessible URL for the generated PDF. Ideal for creating reports, plans, or documents with embedded images. For images, use full HTTP URLs instead of placeholders.", returnDirect = true)
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF. Use plain text for written content. For images, include them using Markdown image syntax ![description](URL) where URL must be a valid and accessible web address (http/https). Example: 'Here is my report. ![Company Logo](https://example.com/logo.png) The data shows...'") String content) {

        // 文件名简单合法性处理
        fileName = fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
        fileName = String.format("%s/%s", System.currentTimeMillis(), fileName);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // 创建 PdfWriter 和 PdfDocument 对象，写入到 ByteArrayOutputStream
            try (PdfWriter writer = new PdfWriter(baos);
                    PdfDocument pdf = new PdfDocument(writer);
                    Document document = new Document(pdf)) {

                // 使用本地中文字体，保证兼容性
                String fontPath = Paths.get("src/main/resources/static/NotoSansSC-Regular.ttf").toAbsolutePath()
                        .toString();
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
                        System.out.println("尝试加载图片: " + imageUrl);
                        
                        // 创建临时文件
                        File tempFile = File.createTempFile("pdf_img_", ".tmp");
                        tempFile.deleteOnExit(); // 程序结束时自动删除
                        
                        try {
                            // 添加请求头，模拟浏览器行为，绕过防盗链
                            String host = "";
                            try {
                                URL url = new URL(imageUrl);
                                host = url.getHost();
                            } catch(Exception ex) {
                                // 忽略URL解析错误
                            }
                            
                            HttpResponse response = HttpRequest.get(imageUrl)
                                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                                .header("Referer", "https://" + host + "/")
                                .header("Accept", "image/webp,image/apng,image/*,*/*;q=0.8")
                                .timeout(10000) // 10秒超时
                                .execute();
                                
                            if (response.isOk()) {
                                // 保存响应内容到临时文件
                                FileUtil.writeBytes(response.bodyBytes(), tempFile);
                                System.out.println("图片下载成功: " + tempFile.getAbsolutePath() + " (大小: " + tempFile.length() + " 字节)");
                                
                                // 从临时文件加载图片
                                ImageData data = ImageDataFactory.create(tempFile.getAbsolutePath());
                                Image img = new Image(data);
                                
                                // 设置合理的图片尺寸
                                float width = pdf.getDefaultPageSize().getWidth() - 50;
                                img.setWidth(width).setAutoScale(true);
                                
                                // 添加图片到文档
                                document.add(img);
                                System.out.println("图片添加到PDF成功: " + imageUrl);
                            } else {
                                throw new IOException("HTTP请求失败，状态码: " + response.getStatus());
                            }
                        } catch (Exception ex) {
                            System.out.println("下载或处理图片失败: " + ex.getMessage());
                            // 图片加载失败时不添加任何内容到PDF
                        } finally {
                            // 确保临时文件被删除
                            if (tempFile.exists()) {
                                tempFile.delete();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("图片处理异常: " + e.getMessage());
                        // 图片加载失败时不添加任何内容到PDF
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
