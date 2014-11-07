package com.palsulich.gamescoring.core;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Chad on 10/13/13.
 */
public class Player implements Serializable{
    private String name;
    private ArrayList<Integer> scores;

    public Player(String name){
        this.name = name;
        scores = new ArrayList<Integer>();
    }

    public String getName (){
        return name;
    }

    public void setName(String newName) {
        name=newName;
    }

    public void addScore (int score){
        scores.add(score);
    }

    public void setScore (int score, int index){
        if(scores.size()<index+1){
            while (scores.size()<index+1) addScore(0);
        }
        scores.set(index, score);
    }

    public ArrayList<Integer> getScores (){
        return scores;
    }

    public int getLatestScore () {
        return scores.get(scores.size()-1);
    }

    public int getNumRounds(){
        return scores.size();
    }

    public Integer getScoreTotal () {
        return getSum(scores);
    }

    private Integer getSum (ArrayList<Integer> list) {
        Integer sum = 0;
        if (list!=null){
            for (int i=0;i<list.size();i++){
                sum+=list.get(i);
            }
        }
        return sum;
    }

}
