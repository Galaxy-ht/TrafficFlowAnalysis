package com.egon.analysis.entity.enums;

public enum AreaEnum {

    WJ("01", "武进区"),
    XB("02", "新北区"),
    ZL("03", "钟楼区"),
    TN("04", "天宁区"),
    JT("05", "金坛区"),
    LY("06", "溧阳市");

    private String id;
    private String county;

    AreaEnum(String id, String county) {
        this.id = id;
        this.county = county;
    }

    public static String getById(String id) {
        for (AreaEnum item : AreaEnum.values()) {
            if (item.getId().equals(id)) {
                return item.getCounty();
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }
}
