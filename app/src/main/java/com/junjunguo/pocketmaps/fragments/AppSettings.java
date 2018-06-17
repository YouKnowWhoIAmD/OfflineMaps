package com.junjunguo.pocketmaps.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.junjunguo.pocketmaps.R;
import com.junjunguo.pocketmaps.activities.MainActivity;
import com.junjunguo.pocketmaps.activities.Analytics;
import com.junjunguo.pocketmaps.map.Tracking;
import com.junjunguo.pocketmaps.util.Variable;

import java.text.SimpleDateFormat;
/**
 * Created by Google Deveoper Student Club
 * Varshit Ratna(leader)
 * Devaraj Akhil(Core team)
 */

public class AppSettings {
    private Activity activity;
    private ViewGroup appSettingsVP, trackingAnalyticsVP, changeMapItemVP;
    private TextView tvspeed, tvdistance, tvdisunit;

    /**
     * init and set
     *
     * @param activity
     * @param calledFromVP
     */
    public AppSettings (Activity activity) {
        this.activity = activity;
        appSettingsVP = (ViewGroup) activity.findViewById(R.id.app_settings_layout);
        trackingAnalyticsVP = (ViewGroup) activity.findViewById(R.id.app_settings_tracking_analytics);
        changeMapItemVP = (ViewGroup) activity.findViewById(R.id.app_settings_change_map);
    }
    
    public void showAppSettings(final ViewGroup calledFromVP)
    {
        initClearBtn(appSettingsVP, calledFromVP);
        chooseMapBtn(appSettingsVP);
        trackingBtn(appSettingsVP);
        alternateRoute();
        appSettingsVP.setVisibility(View.VISIBLE);
        calledFromVP.setVisibility(View.INVISIBLE);
        naviDirections();
        if (Tracking.getTracking().isTracking()) resetAnalyticsItem();
    }


    public ViewGroup getAppSettingsVP() {
        return appSettingsVP;
    }

