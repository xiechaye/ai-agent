package cn.edu.xmu.aiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PDFGenerationToolTest {

    @Test
    void generatePDF() {
        String fileName = "test.pdf";
        String content = "# 四季旅行指南\n" +
                "\n" +
                "这是一份简单的四季旅行指南，包含了不同季节的旅行建议和风景图片。\n" +
                "\n" +
                "## 春季旅行\n" +
                "\n" +
                "春天是万物复苏的季节，非常适合赏花和户外活动。樱花、郁金香和油菜花是春季的代表性花卉，各地的花海和公园都是理想的春游目的地。\n" +
                "\n" +
                "![春季樱花](https://cdn.deepseek.com/logo.png?x-image-process=image%2Fresize%2Cw_1920)\n" +
                "\n" +
                "温馨提示：春季天气多变，外出时请携带轻便外套，注意防晒也不要忘记防雨。\n" +
                "\n" +
                "## 夏季旅行\n" +
                "\n" +
                "夏季是海滩和水上活动的黄金季节，海边度假、游泳和冲浪都是不错的选择。山区也是避暑的好去处，山间溪流和森林能为您带来清凉体验。\n" +
                "\n" +
                "![夏季海滩](https://cdn.deepseek.com/logo.png?x-image-process=image%2Fresize%2Cw_1920)\n" +
                "\n" +
                "温馨提示：夏季紫外线强烈，户外活动时请做好防晒措施，多补充水分。\n" +
                "\n" +
                "## 秋季旅行\n" +
                "\n" +
                "秋天是观赏红叶的最佳季节，各地的枫叶和银杏都会呈现出绚丽的色彩。秋季天高气爽，非常适合徒步旅行和摄影。\n" +
                "\n" +
                "![秋季红叶](https://cdn.deepseek.com/logo.png?x-image-process=image%2Fresize%2Cw_1920)\n" +
                "\n" +
                "温馨提示：秋季昼夜温差大，请准备足够的衣物应对气温变化。\n" +
                "\n" +
                "## 冬季旅行\n" +
                "\n" +
                "冬天是赏雪和滑雪的季节，北方的雪景和南方的温泉都各有特色。室内博物馆和美食之旅也是冬季旅行的不错选择。\n" +
                "\n" +
                "![冬季雪景](https://cdn.deepseek.com/logo.png?x-image-process=image%2Fresize%2Cw_1920)\n" +
                "\n" +
                "温馨提示：冬季出行注意保暖，特别是前往寒冷地区时需准备足够的保暖衣物。";
        String result = new PDFGenerationTool().generatePDF(fileName, content);
        assertNotNull(result);
    }
}