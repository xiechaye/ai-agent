package cn.edu.xmu.aiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class QueryDateTimeToolTest {
    private final QueryDateTimeTool queryDateTimeTool = new QueryDateTimeTool();

    @Test
    void getCurrentDate() {
        String currentDate = queryDateTimeTool.getCurrentDate();
        assertNotNull(currentDate);
    }

    @Test
    void getCurrentDayOfWeek() {
        String currentDayOfWeek = queryDateTimeTool.getCurrentDayOfWeek();
        assertNotNull(currentDayOfWeek);
    }
}