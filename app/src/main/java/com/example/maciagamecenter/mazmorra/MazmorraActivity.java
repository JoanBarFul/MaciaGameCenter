// Actualizar el nombre de la clase y el package
package com.example.maciagamecenter.mazmorra;

// Añadir esta importación
import com.example.maciagamecenter.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Point;
import java.util.List;
import java.util.ArrayList;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
// Cambiar el nombre de la clase de MainActivity a MazmorraActivity
public class MazmorraActivity extends AppCompatActivity {
    private char[][] dungeon;
    private Player player;
    private List<Enemy> enemies;
    private int currentLevel = 1;
    private boolean hasKey = false;
    private Point keyLocation;
    private Point exitLocation;
    private static final int INITIAL_ENEMIES = 2;
    private TextView levelText;
    private TextView healthText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mazmorra);  // Cambiar esto
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Dungeon");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        levelText = findViewById(R.id.level_text);
        healthText = findViewById(R.id.health_text);
        
        initializeLevel();
        setupControls();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void initializeLevel() {
        // Generar mazmorra
        dungeon = DungeonGenerator.createDungeonWithFixedRooms(20, 5, 10);
        
        // Inicializar jugador en la entrada
        player = new Player(findEntrance(dungeon));
        
        // Generar enemigos
        enemies = generateEnemies(currentLevel);
        
        // Colocar llave en un cofre aleatorio
        placeKeyInRandomChest(dungeon);
        updateGameUI();
        updateStatusTexts();
    }

    private void updateStatusTexts() {
        levelText.setText("Level: " + currentLevel);
        healthText.setText("Health: " + player.getHealth() + "/" + player.getMaxHealth());
    }

    private void setupControls() {
        GridLayout mapGrid = findViewById(R.id.map_grid);
        mapGrid.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeRight() { handlePlayerMove(Direction.RIGHT); }
            @Override
            public void onSwipeLeft() { handlePlayerMove(Direction.LEFT); }
            @Override
            public void onSwipeUp() { handlePlayerMove(Direction.UP); }
            @Override
            public void onSwipeDown() { handlePlayerMove(Direction.DOWN); }
        });
    }

    private void updateGameUI() {
        GridLayout mapGrid = findViewById(R.id.map_grid);
        mapGrid.removeAllViews();
        
        // Set the number of columns in the grid
        mapGrid.setColumnCount(dungeon[0].length);
        mapGrid.setRowCount(dungeon.length);

        // Calculate cell size based on screen width and height
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = mapGrid.getHeight();
        
        int cellSize = Math.min(
            screenWidth / dungeon[0].length,
            screenHeight / dungeon.length
        );

        // Fill GridLayout with images based on the map
        for (int i = 0; i < dungeon.length; i++) {
            for (int j = 0; j < dungeon[i].length; j++) {
                FrameLayout cell = new FrameLayout(this);
                ImageView backgroundImage = new ImageView(this);
                
                // Set image scaling
                backgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
                
                if (dungeon[i][j] == '#') {
                    backgroundImage.setImageResource(R.drawable.vacio);
                } else if (dungeon[i][j] == '.') {
                    backgroundImage.setImageResource(R.drawable.suelo);
                } else if (dungeon[i][j] == 'E') {
                    backgroundImage.setImageResource(R.drawable.entrada);
                } else if (dungeon[i][j] == 'S') {
                    backgroundImage.setImageResource(R.drawable.salida);
                } else if (dungeon[i][j] == 'C') {
                    backgroundImage.setImageResource(R.drawable.cofre);
                } else if (dungeon[i][j] == 'c') {
                    backgroundImage.setImageResource(R.drawable.cofreabierto);
                }

                cell.addView(backgroundImage);

                if (dungeon[i][j] == '#') {
                    checkAndPlaceWall(cell, dungeon, i, j, 0);
                    checkAndPlaceWall(cell, dungeon, i, j, 90);
                    checkAndPlaceWall(cell, dungeon, i, j, 180);
                    checkAndPlaceWall(cell, dungeon, i, j, 270);
                }

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                params.setMargins(1, 1, 1, 1);
                
                cell.setLayoutParams(params);
                mapGrid.addView(cell);
            }
        }
    }
    private void handlePlayerMove(Direction direction) {
        if (player.move(direction, dungeon)) {
            checkCollisions();
            handleEnemyTurns();
            updateGameUI();
            updateStatusTexts();
        }
    }

    private void handleEnemyTurns() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                Point prevPos = enemy.getPosition();
                enemy.moveTowards(player.getPosition(), dungeon);
                Point newPos = enemy.getPosition();
                
                if (newPos.equals(player.getPosition())) {
                    handleCombat(enemy);
                }
            }
        }
    }

    private void handleCombat(Enemy enemy) {
        int damage = enemy.getAttack() - player.getDefense();
        if (damage > 0) {
            player.takeDamage(damage);
        }
        
        if (player.getHealth() <= 0) {
            gameOver();
            return;
        }

        damage = player.getAttack() - enemy.getDefense();
        if (damage > 0) {
            enemy.takeDamage(damage);
            if (!enemy.isAlive()) {
                player.addExperience(enemy.getExperienceValue());
            }
        }
    }

    private void checkCollisions() {
        Point playerPos = player.getPosition();
        
        if (dungeon[playerPos.x][playerPos.y] == 'C') {
            if (playerPos.equals(keyLocation)) {
                hasKey = true;
                dungeon[playerPos.x][playerPos.y] = 'c';
                showMessage("You found the key!");
            }
        }
        
        if (dungeon[playerPos.x][playerPos.y] == 'S' && hasKey) {
            nextLevel();
        }
    }

    private void nextLevel() {
        currentLevel++;
        hasKey = false;
        player.heal(player.getMaxHealth() / 2);
        showMessage("Level " + currentLevel + " reached!");
        initializeLevel();
    }

    private void restartGame() {
        currentLevel = 1;
        hasKey = false;
        initializeLevel();
        showMessage("Game restarted");
    }

    private void gameOver() {
        showMessage("Game Over! Final Level: " + currentLevel);
        finish();
    }

    private void showMessage(String message) {
        TextView messageText = findViewById(R.id.message_text);
        messageText.setText(message);
        messageText.setVisibility(View.VISIBLE);
        messageText.postDelayed(() -> messageText.setVisibility(View.GONE), 2000);
    }
    private Point findEntrance(char[][] dungeon) {
        for (int i = 0; i < dungeon.length; i++) {
            for (int j = 0; j < dungeon[i].length; j++) {
                if (dungeon[i][j] == 'E') {
                    return new Point(i, j);
                }
            }
        }
        // Si no se encuentra la entrada, usar una posición por defecto
        return new Point(0, 0);
    }
    private List<Enemy> generateEnemies(int level) {
        List<Enemy> enemies = new ArrayList<>();
        int numEnemies = INITIAL_ENEMIES + (level - 1);
        
        for (int i = 0; i < numEnemies; i++) {
            Point enemyPosition;
            do {
                // Buscar una posición válida (suelo) para el enemigo
                int x = (int) (Math.random() * dungeon.length);
                int y = (int) (Math.random() * dungeon[0].length);
                enemyPosition = new Point(x, y);
            } while (dungeon[enemyPosition.x][enemyPosition.y] != '.' || 
                    player.getPosition().equals(enemyPosition));
            
            enemies.add(new Enemy(enemyPosition, level));
        }
        
        return enemies;
    }
    private void placeKeyInRandomChest(char[][] dungeon) {
        List<Point> chests = new ArrayList<>();
        
        // Encontrar todos los cofres
        for (int i = 0; i < dungeon.length; i++) {
            for (int j = 0; j < dungeon[i].length; j++) {
                if (dungeon[i][j] == 'C') {
                    chests.add(new Point(i, j));
                }
            }
        }
        
        // Seleccionar un cofre aleatorio para la llave
        if (!chests.isEmpty()) {
            int randomIndex = (int) (Math.random() * chests.size());
            keyLocation = chests.get(randomIndex);
        }
    }
    // Método para comprobar si hay suelo en una dirección específica y colocar la pared si es necesario
    private void checkAndPlaceWall(FrameLayout cell, char[][] dungeon, int x, int y, float rotation) {
        // Direcciones correspondientes a las rotaciones
        int[] direction = getDirection(rotation);
        int newX = x + direction[0];
        int newY = y + direction[1];
    
        // Comprobar si está dentro de los límites del mapa
        if (newX >= 0 && newX < dungeon.length && newY >= 0 && newY < dungeon[0].length) {
            // Comprobar si en esa dirección hay un suelo, entrada o salida
            if (dungeon[newX][newY] == '.' || dungeon[newX][newY] == 'E' || dungeon[newX][newY] == 'S' || dungeon[newX][newY] == 'C' || dungeon[newX][newY] == 'c'){
                addWall(cell, rotation); // Añadir la pared con la rotación especificada
            }
        }
    }
    
    // Método para obtener las direcciones basadas en la rotación
    private int[] getDirection(float rotation) {
        switch ((int) rotation) {
            case 0:   // Abajo
                return new int[]{1, 0}; // Mover hacia abajo
            case 270:  // Derecha
                return new int[]{0, 1}; // Mover hacia la derecha
            case 180: // Arriba
                return new int[]{-1, 0}; // Mover hacia arriba
            case 90: // Izquierda
                return new int[]{0, -1}; // Mover hacia la izquierda
            default:
                return new int[]{0, 0}; // Caso por defecto (no debería ocurrir)
        }
    }
    
    // Método para añadir la pared con una orientación específica
    private void addWall(FrameLayout cell, float rotation) {
        ImageView wallImage = new ImageView(this);
        wallImage.setImageResource(R.drawable.pared); // Imagen para la pared
    
        // Rotar la pared hacia la dirección correspondiente
        wallImage.setRotation(rotation);
    
        // Añadir la pared al FrameLayout
        cell.addView(wallImage);
    }
    
}

