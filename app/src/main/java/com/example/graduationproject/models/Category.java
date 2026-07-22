package com.example.graduationproject.models;

public class Category {
    private int id;
    private String name;
    private boolean isSelected; // لتحديد الزر النشط وتغيير لونه

    public Category(int id, String name, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.isSelected = isSelected;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}