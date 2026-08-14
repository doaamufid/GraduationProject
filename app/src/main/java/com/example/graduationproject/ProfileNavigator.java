package com.example.graduationproject;

public interface ProfileNavigator {
    void navigate(String key);
    void showHome();
    void showChildren();
    void showChildDetail(long id);
    void showToast(String message);
}
