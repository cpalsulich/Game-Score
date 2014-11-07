package com.palsulich.gamescoring.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.palsulich.gamescore.R;
import com.palsulich.gamescoring.sql.GameSQLiteHelper;
import com.palsulich.gamescoring.adapters.EnterNamesAdapter;

import java.util.ArrayList;

public class EnterNamesActivity extends Activity {

    public static final int MAX_NUM_PLAYERS = 12;

    private ListView playerList;
    private ArrayList<String> currentEntries;
    private EnterNamesAdapter enterNamesAdapter;
    private Button addPlayerButton;
    private Button startButton;
    private Button loadGamesButton;
    private GameSQLiteHelper gameSqlHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_names);
        if (currentEntries == null){
            currentEntries = new ArrayList<String>();
            currentEntries.add("");
            currentEntries.add("");
            currentEntries.add("");
        }

        playerList = (ListView) findViewById(R.id.enter_names_list);
        startButton = (Button) findViewById(R.id.start_game_button);
        addPlayerButton = (Button) findViewById(R.id.add_player_button);
        loadGamesButton = (Button) findViewById(R.id.load_game_button);
        enterNamesAdapter = new EnterNamesAdapter(getApplicationContext(), currentEntries, startButton);
        playerList.setAdapter(enterNamesAdapter);
        gameSqlHelper = new GameSQLiteHelper(this);
        if(gameSqlHelper.getAllGames().size() < 1){ // No games to load
            loadGamesButton.setEnabled(false);
        }
        ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.hide();
    }

    protected void onResume() {
        super.onResume();
        if(gameSqlHelper.getAllGames().size() < 1){ // No games to load
            loadGamesButton.setEnabled(false);
        }
        else {
            loadGamesButton.setEnabled(true);
        }
    }

    public void addPlayer(View view){
        currentEntries.add("");
        if (currentEntries.size() == MAX_NUM_PLAYERS+1) findViewById(R.id.add_player_button).setEnabled(false);
        enterNamesAdapter.notifyDataSetChanged();
    }

    public void loadGames(View view){
        Intent myIntent = new Intent(EnterNamesActivity.this, LoadGamesActivity.class);
        EnterNamesActivity.this.startActivity(myIntent);
    }

    public void startGame(View view){
        Intent myIntent = new Intent(EnterNamesActivity.this, MainActivity.class);
        ArrayList<String> finalEntries = new ArrayList<String>(currentEntries.size());
        for (String s : currentEntries){
            if (s.length() > 0) finalEntries.add(s);
        }
        myIntent.putStringArrayListExtra("players", finalEntries);
        EnterNamesActivity.this.startActivity(myIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.enter_names, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

}
