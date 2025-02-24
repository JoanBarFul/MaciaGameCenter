package com.example.maciagamecenter.mazmorra;

import java.util.Random;

public class DungeonLevel {
    private static final int WALL = 0;
    private static final int FLOOR = 1;
    
    private int width;
    private int height;
    private int[][] map;
    private Random random;

    public DungeonLevel(int width, int height) {
        this.width = width;
        this.height = height;
        this.map = new int[height][width];
        this.random = new Random();
        generateLevel();
    }

    private void generateLevel() {
        // Initialize with walls
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map[y][x] = WALL;
            }
        }

        // Create rooms and corridors
        createRooms();
    }

    private void createRooms() {
        int numRooms = random.nextInt(5) + 5; // 5-10 rooms
        for (int i = 0; i < numRooms; i++) {
            int roomWidth = random.nextInt(5) + 4; // 4-8 width
            int roomHeight = random.nextInt(5) + 4; // 4-8 height
            int roomX = random.nextInt(width - roomWidth - 2) + 1;
            int roomY = random.nextInt(height - roomHeight - 2) + 1;

            for (int y = roomY; y < roomY + roomHeight; y++) {
                for (int x = roomX; x < roomX + roomWidth; x++) {
                    map[y][x] = FLOOR;
                }
            }
        }
    }

    public boolean isWalkable(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }
        return map[y][x] == FLOOR;
    }

    public int[][] getMap() {
        return map;
    }
}