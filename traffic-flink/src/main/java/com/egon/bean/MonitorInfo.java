package com.egon.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * {"actionTime":1686647522,"monitorId":"0003","cameraId":"1","car":"豫A99999","speed":60,"roadId":"01","areaId":"20"}
 */
public class MonitorInfo {

    private Long actionTime;
    private String monitorId;
    private String cameraId;
    private String car;
    private Double speed;  //车辆通过卡口的实际车速
    private String roadId;
    private String areaId;

    private Integer speedLimit;  //卡口的限速

}
