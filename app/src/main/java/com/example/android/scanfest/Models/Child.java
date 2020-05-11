package com.example.android.scanfest.Models;

import java.io.Serializable;

public class Child  implements Serializable {


    String id;
    String schoolName;
    String studentClass;
    String relationship;
    String nameOfStudent;

    public Child(){}

    public Child(String id,String schoolName, String nameOfStudent, String studentClass, String relationship) {
        this.id = id;
        this.schoolName = schoolName;
        this.nameOfStudent = nameOfStudent;
        this.studentClass = studentClass;
        this.relationship = relationship;
    }


    public String getId() { return id; }

    public void setId(String id) { this.id = id; }
    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getNameOfStudent() {
        return nameOfStudent;
    }

    public void setNameOfStudent(String nameOfStudent) {
        this.nameOfStudent = nameOfStudent;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }


}
