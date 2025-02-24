package com.example.maciagamecenter.mazmorra;

import android.graphics.Point;

public class Enemy {
    private Point position;
    private int health;
    private int maxHealth;
    private int attack;
    private int defense;
    private int experienceValue;
    private boolean alive;
    private int level;

    public Enemy(Point position, int level) {
        this.position = position;
        this.level = level;
        this.maxHealth = 50 + (level * 10);
        this.health = maxHealth;
        this.attack = 5 + (level * 2);
        this.defense = 2 + level;
        this.experienceValue = 20 + (level * 10);
        this.alive = true;
    }

    public void moveTowards(Point target, char[][] dungeon) {
        if (!alive) return;
        
        Point newPos = new Point(position.x, position.y);
        
        // Simple AI: Move towards player
        if (Math.abs(target.x - position.x) > Math.abs(target.y - position.y)) {
            newPos.x += Integer.compare(target.x, position.x);
        } else {
            newPos.y += Integer.compare(target.y, position.y);
        }
        
        if (isValidMove(newPos, dungeon)) {
            position = newPos;
        }
    }

    private boolean isValidMove(Point newPos, char[][] dungeon) {
        if (newPos.x < 0 || newPos.x >= dungeon.length || 
            newPos.y < 0 || newPos.y >= dungeon[0].length) {
            return false;
        }
        return dungeon[newPos.x][newPos.y] != '#';
    }

    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
        if (health <= 0) {
            alive = false;
        }
    }

    public Point getPosition() { return position; }
    public int getHealth() { return health; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public boolean isAlive() { return alive; }
    public int getExperienceValue() { return experienceValue; }
}