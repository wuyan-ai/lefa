package com.nova.novaidea.bean;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Measurement(name = "MachineWorkInfo")
public class MachineWorkInfo {
    //一条消息代表加工一个龙眼

    // Column中的name为measurement中的列名
    // InfluxDB中时间戳均是以UTC时保存,在保存以及提取过程中需要注意时区转换
    @Column(name = "time")
    private String time;

    // 注解中添加tag = true,表示当前字段内容为tag内容
    @Column(name = "device_num", tag = true)
    private String device_num;     //设备编号
    @Column(name = "id", tag = true)
    private String device_slot_id;        //设备中工位的id，值：1、2、3、4、5、6
    @Column(name = "is_ok", tag = true)
    private String is_ok;
    @Column(name = "has_longan", tag = true)
    private String has_longan;

    @Column(name = "do_tomes")
    private String do_tomes;
    @Column(name = "done_time")
    private String done_time;
    @Column(name = "range")
    private Double range;
    @Column(name = "p")
    private Double p;
    @Column(name = "group")
    private Double group;

}
