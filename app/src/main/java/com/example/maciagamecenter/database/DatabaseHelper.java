package com.example.maciagamecenter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;  // Añadimos este import

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GameCenter.db";
    private static final int DATABASE_VERSION = 2; // Incrementar versión

    // Tabla Usuarios
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PROFILE_IMAGE = "profile_image";

    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, "
            + COLUMN_PASSWORD + " TEXT NOT NULL, "
            + COLUMN_PROFILE_IMAGE + " TEXT)";
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        Log.d("DatabaseHelper", "Base de datos creada");
        
        // Insertar usuario predeterminado
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, "Joan");
        values.put(COLUMN_PASSWORD, "Android");
        values.put(COLUMN_PROFILE_IMAGE, ""); // Añadir campo de imagen vacío
        long result = db.insert(TABLE_USERS, null, values);
        Log.d("DatabaseHelper", "Usuario predeterminado creado: " + (result != -1));
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
    public boolean addUser(String username, String password, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PROFILE_IMAGE, imageUri);
        
        long result = db.insert(TABLE_USERS, null, values);
        Log.d("DatabaseHelper", "Nuevo usuario agregado - Username: " + username + ", Resultado: " + (result != -1));
        
        // Verificar usuarios en la base de datos después de la inserción
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_PROFILE_IMAGE}, 
            null, null, null, null, null);
        Log.d("DatabaseHelper", "Total usuarios en la base de datos: " + cursor.getCount());
        // En el método addUser
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
}