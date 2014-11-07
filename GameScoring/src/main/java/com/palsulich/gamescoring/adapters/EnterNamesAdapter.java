package com.palsulich.gamescoring.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.palsulich.gamescore.R;

import java.util.ArrayList;

public class EnterNamesAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    ArrayList<String> entries;
    Button startButton;

    public EnterNamesAdapter(Context context, ArrayList<String> entries, Button startButton) {
        // Cache the LayoutInflate to avoid asking for a new one each time.
        this.entries = entries;
        mInflater = LayoutInflater.from(context);
        this.startButton = startButton;
    }

    // Number of elements to retrieve from this adapter.
    public int getCount() {
        // One row for the names, one for each round, one for the edit scores, and one for the totals.
        return entries.size();
    }

    // Return the object at the given position.
    public Object getItem(int position) {
        return entries.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        EditText view = (EditText) mInflater.inflate(R.layout.enter_name_edit_text, parent, false);
        if (view == null) return new View(null);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                entries.set(position, s.toString());
                boolean flag = false;
                boolean first = true;
                for(String entry : entries){
                    if(first){ //Check to make sure game name is set.
                        first = false;
                        if(entry.length() <= 0) {
                            break;
                        }
                    }
                    else if (entry.length() > 0){
                        flag = true;
                        break;
                    }
                }
                startButton.setEnabled(flag);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        String name = entries.get(position);
        view.addTextChangedListener(textWatcher);
        if (name.trim().length() == 0){
            if(position == 0){
                view.setHint("Game Name");
            }
            else{
                view.setHint("Player " + position);
            }
        }
        else view.setText(name.trim());
        return view;
    }
}
