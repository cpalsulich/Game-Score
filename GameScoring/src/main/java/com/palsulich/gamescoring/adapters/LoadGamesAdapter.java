package com.palsulich.gamescoring.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;

import com.palsulich.gamescore.R;
import com.palsulich.gamescoring.core.Game;

import java.util.ArrayList;

public class LoadGamesAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    ArrayList<Game> games;
    int currentlySelectedGame;
    CheckedTextView currentlySelectedCheckBox;
    Button startButton;
    Button deleteButton;

    public LoadGamesAdapter(Context context, ArrayList<Game> games, Button startButton, Button deleteButton) {
        // Cache the LayoutInflate to avoid asking for a new one each time.
        this.games = games;
        mInflater = LayoutInflater.from(context);
        this.startButton = startButton;
        this.deleteButton = deleteButton;
        currentlySelectedGame = -1;
        currentlySelectedCheckBox = null;
        startButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    // Number of elements to retrieve from this adapter.
    public int getCount() {
        return games.size();
    }

    // Return the object at the given position.
    public Object getItem(int position) {
        return games.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final CheckedTextView cTextView = (CheckedTextView)mInflater.inflate(R.layout.load_game_checked_textview, parent, false);
        if (cTextView == null) return new View(null);
        cTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = cTextView.isChecked();
                if (currentlySelectedCheckBox != null) currentlySelectedCheckBox.setChecked(false);
                if (!isChecked) {
                    currentlySelectedGame = position;
                    currentlySelectedCheckBox = (CheckedTextView) v;
                }
                if (isChecked && currentlySelectedGame == position) {
                    currentlySelectedGame = -1;
                    currentlySelectedCheckBox = null;
                }
                cTextView.setChecked(position == currentlySelectedGame);
                startButton.setEnabled(-1 != currentlySelectedGame);
                deleteButton.setEnabled(-1 != currentlySelectedGame);
            }
        });
        cTextView.setChecked(position == currentlySelectedGame);
        startButton.setEnabled(-1 != currentlySelectedGame);
        deleteButton.setEnabled(-1 != currentlySelectedGame);
        String text = games.get(position).getName() + " | " + games.get(position).getDate();
        cTextView.setText(text);
        return cTextView;
    }
}
