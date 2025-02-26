package com.example.maciagamecenter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;  // Añadimos este import

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GameCenter.db";
    private static final int DATABASE_VERSION = 4; // Incrementado de 3 a 4
    private static String currentUsername = null;  // Keep only one declaration here
    
    // Database table and column names
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PROFILE_IMAGE = "profile_image";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_LEVEL = "level";
    public static final String COLUMN_XP = "experience_points";
    public static final String COLUMN_GAMES_PLAYED = "games_played";
    public static final String COLUMN_WINS = "wins";
    public static final String TABLE_GAME_SCORES = "game_scores";
    public static final String COLUMN_GAME_NAME = "game_name";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_PLAYER_NAME = "player_name";
    public static final String COLUMN_DATE = "date";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, "
            + COLUMN_PASSWORD + " TEXT NOT NULL, "
            + COLUMN_PROFILE_IMAGE + " TEXT, "
            + COLUMN_EMAIL + " TEXT, "
            + COLUMN_LEVEL + " INTEGER DEFAULT 1, "
            + COLUMN_XP + " INTEGER DEFAULT 0, "
            + COLUMN_GAMES_PLAYED + " INTEGER DEFAULT 0, "
            + COLUMN_WINS + " INTEGER DEFAULT 0)";
    private static final String CREATE_GAME_SCORES_TABLE = "CREATE TABLE " + TABLE_GAME_SCORES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_GAME_NAME + " TEXT NOT NULL, "
            + COLUMN_SCORE + " INTEGER NOT NULL, "
            + COLUMN_PLAYER_NAME + " TEXT NOT NULL, "
            + COLUMN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_GAME_SCORES_TABLE);
        Log.d("DatabaseHelper", "Base de datos creada");
        
        // Usuario predeterminado con datos completos
        ContentValues userValues = new ContentValues();
        userValues.put(COLUMN_USERNAME, "Joan");
        userValues.put(COLUMN_PASSWORD, "Android");
        userValues.put(COLUMN_PROFILE_IMAGE, "");
        userValues.put(COLUMN_EMAIL, "joan@example.com");
        userValues.put(COLUMN_LEVEL, 5);
        userValues.put(COLUMN_XP, 2500);
        userValues.put(COLUMN_GAMES_PLAYED, 10);
        userValues.put(COLUMN_WINS, 7);
        
        long result = db.insert(TABLE_USERS, null, userValues);
        Log.d("DatabaseHelper", "Usuario predeterminado creado: " + (result != -1));
        
        // Add default scores
        ContentValues scoreValues = new ContentValues();
        
        // Scores for 2024
        scoreValues.put(COLUMN_GAME_NAME, "2024");
        scoreValues.put(COLUMN_PLAYER_NAME, "Joan");
        scoreValues.put(COLUMN_SCORE, 1000);
        db.insert(TABLE_GAME_SCORES, null, scoreValues);
        
        scoreValues.clear();
        scoreValues.put(COLUMN_GAME_NAME, "2024");
        scoreValues.put(COLUMN_PLAYER_NAME, "Test1");
        scoreValues.put(COLUMN_SCORE, 800);
        db.insert(TABLE_GAME_SCORES, null, scoreValues);
        
        // Scores for Dungeon
        scoreValues.clear();
        scoreValues.put(COLUMN_GAME_NAME, "Dungeon");
        scoreValues.put(COLUMN_PLAYER_NAME, "Joan");
        scoreValues.put(COLUMN_SCORE, 500);
        db.insert(TABLE_GAME_SCORES, null, scoreValues);
        
        scoreValues.clear();
        scoreValues.put(COLUMN_GAME_NAME, "Dungeon");
        scoreValues.put(COLUMN_PLAYER_NAME, "Test2");
        scoreValues.put(COLUMN_SCORE, 300);
        db.insert(TABLE_GAME_SCORES, null, scoreValues);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar tablas existentes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME_SCORES);
        // Recrear tablas
        onCreate(db);
    }
    // Add the method here at class level
    public static void setCurrentUsername(String username) {
        currentUsername = username;
        Log.d("DatabaseHelper", "Current username set to: " + username);
    }
    // Fix the addUser method by removing the misplaced setCurrentUsername method
    public boolean addUser(String username, String password, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PROFILE_IMAGE, imageUri);
        
        // Add this line to perform the insert
        long result = db.insert(TABLE_USERS, null, values);
        Log.d("DatabaseHelper", "Nuevo usuario agregado - Username: " + username + ", Resultado: " + (result != -1));
        
        // Verification code
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_PROFILE_IMAGE}, 
            null, null, null, null, null);
        Log.d("DatabaseHelper", "Total usuarios en la base de datos: " + cursor.getCount());
        
        while(cursor.moveToNext()) {
            String dbUsername = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
            String dbImage = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_IMAGE));
            Log.d("DatabaseHelper", "Usuario encontrado: " + dbUsername + ", Imagen: " + dbImage);
        }
        cursor.close();
        
        return result != -1;
    }
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_USERNAME + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        
        Log.d("DatabaseHelper", "Intentando login con - Username: " + username + ", Password: " + password);
        
        // Verificar todos los usuarios en la base de datos primero
        Cursor allUsers = db.query(TABLE_USERS, new String[]{COLUMN_USERNAME, COLUMN_PASSWORD}, 
            null, null, null, null, null);
        Log.d("DatabaseHelper", "=== Usuarios en la base de datos ===");
        Log.d("DatabaseHelper", "Total usuarios encontrados: " + allUsers.getCount());
        while(allUsers.moveToNext()) {
            String dbUsername = allUsers.getString(allUsers.getColumnIndexOrThrow(COLUMN_USERNAME));
            String dbPassword = allUsers.getString(allUsers.getColumnIndexOrThrow(COLUMN_PASSWORD));
            Log.d("DatabaseHelper", "Usuario encontrado -> Username: " + dbUsername + ", Password: " + dbPassword);
        }
        allUsers.close();
        
        // Ahora verificar el login
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        
        Log.d("DatabaseHelper", "Resultado de la verificación: " + (count > 0));
        return count > 0;
    }
    public void getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, 
            new String[]{COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_PROFILE_IMAGE}, 
            null, null, null, null, null);
        
        Log.d("DatabaseHelper", "=== Contenido de la base de datos ===");
        Log.d("DatabaseHelper", "Total usuarios: " + cursor.getCount());
        
        while(cursor.moveToNext()) {
            String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_IMAGE));
            Log.d("DatabaseHelper", "Usuario: " + username + 
                                      ", Password: " + password + 
                                      ", Image: " + imageUri);
        }
        cursor.close();
    }
    public void saveDungeonScore(int score) {
        if (currentUsername == null) {
            Log.e("DatabaseHelper", "No user logged in");
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues scoreValues = new ContentValues();
        scoreValues.put(COLUMN_GAME_NAME, "Dungeon");
        scoreValues.put(COLUMN_SCORE, score);
        scoreValues.put(COLUMN_PLAYER_NAME, currentUsername);
        
        long result = db.insert(TABLE_GAME_SCORES, null, scoreValues);
        Log.d("DatabaseHelper", "Dungeon score saved: " + result + " for user: " + currentUsername + " with score: " + score);
        
        // Update user stats
        ContentValues userValues = new ContentValues();
        userValues.put(COLUMN_GAMES_PLAYED, "games_played + 1");
        userValues.put(COLUMN_XP, "experience_points + " + score);

        String whereClause = COLUMN_USERNAME + " = ?";
        String[] whereArgs = {currentUsername};
        
        db.update(TABLE_USERS, userValues, whereClause, whereArgs);
        
        db.close();
    }

    // Add this method to get scores
    public Cursor getGameScores(String gameName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_PLAYER_NAME, COLUMN_SCORE, COLUMN_DATE};
        String selection = COLUMN_GAME_NAME + " = ?";
        String[] selectionArgs = {gameName};
        String orderBy = COLUMN_SCORE + " DESC";
        
        return db.query(TABLE_GAME_SCORES, columns, selection, selectionArgs, 
                       null, null, orderBy);
    }
}