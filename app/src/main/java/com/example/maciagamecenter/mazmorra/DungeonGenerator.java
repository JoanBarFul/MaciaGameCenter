package com.example.maciagamecenter.mazmorra;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonGenerator {

    private static Random random = new Random();
    private static int gridSize;
    private static char[][] dungeon;

    public static char[][] createDungeonWithFixedRooms(int gridSize, int minRooms, int maxRooms) {
        DungeonGenerator.gridSize = gridSize;
        dungeon = new char[gridSize][gridSize];
        List<int[]> rooms = new ArrayList<>();

        // Inicializar la matriz con paredes
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                dungeon[i][j] = '#'; // Inicializamos todo como pared
            }
        }

        // Crear habitación de entrada (3x3)
        int[] entranceRoom = createRoom(3, 3, 1, 1);
        while (!addRoom(entranceRoom)) {
            entranceRoom = createRoom(3, 3, 1, 1); // Volver a crear habitación si se solapa
        }
        rooms.add(entranceRoom);

        // Colocar la entrada (E) en el centro de la habitación de entrada
        int entranceX = entranceRoom[0] + (entranceRoom[2] - entranceRoom[0]) / 2;
        int entranceY = entranceRoom[1] + (entranceRoom[3] - entranceRoom[1]) / 2;
        dungeon[entranceX][entranceY] = 'E'; // Marcar entrada

        // Crear habitaciones adicionales
        int numRooms = random.nextInt(maxRooms - minRooms + 1) + minRooms;
        for (int i = 0; i < numRooms; i++) {
            while (true) {
                int width = random.nextInt(4) + 3;
                int height = random.nextInt(4) + 3;
                int[] newRoom = createRoom(width, height, null, null);
                if (!isOverlapping(newRoom, rooms) && addRoom(newRoom)) {
                    rooms.add(newRoom);
                    break;
                }
            }
        }

        // Conectar habitaciones (sin puertas)
        for (int i = 1; i < rooms.size(); i++) {
            connectRooms(rooms.get(i - 1), rooms.get(i));
        }

        // Buscar la habitación más alejada de la entrada para colocar la salida (S)
        int[] farthestRoom = findFarthestRoom(entranceX, entranceY, rooms);

        // Colocar la salida (S) en el centro de la habitación más alejada
        int exitX = farthestRoom[0] + (farthestRoom[2] - farthestRoom[0]) / 2;
        int exitY = farthestRoom[1] + (farthestRoom[3] - farthestRoom[1]) / 2;
        dungeon[exitX][exitY] = 'S'; // Marcar salida

        // Comprobar si faltan la entrada o salida, si falta alguno, volver a generar
        if (!hasEntranceAndExit()) {
            return createDungeonWithFixedRooms(gridSize, minRooms, maxRooms);
        }

        // Colocar entre 1 y 3 cofres, asegurando un cofre por habitación como máximo
        placeChests(rooms, entranceRoom, farthestRoom);

        return dungeon;
    }

    private static boolean hasEntranceAndExit() {
        boolean hasEntrance = false;
        boolean hasExit = false;

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (dungeon[i][j] == 'E') {
                    hasEntrance = true;
                }
                if (dungeon[i][j] == 'S') {
                    hasExit = true;
                }
                if (hasEntrance && hasExit) {
                    return true; // Ya no necesitamos seguir buscando
                }
            }
        }

        return hasEntrance && hasExit;
    }

    private static int[] createRoom(int width, int height, Integer x, Integer y) {
        if (x == null) {
            x = random.nextInt(gridSize - height - 1) + 1;
        }
        if (y == null) {
            y = random.nextInt(gridSize - width - 1) + 1;
        }
        return new int[]{x, y, x + height, y + width};
    }

    private static boolean addRoom(int[] room) {
        int x1 = room[0], y1 = room[1], x2 = room[2], y2 = room[3];
        if (x2 > gridSize || y2 > gridSize) return false;

        for (int i = x1; i < x2; i++) {
            for (int j = y1; j < y2; j++) {
                if (dungeon[i][j] != '#') return false;
            }
        }

        for (int i = x1; i < x2; i++) {
            for (int j = y1; j < y2; j++) {
                dungeon[i][j] = '.'; // Reemplazar con suelo (pasillo)
            }
        }
        return true;
    }

    private static boolean isOverlapping(int[] room, List<int[]> rooms) {
        int x1 = room[0], y1 = room[1], x2 = room[2], y2 = room[3];
        for (int[] other : rooms) {
            int ox1 = other[0], oy1 = other[1], ox2 = other[2], oy2 = other[3];
            if (!(x2 <= ox1 || x1 >= ox2 || y2 <= oy1 || y1 >= oy2)) {
                return true;
            }
        }
        return false;
    }

    private static void connectRooms(int[] r1, int[] r2) {
        int x1 = random.nextInt(r1[2] - r1[0]) + r1[0];
        int y1 = random.nextInt(r1[3] - r1[1]) + r1[1];
        int x2 = random.nextInt(r2[2] - r2[0]) + r2[0];
        int y2 = random.nextInt(r2[3] - r2[1]) + r2[1];

        if (random.nextBoolean()) {
            for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                dungeon[x][y1] = '.';
            }
            for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                dungeon[x2][y] = '.';
            }
        } else {
            for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                dungeon[x1][y] = '.';
            }
            for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                dungeon[x][y2] = '.';
            }
        }
    }

    private static int[] findFarthestRoom(int entranceX, int entranceY, List<int[]> rooms) {
        int maxDistance = 0;
        int[] farthestRoom = null;

        for (int[] room : rooms) {
            int roomCenterX = room[0] + (room[2] - room[0]) / 2;
            int roomCenterY = room[1] + (room[3] - room[1]) / 2;

            int distance = Math.abs(roomCenterX - entranceX) + Math.abs(roomCenterY - entranceY);
            if (distance > maxDistance) {
                maxDistance = distance;
                farthestRoom = room;
            }
        }

        return farthestRoom;
    }

    private static void placeChests(List<int[]> rooms, int[] entranceRoom, int[] exitRoom) {
        int numChests = random.nextInt(3) + 1;
        int chestsPlaced = 0;

        List<int[]> eligibleRooms = new ArrayList<>();
        for (int[] room : rooms) {
            if (room != entranceRoom && room != exitRoom) {
                eligibleRooms.add(room);
            }
        }

        while (chestsPlaced < numChests && !eligibleRooms.isEmpty()) {
            int roomIndex = random.nextInt(eligibleRooms.size());
            int[] room = eligibleRooms.remove(roomIndex);

            int chestX = random.nextInt(room[2] - room[0]) + room[0];
            int chestY = random.nextInt(room[3] - room[1]) + room[1];

            if (dungeon[chestX][chestY] == '.') {
                dungeon[chestX][chestY] = 'C';
                chestsPlaced++;
            }
        }
    }
}
