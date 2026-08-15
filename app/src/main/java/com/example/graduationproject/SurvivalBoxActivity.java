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
        EdgeToEdge.enable(this);

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
