package cn.edu.xmu.aiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TerminalOperationToolTest {

    @Test
    void executeTerminalCommand() {
        String command = "echo Hello, World!";
        String result = new TerminalOperationTool().executeTerminalCommand(command);
        assertNotNull(result);
    }
}