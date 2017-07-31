package com.choch.michaeldicioccio.chemitool_lite;

/**
 * Created by michaeldicioccio on 4/27/17.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CardFragmentPagerAdapter extends FragmentStatePagerAdapter implements CardAdapter {

    private List<CardFragment> mFrontFragments;
    private ArrayList<ElementInfo> mOrdering = new ArrayList<>();
    private float mBaseElevation;

    public CardFragmentPagerAdapter(FragmentManager fm, float baseElevation, ArrayList<ElementInfo> ordering, Map<Integer, ElementInfo> data) {
        super(fm);
        mFrontFragments = new ArrayList<>();
        mOrdering = ordering;
        mBaseElevation = baseElevation;

        // create all element cards from data map
        for (int i = 0; i < data.size(); i++) {
            addCardFragment(new CardFragment(mOrdering.get(i)));
        }
    }

    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mFrontFragments.get(position).getCardView();
    }

    @Override
    public int getCount() {
        return mFrontFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFrontFragments.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        mFrontFragments.set(position, (CardFragment) fragment);
        return fragment;
    }

    public void addCardFragment(CardFragment fragment) {
        mFrontFragments.add(fragment);
    }

    public ArrayList<ElementInfo> getDataSet() {
        return mOrdering;
    }
}
