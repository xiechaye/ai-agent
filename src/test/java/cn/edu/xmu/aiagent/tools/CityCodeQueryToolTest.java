package cn.edu.xmu.aiagent.tools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CityCodeQueryToolTest {

    @Resource
    private CityCodeQueryTool cityCodeQueryTool;

    @Test
    void queryCityCode() {
        String code = cityCodeQueryTool.queryCityCode("石家庄");
        Assertions.assertNotNull(code);
    }
}