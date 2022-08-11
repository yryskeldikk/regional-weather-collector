package com.siemens.mo.ogcio.data.regionalweather.bean;

import java.util.Date;

public class Wind {
    Date timestamp;
    String automaticWeatherStation;
    String meanWindDirection;
    Integer meanSpeed;
    Integer maxGust;

    public Wind() {}

    public Wind(Date timestamp, String automaticWeatherStation, String meanWindDirection, int meanSpeed, int maxGust) {
        this.timestamp = timestamp;
        this.automaticWeatherStation = automaticWeatherStation;
        this.meanWindDirection = meanWindDirection;
        this.meanSpeed = meanSpeed;
        this.maxGust = maxGust;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getAutomaticWeatherStation() {
        return automaticWeatherStation;
    }

    public void setAutomaticWeatherStation(String automaticWeatherStation) {
        this.automaticWeatherStation = automaticWeatherStation;
    }

    public String getMeanWindDirection() {
        return meanWindDirection;
    }

    public void setMeanWindDirection(String meanWindDirection) {
        this.meanWindDirection = meanWindDirection;
    }

    public Integer getMeanSpeed() {
        return meanSpeed;
    }
    public void setMeanSpeed(Integer meanSpeed) {
        this.meanSpeed = meanSpeed;
    }

    public Integer getMaxGust() {
        return maxGust;
    }
    public void setMaxGust(Integer maxGust) {
        this.maxGust = maxGust;
    }

    @Override
    public String toString() {
        return "Wind{" +
                "timestamp='" + timestamp + '\'' +
                ", automaticWeatherStation='" + automaticWeatherStation + '\'' +
                ", meanWindDirection='" + meanWindDirection + '\'' +
                ", meanSpeed='" + meanSpeed + '\'' +
                ", maxGust='" + maxGust + '\'' +
                '}';
    }
}
