package com.example.graduationproject.models;

public class ChildProfile {
    private final long id;
    private final String name;
    private final int age;
    private final String avatar;

    public ChildProfile(long id, String name, int age, String avatar) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.avatar = avatar;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getAvatar() { return avatar; }
}
