package com.example.graduationproject.models;

import com.example.graduationproject.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Static data source. Updated with 5 environments, each having GIFs and sound layers. */
public final class HealingEnvironmentRepository {

    public static final int TRACK_LEN = 652; // seconds

    public static final String ENV_SEA_WAVES = "sea_waves";
    public static final String ENV_RAIN = "rain";
    public static final String ENV_FIREPLACE = "fireplace";
    public static final String ENV_NIGHT_FOREST = "night_forest";
    public static final String ENV_LIBRARY = "library";

    private static final List<HealingEnvironment> ENVIRONMENTS = new ArrayList<>();
    public static final List<TimerOption> TIMERS = Arrays.asList(
            new TimerOption(15, "١٥ د"),
            new TimerOption(30, "٣٠ د"),
            new TimerOption(45, "٤٥ د"),
            new TimerOption(TimerOption.SLEEP, "نوم")
    );

    static {
        // 1. sea Waves
        ENVIRONMENTS.add(new HealingEnvironment(
                ENV_SEA_WAVES, "أمواج البحر", "SEA WAVES", R.drawable.ic_waves, R.drawable.bg_env_beach, R.drawable.waves,
                Arrays.asList(
                        new SoundLayer("waves", "الأمواج", R.drawable.ic_waves_primary, R.raw.gentle_ocean_waves, 80),
                        new SoundLayer("water", "ماء", R.drawable.ic_droplet_primary, R.raw.water_stream, 10),
                        new SoundLayer("birds", "طيور", R.drawable.ic_bird_primary, R.raw.birds_ambient, 20),
                        new SoundLayer("fire", "مدفأة", R.drawable.ic_zap_primary, R.raw.forest_nature, 0),
                        new SoundLayer("gentle_rain", "مطر هادئ", R.drawable.ic_cloud_rain_primary, R.raw.gentle_rain, 0)
                )
        ));

        // 2. rain
        ENVIRONMENTS.add(new HealingEnvironment(
                ENV_RAIN, "مطر ليلي", "RAIN", R.drawable.ic_cloud_rain, R.drawable.bg_env_rain, R.drawable.rain,
                Arrays.asList(
                        new SoundLayer("rain", "المطر", R.drawable.ic_cloud_rain_primary, R.raw.gentle_rain, 75),
                        new SoundLayer("water", "ماء", R.drawable.ic_droplet_primary, R.raw.water_stream, 20),
                        new SoundLayer("birds", "طيور", R.drawable.ic_bird_primary, R.raw.birds_ambient, 0),
                        new SoundLayer("fire", "مدفأة", R.drawable.ic_zap_primary, R.raw.forest_nature, 0),
                        new SoundLayer("heavy_rain", "وقع المطر", R.drawable.ic_droplet_primary, R.raw.freak_rain_sound, 30)
                )
        ));

        // 3. Fireplace
        ENVIRONMENTS.add(new HealingEnvironment(
                ENV_FIREPLACE, "مدفأة دافئة", "FIREPLACE", R.drawable.ic_zap_primary, R.drawable.bg_env_mountain, R.drawable.fireplace,
                Arrays.asList(
                        new SoundLayer("fire", "النار", R.drawable.ic_zap_primary, R.raw.forest_nature, 70),
                        new SoundLayer("water", "ماء", R.drawable.ic_droplet_primary, R.raw.water_stream, 0),
                        new SoundLayer("birds", "طيور", R.drawable.ic_bird_primary, R.raw.birds_ambient, 10),
                        new SoundLayer("gentle_rain", "مطر هادئ", R.drawable.ic_cloud_rain_primary, R.raw.gentle_rain, 15)
                )
        ));

        // 4. Night Forest
        ENVIRONMENTS.add(new HealingEnvironment(
                ENV_NIGHT_FOREST, "غابة ", "FOREST", R.drawable.ic_trees, R.drawable.bg_env_forest, R.drawable.forest,
                Arrays.asList(
                        new SoundLayer("forest", "أصوات الليل", R.drawable.ic_trees_primary, R.raw.forest_soundscape_night, 70),
                        new SoundLayer("water", "ماء", R.drawable.ic_droplet_primary, R.raw.water_stream, 25),
                        new SoundLayer("birds", "طيور", R.drawable.ic_bird_primary, R.raw.birds_ambient, 30),
                        new SoundLayer("fire", "مدفأة", R.drawable.ic_zap_primary, R.raw.forest_nature, 0),
                        new SoundLayer("gentle_rain", "مطر هادئ", R.drawable.ic_cloud_rain_primary, R.raw.gentle_rain, 10)
                )
        ));

        // 5. Library
        ENVIRONMENTS.add(new HealingEnvironment(
                ENV_LIBRARY, "مكتبة هادئة", "LIBRARY", R.drawable.ic_mountain, R.drawable.bg_env_library, R.drawable.library,
                Arrays.asList(
                        new SoundLayer("library", "أجواء المكتبة", R.drawable.ic_wind_primary, R.raw.a_small_library_in_kore6, 85),
                        new SoundLayer("water", "ماء", R.drawable.ic_droplet_primary, R.raw.water_stream, 0),
                        new SoundLayer("birds", "طيور", R.drawable.ic_bird_primary, R.raw.birds_ambient, 0),
                        new SoundLayer("fire", "مدفأة", R.drawable.ic_zap_primary, R.raw.forest_nature, 0),
                        new SoundLayer("gentle_rain", "مطر هادئ", R.drawable.ic_cloud_rain_primary, R.raw.gentle_rain, 10)
                )
        ));
    }

    private HealingEnvironmentRepository() {
    }

    public static List<HealingEnvironment> getAll() {
        return ENVIRONMENTS;
    }


    /** Index of the default starting environment. */
    public static int defaultIndex() {
        return 0;
    }
}
