package com.example.maciagamecenter.mazmorra;

import android.graphics.Point;

public class Player {
    // Add this with other class variables at the top
    private Point position;
    private int health;
    private int maxHealth;
    private int attack;
    private int defense;
    private int experience;
    private int level = 1;  // Add this line
    private static final int INITIAL_HEALTH = 40;
    private float rotation = 0;  // 0: derecha, 90: abajo, 180: izquierda, 270: arriba
    // Eliminar el constructor sin argumentos si existe
    
    public Player(Point startPosition) {
        this.position = startPosition;
        this.maxHealth = INITIAL_HEALTH;
        this.health = maxHealth;
        this.attack = 5;
        this.defense = 2;
        this.experience = 0;
        this.level = 1;
        this.rotation = 0;  // Inicializar la rotación
    }
    
    public float getRotation() {
        return rotation;
    }
    
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
    
    public boolean move(Direction direction, char[][] dungeon) {
        Point newPosition = new Point(position.x, position.y);
        
        switch (direction) {
            case UP:
                newPosition.x--;
                rotation = 270;
                break;
            case DOWN:
                newPosition.x++;
                rotation = 90;
                break;
            case LEFT:
                newPosition.y--;
                rotation = 180;
                break;
            case RIGHT:
                newPosition.y++;
                rotation = 0;
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
    
    public void setPosition(Point position) {
        this.position = position;
    }
    
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getLevel() { return level; }
    
    public void setHealth(int health) {
        this.health = Math.min(health, maxHealth);
    }
    
    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
    }
    private int attackBonus = 0;  // Add this field
    
    public void increaseDamage(int amount) {
        attackBonus += amount;  // Accumulate the bonus instead of setting it
    }
    
    public int getAttackBonus() {
        return attackBonus;
    }
    public void heal(int amount) {
        this.health = Math.min(this.health + amount, this.maxHealth);
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