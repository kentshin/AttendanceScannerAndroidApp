package com.example.ivolunteerschedscanner;

import com.google.firebase.Timestamp;

public class record_lists {

    private String event_id;
    private Timestamp time_date;
    private String hours;




    public record_lists () {
    }

    public record_lists (String event_id, Timestamp time_date, String hours) {
        this.event_id =event_id;
        this.time_date = time_date;
        this.hours = hours;



    }


    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public Timestamp getTime_date() {
        return time_date;
    }

    public void setTime_date(Timestamp time_date) {
        this.time_date = time_date;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }



}
