package com.palsulich.gamescoring.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.palsulich.gamescore.R;
import com.palsulich.gamescoring.activities.MainActivity;

public class PlayerAddFragment extends DialogFragment{

    private boolean nameHasText=false;
    private String nameText = "";
    private boolean scoreHasText=false;
    private String scoreText = "";
    private static final String TAG = "PlayerAddition";
    private MainActivity core;
//    public PlayerAddFragment(MainActivity core){
//        this.core = core;
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the layout inflater
        //Log.i(TAG, "Function called");
        Activity a = getActivity();
        if (a != null){
            LayoutInflater inflater = a.getLayoutInflater();
            View view = inflater.inflate(R.layout.activity_player_add, null);
            if (view != null){
                EditText name = (EditText) view.findViewById(R.id.addPlayerName);
                name.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //Log.i(TAG, "String Changed");
                        if(s.length()>0){
                            nameHasText=true;
                            nameText=s.toString();
                        }
                        else
                            nameHasText=false;
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                EditText score = (EditText) view.findViewById(R.id.initAddPlayerScore);
                // Limit score length to 5 characters
                score.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                score.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(s.length()>0){
                            scoreHasText=true;
                            scoreText=s.toString();
                        }
                        else
                            scoreHasText=false;
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(view);
                builder.setTitle(R.string.add_player);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (nameHasText && scoreHasText) {
                            String[] out = {nameText,scoreText};
                            dialog.dismiss();
                            ((MainActivity)getActivity()).addPlayerToGame(out);
                        }
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button_default is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
                return builder.create();
            }
        }
        return null;
    }
}
