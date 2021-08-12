package com.nova.novaidea.bean;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table
public class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;    //设备id :1 2....
    private String deviceNum;  //设备编号（使用mqtt为设备自动创建）
    private String createTime;//使用mqtt为设备创建编号的时间
    private Integer deviceName;//设备类型
    private String deviceKey;  //设备验证码
}
