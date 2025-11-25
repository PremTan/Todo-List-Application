package com.mojoes.filehandling;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private int age;

    private transient long adharNo;

    public User(String name, int age, long adharNo) {
        this.name = name;
        this.age = age;
        this.adharNo = adharNo;
    }
}
