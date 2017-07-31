package com.choch.michaeldicioccio.chemitool_lite;

/**
 * Created by michaeldicioccio on 3/15/17.
 */

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.BadParcelableException;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    //-----------------------------------------------------
    // Globals
    //-----------------------------------------------------
    private static int backPressedCount = 0;
    private static int FOCUS_CHANGES = 9;
    private static String currentSearchQuery;

    private boolean[] indicatorVisible = {true, true, true, true, true, true, true, true, true, true, true};
    private int[] elementTypeButtonIDs = {R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9, R.id.button10, R.id.button11};
    private int[] buttonIdicatorIDs = {R.id.indicator1, R.id.indicator2, R.id.indicator3, R.id.indicator4, R.id.indicator5, R.id.indicator6, R.id.indicator7, R.id.indicator8, R.id.indicator9, R.id.indicator10, R.id.indicator11};

    private static MainActivity mainActivity = new MainActivity();

    private CustomRecycleViewAdapter customRecycleViewAdapter;
    private ImageView atomImage;
    private RelativeLayout[] buttonIndicators = new RelativeLayout[11];
    private RelativeLayout relativeLayoutHeaderCount;
    private RecyclerView rv;
    private TextView atomText;
    private static TextView resultsCountTextView;
    private Toolbar toolbar;

    private ArrayList<ElementInfoPost> elementInfoPostsArrayList = new ArrayList<ElementInfoPost>();
    private ArrayList<ElementInfoPost> fullElementInfoPostsArrayList = new ArrayList<ElementInfoPost>();

    private static Map<Integer, ElementInfo> sortedElementInfoMap;
    private static HashMap<Integer, ElementInfo> elementInfoHashMap = new HashMap<Integer, ElementInfo>();


    //-----------------------------------------------------
    // onCreateOptionsMenu
    // inflate a menu xml into actionbar
    //-----------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Search element...");
        searchView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS|InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        searchView.onActionViewExpanded();

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        final AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchTextView.setFocusable(true);
        searchTextView.setFocusableInTouchMode(true);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.cursor);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //-----------------------------------------------------
        // setOnTouchListener
        // listener for searchTextView touch
        //-----------------------------------------------------
        searchTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FOCUS_CHANGES = 7;
                backPressedCount = 0;
                return false;
            }
        });

        //-----------------------------------------------------
        // setOnClickListener
        // listener for X button to clear searchTextView text
        // since it is override, keyboard won't come up if the X is clicked while the keyboard is down
        //-----------------------------------------------------
        ImageView clearSearchViewButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        clearSearchViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTextView.setText("");
            }
        });

        //-----------------------------------------------------
        // setOnQueryTextFocusChangeListener
        // listener for focus change in activity
        //-----------------------------------------------------
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // keeps cursor blinking when searchView doesn't have focus
                // *** WARNING: this causes built-in back button to not be able to gain focus and finish activity ***
                searchTextView.requestFocus();
                searchTextView.setCursorVisible(true);
