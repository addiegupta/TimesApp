package com.addie.maxfocus.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.addie.maxfocus.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Main activity that displays all the options of the app
 */
//TODO Implement tutorial activities to be displayed on first launch
//TODO Layout to display past usage of apps with/without usage of timers
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_apps)
    Button mAppsButton;
    private SharedPreferences preferences;
    private CheckBox mNeverAskAgainCheckbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.plant(new Timber.DebugTree());
//          Old library
//        launchIntroActivityIfFirstLaunch();

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);


        mAppsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAppsActivity();
            }
        });

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Check if permission has been granted manually
        if (!preferences.getBoolean(getString(R.string.usage_permission_pref), false)) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    && hasUsageStatsPermission(this)) {
                preferences.edit().putBoolean(getString(R.string.usage_permission_pref), true).apply();

            }
        }

        //TODO UI Added. Might contains some bugs related to absence of Usage Access Permission
        //FIXME While returning from settings screen , app activity has been destroyed
        if (!preferences.getBoolean(getString(R.string.usage_never_ask_again_pref_key), false)) {
            showRequestUsageAccessDialog();
        }

    }

    private void launchIntroActivityIfFirstLaunch() {

        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = prefs.getBoolean(getString(R.string.is_first_start), true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    final Intent i = new Intent(MainActivity.this, IntroActivityNew.class);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(i);
                        }
                    });

                    //  Edit preference to make it false because we don't want this to run again
//                    TODO: Comment this out. Done for debugging
//                    prefs.edit().putBoolean(getString(R.string.is_first_start),false).apply();
                }
            }
        });

        // Start the thread
        t.start();

    }

    private void showRequestUsageAccessDialog() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View checkboxLayout = layoutInflater.inflate(R.layout.never_ask_again_checkbox, null);

            mNeverAskAgainCheckbox = (CheckBox) checkboxLayout.findViewById(R.id.skip);
            builder.setView(checkboxLayout)
                    .setTitle(R.string.usage_permission_title)
                    .setMessage(R.string.usage_permission_message)
                    .setPositiveButton(R.string.grant_permission, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestUsageStatsPermission();
                        }
                    })

                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            preferences.edit().putBoolean(getString(R.string.usage_never_ask_again_pref_key)
                                    , mNeverAskAgainCheckbox.isChecked()).apply();
                            dialogInterface.dismiss();
                        }
                    });
            builder.show();
        }
    }

    void requestUsageStatsPermission() {
        //TODO Change to new app name
        Toast.makeText(this, R.string.usage_permission_instruction, Toast.LENGTH_LONG).show();
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
//        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putBoolean(getString(R.string.usage_permission_pref), granted).apply();

        return granted;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_main_action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            default:
                break;
        }

        return true;
    }

    private void startAppsActivity() {
        finish();
        Timber.e("AppsActivity:%s", AppsActivity.class.getSimpleName());
        startActivity(new Intent(MainActivity.this, AppsActivity.class));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //  Create a new boolean and preference and set it to true
        boolean isFirstStart = preferences.getBoolean(getString(R.string.is_first_start), true);

        //  If the activity has never started before...
        if (!isFirstStart) {
            finish();
        }
    }
}