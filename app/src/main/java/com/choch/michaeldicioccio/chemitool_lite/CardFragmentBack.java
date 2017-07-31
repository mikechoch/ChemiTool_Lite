package com.choch.michaeldicioccio.chemitool_lite;

/**
 * Created by michaeldicioccio on 4/27/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class CardFragmentBack extends Fragment {

    private CardView mCardView;
    private ElementInfo item;

    private static String CHEMITOOL_PRO_URL = "https://play.google.com/store/apps/details?id=com.choch.michaeldicioccio.chemitool_pro&hl=en";


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    public CardFragmentBack() {

    }


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    public CardFragmentBack(ElementInfo elementInfo) {
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

        View view = inflater.inflate(R.layout.card_view_back, container, false);
        mCardView = (CardView) view.findViewById(R.id.card_view_back);
        mCardView.setMaxCardElevation(mCardView.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);

        CardView getProCardView = (CardView) mCardView.findViewById(R.id.backcard_getpro_button_bottom);

//        ImageView imageViewElementElectronShell = (ImageView) mCardView.findViewById(R.id.backcard_element_electron_shell_image);

        ImageView imageViewElementPhaseIcon = (ImageView) mCardView.findViewById(R.id.backcard_element_phase_icon);
        ImageView imageViewElementRadioactive = (ImageView) mCardView.findViewById(R.id.backcard_element_radioactive);

        TextView textViewElementName = (TextView) mCardView.findViewById(R.id.backcard_element_name);
        TextView textViewElementAtomicNumber = (TextView) mCardView.findViewById(R.id.backcard_element_atomic_number);
        TextView textViewElementPhase = (TextView) mCardView.findViewById(R.id.backcard_element_phase);
        TextView textViewElementPeriod = (TextView) mCardView.findViewById(R.id.backcard_element_period);
        TextView textViewElementGroup = (TextView) mCardView.findViewById(R.id.backcard_element_group);
        TextView textViewElementYear = (TextView) mCardView.findViewById(R.id.backcard_element_year);
        TextView textViewElementAtomicMass = (TextView) mCardView.findViewById(R.id.backcard_element_atomic_mass);
        TextView textViewElementDensity = (TextView) mCardView.findViewById(R.id.backcard_element_density);
        TextView textViewElementConfig = (TextView) mCardView.findViewById(R.id.backcard_element_config);
        TextView textViewElementElectronShell = (TextView) mCardView.findViewById(R.id.backcard_element_electron_shell_text);
        TextView textViewElementElectronegativity = (TextView) mCardView.findViewById(R.id.backcard_element_electronegativity);
        TextView textViewElementAtomicRadius = (TextView) mCardView.findViewById(R.id.backcard_element_atomic_radius);
        TextView textViewElementCovRadius = (TextView) mCardView.findViewById(R.id.backcard_element_cov_radius);
        TextView textViewElementVanRadius = (TextView) mCardView.findViewById(R.id.backcard_element_van_radius);
        TextView textViewElementThermalConductivity = (TextView) mCardView.findViewById(R.id.backcard_element_thermal_conductivity);
        TextView textViewElementMelting = (TextView) mCardView.findViewById(R.id.backcard_element_melting);
        TextView textViewElementBoiling = (TextView) mCardView.findViewById(R.id.backcard_element_boiling);
        TextView textViewElementCrystalStructure = (TextView) mCardView.findViewById(R.id.backcard_element_crystal);
        TextView textViewElementYoungsModulus = (TextView) mCardView.findViewById(R.id.backcard_element_youngs_modulus);
        TextView textViewElementShearModulus = (TextView) mCardView.findViewById(R.id.backcard_element_shear_modulus);
        TextView textViewElementBulkModulus = (TextView) mCardView.findViewById(R.id.backcard_element_bulk_modulus);
        TextView textViewElementMohsHardness = (TextView) mCardView.findViewById(R.id.backcard_element_mohs_hardness);
        TextView textViewElementVickersHardness = (TextView) mCardView.findViewById(R.id.backcard_element_vickers_hardness);
        TextView textViewElementBrinellHardness = (TextView) mCardView.findViewById(R.id.backcard_element_brinell_hardness);

        RelativeLayout relativeLayoutElementType = (RelativeLayout) mCardView.findViewById(R.id.cardview_element_type_back);
        RelativeLayout doubleTapLayout = (RelativeLayout) mCardView.findViewById(R.id.double_tap_view);

        imageViewElementPhaseIcon.setImageDrawable(getResources().getDrawable(checkElementPhaseIcon(item.getElementPhase().toLowerCase().trim())));

        if (item.isElementRadioactive()) {
            imageViewElementRadioactive.setVisibility(View.VISIBLE);
        }

        textViewElementName.setText(item.getElementName());
        textViewElementAtomicNumber.setText(Html.fromHtml("<b>Atomic Number:</b> " + item.getElementAtomicNumber()));
        textViewElementPhase.setText(Html.fromHtml("<b>Phase:</b> " + firstLetterToUpperCase(item.getElementPhase())));
        textViewElementPeriod.setText(Html.fromHtml("<b>Period:</b> " + item.getElementPeriod()));
        textViewElementGroup.setText(Html.fromHtml("<b>Group:</b> " + item.getElementGroup()));
        textViewElementYear.setText(Html.fromHtml("<b>Year Discovered:</b> " + item.getElementDiscovery('Y')));
        textViewElementAtomicMass.setText(Html.fromHtml("<b>Atomic Mass:</b> " + item.getElementAtomicMass() + " u"));
        textViewElementDensity.setText(Html.fromHtml("<b>Density:</b> " + item.getElementDensity() + " g/cm<sup><small>3</small></sup>"));
        textViewElementConfig.setText(Html.fromHtml("<b>Element Config:</b> " + item.getElementConfig()));
        textViewElementElectronShell.setText(Html.fromHtml("<b>Electron Shell Config:</b> " + item.printElementElectronShell()));
        textViewElementElectronegativity.setText(Html.fromHtml("<b>Electronegativity:</b> " + item.getElementElectronegativity()));
        textViewElementAtomicRadius.setText(Html.fromHtml("<b>Atomic Radius:</b> " + item.getElementRadius('A') + " pm"));
        textViewElementCovRadius.setText(Html.fromHtml("<b>Covalent Radius:</b> " + item.getElementRadius('C') + " pm"));
        textViewElementVanRadius.setText(Html.fromHtml("<b>Van der Waals Radius:</b> " + item.getElementRadius('V') + " pm"));
        textViewElementThermalConductivity.setText(Html.fromHtml("<b>Thermal Conductivity:</b> " + item.getElementThermalConductivity() + " W/(m-K)"));
        textViewElementMelting.setText(Html.fromHtml(item.getElementMeltingPoint('K') + " K" +
                "<br>" + item.getElementMeltingPoint('C') + " C" +
                "<br>" + item.getElementMeltingPoint('F') + " F"));
        textViewElementBoiling.setText(Html.fromHtml(item.getElementBoilingPoint('K') + " K" +
                "<br>" + item.getElementBoilingPoint('C') + " C" +
                "<br>" + item.getElementBoilingPoint('F') + " F"));
        textViewElementCrystalStructure.setText(checkElementCrystalStructure(item.getElementCrystalStructure('1'), item.getElementCrystalStructure('2')));

        textViewElementYoungsModulus.setText(Html.fromHtml("<b>Young's Modulus:</b> " + item.getElementModulus('Y') + " Pa"));
        textViewElementShearModulus.setText(Html.fromHtml("<b>Shear Modulus:</b> " + item.getElementModulus('S') + " Pa"));
        textViewElementBulkModulus.setText(Html.fromHtml("<b>Bulk Modulus:</b> " + item.getElementModulus('B') + " Pa"));
        textViewElementMohsHardness.setText(Html.fromHtml("<b>Moh's Hardness:</b> " + item.getElementHardness('M') + " Mohs"));
        textViewElementVickersHardness.setText(Html.fromHtml("<b>Vicker's Hardness:</b> " + item.getElementHardness('V') + " kgf/mm<sup><small>2</small></sup>"));
        textViewElementBrinellHardness.setText(Html.fromHtml("<b>Brinell Hardness:</b> " + item.getElementHardness('B') + " kgf/mm<sup><small>2</small></sup>"));

        relativeLayoutElementType.setBackgroundResource(pickElementTypeColor(item.getElementType()));
        getProCardView.setCardBackgroundColor(getResources().getColor(pickElementTypeColor(item.getElementType())));
//        imageViewElementElectronShell.setImageBitmap(createElementElectronShell(item.getElementElectronShell()));

        getProCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent buy = new Intent(Intent.ACTION_VIEW, Uri.parse(CHEMITOOL_PRO_URL));
                startActivity(buy);
            }
        });

        doubleTapLayout.setOnClickListener(new View.OnClickListener() {

//            int i = 0;

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                returnToMainFragment();

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
//                    returnToMainFragment();
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
    private void returnToMainFragment() {
        getFragmentManager().popBackStackImmediate();
    }


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    public Bitmap createElementElectronShell(String[] electronShell) {
        Bitmap circleBitmap = Bitmap.createBitmap(326, 326, Bitmap.Config.ARGB_4444);

        Paint circleStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleStrokePaint.setColor(Color.BLACK);
        circleStrokePaint.setStyle(Paint.Style.STROKE);
        circleStrokePaint.setStrokeWidth(4);
        circleStrokePaint.setAntiAlias(true);

        Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);

        Canvas canvas = new Canvas(circleBitmap);
        canvas.drawCircle(circleBitmap.getWidth() / 2, circleBitmap.getHeight() / 2, 40, circlePaint);
        canvas.drawCircle(circleBitmap.getWidth() / 2, circleBitmap.getHeight() / 2, 60, circleStrokePaint);
        canvas.drawCircle(circleBitmap.getWidth() / 2, circleBitmap.getHeight() / 2, 80, circleStrokePaint);
        canvas.drawCircle(circleBitmap.getWidth() / 2, circleBitmap.getHeight() / 2, 100, circleStrokePaint);
        canvas.drawCircle(circleBitmap.getWidth() / 2, circleBitmap.getHeight() / 2, 120, circleStrokePaint);
        canvas.drawCircle(circleBitmap.getWidth() / 2, circleBitmap.getHeight() / 2, 140, circleStrokePaint);
        canvas.drawCircle(circleBitmap.getWidth() / 2, circleBitmap.getHeight() / 2, 160, circleStrokePaint);

        return circleBitmap;
    }


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    public int checkElementPhaseIcon(String phase) {
        switch(phase) {
            case "solid":
                return R.drawable.ic_cube_black_24dp;
            case "liquid":
                return R.drawable.ic_water_black_24dp;
            case "gas":
                return R.drawable.ic_cloud_black_24dp;
        }
        return R.drawable.ic_cube_black_24dp;
    }


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    public String checkElementCrystalStructure(String structure1, String structure2) {
        String crystal_structure;
        if ((!structure1.equals("--")) && (!structure2.equals("--"))) {
            crystal_structure = structure1 + "\n" + structure2;
        } else if ((structure1.equals("--")) && (!structure2.equals("--"))) {
            crystal_structure =  structure2;
        } else if ((!structure1.equals("--")) && (structure2.equals("--"))) {
            crystal_structure = structure1;
        } else {
            crystal_structure = "--";
        }
        return crystal_structure;
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
    //
    //
    //-----------------------------------------------------
    public String firstLetterToUpperCase(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}