package com.example.graduationproject.models.profile.settings;

/** Equivalent of one entry in ChildProfilesScreen's `children` state. */
public class ChildProfile {
    public final int id;
    public final String name;
    public final int age;
    public final String avatarEmoji;

    public ChildProfile(int id, String name, int age, String avatarEmoji) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.avatarEmoji = avatarEmoji;
    }
}
