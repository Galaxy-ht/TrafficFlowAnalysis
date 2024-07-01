package com.egon.analysis;

import com.egon.analysis.entity.vo.CzRoadVO;
import com.egon.analysis.service.CzRoadService;
import com.egon.analysis.service.HbaseService;
import org.apache.hadoop.hbase.client.Scan;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest
class TrafficAnalysisApplicationTests {

    @Resource
    HbaseService hbaseService;

    @Resource
    CzRoadService czRoadService;

    @Test
    void contextLoads() {
        String s = "MultiLineString(31.5421889 119.2604873,31.5424704 119.259946,31.5426688 119.2594488,31.5428216 119.2591335,31.5430294 119.2588409,31.5432345 119.2586253,31.5434612 119.2584678,31.5437325 119.2583433,31.5440318 119.2582792,31.5443155 119.2582719,31.5445938 119.2583125,31.5447973 119.2583666)";

        // 截取括号中的内容
        String content = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));
        // 按逗号分割字符串并处理每个元素
        String[] elements = content.split(",");
        List<List<String>> resultList = new ArrayList<>();
        List<String> coordinatePair = new ArrayList<>();
        for (String element : elements) {
            // 将空格替换为逗号，并添加到坐标对列表中
            String replacedElement = element.replace(" ", ",");
            coordinatePair.add(replacedElement);
            // 每两个元素为一对坐标，添加到结果列表中，并清空坐标对列表
            if (coordinatePair.size() == 2) {
                resultList.add(new ArrayList<>(coordinatePair));
                coordinatePair.clear();
            }
        }

        // 打印结果列表
        System.out.println(resultList);
    }

    @Test
    void HBaseTest() throws Exception {
        Map<String, Map<String, String>> test = hbaseService.queryData("user", new Scan());
        System.out.println(test);
    }

    @Test
    void Monitor() throws Exception {
        czRoadService.insertMonitor();
    }

}
