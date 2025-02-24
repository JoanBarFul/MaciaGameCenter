package com.example.maciagamecenter.mazmorra;

import android.graphics.Point;

public class Player {
    private Point position;
    private int health;
    private int damage;
    
    public Player(Point startPosition) {
        this.position = startPosition;
        this.health = 100;
        this.damage = 10;
    }
    
    public boolean move(Direction direction, char[][] dungeon) {
        Point newPosition = calculateNewPosition(direction);
        if (isValidMove(newPosition, dungeon)) {
            position = newPosition;
            return true;
        }
        return false;
    }
    
    private Point calculateNewPosition(Direction direction) {
        Point newPosition = new Point(position.x, position.y);
        switch (direction) {
            case UP:
                newPosition.y--;
                break;
            case DOWN:
                newPosition.y++;
                break;
            case LEFT:
                newPosition.x--;
                break;
            case RIGHT:
                newPosition.x++;
                break;
        }
        return newPosition;
    }
    
    private boolean isValidMove(Point newPosition, char[][] dungeon) {
        if (newPosition.x < 0 || newPosition.x >= dungeon.length ||
            newPosition.y < 0 || newPosition.y >= dungeon[0].length) {
            return false;
        }
        
        char cell = dungeon[newPosition.x][newPosition.y];
        return cell == '.' || cell == 'E' || cell == 'S' || cell == 'C' || cell == 'c';
    }
    
    public Point getPosition() {
        return position;
    }
    
    public void takeDamage(int damage) {
        health -= damage;
    }
    
    public boolean isAlive() {
        return health > 0;
    }
    
    public int getDamage() {
        return damage;
    }
    
    public int getHealth() {
        return health;
    }
}