package com.example.maciagamecenter.mazmorra;

import android.graphics.Point;

public class Enemy {
    private Point position;
    private int health;
    private int maxHealth;
    private int attack;
    private int defense;
    private int experienceValue;
    private static final int BASE_HEALTH = 10;
    private static final double HEALTH_SCALING = 1.5;
    private boolean isAlive;
    private static int turnCounter = 0;

    public Enemy(Point position, int level) {
        this.position = position;
        this.maxHealth = (int)(BASE_HEALTH * (1 + (level - 1) * 0.5));
        this.health = maxHealth;
        this.attack = 3 + level;
        this.defense = 1 + (level / 2);
        this.experienceValue = 5 * level;
        this.isAlive = true;
    }
    
    // Remove the second constructor completely
    
    public Point getPosition() {
        return position;
    }
    
    public void setPosition(Point position) {
        this.position = position;
    }

    public int getHealth() {
        return health;
    }
    public int getMaxHealth() {
        return maxHealth;
    }
    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getExperienceValue() {
        return experienceValue;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            isAlive = false;
        }
    }

    public static void incrementTurn() {
        turnCounter++;
    }

    public static boolean shouldMove() {
        return turnCounter % 2 == 0;
    }

    public static void resetTurnCounter() {
        turnCounter = 0;
    }

    public void moveTowards(Point target, char[][] dungeon) {
        if (!isAlive) return;

        int dx = Integer.compare(target.x - position.x, 0);
        int dy = Integer.compare(target.y - position.y, 0);

        // Try to move in x direction
        if (dx != 0 && isValidMove(position.x + dx, position.y, dungeon)) {
            position.x += dx;
            return;
        }

        // Try to move in y direction
        if (dy != 0 && isValidMove(position.x, position.y + dy, dungeon)) {
            position.y += dy;
        }
    }

    private boolean isValidMove(int x, int y, char[][] dungeon) {
        return x >= 0 && x < dungeon.length && 
               y >= 0 && y < dungeon[0].length && 
               (dungeon[x][y] == '.' || dungeon[x][y] == 'E' || dungeon[x][y] == 'S');
    }
}