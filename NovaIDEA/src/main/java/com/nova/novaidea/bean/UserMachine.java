package com.nova.novaidea.bean;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table
public class UserMachine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userid;
    private Integer machineid;
}
