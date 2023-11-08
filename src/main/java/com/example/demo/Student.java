package com.example.demo;

public class Student {
    private String firstName;
    private String lastName;
    private String gender;
    private String GPA;
    private String level;
    private String address;
    private String id;

    public Student(String id, String firstName, String lastName, String gender, String gpa, String level, String address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.GPA = gpa;
        this.gender = gender;
        this.level = level;
        this.address = address;
    }

    public Student() {};

    // Getters and setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGPA() {
        return GPA;
    }

    public void setGPA(String GPA) {
        this.GPA = GPA;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
