package com.nova.novaidea.repository;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import org.influxdb.InfluxDB;

import org.influxdb.dto.Point;

import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;


@RestController
public class InfluxdbInterface {
    @Autowired
    private InfluxDB influxDB;

    //measurement
    private final String measurement = "State";

    @Value("${spring.influx.database}")
    private String database;

    /**
     * 插入数据
     */
    @GetMapping("/index")
    public void insert(){
        List<String> lines = new ArrayList<String>();
        Point point = null;

            point = Point.measurement(measurement)
                    .tag("id", "sensor")
                    .addField("state", 1)
                    .addField("work_time", 1.5).build();
            lines.add(point.lineProtocol());
        //写入
        influxDB.write(lines);
    }

    /**
     * 获取数据
     */
    @GetMapping("/datas")
    public void datas(){
        String queryCondition = "";  //查询条件暂且为空
        // 此处查询所有内容,如果
        String queryCmd = "SELECT * FROM "
                // 查询指定设备下的日志信息
                // 要指定从 RetentionPolicyName.measurement中查询指定数据,默认的策略可以不加；
                // + 策略name + "." + measurement
                + measurement
                // 添加查询条件(注意查询条件选择tag值,选择field数值会严重拖慢查询速度)
                + queryCondition
                // 查询结果需要按照时间排序
                + " ORDER BY time DESC";
                // 添加分页查询条件
               // + pageQuery;

        QueryResult queryResult = influxDB.query(new Query(queryCmd, database));
        System.out.println("query result =>"+ queryResult);
    }

    //
}
