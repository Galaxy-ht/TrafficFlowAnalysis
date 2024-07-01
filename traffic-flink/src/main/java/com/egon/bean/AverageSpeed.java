package com.egon.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AverageSpeed {
    private int id;
    private long startTime;
    private long endTime;
    private String monitorId;
    private double avgSpeed;
    private int carCount;
}
