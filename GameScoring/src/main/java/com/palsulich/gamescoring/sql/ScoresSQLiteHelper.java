package com.palsulich.gamescoring.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.palsulich.gamescoring.core.Game;
import com.palsulich.gamescoring.core.Player;

import java.util.ArrayList;

/**
 * Created by Chad on 6/19/2014.
 */
public class ScoresSQLiteHelper extends SQLiteOpenHelper{

    public static final String TABLE_SCORES = "scores";
    public static final String COLUMN_GAME_ID = "game_id";
    public static final String COLUMN_PLAYER = "player";
    public static final String COLUMN_ROUND = "round";
    public static final String COLUMN_SCORE = "score";
    private static final String[] COLUMNS = {COLUMN_GAME_ID, COLUMN_PLAYER, COLUMN_ROUND, COLUMN_SCORE};
    private static final String[] GAME_COLUMNS = {COLUMN_PLAYER, COLUMN_ROUND, COLUMN_SCORE};
    private static final String DATABASE_NAME = "scoresDb";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_SCORES + "(" + COLUMN_GAME_ID
            + " integer not null, " + COLUMN_PLAYER
            + " text not null, " + COLUMN_ROUND
            + " integer not null, " + COLUMN_SCORE
            + " integer nut null);";

    public ScoresSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
//        Log.d("ScoresSQLiteHelper", "onCreate string: " + DATABASE_CREATE);
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Log.w(GameSQLiteHelper.class.getName(),
//                "Upgrading database from version " + oldVersion + " to "
//                        + newVersion + ", which will destroy all old data"
//        );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        this.onCreate(db);
    }

    public void addScores(Game game){
        SQLiteDatabase db = this.getWritableDatabase();
        int gameId = game.getId();
        ArrayList<Player> playerList = game.getPlayerList();
        int round;
        ContentValues values;
        for(Player player : playerList) {
            String name = player.getName();
            round = 1;
            for(int score : player.getScores()) {
                values = new ContentValues();
                values.put(COLUMN_GAME_ID, gameId);
                values.put(COLUMN_PLAYER, name);
                values.put(COLUMN_ROUND, round);
                values.put(COLUMN_SCORE, score);
                db.insert(TABLE_SCORES, null, values);
                round++;
            }
        }
        db.close();
    }

    public int deleteScores (int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SCORES, COLUMN_GAME_ID + "=" + id, null);
    }

    public void addPlayerScores(int gameId, Player player){
        SQLiteDatabase db = this.getWritableDatabase();
        int round = 1;
        String name = player.getName();
        ContentValues values;
        for (int score : player.getScores()){
            values = new ContentValues();
            values.put(COLUMN_GAME_ID, gameId);
            values.put(COLUMN_PLAYER, name);
            values.put(COLUMN_ROUND, round);
            values.put(COLUMN_SCORE, score);
            db.insert(TABLE_SCORES, null, values);
            round++;
        }
        db.close();
    }

    public void addLatestRound (Game game) {
        SQLiteDatabase db = this.getWritableDatabase();
        int gameId = game.getId();
        ArrayList<Player> playerList = game.getPlayerList();
        ContentValues values;
        int round = playerList.get(0).getScores().size();
        for(Player player : playerList) {
            String name = player.getName();
            values = new ContentValues();
            values.put(COLUMN_GAME_ID, gameId);
            values.put(COLUMN_PLAYER, name);
            values.put(COLUMN_ROUND, round);
            values.put(COLUMN_SCORE, player.getLatestScore());
            db.insert(TABLE_SCORES, null, values);
//            Log.d("addLatestRound", gameId + " " + name + " " + round + " " + player.getLatestScore());
        }
        db.close();
    }

//    public void addScoreEntry (int gameId, String playerName, int round, int score) {
//        Log.v("scoreAdd", "ID: "+Integer.toString(gameId)+", Player: "+playerName+", Round: "+Integer.toString(round)+", Score: "+Integer.toString(score));
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_GAME_ID, gameId);
//        values.put(COLUMN_PLAYER, playerName);
//        values.put(COLUMN_ROUND, round);
//        values.put(COLUMN_SCORE, score);
//        db.insert(TABLE_SCORES, null, values);
//        db.close();
//    }

    public void updateScores (Game game){
        deleteScores(game.getId());
        addScores(game);
    }

    public ArrayList<Player> getGameScores(int id){
        ArrayList<Player> playerList = new ArrayList<Player>();
        ArrayList<String> playerNames = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SCORES,
                GAME_COLUMNS, // {COLUMN_PLAYER, COLUMN_ROUND, COLUMN_SCORE};
                " game_id = ?",
                new String[] { String.valueOf(id) },
                null,
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            do {
                String playerName = cursor.getString(0);
                int round = cursor.getInt(1);
                int score = cursor.getInt(2);
                if (playerNames.indexOf(playerName) == -1) {
                    playerNames.add(playerName);
                    playerList.add(new Player(playerName));
                }
                playerList.get(getIndexOfPlayerName(playerList, playerName))
                        .setScore(score, round-1);
            } while (cursor.moveToNext());
        }
        return playerList;
    }

    private int getIndexOfPlayerName(ArrayList<Player> playerList, String playerName){
        int index=0;
        for (Player player : playerList){
            if(player.getName().equals(playerName)) return index;
            index++;
        }
        return -1;
    }
}

