
package com.example.maskinfo.model;

import com.squareup.moshi.Json;
// Comparable -> 비교하는 규칙을 재정의 할 수 있음
public class Store implements Comparable<Store>{

    @Json(name = "addr")
    private String addr;
    @Json(name = "code")
    private String code;
    @Json(name = "created_at")
    private String createdAt;
    @Json(name = "lat")
    private double lat;
    @Json(name = "lng")
    private double lng;
    @Json(name = "name")
    private String name;
    @Json(name = "remain_stat")
    private String remainStat;
    @Json(name = "stock_at")
    private String stockAt;
    @Json(name = "type")
    private String type;

    private double distance;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemainStat() {
        return remainStat;
    }

    public void setRemainStat(String remainStat) {
        this.remainStat = remainStat;
    }

    public String getStockAt() {
        return stockAt;
    }

    public void setStockAt(String stockAt) {
        this.stockAt = stockAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int compareTo(Store o) {
        // 비교를 해서 내가 더 크면 양수, 작으면 음수, 같으면 0을 리턴
        return Double.compare(distance, o.distance);
    }
}
