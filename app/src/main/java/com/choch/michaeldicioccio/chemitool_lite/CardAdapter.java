package com.choch.michaeldicioccio.chemitool_lite;

/**
 * Created by michaeldicioccio on 4/26/17.
 */

import android.support.v7.widget.CardView;

public interface CardAdapter {

    float getBaseElevation();

    int MAX_ELEVATION_FACTOR = 8;
    int getCount();

    CardView getCardViewAt(int position);
}
