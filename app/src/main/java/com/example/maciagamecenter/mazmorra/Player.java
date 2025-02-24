package com.example.maciagamecenter.mazmorra;

import android.graphics.Point;

public class Player {
    private Point position;
    private int health;
    private int maxHealth;
    private int attack;
    private int defense;
    private int experience;
    private int level;

    public Player(Point startPosition) {
        this.position = startPosition;
        this.health = 100;
        this.maxHealth = 100;
        this.attack = 10;
        this.defense = 5;
        this.experience = 0;
        this.level = 1;
    }

    public boolean move(Direction direction, char[][] dungeon) {
        Point newPosition = new Point(position.x, position.y);
        
        switch (direction) {
            case UP:
                newPosition.x--;
                break;
            case DOWN:
                newPosition.x++;
                break;
            case LEFT:
                newPosition.y--;
                break;
            case RIGHT:
                newPosition.y++;
                break;
        }

        if (isValidMove(newPosition, dungeon)) {
            position = newPosition;
            return true;
        }
        return false;
    }

    private boolean isValidMove(Point newPos, char[][] dungeon) {
        if (newPos.x < 0 || newPos.x >= dungeon.length || 
            newPos.y < 0 || newPos.y >= dungeon[0].length) {
            return false;
        }
        return dungeon[newPos.x][newPos.y] != '#';
    }

    public Point getPosition() {
        return position;
    }

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getLevel() { return level; }

    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
    }

    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }

    public void addExperience(int exp) {
        experience += exp;
        checkLevelUp();
    }

    private void checkLevelUp() {
        int expNeeded = level * 100;
        if (experience >= expNeeded) {
            level++;
            maxHealth += 20;
            health = maxHealth;
            attack += 5;
            defense += 3;
            experience -= expNeeded;
        }
    }
}