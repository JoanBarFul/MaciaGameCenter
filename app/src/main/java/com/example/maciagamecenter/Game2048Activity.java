package com.example.maciagamecenter;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import com.example.maciagamecenter.database.DatabaseHelper;
import com.example.maciagamecenter.databinding.ActivityGame2048Binding;
import java.util.List;
import java.util.ArrayList;
import android.graphics.Point;
import java.util.Random;
import android.util.Log;
public class Game2048Activity extends AppCompatActivity {
    private int[][] board = new int[4][4];
    private TextView[][] cellViews = new TextView[4][4];
    private int score = 0;
    private DatabaseHelper dbHelper;
    private GestureDetector gestureDetector;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private int[][] previousBoard = new int[4][4];
    private int previousScore = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2048);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("2048");
        }
        
        // Inicializar los botones primero
        findViewById(R.id.new_game_button).setOnClickListener(v -> resetGame());
        findViewById(R.id.undo_button).setOnClickListener(v -> undoMove());
        findViewById(R.id.end_game_button).setOnClickListener(v -> endGameEarly());
        
        // Inicializar el grid después
        dbHelper = new DatabaseHelper(this);
        setupGestureDetector();
        initializeBoard();
    }
    private void initializeBoard() {
        // Initialize cellViews array with TextViews from the layout
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                String cellId = "cell_" + i + "_" + j;  // Cambiar formato del ID
                int resId = getResources().getIdentifier(cellId, "id", getPackageName());
                cellViews[i][j] = findViewById(resId);
                if (cellViews[i][j] == null) {
                    Log.e("Game2048", "Cell not found: " + cellId);
                } else {
                    // Establecer propiedades iniciales de la celda
                    cellViews[i][j].setBackgroundColor(getResources().getColor(R.color.cell_empty));
                    cellViews[i][j].setTextSize(24);
                    cellViews[i][j].setTextColor(getResources().getColor(android.R.color.black));
                    cellViews[i][j].setGravity(android.view.Gravity.CENTER); // Centrar el texto
                    cellViews[i][j].setPadding(8, 8, 8, 8);
                    
                    // Establecer dimensiones mínimas para las celdas
                    cellViews[i][j].setMinWidth(96);
                    cellViews[i][j].setMinHeight(96);
                }
            }
        }
        
        resetGame();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void undoMove() {
        // Verificar si hay un estado anterior válido
        boolean hasValidPreviousState = false;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (previousBoard[i][j] != 0) {
                    hasValidPreviousState = true;
                    break;
                }
            }
            if (hasValidPreviousState) break;
        }

        if (hasValidPreviousState) {
            // Restaurar el estado anterior
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    board[i][j] = previousBoard[i][j];
                }
            }
            score = previousScore;
            updateUI();
            
            // Limpiar el estado anterior después de usarlo
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    previousBoard[i][j] = 0;
                }
            }
            previousScore = 0;
        }
    }
    
    // Modify your movement methods to save the state before moving
    private void savePreviousState() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                previousBoard[i][j] = board[i][j];
            }
        }
        previousScore = score;
    }
    private void updateUI() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                TextView cell = cellViews[i][j];
                if (cell == null) continue; // Evitar NPE
                
                int value = board[i][j];
                if (value == 0) {
                    cell.setText("");
                    cell.setBackgroundColor(getResources().getColor(R.color.cell_empty));
                } else {
                    cell.setText(String.valueOf(value));
                    cell.setBackgroundColor(getCellColor(value));
                }
            }
        }
        // Usar try-catch para debuggear el problema
        try {
            TextView scoreText = findViewById(R.id.scoreText);
            if (scoreText != null) {
                scoreText.setText("Score: " + score);
            } else {
                Log.e("Game2048", "scoreText is null");
            }
        } catch (Exception e) {
            Log.e("Game2048", "Error updating score: " + e.getMessage());
        }
    }

    private void resetGame() {
        score = 0;
        previousScore = 0;
        
        // Clear board
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                board[i][j] = 0;
                previousBoard[i][j] = 0;
            }
        }
        
        // Add initial tiles
        addNewTile();
        addNewTile();
        
        // Update UI al final
        try {
            TextView scoreText = findViewById(R.id.scoreText);
            if (scoreText != null) {
                scoreText.setText("Score: 0");
            }
            updateUI();
        } catch (Exception e) {
            Log.e("Game2048", "Error in resetGame: " + e.getMessage());
        }
    }
    private int getCellColor(int value) {
        switch (value) {
            case 2: return getResources().getColor(R.color.cell_2);
            case 4: return getResources().getColor(R.color.cell_4);
            case 8: return getResources().getColor(R.color.cell_8);
            case 16: return getResources().getColor(R.color.cell_16);
            case 32: return getResources().getColor(R.color.cell_32);
            case 64: return getResources().getColor(R.color.cell_64);
            case 128: return getResources().getColor(R.color.cell_128);
            case 256: return getResources().getColor(R.color.cell_256);
            case 512: return getResources().getColor(R.color.cell_512);
            case 1024: return getResources().getColor(R.color.cell_1024);
            case 2048: return getResources().getColor(R.color.cell_2048);
            default: return getResources().getColor(R.color.cell_empty);
        }
    }
    // Add savePreviousState() at the beginning of moveLeft, moveRight, moveUp, and moveDown methods
    private void moveRight() {
        savePreviousState();
        boolean moved = false;
        for (int i = 0; i < 4; i++) {
            // First, merge tiles
            for (int j = 2; j >= 0; j--) {
                if (board[i][j] != 0) {
                    int k = j;
                    while (k + 1 < 4 && board[i][k + 1] == 0) {
                        k++;
                    }
                    if (k + 1 < 4 && board[i][k + 1] == board[i][j]) {
                        board[i][k + 1] *= 2;
                        score += board[i][k + 1];
                        board[i][j] = 0;
                        moved = true;
                    }
                }
            }
            // Then, move all tiles to the rightmost possible position
            for (int j = 2; j >= 0; j--) {
                if (board[i][j] != 0) {
                    int k = j;
                    while (k + 1 < 4 && board[i][k + 1] == 0) {
                        board[i][k + 1] = board[i][k];
                        board[i][k] = 0;
                        k++;
                        moved = true;
                    }
                }
            }
        }
        if (moved) {
            addNewTile();
            updateUI();
            if (isGameOver()) {
                gameOver();
            }
        }
    }
    private void addNewTile() {
            List<Point> emptyCells = new ArrayList<>();
            
            // Find all empty cells
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (board[i][j] == 0) {
                        emptyCells.add(new Point(i, j));
                    }
                }
            }
            
            if (!emptyCells.isEmpty()) {
                // Randomly select an empty cell
                Random random = new Random();
                Point selectedCell = emptyCells.get(random.nextInt(emptyCells.size()));
                
                // Set the value (90% chance for 2, 10% chance for 4)
                board[selectedCell.x][selectedCell.y] = random.nextFloat() < 0.9f ? 2 : 4;
            }
        }
    private void moveLeft() {
        savePreviousState();  // Add this line
        boolean moved = false;
        for (int i = 0; i < 4; i++) {
            // First, merge tiles
            for (int j = 1; j < 4; j++) {
                if (board[i][j] != 0) {
                    int k = j;
                    while (k - 1 >= 0 && board[i][k - 1] == 0) {
                        k--;
                    }
                    if (k - 1 >= 0 && board[i][k - 1] == board[i][j]) {
                        board[i][k - 1] *= 2;
                        score += board[i][k - 1];
                        board[i][j] = 0;
                        moved = true;
                    }
                }
            }
            // Then, move all tiles to the leftmost possible position
            for (int j = 1; j < 4; j++) {
                if (board[i][j] != 0) {
                    int k = j;
                    while (k - 1 >= 0 && board[i][k - 1] == 0) {
                        board[i][k - 1] = board[i][k];
                        board[i][k] = 0;
                        k--;
                        moved = true;
                    }
                }
            }
        }
        if (moved) {
            addNewTile();
            updateUI();
            if (isGameOver()) {
                gameOver();
            }
        }
    }

    private void moveUp() {
        savePreviousState();  // Add this line
        boolean moved = false;
        for (int j = 0; j < 4; j++) {
            // First, merge tiles
            for (int i = 1; i < 4; i++) {
                if (board[i][j] != 0) {
                    int k = i;
                    while (k - 1 >= 0 && board[k - 1][j] == 0) {
                        k--;
                    }
                    if (k - 1 >= 0 && board[k - 1][j] == board[i][j]) {
                        board[k - 1][j] *= 2;
                        score += board[k - 1][j];
                        board[i][j] = 0;
                        moved = true;
                    }
                }
            }
            // Then, move all tiles to the topmost possible position
            for (int i = 1; i < 4; i++) {
                if (board[i][j] != 0) {
                    int k = i;
                    while (k - 1 >= 0 && board[k - 1][j] == 0) {
                        board[k - 1][j] = board[k][j];
                        board[k][j] = 0;
                        k--;
                        moved = true;
                    }
                }
            }
        }
        if (moved) {
            addNewTile();
            updateUI();
            if (isGameOver()) {
                gameOver();
            }
        }
    }

    private void moveDown() {
        savePreviousState();  // Add this line
        boolean moved = false;
        for (int j = 0; j < 4; j++) {
            // First, merge tiles
            for (int i = 2; i >= 0; i--) {
                if (board[i][j] != 0) {
                    int k = i;
                    while (k + 1 < 4 && board[k + 1][j] == 0) {
                        k++;
                    }
                    if (k + 1 < 4 && board[k + 1][j] == board[i][j]) {
                        board[k + 1][j] *= 2;
                        score += board[k + 1][j];
                        board[i][j] = 0;
                        moved = true;
                    }
                }
            }
            // Then, move all tiles to the bottommost possible position
            for (int i = 2; i >= 0; i--) {
                if (board[i][j] != 0) {
                    int k = i;
                    while (k + 1 < 4 && board[k + 1][j] == 0) {
                        board[k + 1][j] = board[k][j];
                        board[k][j] = 0;
                        k++;
                        moved = true;
                    }
                }
            }
        }
        if (moved) {
            addNewTile();
            updateUI();
            if (isGameOver()) {
                gameOver();
            }
        }
    }
    private void setupGestureDetector() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            moveRight();
                        } else {
                            moveLeft();
                        }
                        return true;
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            moveDown();
                        } else {
                            moveUp();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void endGameEarly() {
        if (score > 0) {
            gameOver();
        }
        resetGame();
    }
    private boolean isGameOver() {
        // Check for empty cells
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        
        // Check for possible merges horizontally
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == board[i][j + 1]) {
                    return false;
                }
            }
        }
        
        // Check for possible merges vertically
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 3; i++) {
                if (board[i][j] == board[i + 1][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    private void gameOver() {
                // Save high score if needed
                SharedPreferences prefs = getSharedPreferences("GamePrefs", MODE_PRIVATE);
                int highScore = prefs.getInt("highScore", 0);
                if (score > highScore) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("highScore", score);
                    editor.apply();
                }
        
                // Save to database
                ContentValues values = new ContentValues();
                values.put("game_name", "2048");
                values.put("score", score);
                values.put("date", System.currentTimeMillis());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.insert("scores", null, values);
                db.close();
        
                // Show game over message
                Toast.makeText(this, "Game Over! Score: " + score, Toast.LENGTH_LONG).show();
            }
    }