//                mainActivity.println(String.valueOf(backPressedCount));
                backPressedCount++;

                if (backPressedCount > FOCUS_CHANGES) {
                    backPressedCount = 0;
                    onBackPressed();
                }
            }
        });

        //-----------------------------------------------------
        // setOnQueryTextListener
        // listener for query text change
        //-----------------------------------------------------
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                currentSearchQuery = searchQuery.toString().trim();
                customRecycleViewAdapter.filterForElementsInSearchView(currentSearchQuery);
                rv.invalidate();
                return true;
            }
        });

        return true;
    }


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
        setTheme(R.style.AppThemeSearch);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // adds back button to actionbar top left
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FOCUS_CHANGES = 9;
        backPressedCount = 0;

        new Task().execute();

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
            CoordinatorLayout searchContainer = (CoordinatorLayout) findViewById(R.id.search_container);

            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            relativeLayout.setVisibility(View.INVISIBLE);
            progressView.setVisibility(View.INVISIBLE);
            searchContainer.setVisibility(View.VISIBLE);

            super.onPostExecute(result);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // adds layout transitions to any changing layout components
            CoordinatorLayout searchContainer = (CoordinatorLayout) findViewById(R.id.search_container);
            LayoutTransition layoutTransition = new LayoutTransition();
            layoutTransition.setDuration(50);
            searchContainer.setLayoutTransition(layoutTransition);


            // reads in periodic table csv file with all elementSymbol info
            AssetManager assetManager = getAssets();
            try {
                // all files in asset folder
                String[] files = assetManager.list("");
                InputStream input1 = assetManager.open("periodic_table.csv");
                elementInfoHashMap = mainActivity.readPeriodicCSV(input1);
                sortedElementInfoMap = new TreeMap<Integer, ElementInfo>(elementInfoHashMap);

            } catch (IOException e) {
                e.printStackTrace();
            }

            // declare 11 cardview array with onClickListeners for category button bar
            CardView[] elementTypeButtons = new CardView[11];
            for (int i = 0; i < elementTypeButtons.length; i++) {
                elementTypeButtons[i] = (CardView) findViewById(elementTypeButtonIDs[i]);
                elementTypeButtons[i].setTag(i);
                elementTypeButtons[i].setOnClickListener(SearchActivity.this);

                buttonIndicators[i] = (RelativeLayout) findViewById(buttonIdicatorIDs[i]);
            }

            // declare some of UI elements
            currentSearchQuery = "";
            resultsCountTextView = (TextView) findViewById(R.id.number_of_results);
            resultsCountTextView.setText(String.valueOf(sortedElementInfoMap.size()));
            atomImage = (ImageView) findViewById(R.id.atom_image);
            atomText = (TextView) findViewById(R.id.atom_text);
            relativeLayoutHeaderCount = (RelativeLayout) findViewById(R.id.top_control_bar);

            for (Integer key : sortedElementInfoMap.keySet()) {
                elementInfoPostsArrayList.add(new ElementInfoPost(sortedElementInfoMap.get(key).getElementAtomicNumber(),
                        sortedElementInfoMap.get(key).getElementSymbol(),
                        sortedElementInfoMap.get(key).getElementName(),
                        sortedElementInfoMap.get(key).getElementAtomicMass(),
                        sortedElementInfoMap.get(key).getElementConfig(),
                        sortedElementInfoMap.get(key).getElementType(),
                        sortedElementInfoMap.get(key).getElementImage(),
                        sortedElementInfoMap.get(key).getElementDescription()));
            }

            rv = (RecyclerView) findViewById(R.id.searchViewListView);

            //-----------------------------------------------------
            // runOnUiThread()
            // any UI changes need to be done in main thread
            //-----------------------------------------------------
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rv.setHasFixedSize(false);
                    rv.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                    customRecycleViewAdapter = new CustomRecycleViewAdapter(SearchActivity.this, elementInfoPostsArrayList);
                    rv.setAdapter(customRecycleViewAdapter);
                }
            });

            //-----------------------------------------------------
            // setOnScrollListener
            // when listview is scrolled, hide keyboard
            //-----------------------------------------------------
            rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == 1 && recyclerView != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
                        backPressedCount += 4;
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                }
            });

            return true;
        }
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
    // when a category button is pressed its visibility and boolean connector adjust
    //-----------------------------------------------------
    @Override
    public void onClick(View v) {
        int button_clicked = (int) v.getTag();
        if (buttonIndicators[button_clicked].isShown()) {
            if (button_clicked == 0) {
                for (int i = 0; i < buttonIndicators.length; i++) {
                    buttonIndicators[i].setVisibility(View.INVISIBLE);
                    indicatorVisible[i] = false;
                }
            } else {
                buttonIndicators[button_clicked].setVisibility(View.INVISIBLE);
                indicatorVisible[button_clicked] = false;
            }
        } else {
            if (button_clicked == 0) {
                for (int j = 0; j < buttonIndicators.length; j++) {
                    buttonIndicators[j].setVisibility(View.VISIBLE);
                    indicatorVisible[j] = true;
                }
            } else {
                buttonIndicators[button_clicked].setVisibility(View.VISIBLE);
                indicatorVisible[button_clicked] = true;
            }
        }

        int count = 0;
        for (int k = 1; k < buttonIndicators.length; k++) {
            if (buttonIndicators[k].isShown()) {
                indicatorVisible[k] = true;
                count++;
            } else {
                indicatorVisible[k] = false;
            }
        }

        if (count < buttonIndicators.length - 1) {
            buttonIndicators[0].setVisibility(View.INVISIBLE);
        } else if (count == buttonIndicators.length - 1) {
            buttonIndicators[0].setVisibility(View.VISIBLE);
        }

        customRecycleViewAdapter.filterForElementType();
    }


    //-----------------------------------------------------
    // CustomRecycleViewAdapter
    // adapter for recycleview to add and remove elements when searching
    //-----------------------------------------------------
    public class CustomRecycleViewAdapter extends RecyclerView.Adapter {

        public LayoutInflater inflater;
        public Context context;
        public List<ElementInfoPost> elementListViewList;
        public ArrayList<ElementInfoPost> filteredElementListViewList;

        public CustomRecycleViewAdapter(Context context, List<ElementInfoPost> elementInfoPosts) {
            super();

            this.elementListViewList = elementInfoPosts;
            this.context = context;
            filteredElementListViewList = new ArrayList<ElementInfoPost>();
            filteredElementListViewList.addAll(elementInfoPosts);
            fullElementInfoPostsArrayList.addAll(elementInfoPosts);

            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView atomicNumberTextView, elementNameTextView, elementSymbolTextView, elementMassTextView;
            public ImageView elementImageView, elementTypeImageView;

            public ViewHolder(View v) {
                super(v);
                atomicNumberTextView = (TextView) v.findViewById(R.id.atomic_number);
                elementNameTextView = (TextView) v.findViewById(R.id.title);
                elementSymbolTextView = (TextView) v.findViewById(R.id.subtitle);
                elementMassTextView = (TextView) v.findViewById(R.id.element_config);
                elementTypeImageView = (ImageView) v.findViewById(R.id.element_type);
                elementImageView = (ImageView) v.findViewById(R.id.element_image);

            }
        }

        public void add(int position) {
//            arr_userid.add(position, item);
//            notifyItemInserted(position);
        }

        public void remove(int position) {
//            int position = arr_userid.indexOf(item);
//            arr_userid.remove(position);
//            notifyItemRemoved(position);
        }

        public CustomRecycleViewAdapter(ArrayList<String> myDataset) {
//            arr_userid = myDataset;
        }

        @Override
        public CustomRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_search_listview, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backPressedCount = 0;

                    Intent main_intent = getIntent();
                    main_intent.putExtra("Element", elementListViewList.get(position).getPostNumber().toString());
                    setResult(Activity.RESULT_OK, main_intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast);
                }
            });

            viewHolder.atomicNumberTextView.setText(elementListViewList.get(position).getPostNumber().toString());
            viewHolder.elementNameTextView.setText(elementListViewList.get(position).getPostTitle());
            viewHolder.elementSymbolTextView.setText(elementListViewList.get(position).getPostSubTitle());
            viewHolder.elementMassTextView.setText(Html.fromHtml("Weight: " + elementListViewList.get(position).getPostMass() + " u"));
            viewHolder.elementTypeImageView.setBackgroundColor(getResources().getColor(mainActivity.pickElementTypeColor(elementListViewList.get(position).getPostType())));

            System.out.println(elementListViewList.get(position).getPostImage());
            int resID = getResources().getIdentifier(elementListViewList.get(position).getPostImage(), "drawable", getPackageName());
            viewHolder.elementImageView.setImageDrawable(getDrawable(resID));
        }

        @Override
        public int getItemCount() {
            return elementListViewList.size();
        }

        //-----------------------------------------------------
        // filterForElementsInSearchView
        // filter to show any elements that follow search query if-statements
        //-----------------------------------------------------
        public void filterForElementsInSearchView(String searchBarQuery) {

            String rawSearchBarQuery = searchBarQuery;
            String lowerCaseSearchBarQuery = searchBarQuery.toLowerCase(Locale.getDefault());

            elementListViewList.clear();
            if (searchBarQuery.length() < 1) {
                relativeLayoutHeaderCount.setVisibility(View.GONE);
                elementListViewList.addAll(filteredElementListViewList);
            } else {
                relativeLayoutHeaderCount.setVisibility(View.VISIBLE);
                for (ElementInfoPost postDetail : filteredElementListViewList) {
                    if (rawSearchBarQuery.length() > 0 &&
                            postDetail.getPostNumber().toString().contains(rawSearchBarQuery) &&
                            checkAtomicNumber(searchBarQuery, postDetail.getPostNumber().toString())) {
                        if (filteredElementListViewList.contains(postDetail)) {
                            elementListViewList.add(postDetail);
                        }

                    } else if (rawSearchBarQuery.length() != 0 && postDetail.getPostTitle().contains(rawSearchBarQuery)) {
                        if (filteredElementListViewList.contains(postDetail)) {
                            elementListViewList.add(postDetail);
                        }
                    } else if (rawSearchBarQuery.length() != 0 && postDetail.getPostSubTitle().toLowerCase().contains(lowerCaseSearchBarQuery)) {
                        if (filteredElementListViewList.contains(postDetail)) {
                            elementListViewList.add(postDetail);
                        }
                    }
                }
            }

            if (elementListViewList.isEmpty()) {
                atomImage.setVisibility(View.VISIBLE);
                atomText.setVisibility(View.VISIBLE);

            } else {
                atomImage.setVisibility(View.GONE);
                atomText.setVisibility(View.GONE);
            }

            resultsCountTextView.setText(String.valueOf(rv.getAdapter().getItemCount()));

            notifyDataSetChanged();
        }

        //-----------------------------------------------------
        // filterForElementType
        // filter to show any elements that follow category button switch case
        //-----------------------------------------------------
        public void filterForElementType() {
            for (int i = 0; i < indicatorVisible.length; i++) {
                switch (i) {
                    case 0:
                        if (indicatorVisible[0]) {
                            if (filteredElementListViewList.size() != 118) {
                                filteredElementListViewList.clear();
                                filteredElementListViewList.addAll(fullElementInfoPostsArrayList);
                            }
                        } else {
                            filteredElementListViewList.clear();
                        }
                        break;
                    case 1:
                        if (indicatorVisible[1]) {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().toString().equals("actinoid") && (!filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.add(elementInfoPost);
                                }
                            }
                        } else {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().toString().equals("actinoid") && (filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.remove(elementInfoPost);
                                }
                            }
                        }
                        break;
                    case 2:
                        if (indicatorVisible[2]) {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().equals("alkaline earth metal") && (!filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.add(elementInfoPost);
                                }
                            }
                        } else {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().equals("alkaline earth metal") && (filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.remove(elementInfoPost);
                                }
                            }
                        }
                        break;
                    case 3:
                        if (indicatorVisible[3]) {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().equals("alkali metal") && (!filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.add(elementInfoPost);
                                }
                            }
                        } else {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().equals("alkali metal") && (filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.remove(elementInfoPost);
                                }
                            }
                        }
                        break;
                    case 4:
                        if (indicatorVisible[4]) {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().equals("halogen") && (!filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.add(elementInfoPost);
                                }
                            }
                        } else {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().equals("halogen") && (filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.remove(elementInfoPost);
                                }
                            }
                        }
                        break;
                    case 5:
                        if (indicatorVisible[5]) {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().equals("lanthanoid") && (!filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.add(elementInfoPost);
                                }
                            }
                        } else {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().equals("lanthanoid") && (filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.remove(elementInfoPost);
                                }
                            }
                        }
                        break;
                    case 6:
                        if (indicatorVisible[6]) {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().equals("metal") && (!filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.add(elementInfoPost);
                                }
                            }
                        } else {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().equals("metal") && (filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.remove(elementInfoPost);
                                }
                            }
                        }
                        break;
                    case 7:
                        if (indicatorVisible[7]) {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().equals("metalloid") && (!filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.add(elementInfoPost);
                                }
                            }
                        } else {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().equals("metalloid") && (filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.remove(elementInfoPost);
                                }
                            }
                        }
                        break;
                    case 8:
                        if (indicatorVisible[8]) {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().equals("noble gas") && (!filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.add(elementInfoPost);
                                }
                            }
                        } else {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().equals("noble gas") && (filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.remove(elementInfoPost);
                                }
                            }
                        }
                        break;
                    case 9:
                        if (indicatorVisible[9]) {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().equals("nonmetal") && (!filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.add(elementInfoPost);
                                }
                            }
                        } else {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().equals("nonmetal") && (filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.remove(elementInfoPost);
                                }
                            }
                        }
                        break;
                    case 10:
                        if (indicatorVisible[10]) {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().equals("transition metal") && (!filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.add(elementInfoPost);
                                }
                            }
                        } else {
                            for (ElementInfoPost elementInfoPost : fullElementInfoPostsArrayList) {
                                if (elementInfoPost.getPostType().equals("transition metal") && (filteredElementListViewList.contains(elementInfoPost))) {
                                    filteredElementListViewList.remove(elementInfoPost);
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            }

            Collections.sort(filteredElementListViewList, new Comparator<ElementInfoPost>() {
                @Override
                public int compare(ElementInfoPost o1, ElementInfoPost o2) {
                    return o1.getPostNumber().compareTo(o2.getPostNumber());
                }
            });

            customRecycleViewAdapter.filterForElementsInSearchView(currentSearchQuery);
        }
    }


    //-----------------------------------------------------
    // checkAtomicNumber
    // checks in order of entered number instead of if it contains the number
    // ex. if "1" is entered
    // shown: 1, 10, 11, 100, etc.
    // not shown: 21, 31, 41, etc.
    //-----------------------------------------------------
    private boolean checkAtomicNumber(String inputAtomicNumber, String storedAtomicNumber) {
        for (int i = 0; i < inputAtomicNumber.length(); i++) {
            if (inputAtomicNumber.charAt(i) != storedAtomicNumber.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}