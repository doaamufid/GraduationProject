package com.example.graduationproject.models.profile;

public class ChildProfile {
    public final long id;
    public final String name;
    public final int age;
    public final String avatarEmoji;

    public ChildProfile(long id, String name, int age, String avatarEmoji) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.avatarEmoji = avatarEmoji;
    }
}
