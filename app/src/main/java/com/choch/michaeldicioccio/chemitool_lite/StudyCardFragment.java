package com.choch.michaeldicioccio.chemitool_lite;

/**
 * Created by michaeldicioccio on 6/15/17.
 */

import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StudyCardFragment extends Fragment {

    private CardView mCardView;
    private ElementInfo item;
    private Integer studyType;
    private double random = -1;
    private boolean correct_answer;
    private Integer attempts = 0;
    private ElementStudyReport elementStudyReport;


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    public StudyCardFragment() {

    }


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    public StudyCardFragment(ElementInfo elementInfo, Integer study_type) {
        item = elementInfo;
        studyType = study_type;

        elementStudyReport = new ElementStudyReport(
                item.getElementAtomicNumber(),
                item.getElementSymbol(),
                item.getElementName());
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

            if (savedInstanceState.get("result") != null) {
                correct_answer = (boolean) savedInstanceState.get("result");
            }

            if (savedInstanceState.get("random") != null) {
                random = (double) savedInstanceState.get("random");
            }

            if (savedInstanceState.getSerializable("studyinfo") != null) {
                elementStudyReport = (ElementStudyReport) savedInstanceState.get("studyinfo");
            }
        }

        View view = inflater.inflate(R.layout.card_view_study, container, false);

        mCardView = (CardView) view.findViewById(R.id.card_view);
        mCardView.setMaxCardElevation(mCardView.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);

        TextView textViewStudy = (TextView) mCardView.findViewById(R.id.cardview_element_study);
        final TextView textViewAttempts = (TextView) mCardView.findViewById(R.id.attempts_counter);
        RelativeLayout relativeLayoutElementType = (RelativeLayout) mCardView.findViewById(R.id.cardview_element_type);
        CardView cardViewStudySubmitButton = (CardView) mCardView.findViewById(R.id.study_submit_button);
        final ImageView mImgCheck = (ImageView) mCardView.findViewById(R.id.green_check);
        final ImageView mImgX = (ImageView) mCardView.findViewById(R.id.red_x);

        switch (studyType) {
            case 0:
                textViewStudy.setText(item.getElementSymbol().toString());
                break;
            case 1:
                textViewStudy.setText(item.getElementAtomicNumber().toString());
                break;
            case 2:
                if (random == -1) {
                    random = Math.random();
                }

                if (random < 0.5) {
                    textViewStudy.setText(item.getElementAtomicNumber().toString());
                } else {
                    textViewStudy.setText(item.getElementSymbol().toString());
                }
                break;
        }
        textViewAttempts.setText("Attempts: " + elementStudyReport.getAttempts() + "/3");
        relativeLayoutElementType.setBackgroundResource(pickElementTypeColor(item.getElementType()));

        cardViewStudySubmitButton.setCardBackgroundColor(getResources().getColor(pickElementTypeColor(item.getElementType())));
        cardViewStudySubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextStudyAnswer = (EditText) mCardView.findViewById(R.id.cardview_element_answer);
                if (elementStudyReport.getAttempts() < 3) {
                    if (!editTextStudyAnswer.getText().toString().toLowerCase().equals("")) {
                        if (!elementStudyReport.isCorrect()) {
                            if (editTextStudyAnswer.getText().toString().toLowerCase().equals(item.getElementName().toLowerCase())) {
                                elementStudyReport.setCorrect(true);
                                elementStudyReport.addAttempt();
                                checkResult(mImgX, mImgCheck);
                                textViewAttempts.setText("Attempts: " + elementStudyReport.getAttempts() + "/3");
                                editTextStudyAnswer.setEnabled(false);

                            } else {
                                elementStudyReport.addAttempt();
                                checkResult(mImgX, mImgCheck);
                                textViewAttempts.setText("Attempts: " + elementStudyReport.getAttempts() + "/3");

                                if (elementStudyReport.getAttempts() == 3) {
                                    editTextStudyAnswer.setEnabled(false);
                                }

                            }
                        }
                    }
                }
            }
        });

        checkResult(mImgX, mImgCheck);

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
        outState.putDouble("random", random);
        outState.putBoolean("result", correct_answer);
        outState.putSerializable("studyinfo", elementStudyReport);
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
    public ElementStudyReport getElementStudyReport() {
        return elementStudyReport;
    }


    //-----------------------------------------------------
    //
    //
    //-----------------------------------------------------
    public void checkResult(ImageView x, ImageView check) {
        if (elementStudyReport.isCorrect()) {
            EditText editTextStudyAnswer = (EditText) mCardView.findViewById(R.id.cardview_element_answer);
            editTextStudyAnswer.setEnabled(false);
            x.setVisibility(View.INVISIBLE);

            check.setVisibility(View.VISIBLE);
            ((Animatable) check.getDrawable()).start();

        } else if (!elementStudyReport.isCorrect()) {
            if (elementStudyReport.getAttempts() > 0) {

                check.setVisibility(View.INVISIBLE);

                x.setVisibility(View.VISIBLE);
                ((Animatable) x.getDrawable()).start();
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
}