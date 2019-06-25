package com.example.studentlocator.Model;

public class Student {

    private String imageEncoded, userName, department, location;


    public Student(String imageEncoded, String userName, String department, String location) {
        this.imageEncoded = imageEncoded;
        this.userName = userName;
        this.department = department;
        this.location = location;
    }


    public Student() {
    }


    public String getImageEncoded() {
        return imageEncoded;
    }

    public void setImageEncoded(String imageEncoded) {
        this.imageEncoded = imageEncoded;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }




}
