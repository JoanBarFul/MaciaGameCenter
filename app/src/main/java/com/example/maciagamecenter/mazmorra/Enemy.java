package com.example.maciagamecenter.mazmorra;

import android.graphics.Point;

public class Enemy {
    private Point position;
    private int speed;
    private int health;
    private int damage;
    
    public Enemy(Point startPosition, int level) {
        this.position = startPosition;
        this.speed = Math.min(level, 5);
        this.health = 50 + (level * 10);
        this.damage = 5 + (level * 2);
    }
    
    public void moveTowards(Point playerPosition, char[][] dungeon) {
        // Solo mover si tiene velocidad suficiente
        for(int i = 0; i < speed; i++) {
            Point nextStep = calculateNextStep(playerPosition, dungeon);
            if(nextStep != null) {
                position = nextStep;
            }
        }
    }

    private Point calculateNextStep(Point playerPosition, char[][] dungeon) {
        int dx = Integer.compare(playerPosition.x, position.x);
        int dy = Integer.compare(playerPosition.y, position.y);
        
        // Intentar moverse en la dirección con mayor diferencia primero
        if(Math.abs(playerPosition.x - position.x) > Math.abs(playerPosition.y - position.y)) {
            if(isValidMove(new Point(position.x + dx, position.y), dungeon)) {
                return new Point(position.x + dx, position.y);
            } else if(isValidMove(new Point(position.x, position.y + dy), dungeon)) {
                return new Point(position.x, position.y + dy);
            }
        } else {
            if(isValidMove(new Point(position.x, position.y + dy), dungeon)) {
                return new Point(position.x, position.y + dy);
            } else if(isValidMove(new Point(position.x + dx, position.y), dungeon)) {
                return new Point(position.x + dx, position.y);
            }
        }
        return null;
    }

    private boolean isValidMove(Point newPosition, char[][] dungeon) {
        // Verificar límites del mapa
        if(newPosition.x < 0 || newPosition.x >= dungeon.length ||
           newPosition.y < 0 || newPosition.y >= dungeon[0].length) {
            return false;
        }
        
        // Verificar si la nueva posición es transitable
        char cell = dungeon[newPosition.x][newPosition.y];
        return cell == '.' || cell == 'E' || cell == 'S';
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

    public void setPosition(Point position) {
        this.position = position;
    }

    public int getHealth() {
        return health;
    }
}