    /**
     * init and implement directions checkbox
     */
    private void naviDirections() {
        CheckBox cb = (CheckBox) activity.findViewById(R.id.app_settings_directions_cb);
        final CheckBox cb_voice = (CheckBox) activity.findViewById(R.id.app_settings_voice);
        final CheckBox cb_light = (CheckBox) activity.findViewById(R.id.app_settings_light);
        final TextView txt_voice = (TextView) activity.findViewById(R.id.txt_voice);
        final TextView txt_light = (TextView) activity.findViewById(R.id.txt_light);
        cb.setChecked(Variable.getVariable().isDirectionsON());
        cb_voice.setChecked(Variable.getVariable().isVoiceON());
        cb_light.setChecked(Variable.getVariable().isLightSensorON());
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Variable.getVariable().setDirectionsON(isChecked);
                cb_voice.setEnabled(isChecked);
                cb_light.setEnabled(isChecked);
                txt_voice.setEnabled(isChecked);
                txt_light.setEnabled(isChecked);
            }
        });
        cb_voice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              Variable.getVariable().setVoiceON(isChecked);
          }
        });
        cb_light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              Variable.getVariable().setLightSensorON(isChecked);
          }
        });
        if (!Variable.getVariable().isDirectionsON())
        {
          cb_voice.setEnabled(false);
          cb_light.setEnabled(false);
          txt_voice.setEnabled(false);
          txt_light.setEnabled(false);
        }
    }
    
    /**
     * init and set alternate route radio button option
     */
    private void alternateRoute() {
        RadioGroup rg = (RadioGroup) activity.findViewById(R.id.app_settings_weighting_rbtngroup);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.app_settings_fastest_rbtn:
                        Variable.getVariable().setWeighting("fastest");
                        break;
                    case R.id.app_settings_shortest_rbtn:
                        Variable.getVariable().setWeighting("shortest");
                        break;
                }
            }
        });
        RadioButton rbf, rbs;
        rbf = (RadioButton) activity.findViewById(R.id.app_settings_fastest_rbtn);
        rbs = (RadioButton) activity.findViewById(R.id.app_settings_shortest_rbtn);
        if (Variable.getVariable().getWeighting().equalsIgnoreCase("fastest")) {
            rbf.setChecked(true);
        } else {
            rbs.setChecked(true);
        }
    }

    /**
     * tracking item btn handler
     *
     * @param appSettingsVP
     */
    private void trackingBtn(final ViewGroup appSettingsVP) {
        //        final ImageView iv = (ImageView) activity.findViewById(R.id.app_settings_tracking_iv);
        //        final TextView tv = (TextView) activity.findViewById(R.id.app_settings_tracking_tv);
        trackingBtnClicked();
        final ViewGroup tbtn = (ViewGroup) activity.findViewById(R.id.app_settings_tracking);
        tbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        tbtn.setBackgroundColor(activity.getResources().getColor(R.color.my_primary_light));
                        return true;
                    case MotionEvent.ACTION_UP:
                        tbtn.setBackgroundColor(activity.getResources().getColor(R.color.my_icons));
                        if (Tracking.getTracking().isTracking()) {
                            confirmWindow();
                        } else {
                            Tracking.getTracking().startTracking();
                        }
                        trackingBtnClicked();
                        return true;
                }
                return false;
            }
        });
    }

    private void confirmWindow() {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        final EditText edittext = new EditText(activity);
        builder.setTitle(activity.getResources().getString(R.string.dialog_stop_save_tracking));
        builder.setMessage("path: " + Variable.getVariable().getTrackingFolder().getAbsolutePath() + "/");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String formattedDate = df.format(System.currentTimeMillis());
        edittext.setText(formattedDate);
        builder.setView(edittext);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        //        builder.setView(inflater.inflate(R.layout.dialog_tracking_exit, null));
        // Add action buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int id) {
                // save file
                Tracking.getTracking().saveAsGPX(edittext.getText().toString());
                Tracking.getTracking().stopTracking(AppSettings.this);
                trackingBtnClicked();
            }
        }).setNeutralButton(R.string.stop, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Tracking.getTracking().stopTracking(AppSettings.this);
                trackingBtnClicked();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        //        ((EditText) ((AlertDialog) dialog).findViewById(R.id.dialog_tracking_exit_et)).setText(formattedDate);
        dialog.show();
    }

    /**
     * dynamic show start or stop tracking
     */
    public void trackingBtnClicked() {
        final ImageView iv = (ImageView) activity.findViewById(R.id.app_settings_tracking_iv);
        final TextView tv = (TextView) activity.findViewById(R.id.app_settings_tracking_tv_switch);
        if (Tracking.getTracking().isTracking()) {
            iv.setImageResource(R.drawable.ic_stop_orange_24dp);
            tv.setTextColor(activity.getResources().getColor(R.color.my_accent));
            tv.setText(R.string.tracking_stop);
            resetAnalyticsItem();
        } else {
            iv.setImageResource(R.drawable.ic_play_arrow_light_green_a700_24dp);
            tv.setTextColor(activity.getResources().getColor(R.color.my_primary));
            tv.setText(R.string.tracking_start);
            trackingAnalyticsVP.setVisibility(View.GONE);
            changeMapItemVP.setVisibility(View.VISIBLE);
        }
    }

    /**
     * init and reset analytics items to visible (when tracking start)
     */
    private void resetAnalyticsItem() {
        changeMapItemVP.setVisibility(View.GONE);
        trackingAnalyticsVP.setVisibility(View.VISIBLE);
        trackingAnalyticsBtn();
        tvspeed = (TextView) activity.findViewById(R.id.app_settings_tracking_tv_tracking_speed);
        tvdistance = (TextView) activity.findViewById(R.id.app_settings_tracking_tv_tracking_distance);
        tvdisunit = (TextView) activity.findViewById(R.id.app_settings_tracking_tv_tracking_distance_unit);
        updateAnalytics(Tracking.getTracking().getAvgSpeed(), Tracking.getTracking().getDistance());
    }

    /**
     * actions to preform when tracking analytics item (btn) is clicked
     */
    private void trackingAnalyticsBtn() {
        trackingAnalyticsVP.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        trackingAnalyticsVP
                                .setBackgroundColor(activity.getResources().getColor(R.color.my_primary_light));
                        return true;
                    case MotionEvent.ACTION_UP:
                        trackingAnalyticsVP.setBackgroundColor(activity.getResources().getColor(R.color.my_icons));
                        openAnalyticsActivity();
                        return true;
                }
                return false;
            }
        });
    }

    /**
     * update speed and distance at analytics item
     *
     * @param speed
     * @param distance
     */
    public void updateAnalytics(double speed, double distance) {
        if (distance < 1000) {
            tvdistance.setText(String.valueOf(Math.round(distance)));
            tvdisunit.setText(R.string.meter);
        } else {
            tvdistance.setText(String.format("%.1f", distance / 1000));
            tvdisunit.setText(R.string.km);
        }
        tvspeed.setText(String.format("%.1f", speed));
    }


    /**
     * move to select and load map view
     *
     * @param appSettingsVP
     */
    private void chooseMapBtn(final ViewGroup appSettingsVP) {
        changeMapItemVP.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        changeMapItemVP.setBackgroundColor(activity.getResources().getColor(R.color.my_primary_light));
                        return true;
                    case MotionEvent.ACTION_UP:
                        changeMapItemVP.setBackgroundColor(activity.getResources().getColor(R.color.my_icons));
                        // Variable.getVariable().setAutoLoad(false); // close auto load from
                        // main activity
                        activity.finish();
                        startMainActivity();
                        return true;
                }
                return false;
            }
        });
    }

    /**
     * init clear btn
     */
    private void initClearBtn(final ViewGroup appSettingsVP, final ViewGroup calledFromVP) {
        ImageButton appsettingsClearBtn = (ImageButton) activity.findViewById(R.id.app_settings_clear_btn);
        appsettingsClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                appSettingsVP.setVisibility(View.INVISIBLE);
                calledFromVP.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * move to main activity
     */
    private void startMainActivity() {
        //        if (Tracking.getTracking().isTracking()) {
        //            Toast.makeText(activity, "You need to stop your tracking first!", Toast.LENGTH_LONG).show();
        //        } else {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra("SELECTNEWMAP", true);
        activity.startActivity(intent);
        //        activity.finish();
        //        }
    }

    /**
     * open analytics activity
     */

    private void openAnalyticsActivity() {
        Intent intent = new Intent(activity, Analytics.class);
        activity.startActivity(intent);
        //        activity.finish();
    }

    /**
     * send message to logcat
     *
     * @param str
     */
    private void log(String str) {
        Log.i(this.getClass().getName(), str);
    }

    /**
     * send message to logcat and Toast it on screen
     *
     * @param str: message
     */
    private void logToast(String str) {
        log(str);
        Toast.makeText(activity, str, Toast.LENGTH_SHORT).show();
    }
}
