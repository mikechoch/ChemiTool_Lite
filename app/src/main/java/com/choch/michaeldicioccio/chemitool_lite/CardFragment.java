package com.choch.michaeldicioccio.chemitool_lite;

/**
 * Created by michaeldicioccio on 4/27/17.
 */

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class CardFragment extends Fragment {

    private CardView mCardView;
    private ElementInfo item;


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    public CardFragment() {

    }


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    public CardFragment(ElementInfo elementInfo) {
        item = elementInfo;
    }


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getSerializable("item") != null) {
                item = (ElementInfo) savedInstanceState.get("item");
            }
        }

        View view = inflater.inflate(R.layout.card_view, container, false);

        mCardView = (CardView) view.findViewById(R.id.card_view);
        mCardView.setMaxCardElevation(mCardView.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);

        ImageView chatHead = (ImageView) mCardView.findViewById(R.id.wikipedia_icon);
        chatHead.setVisibility(View.INVISIBLE);

        TextView textViewAtomicNumber = (TextView) mCardView.findViewById(R.id.cardview_atomic_number);
        TextView textViewElement = (TextView) mCardView.findViewById(R.id.cardview_element);
        TextView textViewElementName = (TextView) mCardView.findViewById(R.id.cardview_element_name);
        TextView textViewElementConfig = (TextView) mCardView.findViewById(R.id.cardview_element_config);
        TextView textViewElementMass = (TextView) mCardView.findViewById(R.id.cardview_element_mass);
        TextView textViewElementDescription = (TextView) mCardView.findViewById(R.id.cardview_image_description);
        RelativeLayout relativeLayoutElementType = (RelativeLayout) mCardView.findViewById(R.id.cardview_element_type);
        ImageView imageViewElementImage = (ImageView) mCardView.findViewById(R.id.cardview_element_image);

        textViewAtomicNumber.setText(item.getElementAtomicNumber().toString());

        textViewElement.setText(item.getElementSymbol());

        textViewElementName.setText(item.getElementName());
        textViewElementName.setTextSize(17);

        textViewElementConfig.setText(Html.fromHtml("<b>Config:</b> " + item.getElementConfig()));
        textViewElementMass.setText(Html.fromHtml("<b>Weight:</b> " + item.getElementAtomicMass() + " u"));

        textViewElementDescription.setText(item.getElementDescription());

        relativeLayoutElementType.setBackgroundResource(pickElementTypeColor(item.getElementType()));

        Context context = mCardView.getContext();
        int resID = context.getResources().getIdentifier(item.getElementImageFull(), "drawable", context.getApplicationContext().getPackageName());
        Picasso.with(context).load(resID).fit().centerInside().into(imageViewElementImage);

        mCardView.setOnClickListener(new View.OnClickListener() {

//            int i = 0;

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                enterNextFragment();

//                i++;
//                Handler handler = new Handler();
//                Runnable r = new Runnable() {
//
//                    @Override
//                    public void run() {
//                        i = 0;
//                    }
//                };
//
//                if (i == 1) {
//                    //Single click
//                    handler.postDelayed(r, 250);
//                } else if (i == 2) {
//                    //Double click
//                    i = 0;
//                    enterNextFragment();
//                }
            }
        });

        return mCardView;
    }

    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("item", item);
    }


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    public CardView getCardView() {
        return mCardView;
    }


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    private void enterNextFragment() {
        CardFragmentBack cardFragmentBack = new CardFragmentBack(item);
        getChildFragmentManager().beginTransaction()
//                .setCustomAnimations(R.anim.fade_in, 0, 0, R.anim.fade_out)
                .addToBackStack(null)
                .replace(R.id.card_view, cardFragmentBack).commit();
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
