package it.gdg.ancona.android.firebasepizzaparty;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MainActivity extends FireBaseChatActivity implements OnCompleteListener<Void> {

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        fetchRemoteConfigValuesValues();

    }

    private void fetchRemoteConfigValuesValues() {
        long cacheExpiration = 3600;
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(MainActivity.this, MainActivity.this);
    }


    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {
            mFirebaseRemoteConfig.activateFetched();
        } else {
            //TODO
        }
        setTheColor();
    }

    private static final String THE_COLOR_KEY = "the_color";
    private void setTheColor() {
        String the_color_value = mFirebaseRemoteConfig.getString(THE_COLOR_KEY);
        int the_color = Color.parseColor(the_color_value);
        findViewById(R.id.send_layout).setBackgroundColor(the_color);
    }

}
