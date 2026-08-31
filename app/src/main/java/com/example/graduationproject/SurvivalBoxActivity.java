package com.example.graduationproject;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.example.graduationproject.ui.SummaryFragment;
import com.example.graduationproject.Fragments.CrisisModeFragment;

public class SurvivalBoxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        int screenColor = androidx.core.content.ContextCompat.getColor(this, R.color.bg);
        EdgeToEdge.enable(this, 
                androidx.activity.SystemBarStyle.light(screenColor, screenColor),
                androidx.activity.SystemBarStyle.light(screenColor, screenColor));

        setContentView(R.layout.activity_survival_box);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });

        // We use android.R.id.content as the root container to host the fragments.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SummaryFragment())
                    .commit();
        }
    }

    /** Opens the full-screen crisis-mode overlay. */
    public void openCrisisMode() {
        new CrisisModeFragment().show(getSupportFragmentManager(), "crisis_mode");
    }
}
