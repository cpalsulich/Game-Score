package com.palsulich.gamescoring.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.palsulich.gamescoring.core.Game;

import java.util.ArrayList;

/**
 * Created by Chad on 4/3/14.
 */
public class GameSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_GAMES = "games";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NAME = "game_name";
    private static final String[] COLUMNS = {COLUMN_ID, COLUMN_DATE, COLUMN_NAME};
    private static final String DATABASE_NAME = "gamesDb";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_GAMES + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_NAME
            + " text not null, " + COLUMN_DATE
            + " text not null);";

    public GameSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Log.w(GameSQLiteHelper.class.getName(),
//                "Upgrading database from version " + oldVersion + " to "
//                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
        this.onCreate(db);
    }

    // This function returns a new Game that contains it's ID.
    // The game's ID is unknown until it's inserted into the table.
    public Game addGame(Game game){
//        Log.v("gameAdd", game.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, game.getName());
        values.put(COLUMN_DATE, game.getDate());
        db.insert(TABLE_GAMES, null, values);
        return getGame(game.getDate());
    }

    public int deleteGame (int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_GAMES, COLUMN_ID + "=" + id, null);
    }

    public long updateGame (Game game){
//        Log.v("Game Update", game.toString());
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();
        values.put(COLUMN_DATE, game.getDate());
        values.put(COLUMN_NAME, game.getName());
        return db.update(TABLE_GAMES, values, COLUMN_ID + " = ?",
                new String[] { String.valueOf(game.getId()) });
    }

    public Game getGame(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_GAMES,
                COLUMNS,
                " id = ?",
                new String[] { String.valueOf(id) },
                null,
                null,
                null,
                null);
        if (cursor != null)
            cursor.moveToFirst();
        Game game = new Game(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(2)
        );
//        Log.v("getGame("+id+")", game.toString());
        return game;
    }

    public Game getGame(String date){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_GAMES,
                COLUMNS,
                " date = ?",
                new String[] { String.valueOf(date) },
                null,
                null,
                null,
                null);
        if (cursor != null)
            cursor.moveToFirst();
        Game game = new Game(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2)
        );
//        Log.v("getGame("+date+")", game.toString());
        return game;
    }

    public ArrayList<Game> getAllGames() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Game> games = new ArrayList<Game>();
        String query = "SELECT  * FROM " + TABLE_GAMES;
        Cursor cursor = db.rawQuery(query, null);
        Game game;
        if (cursor.moveToFirst()) {
            do {
                game = new Game(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)
                );
                games.add(game);
            } while (cursor.moveToNext());
        }
//        Log.d("getAllGames()", games.toString());
        return games;
    }
}
