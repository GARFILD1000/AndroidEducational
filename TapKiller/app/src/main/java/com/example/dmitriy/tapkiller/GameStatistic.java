package com.example.dmitriy.tapkiller;

public class GameStatistic{
    int points;
    int kills;
    long time;
    long startTime;
    boolean gameOver;
    public GameStatistic(){
        points = 0;
        kills = 0;
        time = 0;
        startTime = System.currentTimeMillis();
        gameOver = false;
    }
}
