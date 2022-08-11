package com.siemens.mo.ogcio.data.regionalweather.bean;

import java.util.Date;

public class Humidity {
    Date timestamp;
    String automaticWeatherStation;
    Integer relativeHumidity;

    public Humidity() {}

    public Humidity(Date timestamp, String automaticWeatherStation, Integer relativeHumidity) {
        this.timestamp = timestamp;
        this.automaticWeatherStation = automaticWeatherStation;
        this.relativeHumidity = relativeHumidity;
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

    public Integer getRelativeHumidity() {
        return relativeHumidity;
    }

    public void setRelativeHumidity(Integer relativeHumidity) {
        this.relativeHumidity = relativeHumidity;
    }

    @Override
    public String toString() {
        return "Humidity{" +
                "timestamp='" + timestamp + '\'' +
                ", automaticWeatherStation='" + automaticWeatherStation + '\'' +
                ", relativeHumidity='" + relativeHumidity + '\'' +
                '}';
    }
}
