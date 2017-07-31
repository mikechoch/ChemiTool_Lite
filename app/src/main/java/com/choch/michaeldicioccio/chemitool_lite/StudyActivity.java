package com.choch.michaeldicioccio.chemitool_lite;

/**
 * Created by michaeldicioccio on 6/17/17.
 */

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StudyActivity extends AppCompatActivity {

    private static int NUMBER_OF_ELEMENTS = 118;
    private static int study_type = 1;

    private static ArrayList<ElementInfo> elementInfoOrderArrayList = new ArrayList<>();

    private static Map<Integer, ElementInfo> sortedElementInfoMap;
    private static HashMap<Integer, ElementInfo> elementInfoHashMap = new HashMap<>();

    private Toolbar toolbar;

    private static MainActivity mainActivity = new MainActivity();

    private static CardView stopStudyingCardView;
    private ViewPager viewPager;
    private StudyCardFragmentPagerAdapter mFragmentStudyCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;

    private InterstitialAd interstitialAd;
    private AdRequest interstitialAdRequest;


    //-----------------------------------------------------
    // onOptionsItemSelected
    // grab id of selected menu option and do specific code
    //-----------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showBeforeExitDialog();
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
        setTheme(R.style.AppThemeSearch);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Study Mode");
        stopStudyingCardView = (CardView) findViewById(R.id.stop_studying_button);

        // adds back button to actionbar top left
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        study_type = getIntent().getIntExtra("STUDYTYPE", 1);

        if (savedInstanceState != null) {
            elementInfoHashMap = (HashMap<Integer, ElementInfo>) savedInstanceState.getSerializable("ELEMENTS");
        } else {
            try {
                InputStream input1 = getAssets().open("periodic_table.csv");
                elementInfoHashMap.putAll(mainActivity.readPeriodicCSV(input1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        sortedElementInfoMap  = new TreeMap<>(elementInfoHashMap);
        if (elementInfoOrderArrayList.size() == 0) {
            for (int i = 1; i <= sortedElementInfoMap.size(); i++) {
                elementInfoOrderArrayList.add(sortedElementInfoMap.get(i));
            }
        }

        new Task().execute();

        if (interstitialAd == null) {
            interstitialAd = new InterstitialAd(this);
            interstitialAd.setAdUnitId("ca-app-pub-4962705810463573/6306278045");
            interstitialAdRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            interstitialAd.loadAd(interstitialAdRequest);
        }
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
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.progressBackground);
            ProgressBar progressView = (ProgressBar) findViewById(R.id.progress_view);
            CoordinatorLayout searchContainer = (CoordinatorLayout) findViewById(R.id.study_content);

            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            relativeLayout.setVisibility(View.INVISIBLE);
            progressView.setVisibility(View.INVISIBLE);
            searchContainer.setVisibility(View.VISIBLE);

            super.onPostExecute(result);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            stopStudyingCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBeforeExitDialog();
                }
            });

            Collections.shuffle(elementInfoOrderArrayList);
            setUpStudyViewPager(elementInfoOrderArrayList);
            viewPager.getAdapter().notifyDataSetChanged();

            return true;
        }
    }


    //-----------------------------------------------------
    // onBackPressed
    // controls the back button on top left of the actionbar
    //-----------------------------------------------------
    @Override
    public void onBackPressed() {
        showBeforeExitDialog();
    }


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("ELEMENTS", elementInfoHashMap);
    }


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }


    //-----------------------------------------------------
    // setUpViewPager
    // creates card fragment adapter, shadow adapter, and view page elements for studying
    //-----------------------------------------------------
    public void setUpStudyViewPager(ArrayList<ElementInfo> elementInfoOrder) {
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        mFragmentStudyCardAdapter = new StudyCardFragmentPagerAdapter(getSupportFragmentManager(), dpToPixels(2, this), elementInfoOrder, sortedElementInfoMap, study_type);

        mFragmentCardShadowTransformer = new ShadowTransformer(viewPager, mFragmentStudyCardAdapter);
        mFragmentCardShadowTransformer.enableScaling(true);

        viewPager.setAdapter(mFragmentStudyCardAdapter);
        viewPager.setPageTransformer(false, mFragmentCardShadowTransformer);
        viewPager.setPageMargin((int) dpToPixels(20, this));
        viewPager.setOffscreenPageLimit(5);

    }


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    public void showBeforeExitDialog() {
        LayoutInflater inflater = getLayoutInflater();
        final View titleView1 = inflater.inflate(R.layout.custom_dialog_title_before_exit, null);

        if ((titleView1.getParent()) != null) {
            ((ViewGroup) titleView1.getParent()).removeAllViews();
        }

        final AlertDialog.Builder beforeExitDialog = new AlertDialog.Builder(StudyActivity.this, R.style.AlertDialogCustom)
                .setCustomTitle(titleView1)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showStudyReportDialog();
                        dialog.dismiss();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        beforeExitDialog.show();
    }


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    public void showStudyReportDialog() {
        LayoutInflater inflater = getLayoutInflater();
        final View titleView1 = inflater.inflate(R.layout.custom_dialog_title_study_report, null);

        if ((titleView1.getParent()) != null) {
            ((ViewGroup) titleView1.getParent()).removeAllViews();
        }

        TextView textViewPercentageCorrect = (TextView) titleView1.findViewById(R.id.study_report_percentage_correct);
        ExpandableListView expandableListViewFeedback = (ExpandableListView) titleView1.findViewById(R.id.correct_incorrect_expandable);

        ArrayList<String> expandableStudyCategoryList = new ArrayList<>();
        HashMap<String, List<String>> crimeCategoryTypeHashMap = new HashMap<>();

        List<String> correctAnswers = new ArrayList<>();
        List<String> incorrectAnswers = new ArrayList<>();
        List<String> noAttemptAnswers = new ArrayList<>();

        double percentage_correct;
        int correct = 0;
        int incorrect = 0;
        int no_attempt = 118;
        for (int i = 0; i < NUMBER_OF_ELEMENTS; i++) {
            StudyCardFragment studyCardFragment = (StudyCardFragment) mFragmentStudyCardAdapter.getItem(i);
            ElementStudyReport studyReport = studyCardFragment.getElementStudyReport();
            if (studyReport.getAttempts() > 0 && studyReport.isCorrect()) {
                correctAnswers.add("• " + studyReport.getElementName() + " (" + studyReport.getElementAtomicNumber() + ", " + studyReport.getElementSymbol() + ")");
                correct++;
                no_attempt--;
            } else if (studyReport.getAttempts() > 0 && !studyReport.isCorrect()) {
                incorrectAnswers.add("• " + studyReport.getElementName() + " (" + studyReport.getElementAtomicNumber() + ", " + studyReport.getElementSymbol() + ")");
                incorrect++;
                no_attempt--;
            } else {
                noAttemptAnswers.add("• " + studyReport.getElementName() + " (" + studyReport.getElementAtomicNumber() + ", " + studyReport.getElementSymbol() + ")");
            }
        }

        expandableStudyCategoryList.add("Correct (" + correct + ")");
        expandableStudyCategoryList.add("Incorrect (" + incorrect + ")");
        expandableStudyCategoryList.add("No Attempt (" + no_attempt + ")");

        percentage_correct = (correct*1.0/(NUMBER_OF_ELEMENTS-no_attempt));
        crimeCategoryTypeHashMap.put(expandableStudyCategoryList.get(0), correctAnswers);
        crimeCategoryTypeHashMap.put(expandableStudyCategoryList.get(1), incorrectAnswers);
        crimeCategoryTypeHashMap.put(expandableStudyCategoryList.get(2), noAttemptAnswers);

        startCountAnimation((int) Math.round(percentage_correct * 100.0), textViewPercentageCorrect);

        ExpandableListAdapter studyReportListAdapter = new ExpandableListAdapter(this, expandableStudyCategoryList, crimeCategoryTypeHashMap);
        expandableListViewFeedback.setDividerHeight(0);
        expandableListViewFeedback.setAdapter(studyReportListAdapter);

        final AlertDialog.Builder studyReportDialog = new AlertDialog.Builder(StudyActivity.this, R.style.AlertDialogCustom)
                .setCustomTitle(titleView1)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showAd();
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        dialog.dismiss();
                    }
                });
        studyReportDialog.show();
    }



    //-----------------------------------------------------
    // checkAdCardCount
    // checks if ad card count is at 6 cards
    // ad is shown and new ad is created
    // card count is reset to 0
    //-----------------------------------------------------
    public void showAd() {
        if (interstitialAd != null) {
            //-----------------------------------------------------
            // runOnUiThread()
            // any UI changes need to be done in main thread
            //-----------------------------------------------------
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();

                        interstitialAd = new InterstitialAd(StudyActivity.this);
                        interstitialAd.setAdUnitId("ca-app-pub-4962705810463573/6306278045");
                        interstitialAdRequest = new AdRequest.Builder()
                                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                .build();
                        interstitialAd.loadAd(interstitialAdRequest);
                    }
                }
            });
        }
    }


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    private void startCountAnimation(int percentage, final TextView textView) {
        final float[] from = new float[3],
                      to = new float[3];

        Color.colorToHSV(Color.parseColor("#FF0000"), from);   // from white
        Color.colorToHSV(Color.parseColor(checkStudyReportPercentage(percentage)), to);

        ValueAnimator animator1 = ValueAnimator.ofInt(0, percentage);

        animator1.setDuration(500);

        final float[] hsv  = new float[3];
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                hsv[0] = from[0] + (to[0] - from[0])*animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1])*animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2])*animation.getAnimatedFraction();

                textView.setText(animation.getAnimatedValue().toString() + "%");
                textView.setTextColor(Color.HSVToColor(hsv));
            }
        });
        animator1.start();

    }


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    public String checkStudyReportPercentage(int percentage) {
        if (percentage < 10) {
            return "#FF0000";
        } else if (percentage >= 10 && percentage < 20) {
            return "#FF3300";
        } else if (percentage >= 20 && percentage < 30) {
            return "#FF5E00";
        } else if (percentage >= 30 && percentage < 40) {
            return "#FF8000";
        } else if (percentage >= 40 && percentage < 50) {
            return "#FFB300";
        } else if (percentage >= 50 && percentage < 60) {
            return "#FFF700";
        } else if (percentage >= 60 && percentage < 70) {
            return "#FFFF00";
        } else if (percentage >= 70 && percentage < 80) {
            return "#C8FF00";
        } else if (percentage >= 80 && percentage < 90) {
            return "#8CFF00";
        } else if (percentage >= 90) {
            return "#48FF00";
        }
        return "FF0000";
    }


    //-----------------------------------------------------
    // dpToPixels
    // converts dp units into pixel units
    //-----------------------------------------------------
    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }


    //-----------------------------------------------------
    // pickElementTypeColor
    // element type color selection
    //-----------------------------------------------------
    public int pickElementTypeColor(String element_type) {
        switch (element_type) {
            case "actinoid":
                return R.color.colorActinide;
//                println(String.valueOf(1));
            case "alkali metal":
                return R.color.colorAlkalineMetal;
//                println(String.valueOf(2));
            case "alkaline earth metal":
                return R.color.colorAlkalineEarthMetal;
//                println(String.valueOf(3));
            case "halogen":
                return R.color.colorHalogen;
//                println(String.valueOf(4));
            case "lanthanoid":
                return R.color.colorLanthanide;
//                println(String.valueOf(5));
            case "metal":
                return R.color.colorBasicMetal;
//                println(String.valueOf(6));
            case "metalloid":
                return R.color.colorMetalloid;
//                println(String.valueOf(7));
            case "noble gas":
                return R.color.colorNobleGas;
//                println(String.valueOf(8));
            case "nonmetal":
                return R.color.colorNonmetal;
//                println(String.valueOf(9));
            case "transition metal":
                return R.color.colorTransitionMetal;
//                println(String.valueOf(10));
            default:
                return android.R.color.black;
//                println(String.valueOf(11));
        }
    }
}
