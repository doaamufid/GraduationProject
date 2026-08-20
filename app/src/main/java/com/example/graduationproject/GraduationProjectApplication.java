package com.example.graduationproject;

import android.app.Application;
import app.rive.runtime.kotlin.core.Rive;
import app.rive.runtime.kotlin.core.RendererType;

public class GraduationProjectApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Rive with Canvas renderer for better compatibility
        Rive.INSTANCE.init(this, RendererType.Canvas);
    }
}
