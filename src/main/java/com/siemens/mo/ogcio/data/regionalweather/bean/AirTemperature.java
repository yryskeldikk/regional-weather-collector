package com.siemens.mo.ogcio.data.regionalweather.bean;

import java.util.Date;

public class AirTemperature {
    Date timestamp;
    String automaticWeatherStation;
    Float maxAirTempSinceMidnight;
    Float minAirTempSinceMidnight;

    public AirTemperature() {}

    public AirTemperature(Date timestamp, String automaticWeatherStation, Float maxAirTempSinceMidnight, Float minAirTempSinceMidnight) {
        this.timestamp = timestamp;
        this.automaticWeatherStation = automaticWeatherStation;
        this.maxAirTempSinceMidnight = maxAirTempSinceMidnight;
        this.minAirTempSinceMidnight = minAirTempSinceMidnight;
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

    public Float getMaxAirTempSinceMidnight() {
        return maxAirTempSinceMidnight;
    }

    public void setMaxAirTempSinceMidnight(Float maxAirTempSinceMidnight) {
        this.maxAirTempSinceMidnight = maxAirTempSinceMidnight;
    }

    public Float getMinAirTempSinceMidnight() {
        return minAirTempSinceMidnight;
    }

    public void setMinAirTempSinceMidnight(Float minAirTempSinceMidnight) {
        this.minAirTempSinceMidnight = minAirTempSinceMidnight;
    }
    @Override
    public String toString() {
        return "AirTemperature{" +
                "timestamp='" + timestamp + '\'' +
                ", automaticWeatherStation='" + automaticWeatherStation + '\'' +
                ", maxAirTempSinceMidnight='" + maxAirTempSinceMidnight + '\'' +
                ", minAirTempSinceMidnight='" + minAirTempSinceMidnight + '\'' +
                '}';
    }

}
