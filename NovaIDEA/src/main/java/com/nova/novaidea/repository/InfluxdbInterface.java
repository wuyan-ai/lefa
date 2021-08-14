package com.nova.novaidea.repository;

import java.awt.*;
import java.util.*;
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
    private final String measurement = "MachineWorkInfo";

    @Value("${spring.influx.database}")
    private String database;

    /**
     * 插入数据
     */
    @GetMapping("/index")
    public void insert(){
        List<String> lines = new ArrayList<String>();
        Point point = null;

            point = Point.measurement("State")
                    .tag("id", "b")
                    .addField("state", 1)
                    .addField("work_time", 1.5).build();
            lines.add(point.lineProtocol());
        //写入
        influxDB.write(lines);
    }
    @GetMapping("/test")
    public void insertMachine(){
        List<String> lines = new ArrayList<String>();
        Point point = null;
        point = Point.measurement(measurement)
                .tag("device_num", "a")
                .tag("id","1")
                .tag("is_ok","true")
                .tag("has_longan","true")
                .addField("do_tomes", 1)
                .addField("done_time", 1)
                .addField("range",1)
                .addField("p", 1.5)
                .addField("group", 1.5).build();
        lines.add(point.lineProtocol());
        //写入
        influxDB.write(lines);
    }
    /**
     * 获取数据
     */
    @GetMapping("/getMachineInfo")
    //查询机器num查询state,今日运行时长，更新时间
    public Map<String,Object> getMachineInfo(@RequestParam String num){
        String queryCmd = "SELECT * FROM State WHERE id='"+num+"'  ORDER BY time desc LIMIT 1 ";
        // 添加查询条件(注意查询条件选择tag值,选择field数值会严重拖慢查询速度)
        QueryResult queryResult = influxDB.query(new Query(queryCmd, database));
        System.out.println("query result =>"+ queryResult.getResults());
        if(queryResult.getResults()==null) return null;
        Map<String,Object> resultList=new HashMap<>();
        for (QueryResult.Result result : queryResult.getResults()) {
            List<QueryResult.Series> series = result.getSeries();
            if(series==null) return null;
            for (QueryResult.Series serie : series) {
                List<List<Object>> values = serie.getValues();//字段字集合
                System.out.println("values:" + values);
                for (List<Object> value : values) {
                    System.out.println("value:" + value.get(1));
                    try{
                        resultList.put("machineUpdateTime",value.get(0).toString());   //注意 0为时间戳
                        resultList.put("machineStatus",Double.valueOf(value.get(2).toString()).intValue());
                        resultList.put("machineWorkTime",Double.valueOf(value.get(3).toString()));
                    }catch (Exception e){
                        System.out.println("state,work_time类型转换出错");
                        return null;
                    }
                }
            }
        }
        return resultList;
    }

    @GetMapping("/yearOutput")
    //查询机器某段时间内的产量（龙眼个数）
    public int yearOutput(@RequestParam String num,@RequestParam String startTime,@RequestParam String endTime){
        /**
         * dayFlag:  时间 1h 1d
         * startTime：起始时间
         * endTime：结束时间 下月起始时刻
         */
        String queryCmd;
        if(endTime!="")
            queryCmd = "SELECT count(*) FROM MachineWorkInfo WHERE device_num='"+num+"' and TIME > '"+startTime+"' and TIME <= '"+endTime+"'";
        else
            queryCmd = "SELECT count(*) FROM MachineWorkInfo WHERE device_num='"+num+"' and TIME >= '"+startTime+"'";
        // 添加查询条件(注意查询条件选择tag值,选择field数值会严重拖慢查询速度)
        QueryResult queryResult = influxDB.query(new Query(queryCmd, database));
        System.out.println("query result =>"+ queryResult.getResults());
        if(queryResult.getResults()==null) return 0;
        //results.getResults()是同时查询多条SQL语句的返回值
        List<Integer> resultList=new LinkedList<>();
        for (QueryResult.Result result : queryResult.getResults()) {
            List<QueryResult.Series> series = result.getSeries();
            if(series==null||series.size()==0) return 0;
            for (QueryResult.Series serie : series) {
                List<List<Object>> values = serie.getValues();//字段字集合
                System.out.println("values:" + values);
                for (List<Object> value : values) {
                    String temp=value.get(1).toString();
                    resultList.add(Double.valueOf(temp).intValue());   //注意 0为时间戳
                }
            }
        }
        return resultList.get(0);
    }

    //根据机器num计算产量（龙眼个数）
    @GetMapping("/outputSum")
    public List<Integer> outputSum(@RequestParam String num,@RequestParam String time,@RequestParam String dayFlag){
        /**
         * dayFlag:  时间 1h 1d
         * 本天：1h
         * 本周：1d
         * 本月：1d
         * 本周和本月 起始time不同
         */
        String queryCmd = "SELECT count(*) FROM MachineWorkInfo WHERE device_num='"+num+"' and TIME >= '"+time+"' GROUP BY time("+dayFlag+")";
        // 添加查询条件(注意查询条件选择tag值,选择field数值会严重拖慢查询速度)
        QueryResult queryResult = influxDB.query(new Query(queryCmd, database));
        System.out.println("query result =>"+ queryResult.getResults());
        if(queryResult.getResults()==null) return null;
        //results.getResults()是同时查询多条SQL语句的返回值
        List<Integer> resultList=new LinkedList<>();
        for (QueryResult.Result result : queryResult.getResults()) {
            List<QueryResult.Series> series = result.getSeries();
            for (QueryResult.Series serie : series) {
                List<List<Object>> values = serie.getValues();//字段字集合
                System.out.println("values:" + values);
                for (List<Object> value : values) {
                    String temp=value.get(1).toString();
                    resultList.add(Double.valueOf(temp).intValue());   //注意 0为时间戳
                }
            }
        }
        return resultList;
    }
}
