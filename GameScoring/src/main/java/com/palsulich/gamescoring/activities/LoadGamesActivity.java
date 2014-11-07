package com.palsulich.gamescoring.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.palsulich.gamescore.R;
import com.palsulich.gamescoring.core.Game;
import com.palsulich.gamescoring.sql.GameSQLiteHelper;
import com.palsulich.gamescoring.adapters.LoadGamesAdapter;
import com.palsulich.gamescoring.sql.ScoresSQLiteHelper;

import java.util.ArrayList;

public class LoadGamesActivity extends Activity {
    ListView gameList;
    ArrayList<Game> gameEntries;
    LoadGamesAdapter loadGamesAdapter;
    Button startButton;
    Button deleteButton;
    GameSQLiteHelper gameSqlHelper;
    ScoresSQLiteHelper scoreSqlHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_games);
        gameSqlHelper = new GameSQLiteHelper(this);
        scoreSqlHelper = new ScoresSQLiteHelper(this);
        if (gameEntries == null){
            gameEntries = gameSqlHelper.getAllGames();
        }

        gameList = (ListView) findViewById(R.id.load_games_list);
        startButton = (Button) findViewById(R.id.load_game_button);
        deleteButton = (Button) findViewById(R.id.delete_game_button);
        loadGamesAdapter = new LoadGamesAdapter(getApplicationContext(), gameEntries, startButton, deleteButton);
        if(gameList != null){
            gameList.setAdapter(loadGamesAdapter);
        }
        ActionBar actionBar = getActionBar();
        if(actionBar != null) actionBar.hide();
    }

    public void startGame(View view){
        Intent myIntent = new Intent(LoadGamesActivity.this, MainActivity.class);
        for(int i=0; i<gameList.getChildCount(); i++){
            CheckedTextView cTextView = (CheckedTextView) gameList.getChildAt(i);
            if(cTextView.isChecked()){
                Game game = gameEntries.get(i);
                myIntent.putExtra("game", game);
                LoadGamesActivity.this.startActivity(myIntent);
            }
        }
    }

    public void deleteGame(View view){
        Game game = new Game(-1,"");
        for(int i=0; i<gameList.getChildCount(); i++) {
            CheckedTextView cTextView = (CheckedTextView) gameList.getChildAt(i);
            if (cTextView.isChecked()) {
                game = gameEntries.get(i);
                break;
            }
        }
        int id = game.getId();
        if(id>=0) {
            gameSqlHelper.deleteGame(id);
            scoreSqlHelper.deleteScores(id);
            gameEntries = gameSqlHelper.getAllGames();
            loadGamesAdapter = new LoadGamesAdapter(getApplicationContext(), gameEntries, startButton, deleteButton);
            gameList.setAdapter(loadGamesAdapter);
        }
        if(gameEntries.size() == 0){
            Intent myIntent = new Intent(LoadGamesActivity.this, EnterNamesActivity.class);
            LoadGamesActivity.this.startActivity(myIntent);
        }
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
