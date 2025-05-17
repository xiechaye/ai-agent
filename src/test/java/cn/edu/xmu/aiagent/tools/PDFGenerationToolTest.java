package cn.edu.xmu.aiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PDFGenerationToolTest {

    @Test
    void generatePDF() {
        String fileName = "test.pdf";
        String content = "This is a test PDF document.";
        String result = new PDFGenerationTool().generatePDF(fileName, content);
        assertNotNull(result);
    }
}