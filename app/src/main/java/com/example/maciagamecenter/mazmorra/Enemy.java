package com.example.maciagamecenter.mazmorra;

import android.graphics.Point;

public class Enemy {
    private Point position;
    private int health;
    private int maxHealth;
    private int attackBonus;
    private int experienceValue;
    private static int turnCounter = 0;
    private static final int MOVE_FREQUENCY = 2;
    private static final int BASE_HEALTH = 10;  // Cambiado de 3 a 10 para volver al valor original
    private static final int BASE_XP = 20;  // Añadir esta línea
    
    public Enemy(Point position, int level) {
        this.position = position;
        this.health = BASE_HEALTH + level;
        this.maxHealth = this.health;
        this.attackBonus = (level - 1) / 2;
        this.experienceValue = BASE_XP + (level * 10);
    }
    
    // Getter methods
    public Point getPosition() { return position; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getAttackBonus() { return attackBonus; }
    public int getExperienceValue() { return experienceValue; }
    
    // Setter method
    public void setPosition(Point position) { this.position = position; }
    
    // Other methods
    public boolean isAlive() { return health > 0; }
    public void takeDamage(int damage) { health = Math.max(0, health - damage); }
    
    public static boolean shouldMove() { return turnCounter % MOVE_FREQUENCY == 0; }
    public static void incrementTurn() { turnCounter++; }
    
    public void moveTowards(Point target, char[][] dungeon) {
        if (!isAlive()) return;  // Changed from isAlive to isAlive()

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