package com.choch.michaeldicioccio.chemitool_lite;

/**
 * Created by michaeldicioccio on 3/15/17.
 */

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class PeriodicTableActivity extends AppCompatActivity implements View.OnClickListener {

    //-----------------------------------------------------
    // Globals
    //-----------------------------------------------------
    private static int NUMBER_ROWS = 11;
    private static int NUMBER_COLUMNS = 20;

    private TableLayout periodicTable;

    private static Animation bounceAnimation;
    private static MyBounceInterpolator bounceInterpolator;

    private static MainActivity mainActivity = new MainActivity();

    private static boolean[] periodicTableFilterBools = {true, true, true, true};
    private static boolean[] tempPeriodicTableFilterBools;
    private static CharSequence[] periodicTableFilterOptions = {"Select All", "Atomic Number", "Element Symbol", "Element Name"};

    private static Map<Integer, ElementInfo> sortedElementInfoMap;

    private static HashMap<Integer, Integer> periodicTilesToHideArrayList = new HashMap<Integer, Integer>();
    private static HashMap<Integer, ElementInfo> elementInfoHashMap = new HashMap<Integer, ElementInfo>();


    //-----------------------------------------------------
    // onCreateOptionsMenu
    // inflate a menu xml into actionbar
    //-----------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.periodic_table_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    //-----------------------------------------------------
    // onOptionsItemSelected
    // grab id of selected menu option and do specific code
    //-----------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;

            case R.id.action_settings:
                Intent settings_intent = new Intent(this, SettingsActivity.class);
                startActivity(settings_intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.periodic_table_activity);

        // sets actionbar title
        setTitle("Periodic Table");
        // adds back button to actionbar top left
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        periodicTable = (TableLayout) findViewById(R.id.periodic_table);

        new Task().execute();

        setUpPeriodicTableFAB();
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
    // onClick()
    // handles when a element tile is clicked in the periodic table
    //-----------------------------------------------------
    @Override
    public void onClick(View v){
        Intent main_intent = getIntent();
        main_intent.putExtra("Element", String.valueOf(v.getTag()));
        setResult(Activity.RESULT_OK, main_intent);
        finish();
        overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast);
    }


    //-----------------------------------------------------
    // Class Task
    // AsyncTask with Spinning Loading Circle
    //-----------------------------------------------------
    class Task extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            checkPeriodicTableDisplay();

            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.progressBackground);
            ProgressBar progressView = (ProgressBar) findViewById(R.id.progress_view);
            TableLayout periodicTable = (TableLayout) findViewById(R.id.periodic_table);
            FloatingActionButton periodicTableFilterFAB = (FloatingActionButton) findViewById(R.id.periodic_table_filter_fab);

            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            relativeLayout.setVisibility(View.INVISIBLE);
            progressView.setVisibility(View.INVISIBLE);
            periodicTable.setVisibility(View.VISIBLE);
            periodicTableFilterFAB.setVisibility(View.VISIBLE);
            periodicTable.startLayoutAnimation();

            super.onPostExecute(result);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                // define a AssetManager to getAssets from assets folder
                AssetManager assetManager = getAssets();
                InputStream input1 = assetManager.open("periodic_table.csv");
                elementInfoHashMap.putAll(mainActivity.readPeriodicCSV(input1));
                sortedElementInfoMap = new TreeMap<Integer, ElementInfo>(elementInfoHashMap);

                InputStream input2 = assetManager.open("periodic_tiles_to_show.csv");
                periodicTilesToHideArrayList.putAll(readCSVIntoHashMap(input2));

