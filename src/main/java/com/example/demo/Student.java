package com.example.demo;

public class Student {
    private String firstName;
    private String id;
    public Student(String id, String firstName) {
        this.id = id;
        this.firstName = firstName;

    }

    // Getters and setters
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }
    String getFirstName(){
        return firstName ;
    }
    public void setId(String id){
        this.id = id;
    }
    String getId(){
        return id ;
    }
}
