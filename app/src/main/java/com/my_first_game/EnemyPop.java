package com.my_first_game;

/*
    敵の動きを記憶しておく
    url: http://androidn.hannnari.com/link200-shooting-11.html
 */

import java.util.ArrayList;

public class EnemyPop {
    public int enemyPopCount;
    public ArrayList<Integer> enemyMoveS = new ArrayList();
    public ArrayList<Integer> enemyMoveR = new ArrayList();
    public ArrayList<Integer> enemyMoveC = new ArrayList();

    EnemyPop(int enemyPopCount, ArrayList enemyMoveS, ArrayList enemyMoveR, ArrayList enemyMoveC){
        this.enemyPopCount = enemyPopCount;
        this.enemyMoveS = enemyMoveS;
        this.enemyMoveR = enemyMoveR;
        this.enemyMoveC = enemyMoveC;
    }
}
