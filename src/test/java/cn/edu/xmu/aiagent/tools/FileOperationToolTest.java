package cn.edu.xmu.aiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileOperationToolTest {

    @Test
    void readFile() {
        String fileName = "test.txt";
        String content = new FileOperationTool().readFile(fileName);
        Assertions.assertNotNull(content);
    }

    @Test
    void writeFile() {
        String fileName = "test.txt";
        String content = "Hello, World!";
        String s = new FileOperationTool().writeFile(fileName, content);
        System.out.println(s);
        Assertions.assertNotNull(s);
    }
}