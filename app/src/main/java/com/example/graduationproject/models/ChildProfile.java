package com.example.graduationproject.models;

public class ChildProfile {
    private final long id;
    private final String name;
    private final int age;
    private final String gender;
    private final String avatar;

    public ChildProfile(long id, String name, int age, String gender, String avatar) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.avatar = avatar;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getAvatar() { return avatar; }
}