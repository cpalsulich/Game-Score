package com.palsulich.gamescoring.core;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Chad on 5/29/14.
 */
public class Game implements Serializable{
    private String name;
    private String date;
    private int id;
    private ArrayList<Player> playerList;

    public Game (int id, String name, String date, ArrayList<Player> playerList){
        super();
        this.id = id;
        this.name = name;
        this.date = date;
        this.playerList = playerList;
    }

    public Game (int id, String name, String date){
        super();
        this.id = id;
        this.name = name;
        this.date = date;
    }

    public Game (int id, String name) {
        super();
        this.id = id;
        this.name = name;
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        this.date = dateFormat.format(date);
    }

    public Game (String name){
        super();
        this.name = name;
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        this.date = dateFormat.format(date);
    }

    public int getId (){
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public String getName (){
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getDate (){
        return date;
    }

    public void setDate () {
        this.date = date;
    }

    public ArrayList<Player> getPlayerList(){
        return playerList;
    }

    public void setPlayerList(ArrayList<Player> playerList){
        this.playerList = playerList;
    }

    public String toString(){
        return "Game: {id: " + id + ", date: " + date + ", name: " + name + "}";
    }
}
