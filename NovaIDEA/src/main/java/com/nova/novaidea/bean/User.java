package com.nova.novaidea.bean;

import lombok.Data;

import javax.persistence.*;
@Entity
@Data
@Table
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userName;
    private String password;
}
