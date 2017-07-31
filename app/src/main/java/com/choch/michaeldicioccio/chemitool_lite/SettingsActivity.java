package com.choch.michaeldicioccio.chemitool_lite;

/**
 * Created by michaeldicioccio on 3/15/17.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private String LINKEDIN_URL = "https://www.linkedin.com/in/michael-dicioccio-b1172b126/";

    //-----------------------------------------------------
    // Globals
    //-----------------------------------------------------
    private static MainActivity mainActivity = new MainActivity();


    //-----------------------------------------------------
    // onOptionsItemSelected
    // grab id of selected menu option and do specific code
    //-----------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    //-----------------------------------------------------
    // onCreate
    // when activity is started do onCreate code
    //-----------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // sets actionbar title
        setTitle("Settings");

        // adds back button to actionbar top left
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 5 relative layouts for the 5 items in the setting activity
        RelativeLayout[] settingRelativeLayouts = new RelativeLayout[5];
        settingRelativeLayouts[0] = (RelativeLayout) findViewById(R.id.no_settings_container);
        settingRelativeLayouts[0].setOnClickListener(this);
        settingRelativeLayouts[1] = (RelativeLayout) findViewById(R.id.recommendation_container);
        settingRelativeLayouts[1].setOnClickListener(this);
        settingRelativeLayouts[2] = (RelativeLayout) findViewById(R.id.bug_container);
        settingRelativeLayouts[2].setOnClickListener(this);
        settingRelativeLayouts[3] = (RelativeLayout) findViewById(R.id.designer_layout);
        settingRelativeLayouts[3].setOnClickListener(this);
//        settingRelativeLayouts[4] = (RelativeLayout) findViewById(R.id.developer_layout);
//        settingRelativeLayouts[4].setOnClickListener(this);
    }


    //-----------------------------------------------------
    // onBackPressed
    // controls the back button on top left of the actionbar
    //-----------------------------------------------------
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    //-----------------------------------------------------
    // onClick
    // controls what happens when one of the clickable RelativeLayouts is pressed
    //-----------------------------------------------------
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no_settings_container:
                mainActivity.toast(SettingsActivity.this, "Settings coming soon...");
                break;

            case R.id.recommendation_container:
                mainActivity.toast(SettingsActivity.this, "Feature requests coming soon...");
                break;

            case R.id.bug_container:
                mainActivity.toast(SettingsActivity.this, "Bug fix requests coming soon...");
                break;

            case R.id.designer_layout:
                Intent linkedin = new Intent(Intent.ACTION_VIEW, Uri.parse(LINKEDIN_URL));
                startActivity(linkedin);
                break;

//            case R.id.developer_layout:
//                mainActivity.toast(SettingsActivity.this, "Website coming soon...");
//                break;

            default:
                break;
        }
    }

}
