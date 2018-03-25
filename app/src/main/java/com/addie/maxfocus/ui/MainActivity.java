package com.addie.maxfocus.ui;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.addie.maxfocus.R;
import com.addie.maxfocus.adapter.AppAdapter;
import com.addie.maxfocus.model.App;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements AppAdapter.AppOnClickHandler{

    @BindView(R.id.rv_main_apps)
    RecyclerView mAppsRecyclerView;

    private AppAdapter mAdapter;
    private ArrayList<App> mAppsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());

        loadAppsList();

        initialiseRecyclerView();

    }

    /**
     * Loads a list of installed apps on the device using PackageManager
     */
    //TODO Change to different thread to prevent main thread from freezing
    private void loadAppsList(){
        mAppsList = new ArrayList<>();

        final PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        // Sorts the list in alphabetical order of app names
        final PackageItemInfo.DisplayNameComparator comparator = new PackageItemInfo.DisplayNameComparator(packageManager);
        Collections.sort(packages, new Comparator<ApplicationInfo>()
        {
            @Override
            public int compare(ApplicationInfo lhs, ApplicationInfo rhs)
            {
                return comparator.compare(lhs, rhs);
            }
        });

        // Adds app to the list if it has a launch activity
        for (ApplicationInfo packageInfo : packages) {

            if (packageManager.getLaunchIntentForPackage(packageInfo.packageName)!=null){

                App app = new App(packageInfo.loadLabel(packageManager).toString(),packageInfo.packageName,packageInfo.loadIcon(packageManager));
                mAppsList.add(app);
                Timber.d(app.getmIcon()+ " " +app.getmPackage() + " "+ app.getmTitle());
            }
        }
    }

    /**
     * Initialises the RecyclerView displaying the list of apps
     */
    private void initialiseRecyclerView(){
        mAdapter = new AppAdapter(this, this);
        mAppsRecyclerView.setAdapter(mAdapter);

        mAdapter.setListData(mAppsList);

        mAppsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAppsRecyclerView.setHasFixedSize(true);

    }

    @Override
    public void onClick(App selectedApp) {
        PackageManager packageManager = getPackageManager();
        Intent launchIntent = packageManager.getLaunchIntentForPackage(selectedApp.getmPackage());
        startActivity(launchIntent);
    }


}
