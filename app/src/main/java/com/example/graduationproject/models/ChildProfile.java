package com.example.graduationproject.models;

public class ChildProfile {
    private final long id;
    private final String name;
    private final int age;
    private final String gender;
    private final String avatar;
    private final int stars;

    // Constructor جديد فيه stars
    public ChildProfile(long id, String name, int age, String gender, String avatar, int stars) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.avatar = avatar;
        this.stars = stars;
    }

    // نبقّي الـ constructor القديم كمان (stars = 0 افتراضياً) حتى ما نكسر أي مكان تاني بيستخدمه بالمشروع
    public ChildProfile(long id, String name, int age, String gender, String avatar) {
        this(id, name, age, gender, avatar, 0);
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getAvatar() { return avatar; }
    public int getStars() { return stars; }
}