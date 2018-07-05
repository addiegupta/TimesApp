package com.addie.timesapp.ui;

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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.addie.timesapp.R;

public class PermissionSlide extends Fragment {

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int layoutResId;
    private SharedPreferences preferences;
    private Context mContext;

    public static PermissionSlide newInstance(int layoutResId) {
        PermissionSlide permissionSlide = new PermissionSlide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        permissionSlide.setArguments(args);

        return permissionSlide;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID)) {
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();

        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        Button mPermissionButton = (Button) getView().findViewById(R.id.btn_usage_permission);

        mPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showRequestUsageAccessDialog();
                grantPermissionClicked();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
//        CheckBox mGrantedCheckbox = (CheckBox) getView().findViewById(R.id.cb_permssion_granted);
        ImageView mCheckImageView =(ImageView) getView().findViewById(R.id.iv_permission_slide_check_state);

        if (hasUsageStatsPermission(mContext)){
                mCheckImageView.setImageResource(R.drawable.ic_check_green_24dp);
//            mGrantedCheckbox.setEnabled(true);
//            mGrantedCheckbox.setChecked(true);
        }
        else{

            mCheckImageView.setImageResource(R.drawable.ic_clear_red_24dp);
//            mGrantedCheckbox.setEnabled(false);
//            mGrantedCheckbox.setChecked(false);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        return inflater.inflate(layoutResId, container, false);
    }

    private void showRequestUsageAccessDialog() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(mContext)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            View checkboxLayout = layoutInflater.inflate(R.layout.never_ask_again_checkbox, null);

            final CheckBox mNeverAskAgainCheckbox = (CheckBox) checkboxLayout.findViewById(R.id.skip);
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
        } else {
            Toast.makeText(mContext, "Permission already granted!", Toast.LENGTH_SHORT).show();
        }
    }

    private void grantPermissionClicked() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(mContext)) {
            requestUsageStatsPermission();
        } else {
            Toast.makeText(mContext, "Permission already granted!", Toast.LENGTH_SHORT).show();
        }
    }


    void requestUsageStatsPermission() {
        Toast.makeText(mContext, R.string.usage_permission_instruction, Toast.LENGTH_LONG).show();
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
//        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;

        preferences.edit().putBoolean(getString(R.string.usage_permission_pref), granted).apply();

        return granted;
    }

}
