package com.example.android.scanfest.Models;

public class ChildInfo {
    String id, nameOfStudent, timeIn,timeOut,date;

    public ChildInfo(){}

    public ChildInfo(String id, String nameOfStudent, String timeIn, String timeOut, String date) {
        this.id = id;
        this.nameOfStudent = nameOfStudent;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameOfStudent() {
        return nameOfStudent;
    }

    public void setNameOfStudent(String nameOfStudent) {
        this.nameOfStudent = nameOfStudent;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