//            for (Integer key : sortedElementInfoMap.keySet()) {
//                mainActivity.println(String.valueOf(key));
//            }

            } catch (IOException e) {
                e.printStackTrace();
            }

            bounceAnimation = AnimationUtils.loadAnimation(PeriodicTableActivity.this, R.anim.bounce);
            bounceInterpolator = new MyBounceInterpolator(0.025, 35);
            bounceAnimation.setInterpolator(bounceInterpolator);

            // create 4 layoutparams, full tiles, row numbering tiles, column numbering tiles and empty row 8 tiles, and single top left empty tile
            TableRow.LayoutParams fullTileLayoutParams = new TableRow.LayoutParams(216, 216, Gravity.CENTER);
            fullTileLayoutParams.setMargins(2, 2, 2, 2);

            TableRow.LayoutParams rowNumberingLayoutParams = new TableRow.LayoutParams(72, 216, Gravity.CENTER);
            rowNumberingLayoutParams.setMargins(2, 2, 2, 2);

            TableRow.LayoutParams columnNumberingLayoutParams = new TableRow.LayoutParams(216, 108, Gravity.CENTER);
            columnNumberingLayoutParams.setMargins(2, 2, 2, 2);

            TableRow.LayoutParams emptyTileLayoutParams = new TableRow.LayoutParams(72, 108, Gravity.CENTER);
            emptyTileLayoutParams.setMargins(2, 2, 2, 2);

            // total_element_count keeps track of number of cells filled in tablelayout
            int total_element_count = 1;
            // 10 rows in table layout
            for (int i = 1; i < NUMBER_ROWS; i++) {
                // redefine a new elementTableRow each iteration
                final TableRow elementTableRow = new TableRow(PeriodicTableActivity.this);

                // 19 columns in table layout
                for (int j = 1; j < NUMBER_COLUMNS; j++) {

                    // redefine a new elementRelativeLayoutTile each iteration
                    RelativeLayout periodicTableTile = new RelativeLayout(PeriodicTableActivity.this);
                    if (i != 8) {
                        if (j == 1) {
                            if (i < 8) {
                                // row numbering
                                LinearLayout periodicTableTileL = new LinearLayout(PeriodicTableActivity.this);
                                TextView elementRowNumberTextView = new TextView(PeriodicTableActivity.this);
                                LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);

                                elementRowNumberTextView.setText(String.valueOf(i));
                                elementRowNumberTextView.setTextColor(Color.WHITE);
                                elementRowNumberTextView.setTypeface(null, Typeface.BOLD);
                                elementRowNumberTextView.setGravity(Gravity.CENTER);
                                elementRowNumberTextView.setPadding(0, 0, 24, 0);
                                periodicTableTileL.addView(elementRowNumberTextView, textViewLayoutParams);

                                elementTableRow.addView(periodicTableTileL, rowNumberingLayoutParams);

                            } else {
                                // empty row values after 7
                                periodicTableTile.setVisibility(View.INVISIBLE);
                                elementTableRow.addView(periodicTableTile, rowNumberingLayoutParams);

                            }
                        } else {
                            if (periodicTilesToHideArrayList.containsValue(total_element_count)) {
                                // populate periodic table tiles with element symbol and atomic number
                                RelativeLayout.LayoutParams[] textViewLayoutParams = new RelativeLayout.LayoutParams[3];
                                TextView[] tileTextViews = new TextView[3];

                                tileTextViews[0] = new TextView(PeriodicTableActivity.this);
                                tileTextViews[1] = new TextView(PeriodicTableActivity.this);
                                tileTextViews[2] = new TextView(PeriodicTableActivity.this);

                                // set correct coloring for each tile
                                for (Map.Entry<Integer, Integer> number : periodicTilesToHideArrayList.entrySet()) {
                                    if (number.getValue() == total_element_count) {
                                        periodicTableTile.setBackground(getDrawable(pickElementTypeColor(sortedElementInfoMap.get(number.getKey()).getElementType())));

                                        tileTextViews[0].setText(sortedElementInfoMap.get(number.getKey()).getElementAtomicNumber().toString());
                                        tileTextViews[1].setText(sortedElementInfoMap.get(number.getKey()).getElementSymbol());
                                        tileTextViews[2].setText(sortedElementInfoMap.get(number.getKey()).getElementName());
                                    }
                                }
                                tileTextViews[0].setTextSize(12);
                                tileTextViews[0].setTextColor(Color.WHITE);
                                tileTextViews[0].setTypeface(null, Typeface.BOLD);
                                tileTextViews[0].setPadding(0, 0, 14, 0);
                                textViewLayoutParams[0] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                textViewLayoutParams[0].addRule(RelativeLayout.ALIGN_PARENT_TOP | RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

                                tileTextViews[1].setTextSize(27);
                                tileTextViews[1].setTextColor(Color.WHITE);
                                tileTextViews[1].setTypeface(null, Typeface.BOLD);
                                tileTextViews[1].setId(View.generateViewId());
                                textViewLayoutParams[1] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                textViewLayoutParams[1].addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

                                tileTextViews[2].setTextSize(10);
                                tileTextViews[2].setTextColor(Color.WHITE);
                                tileTextViews[2].setTypeface(null, Typeface.BOLD);
                                textViewLayoutParams[2] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                textViewLayoutParams[2].addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                                textViewLayoutParams[2].addRule(RelativeLayout.BELOW, tileTextViews[1].getId());

                                periodicTableTile.setClickable(true);
                                periodicTableTile.addView(tileTextViews[0], textViewLayoutParams[0]);
                                periodicTableTile.addView(tileTextViews[1], textViewLayoutParams[1]);
                                periodicTableTile.addView(tileTextViews[2], textViewLayoutParams[2]);
                                periodicTableTile.setAnimation(bounceAnimation);
                                periodicTableTile.setTag(Integer.parseInt(tileTextViews[0].getText().toString()));
                                periodicTableTile.setOnClickListener(PeriodicTableActivity.this);

                                elementTableRow.addView(periodicTableTile, fullTileLayoutParams);

//                            } else if (total_element_count == 99) {
//                                // show range for synthetic element slots (57 - 71)
//                                RelativeLayout.LayoutParams[] textViewLayoutParams = new RelativeLayout.LayoutParams[2];
//                                TextView[] tileTextViews = new TextView[3];
//
//                                tileTextViews[0] = new TextView(PeriodicTableActivity.this);
//                                tileTextViews[1] = new TextView(PeriodicTableActivity.this);
//                                tileTextViews[2] = new TextView(PeriodicTableActivity.this);
//
//                                tileTextViews[0].setText("57 - 71");
//                                tileTextViews[0].setTextSize(12);
//                                tileTextViews[0].setPadding(0, 0, 14, 0);
//                                tileTextViews[0].setTextColor(Color.WHITE);
//                                tileTextViews[0].setGravity(Gravity.TOP | Gravity.RIGHT);
//                                tileTextViews[0].setTypeface(null, Typeface.BOLD);
//                                textViewLayoutParams[0] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                                textViewLayoutParams[0].addRule(RelativeLayout.ALIGN_PARENT_TOP | RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
//
//                                tileTextViews[1].setText("La-Lu");
//                                tileTextViews[1].setTextSize(27);
//                                tileTextViews[1].setTextColor(Color.WHITE);
//                                tileTextViews[1].setGravity(Gravity.CENTER);
//                                tileTextViews[1].setTypeface(null, Typeface.BOLD);
//                                tileTextViews[1].setLayoutParams(fullTileLayoutParams);
//                                textViewLayoutParams[1] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                                textViewLayoutParams[1].addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//
//                                tileTextViews[2].setGravity(Gravity.CENTER);
//
//                                periodicTableTile.setGravity(Gravity.CENTER);
//                                periodicTableTile.setBackground(getDrawable(R.drawable.lanthanoid_selector));
//                                periodicTableTile.addView(tileTextViews[0], textViewLayoutParams[0]);
//                                periodicTableTile.addView(tileTextViews[1], textViewLayoutParams[1]);
//                                periodicTableTile.addView(tileTextViews[2], fullTileLayoutParams);
//                                periodicTableTile.setLayoutParams(fullTileLayoutParams);
//                                periodicTableTile.setAnimation(bounceAnimation);
//
//                                elementTableRow.addView(periodicTableTile);
//
//                            } else if (total_element_count == 118) {
//                                // show range for synthetic element slots (89 - 103)
//                                RelativeLayout.LayoutParams[] textViewLayoutParams = new RelativeLayout.LayoutParams[2];
//                                TextView[] tileTextViews = new TextView[3];
//
//                                tileTextViews[0] = new TextView(PeriodicTableActivity.this);
//                                tileTextViews[1] = new TextView(PeriodicTableActivity.this);
//                                tileTextViews[2] = new TextView(PeriodicTableActivity.this);
//
//                                tileTextViews[0].setText("89-103");
//                                tileTextViews[0].setTextSize(12);
//                                tileTextViews[0].setPadding(0, 0, 14, 0);
//                                tileTextViews[0].setTextColor(Color.WHITE);
//                                tileTextViews[0].setGravity(Gravity.TOP | Gravity.RIGHT);
//                                tileTextViews[0].setTypeface(null, Typeface.BOLD);
//                                textViewLayoutParams[0] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                                textViewLayoutParams[0].addRule(RelativeLayout.ALIGN_PARENT_TOP | RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
//
//                                tileTextViews[1].setText("Ac-Lr");
//                                tileTextViews[1].setTextSize(27);
//                                tileTextViews[1].setTextColor(Color.WHITE);
//                                tileTextViews[1].setGravity(Gravity.CENTER);
//                                tileTextViews[1].setTypeface(null, Typeface.BOLD);
//                                tileTextViews[1].setLayoutParams(fullTileLayoutParams);
//                                textViewLayoutParams[1] = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                                textViewLayoutParams[1].addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//
//                                tileTextViews[2].setGravity(Gravity.CENTER);
//
//                                periodicTableTile.setGravity(Gravity.CENTER);
//                                periodicTableTile.setBackground(getDrawable(R.drawable.actinoid_selector));
//                                periodicTableTile.addView(tileTextViews[0], textViewLayoutParams[0]);
//                                periodicTableTile.addView(tileTextViews[1], textViewLayoutParams[1]);
//                                periodicTableTile.addView(tileTextViews[2], fullTileLayoutParams);
//                                periodicTableTile.setLayoutParams(fullTileLayoutParams);
//                                periodicTableTile.setAnimation(bounceAnimation);
//
//                                elementTableRow.addView(periodicTableTile);

                            } else {
                                // empty tiles
                                periodicTableTile.setVisibility(View.INVISIBLE);
                                periodicTableTile.setClickable(false);
                                elementTableRow.addView(periodicTableTile, fullTileLayoutParams);

                            }
                        }
                    } else {
                        if (j == 1) {
                            // empty column 1 row 8 tile
                            periodicTableTile.setVisibility(View.INVISIBLE);
                            elementTableRow.addView(periodicTableTile, emptyTileLayoutParams);

                        } else {
                            // empty row 8 tile
                            periodicTableTile.setVisibility(View.INVISIBLE);
                            elementTableRow.addView(periodicTableTile, columnNumberingLayoutParams);

                        }
                    }
                    total_element_count++;
                }
                // turn off multi-finger clicks
                elementTableRow.setMotionEventSplittingEnabled(false);

                //-----------------------------------------------------
                // runOnUiThread()
                // any UI changes need to be done in main thread
                //-----------------------------------------------------
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        periodicTable.addView(elementTableRow);
                    }
                });
            }

            return true;
        }
    }


    //-----------------------------------------------------
    // setUpPeriodicTableFAB
    // creates inflater and onClickListener for Periodic Table Filter FAB
    //-----------------------------------------------------
    public void setUpPeriodicTableFAB() {
        LayoutInflater inflater = this.getLayoutInflater();
        final View titleView = inflater.inflate(R.layout.custom_dialog_title_periodic, null);

        FloatingActionButton periodicTableFilterFAB = (FloatingActionButton) findViewById(R.id.periodic_table_filter_fab);
        periodicTableFilterFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();
                tempPeriodicTableFilterBools = periodicTableFilterBools.clone();
                if (((ViewGroup) titleView.getParent()) != null) {
                    ((ViewGroup) titleView.getParent()).removeAllViews();
                }
                final AlertDialog.Builder periodicTableFilterBuilderDialog = new AlertDialog.Builder(PeriodicTableActivity.this, R.style.AlertDialogCustom);
                periodicTableFilterBuilderDialog
                        .setCustomTitle(titleView)
                        .setMultiChoiceItems(periodicTableFilterOptions, periodicTableFilterBools, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                AlertDialog alertDialog = (AlertDialog) dialog;
                                ListView v = alertDialog.getListView();

                                if (isChecked) {
                                    if (which == 0) {
                                        for (int i = 0; i < periodicTableFilterBools.length; i++) {
                                            v.setItemChecked(i, true);
                                            periodicTableFilterBools[i] = true;
                                        }
                                    } else {
                                        v.setItemChecked(which, true);
                                        periodicTableFilterBools[which] = true;
                                    }
                                } else {
                                    if (which == 0) {
                                        for (int j = 0; j < periodicTableFilterBools.length; j++) {
                                            v.setItemChecked(j, false);
                                            periodicTableFilterBools[j] = false;
                                        }
                                    } else {
                                        v.setItemChecked(which, false);
                                        periodicTableFilterBools[which] = false;
                                    }
                                }

                                int count = 0;
                                for (int k = 1; k < periodicTableFilterBools.length; k++) {
                                    if (periodicTableFilterBools[k] == true) {
                                        count++;
                                    }
                                }

                                if (count < periodicTableFilterBools.length - 1) {
                                    v.setItemChecked(0, false);
                                    periodicTableFilterBools[0] = false;
                                } else if (count == periodicTableFilterBools.length - 1) {
                                    v.setItemChecked(0, true);
                                    periodicTableFilterBools[0] = true;
                                }

                            }
                        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        checkPeriodicTableDisplay();

                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        periodicTableFilterBools = tempPeriodicTableFilterBools.clone();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        periodicTableFilterBools = tempPeriodicTableFilterBools.clone();
                    }
                }).show();
            }
        });
    }


    //-----------------------------------------------------
    // checkPeriodicTableDisplay
    // make sure appropriate information is displayed on the periodic table
    // this is based off the periodic table booleans
    //-----------------------------------------------------
    public void checkPeriodicTableDisplay() {
        for (int i = 1; i < periodicTable.getChildCount(); i++) {
            if (i != 8) {
                TableRow periodicTableRow = (TableRow) periodicTable.getChildAt(i);
                for (int j = 1; j < periodicTableRow.getChildCount(); j++) {
                    RelativeLayout periodicTableTile = (RelativeLayout) periodicTableRow.getChildAt(j);

                    if (periodicTableTile.isClickable()) {
                        TextView periodicAtomicNumberTextView = (TextView) periodicTableTile.getChildAt(0);
                        TextView periodicSymbolTextView = (TextView) periodicTableTile.getChildAt(1);
                        TextView periodicNameTextView = (TextView) periodicTableTile.getChildAt(2);

                        if (periodicTableFilterBools[0] == true) {
                            periodicAtomicNumberTextView.setVisibility(View.VISIBLE);
                            periodicSymbolTextView.setVisibility(View.VISIBLE);
                            periodicNameTextView.setVisibility(View.VISIBLE);

                        } else {
                            if (periodicTableFilterBools[1] == true) {
                                periodicAtomicNumberTextView.setVisibility(View.VISIBLE);
                            } else {
                                periodicAtomicNumberTextView.setVisibility(View.INVISIBLE);
                            }

                            if (periodicTableFilterBools[2] == true) {
                                periodicSymbolTextView.setVisibility(View.VISIBLE);
                            } else {
                                periodicSymbolTextView.setVisibility(View.INVISIBLE);
                            }

                            if (periodicTableFilterBools[3] == true) {
                                periodicNameTextView.setVisibility(View.VISIBLE);
                            } else {
                                periodicNameTextView.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }
            }
        }
    }


    //-----------------------------------------------------
    // pickElementTypeColor
    // element type color selection
    //-----------------------------------------------------
    public int pickElementTypeColor(String element_type) {
        switch (element_type) {
            case "actinoid":
                return R.drawable.actinoid_selector;
//                mainActivity.println(String.valueOf(1));
            case "alkali metal":
                return R.drawable.alkali_metal_selector;
//                mainActivity.println(String.valueOf(2));
            case "alkaline earth metal":
                return R.drawable.alkaline_earth_metal_selector;
//                mainActivity.println(String.valueOf(3));
            case "halogen":
                return R.drawable.halogen_selector;
//                mainActivity.println(String.valueOf(4));
            case "lanthanoid":
                return R.drawable.lanthanoid_selector;
//                mainActivity.println(String.valueOf(5));
            case "metal":
                return R.drawable.metal_selector;
//                mainActivity.println(String.valueOf(6));
            case "metalloid":
                return R.drawable.metalloid_selector;
//                mainActivity.println(String.valueOf(7));
            case "noble gas":
                return R.drawable.noble_gas_selector;
//                mainActivity.println(String.valueOf(8));
            case "nonmetal":
                return R.drawable.nonmetal_selector;
//                mainActivity.println(String.valueOf(9));
            case "transition metal":
                return R.drawable.transition_metal_selector;
//                mainActivity.println(String.valueOf(10));
            default:
                return R.drawable.element_tile_selector;
//                mainActivity.println(String.valueOf(11));
        }
    }


    public static HashMap<Integer, Integer> readCSVIntoHashMap(InputStream input) throws FileNotFoundException, IOException {
        HashMap<Integer, Integer> tempPeriodicLocationMap = new HashMap<Integer, Integer>();
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {

            br = new BufferedReader(new InputStreamReader(input));
            while ((line = br.readLine()) != null) {
                String[] periodicTileVals = line.split(cvsSplitBy);

                tempPeriodicLocationMap.put(Integer.parseInt(periodicTileVals[0]), Integer.parseInt(periodicTileVals[1]));

            }

        } finally {
            if (br != null) {
                br.close();
            }
        }
        return tempPeriodicLocationMap;

    }

}
