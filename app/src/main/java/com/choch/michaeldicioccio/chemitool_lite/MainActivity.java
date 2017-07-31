package com.choch.michaeldicioccio.chemitool_lite;

/**
 * Created by michaeldicioccio on 3/15/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Looper;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends AppCompatActivity {

    //-----------------------------------------------------
    // Globals
    //-----------------------------------------------------
    private static int NUMBER_OF_ELEMENTS = 118;
    private static String SHARE_MSG = "Hey, check out ChemiTool. This android app can help you study all of the elments on the periodic table! " +
            "\n\nhttps://play.google.com/store/apps/details?id=com.choch.michaeldicioccio.chemitool_lite";
    private static String RATE_URL = "market://details?id=com.choch.michaeldicioccio.chemitool_lite";
    private static String CHEMITOOL_PRO_URL = "https://play.google.com/store/apps/details?id=com.choch.michaeldicioccio.chemitool_pro&hl=en";

    private Animation bounceAnimation;
    private Interpolator bounceInterpolator;

    private static int cardOrderSelected = 1;
    private static int study_type = 1;
    private static CharSequence[] studyTypes = {"Symbol", "Atomic Number", "Both"};

    private static ArrayList<ElementInfo> elementInfoOrderArrayList = new ArrayList<>();

    private static Map<Integer, ElementInfo> sortedElementInfoMap;
    private static HashMap<Integer, ElementInfo> elementInfoHashMap = new HashMap<Integer, ElementInfo>();

    private static CardView firstElementCardView;
    private static CardView orderingCardView;
    private static CardView lastElementCardView;
    private static CardView startStudyingCardView;

    private ViewPager viewPager;
    private CardFragmentPagerAdapter mFragmentCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;

    private static Timer timer;
    private static int adCardCount;
    private InterstitialAd interstitialAd;
    private AdRequest interstitialAdRequest;

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    //-----------------------------------------------------
    // onCreateOptionsMenu
    // inflate a menu xml into actionbar
    //-----------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        int color = ContextCompat.getColor(this, R.color.colorAccent);
        updateMenuWithIcon(menu.findItem(R.id.action_settings), color);
        updateMenuWithIcon(menu.findItem(R.id.action_rate_app), color);
        updateMenuWithIcon(menu.findItem(R.id.action_share_app), color);
        updateMenuWithIcon(menu.findItem(R.id.action_upgrade_pro), color);

        return true;
    }


    //-----------------------------------------------------
    // updateMenuWithIcon
    // adds icons to menu options in overflow dropdown
    //-----------------------------------------------------
    private static void updateMenuWithIcon(@NonNull final MenuItem item, final int color) {
        SpannableStringBuilder builder = new SpannableStringBuilder()
                .append("*") // the * will be replaced with the icon via ImageSpan
                .append("   ") // This extra space acts as padding. Adjust as you wish
                .append(item.getTitle());

        // Retrieve the icon that was declared in XML and assigned during inflation
        if (item.getIcon() != null && item.getIcon().getConstantState() != null) {
            final Drawable drawable = item.getIcon().getConstantState().newDrawable();

            // Mutate this drawable so the tint only applies here
            drawable.mutate().setTint(color);

            // Needs bounds, or else it won't show up (doesn't know how big to be)
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(drawable);
            builder.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            item.setTitle(builder);
        }
    }


    //-----------------------------------------------------
    // onOptionsItemSelected
    // grab id of selected menu option and do specific code
    //-----------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (timer != null) {
            timer.cancel();
            timer.purge();
        }

        switch (item.getItemId()) {
            case R.id.action_search:
                Intent search_intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(search_intent, 1);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;

            case R.id.action_periodic_table:
                Intent periodic_table_intent = new Intent(this, PeriodicTableActivity.class);
                startActivityForResult(periodic_table_intent, 1);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;

            case R.id.action_settings:
                Intent settings_intent = new Intent(this, SettingsActivity.class);
                startActivity(settings_intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;

            case R.id.action_share_app:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "ChemiTool");
                i.putExtra(Intent.EXTRA_TEXT, SHARE_MSG);
                startActivity(Intent.createChooser(i, "Share via"));
                break;

            case R.id.action_rate_app:
                Intent rate = new Intent(Intent.ACTION_VIEW, Uri.parse(RATE_URL));
                startActivity(rate);
                break;

            case R.id.action_upgrade_pro:
                Intent buy = new Intent(Intent.ACTION_VIEW, Uri.parse(CHEMITOOL_PRO_URL));
                startActivity(buy);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    //-----------------------------------------------------
    // onCreate
    // when activity is started do onCreate code
    //-----------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.splashScreenTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTheme(R.style.AppTheme);

        // define a toolbar from the View
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // sets actionbar title
        setTitle("Element");

//        firstElementCardView = (CardView) findViewById(R.id.first_element_button);
//        orderingCardView = (CardView) findViewById(R.id.order_element_button);
//        lastElementCardView = (CardView) findViewById(R.id.last_element_button);
        startStudyingCardView = (CardView) findViewById(R.id.start_studying_button);

        if (savedInstanceState != null) {
            elementInfoHashMap = (HashMap<Integer, ElementInfo>) savedInstanceState.getSerializable("ELEMENTS");
        } else {
            try {
                InputStream input1 = getAssets().open("periodic_table.csv");
                elementInfoHashMap.putAll(readPeriodicCSV(input1));
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

        bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.card_bounce);
        bounceInterpolator = new MyBounceInterpolator(0.025, 35);
        bounceAnimation.setInterpolator(bounceInterpolator);

        checkCardOrdering();

        //-----------------------------------------------------
        // setOnClickListener
        // go to first card
        //-----------------------------------------------------
//        firstElementCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int prevCard = viewPager.getCurrentItem();
//                viewPager.setCurrentItem(0, false);
//                CardView currentCard = mFragmentCardAdapter.getCardViewAt(0);
//                currentCard.startAnimation(bounceAnimation);
//                if (prevCard != 0) {
//                    adCardCount++;
//                }
//            }
//        });

        //-----------------------------------------------------
        // setOnClickListener
        // create alert dialog menu to choose what card order you want
        //-----------------------------------------------------
//        final LayoutInflater inflater = this.getLayoutInflater();
//        orderingCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                final View titleView = inflater.inflate(R.layout.custom_dialog_title_study_type, null);
//                if (((ViewGroup) titleView.getParent()) != null) {
//                    ((ViewGroup) titleView.getParent()).removeAllViews();
//                }
//
//                final AlertDialog.Builder cardOrderDialog = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom)
//                        .setCustomTitle(titleView)
//                        .setSingleChoiceItems(studyTypes, cardOrderSelected, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                cardOrderSelected = which;
//                                checkCardOrdering();
//                                dialog.dismiss();
//                            }
//                        });
//                cardOrderDialog.show();
//            }
//        });

        //-----------------------------------------------------
        // setOnClickListener
        // go to last card
        //-----------------------------------------------------
//        lastElementCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int prevCard = viewPager.getCurrentItem();
//                viewPager.setCurrentItem(sortedElementInfoMap.size()-1, false);
//                CardView currentCard = mFragmentCardAdapter.getCardViewAt(sortedElementInfoMap.size()-1);
//                currentCard.startAnimation(bounceAnimation);
//                if (prevCard != 117) {
//                    adCardCount++;
//                }
//            }
//        });

        //-----------------------------------------------------
        // setOnClickListener
        // re-orders cards randomly
        //-----------------------------------------------------
//        orderingCardView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (cardOrderSelected == 2) {
//                    // shuffle cards
//                    Collections.shuffle(elementInfoOrderArrayList);
//                    setUpViewPager(elementInfoOrderArrayList);
//                    viewPager.getAdapter().notifyDataSetChanged();
//                    adCardCount++;
//                } else {
//                    toast(MainActivity.this, "Generates new random set of cards");
//                }
//
//                return true;
//            }
//        });

        startStudyingCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                final View titleView1 = inflater.inflate(R.layout.custom_dialog_title_study_type, null);

                if (((ViewGroup) titleView1.getParent()) != null) {
                    ((ViewGroup) titleView1.getParent()).removeAllViews();
                }

                final AlertDialog.Builder studyTypeDialog = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom)
                        .setCustomTitle(titleView1)
                        .setSingleChoiceItems(studyTypes, study_type, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                study_type = which;
                            }
                        }).setPositiveButton("START", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent study_intent = new Intent(MainActivity.this, StudyActivity.class);
                                study_intent.putExtra("STUDYTYPE", study_type);
                                startActivity(study_intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                dialog.dismiss();
                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                studyTypeDialog.show();
            }
        });


        if (interstitialAd == null) {
            interstitialAd = new InterstitialAd(this);
            interstitialAd.setAdUnitId("ca-app-pub-4962705810463573/6306278045");
            interstitialAdRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            interstitialAd.loadAd(interstitialAdRequest);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String element = data.getStringExtra("Element");
                for (int i = 0; i < mFragmentCardAdapter.getDataSet().size(); i++) {
                    if (Integer.parseInt(element) == elementInfoOrderArrayList.get(i).getElementAtomicNumber()) {
                        viewPager.setCurrentItem(i);
                        break;
                    }
                }
            }

            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
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
    // checkCardOrdering
    // checks what value cardOrderSelected is set to
    // then orders cards and creates the view pager and adapter for the view
    //-----------------------------------------------------
    public void checkCardOrdering() {
//        element = getIntent().getStringExtra("Element");
//        getIntent().removeExtra("Element");

        switch (cardOrderSelected) {
            case 0:
                // alphabetize cards
                alphabeticSort(elementInfoOrderArrayList);
                setUpViewPager(elementInfoOrderArrayList);
                viewPager.getAdapter().notifyDataSetChanged();
                break;
            case 1:
                // number cards
                numericSort(elementInfoOrderArrayList);
                setUpViewPager(elementInfoOrderArrayList);
                viewPager.getAdapter().notifyDataSetChanged();
                break;
            case 2:
                // shuffle cards
                Collections.shuffle(elementInfoOrderArrayList);
                setUpViewPager(elementInfoOrderArrayList);
                viewPager.getAdapter().notifyDataSetChanged();
                break;
        }

        adCardCount++;
        checkAdCardCount();
    }


    //-----------------------------------------------------
    // setUpViewPager
    // creates card fragment adapter, shadow adapter, and view page elements for normal view
    //-----------------------------------------------------
    public void setUpViewPager(ArrayList<ElementInfo> elementInfoOrder) {
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        mFragmentCardAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(), dpToPixels(2, this), elementInfoOrder, sortedElementInfoMap);

        mFragmentCardShadowTransformer = new ShadowTransformer(viewPager, mFragmentCardAdapter);
        mFragmentCardShadowTransformer.enableScaling(true);

        viewPager.setAdapter(mFragmentCardAdapter);
        viewPager.setPageTransformer(false, mFragmentCardShadowTransformer);
        viewPager.setPageMargin((int) dpToPixels(20, this));
        viewPager.setOffscreenPageLimit(5);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                checkAdCardCount();
                startCardCountTimer();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        checkAdCardCount();
        startCardCountTimer();
    }


    //-----------------------------------------------------
    // alphabeticSort
    // sorts element array list alphabetically
    //-----------------------------------------------------
    public static void alphabeticSort(ArrayList<ElementInfo> elementInfos) {
        Collections.sort(elementInfos, new Comparator<ElementInfo>(){
            public int compare(ElementInfo obj1, ElementInfo obj2) {
                // ## Ascending order
                 return obj1.elementName.compareToIgnoreCase(obj2.elementName); // To compare string values
                // return obj1.atomicNumber.compareTo(obj2.atomicNumber); // To compare integer values

                // ## Descending order
                // return obj2.elementName.compareToIgnoreCase(obj1.elementName); // To compare string values
                // return Integer.valueOf(obj2.atomicNumber).compareTo(obj1.atomicNumber); // To compare integer values
            }
        });
    }


    //-----------------------------------------------------
    // numericSort
    // sorts element array list numerically
    //-----------------------------------------------------
    public static void numericSort(ArrayList<ElementInfo> elementInfos) {
        Collections.sort(elementInfos, new Comparator<ElementInfo>(){
            public int compare(ElementInfo obj1, ElementInfo obj2) {
                // ## Ascending order
                // return obj1.elementName.compareToIgnoreCase(obj2.elementName); // To compare string values
                 return obj1.elementAtomicNumber.compareTo(obj2.elementAtomicNumber); // To compare integer values

                // ## Descending order
                // return obj2.elementName.compareToIgnoreCase(obj1.elementName); // To compare string values
                // return Integer.valueOf(obj2.atomicNumber).compareTo(obj1.atomicNumber); // To compare integer values
            }
        });
    }


    //-----------------------------------------------------
    // startCardCountTimer
    // checks for a timer started and removes it
    // starts a new timer, if view is on card for 2 seconds count card towards ad count
    //-----------------------------------------------------
    public void startCardCountTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }

        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Looper.prepare();
                adCardCount++;
                Looper.loop();
            }
        };
        timer.schedule(timerTask, 1250);
    }


    //-----------------------------------------------------
    // checkAdCardCount
    // checks if ad card count is at 6 cards
    // ad is shown and new ad is created
    // card count is reset to 0
    //-----------------------------------------------------
    public void checkAdCardCount() {
        if (interstitialAd != null) {
            if (adCardCount >= 10) {
                //-----------------------------------------------------
                // runOnUiThread()
                // any UI changes need to be done in main thread
                //-----------------------------------------------------
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (interstitialAd.isLoaded()) {
                            interstitialAd.show();

                            interstitialAd = new InterstitialAd(MainActivity.this);
                            interstitialAd.setAdUnitId("ca-app-pub-4962705810463573/6306278045");
                            interstitialAdRequest = new AdRequest.Builder()
                                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                    .build();
                            interstitialAd.loadAd(interstitialAdRequest);

                            adCardCount = 0;
                        }
                    }
                });

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


    //-----------------------------------------------------
    // dpToPixels
    // converts dp units into pixel units
    //-----------------------------------------------------
    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }


    //---------------------------------------------------------------
    //
    //
    //---------------------------------------------------------------
    public static HashMap<Integer, ElementInfo> readPeriodicCSV(InputStream input) throws IOException {
        HashMap<Integer, ElementInfo> tempElementInfoMap = new HashMap<>();
        String line;
        String cvsSplitBy = ",";

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
        while ((line = bufferedReader.readLine()) != null) {
            // Use comma as separator
            String[] periodic = line.split(cvsSplitBy);
            // Add elements and other attributes to ElementInfo Object
            ElementInfo elementInfo = new ElementInfo();

            elementInfo.setElementAtomicNumber(Integer.parseInt(periodic[0].replaceAll(" ", "")));
            elementInfo.setElementSymbol(periodic[1].replaceAll(" ", ""));
            elementInfo.setElementName(periodic[2].replaceAll(" ", ""));

            elementInfo.setElementAtomicMass(periodic[3].replaceAll(" ", ""));

            String[] element_config = periodic[4].split(" ");
            StringBuilder element_config_sup = new StringBuilder("");
            for (int i = 0; i < element_config.length; i++) {
                StringBuilder element_config_sb = new StringBuilder(element_config[i]);
                if (element_config_sb.length() > 0 && element_config_sb.charAt(0) != '[') {
                    element_config[i] = "";
                    String exponent;
                    if (!Character.isDigit(element_config_sb.charAt(element_config_sb.length() - 3))) {
                        exponent = "<sup><small>" + (element_config_sb.substring((element_config_sb.length() - 2), (element_config_sb.length()))) + "</small></sup>";
                        element_config_sb.delete((element_config_sb.length() - 2), (element_config_sb.length()));
                        element_config_sb.append(exponent);
                    } else if (!Character.isDigit(element_config_sb.charAt(element_config_sb.length() - 2))) {
                        exponent = "<sup><small>" + element_config_sb.charAt(element_config_sb.length() - 1) + "</small></sup>";
                        element_config_sb.deleteCharAt(element_config_sb.length() - 1);
                        element_config_sb.append(exponent);
                    }
                }
                element_config_sup.append(" " + element_config_sb);
            }
            element_config_sup.deleteCharAt(0);

            String tempElementConfig = element_config_sup.toString().replaceAll(" ", "");

            elementInfo.setElementConfig(tempElementConfig.replace("]", "] "));

            elementInfo.setElementType(periodic[5]);

            elementInfo.setElementPhase(periodic[6].replaceAll(" ", ""));
            elementInfo.setElementRadioactive(Boolean.valueOf(periodic[7].replaceAll(" ", "")));
            elementInfo.setElementGroup(periodic[8].replaceAll(" ", ""));
            elementInfo.setElementPeriod(periodic[9].replaceAll(" ", ""));
            elementInfo.setElementDiscoveryYear(periodic[10].trim());

            if (periodic[11].contains("|")) {
                elementInfo.setElementElectronShell(periodic[11].replace("|", ",").split(","));
            } else {
                elementInfo.setElementElectronShell(new String[]{periodic[11]});
            }
            elementInfo.setElementDensity(periodic[12].replace(" ", ""));
            elementInfo.setElementElectronegativity(periodic[13].replace(" ", ""));
            elementInfo.setElementThermalConductivity(periodic[14].replace(" ", ""));

            String[] tempMeltingPoint = periodic[15].replace("|", ","). split(",");
            elementInfo.setElementMeltingPointK(tempMeltingPoint[0]);
            elementInfo.setElementMeltingPointC(tempMeltingPoint[1]);
            elementInfo.setElementMeltingPointF(tempMeltingPoint[2]);

            String[] tempBoilingPoint = periodic[16].replace("|", ","). split(",");
            elementInfo.setElementBoilingPointK(tempBoilingPoint[0]);
            elementInfo.setElementBoilingPointC(tempBoilingPoint[1].replaceAll("'", ","));
            elementInfo.setElementBoilingPointF(tempBoilingPoint[2].replaceAll("'", ","));

            elementInfo.setElementAtomicRadius(periodic[17]);
            elementInfo.setElementCovRadius(periodic[18]);
            elementInfo.setElementVanRadius(periodic[19]);

            elementInfo.setElementCrystalStructure(periodic[20]);
            elementInfo.setElementCrystalStructure2(periodic[21]);

            elementInfo.setElementYoungsModulus(periodic[22]);
            elementInfo.setElementShearModulus(periodic[23]);
            elementInfo.setElementBulkModulus(periodic[24]);

            elementInfo.setElementMohsHardness(periodic[25]);
            elementInfo.setElementVickersHardness(periodic[26]);
            elementInfo.setElementBrinellHardness(periodic[27]);

            elementInfo.setElementImage(periodic[28].replace(" ", ""));
            elementInfo.setElementImageFull(periodic[29].replace(" ", ""));

            if (periodic[30].equals("none")) {
                elementInfo.setElementDescription("");
            } else {
                if (periodic[30].contains("|")) {
                    elementInfo.setElementDescription(periodic[30].replace("|", ","));
                } else {
                    elementInfo.setElementDescription(periodic[30]);
                }

            }

            tempElementInfoMap.put(elementInfo.getElementAtomicNumber(), elementInfo);
        }
        bufferedReader.close();

        return tempElementInfoMap;
    }


    //-----------------------------------------------------
    // println
    // bypass using "System.out.println"
    //-----------------------------------------------------
    public static void println(String stringToPrint) {
        System.out.println(stringToPrint);
    }


    //-----------------------------------------------------
    // toast
    // bypass using "Toast.makeText(context, string, duration).show()"
    //-----------------------------------------------------
    public static void toast(Context context, String stringToToast) {
        Toast.makeText(context, stringToToast, 500).show();
    }

}
