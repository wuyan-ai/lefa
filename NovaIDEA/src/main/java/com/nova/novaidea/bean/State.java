package com.nova.novaidea.bean;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Measurement(name = "State")
public class State {
    @Column(name = "time")
    private String time;      //更新时间

    // 注解中添加tag = true,表示当前字段内容为tag内容
    @Column(name = "id", tag = true)
    private String device_num;        //设备的编号

    @Column(name = "state")
    private Integer state;   //	由设备向云端提供的状态，值：0，1，2，0：离线 1：空闲 2：运行 3：故障
    @Column(name = "work_time")
    private Double work_time;  //设备本日的工作的时长  单位：h

}
