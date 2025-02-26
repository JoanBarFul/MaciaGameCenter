// Actualizar el nombre de la clase y el package
package com.example.maciagamecenter.mazmorra;

// Añadir esta importación
import com.example.maciagamecenter.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Point;
import java.util.List;
import android.widget.ProgressBar;
import java.util.ArrayList;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
// Cambiar el nombre de la clase de MainActivity a MazmorraActivity
public class MazmorraActivity extends AppCompatActivity {
    // Add this field with other class variables
    private boolean powerUpSelected = true; // Start as true for initial level
    
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
    private boolean inBattle = false;
    private Enemy currentEnemy = null;
    private TextView battleText;
    private Button rollDiceButton;
    private FrameLayout battlePanel;
    private LinearLayout enemyHealthContainer;  // Añadir esta declaración
    private void updateEnemyHealthBar() {
        if (currentEnemy != null) {
            TextView enemyHealthText = findViewById(R.id.enemy_health_text);
            ProgressBar healthBar = findViewById(R.id.enemy_health_bar);
            
            healthBar.setMax(currentEnemy.getMaxHealth());
            healthBar.setProgress(currentEnemy.getHealth());
            enemyHealthText.setText(currentEnemy.getHealth() + "/" + currentEnemy.getMaxHealth());
        }
    }
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
        battlePanel = findViewById(R.id.battle_panel);
        battleText = findViewById(R.id.battle_text);
        rollDiceButton = findViewById(R.id.roll_dice_button);
        enemyHealthContainer = findViewById(R.id.enemy_health_container);  // Añadir esta línea
        // Remove these lines
        // enemyDiceView = findViewById(R.id.enemy_dice);
        // playerDiceView = findViewById(R.id.player_dice);
        rollDiceButton.setOnClickListener(v -> handleDiceRoll());
        
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
        
        if (player == null) {
            // Solo crear nuevo jugador si no existe
            player = new Player(findEntrance(dungeon));
        } else {
            // Si el jugador existe, solo actualizar su posición
            player.setPosition(findEntrance(dungeon));
        }
        
        // Generar enemigos
        enemies = generateEnemies(currentLevel);
        
        // Colocar llave en un cofre aleatorio
        placeKeyInRandomChest(dungeon);
        updateGameUI();
        updateStatusTexts();
    }
    private void handleCombat(Enemy enemy) {
        inBattle = true;
        currentEnemy = enemy;
        battlePanel.setVisibility(View.VISIBLE);
        enemyHealthContainer.setVisibility(View.VISIBLE);
        rollDiceButton.setVisibility(View.VISIBLE);
        battleText.setText("Battle started! Roll your dice!");
        rollDiceButton.setEnabled(true);
        updateEnemyHealthBar();  // Añadir esta línea
    }
    private void handleDiceRoll() {
        if (!inBattle || currentEnemy == null) return;
    
        int playerRoll = (int)(Math.random() * 6) + 1;
        int enemyRoll = (int)(Math.random() * 6) + 1;
    
        // Update battle text with roll results
        battleText.setText("Your roll: " + playerRoll + " | Enemy roll: " + enemyRoll);
    
        int difference = Math.abs(playerRoll - enemyRoll);
        
        if (playerRoll > enemyRoll) {
            currentEnemy.takeDamage(difference);
            battleText.setText(battleText.getText() + "\nYou won! Damage dealt: " + difference);
            updateEnemyHealthBar();  // Añadir esta línea
            if (!currentEnemy.isAlive()) {
                endBattle(true);
            }
        } else if (enemyRoll > playerRoll) {
            player.takeDamage(difference);
            battleText.setText(battleText.getText() + "\nYou lost! Damage taken: " + difference);
            if (player.getHealth() <= 0) {
                gameOver();
                return;
            }
        } else {
            battleText.setText(battleText.getText() + "\nIt's a tie! No damage dealt");
        }
        
        updateStatusTexts();
        rollDiceButton.setEnabled(false);
        battlePanel.postDelayed(() -> {
            if (inBattle) {
                rollDiceButton.setEnabled(true);
                battleText.setText("Roll again!");
            }
        }, 2000);
    }
    private void endBattle(boolean playerWon) {
        inBattle = false;
        battlePanel.setVisibility(View.GONE);
        enemyHealthContainer.setVisibility(View.GONE);
        rollDiceButton.setVisibility(View.GONE);
        
        if (playerWon) {
            player.addExperience(currentEnemy.getExperienceValue());
            showMessage("Enemy defeated!");
        }
        currentEnemy = null;
        updateGameUI();
    }
    // Remove this entire method as we're not using it anymore
    /*
    private int getDiceResource(int value) {
        switch (value) {
            case 1: return R.drawable.dice1;
            case 2: return R.drawable.dice2;
            case 3: return R.drawable.dice3;
            case 4: return R.drawable.dice4;
            case 5: return R.drawable.dice5;
            case 6: return R.drawable.dice6;
            default: return R.drawable.dice1;
        }
    }
    */
    private void updateStatusTexts() {
        levelText.setText("Level: " + currentLevel);
        healthText.setText("Health: " + player.getHealth() + "/" + player.getMaxHealth());
        levelText.setTextColor(getResources().getColor(android.R.color.white));
        healthText.setTextColor(getResources().getColor(android.R.color.white));
    }
    private void checkCollisions() {
        Point playerPos = player.getPosition();
        
        if (dungeon[playerPos.x][playerPos.y] == 'C') {
            dungeon[playerPos.x][playerPos.y] = 'c';
            if (playerPos.equals(keyLocation)) {
                hasKey = true;
                showMessage("You found the key!");
            } else {
                showMessage("Empty chest!");
            }
        }
        
        if (dungeon[playerPos.x][playerPos.y] == 'S' && hasKey) {
            nextLevel();
        }
    }

    private void nextLevel() {
        currentLevel++;
        hasKey = false;
        powerUpSelected = false; // Reset flag for new level
        showMessage("Level " + currentLevel + " reached!");
        initializeLevel();  // First initialize the level
        showLevelUpRewards();  // Then show rewards
    }

    private void showLevelUpRewards() {
        PowerUpFragment powerUpFragment = new PowerUpFragment();
        powerUpFragment.setPowerUpListener(new PowerUpFragment.PowerUpListener() {
            @Override
            public void onAttackSelected() {
                player.increaseDamage(1);
                showMessage("Attack increased!");
                powerUpSelected = true; // Set flag when power-up is selected
                getSupportFragmentManager().beginTransaction()
                    .remove(powerUpFragment)
                    .commit();
            }

            @Override
            public void onHealSelected() {
                player.heal(player.getMaxHealth());
                showMessage("Fully healed!");
                powerUpSelected = true; // Set flag when power-up is selected
                getSupportFragmentManager().beginTransaction()
                    .remove(powerUpFragment)
                    .commit();
            }
        });

        getSupportFragmentManager().beginTransaction()
            .add(android.R.id.content, powerUpFragment)
            .addToBackStack(null)
            .commit();
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
        battlePanel.setVisibility(View.VISIBLE);
        battleText.setText(message);
        battleText.setTextColor(getResources().getColor(android.R.color.white));
        battleText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        
        // Ocultar elementos de batalla cuando es un mensaje
        rollDiceButton.setVisibility(View.GONE);
        enemyHealthContainer.setVisibility(View.GONE);
        
        // Auto-ocultar el mensaje después de 2 segundos
        battlePanel.postDelayed(() -> {
            if (!inBattle) {
                battlePanel.setVisibility(View.GONE);
            } else {
                rollDiceButton.setVisibility(View.VISIBLE);
                enemyHealthContainer.setVisibility(View.VISIBLE);
            }
        }, 2000);
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
            boolean validPosition;
            do {
                validPosition = true;
                // Buscar una posición válida (suelo) para el enemigo
                int x = (int) (Math.random() * dungeon.length);
                int y = (int) (Math.random() * dungeon[0].length);
                enemyPosition = new Point(x, y);
                
                // Verificar que no hay otro enemigo en esta posición
                for (Enemy existingEnemy : enemies) {
                    if (existingEnemy.getPosition().equals(enemyPosition)) {
                        validPosition = false;
                        break;
                    }
                }
                
            } while (!validPosition || 
                    dungeon[enemyPosition.x][enemyPosition.y] != '.' || 
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
    private void addWall(FrameLayout cell, float rotation) {
        ImageView wallImage = new ImageView(this);
        wallImage.setImageResource(R.drawable.imgpared);
        wallImage.setScaleType(ImageView.ScaleType.FIT_XY);
        wallImage.setRotation(rotation);
        
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        );
        wallImage.setLayoutParams(params);
        cell.addView(wallImage);
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

    private void handlePlayerMove(Direction direction) {
        // Add power-up check
        if (!powerUpSelected) {
            showMessage("¡Debes elegir un power-up para continuar!");
            return;
        }

        // Verificación estricta de estado de batalla
        if (inBattle || currentEnemy != null) {
            showMessage("¡No puedes moverte durante el combate!");
            return;
        }
        Point currentPos = player.getPosition();
        // Comprobar si hay enemigos adyacentes antes de moverse
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && isAdjacent(currentPos, enemy.getPosition())) {
                handleCombat(enemy);
                return;
            }
        }

        if (player.move(direction, dungeon)) {
            checkCollisions();
            handleEnemyTurns();
            updateGameUI();
            updateStatusTexts();
        }
    }
    private void updateGameUI() {
        GridLayout mapGrid = findViewById(R.id.map_grid);
        mapGrid.removeAllViews();
        
        mapGrid.setColumnCount(dungeon[0].length);
        mapGrid.setRowCount(dungeon.length);
    
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
                backgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
                
                // Set background tile
                if (dungeon[i][j] == '#') {
                    backgroundImage.setImageResource(android.R.color.transparent);
                } else if (dungeon[i][j] == '.') {
                    backgroundImage.setImageResource(R.drawable.imgsuelo);
                } else if (dungeon[i][j] == 'E') {
                    backgroundImage.setImageResource(R.drawable.imgentrada);
                } else if (dungeon[i][j] == 'S') {
                    backgroundImage.setImageResource(R.drawable.imgsalida);
                } else if (dungeon[i][j] == 'C') {
                    backgroundImage.setImageResource(R.drawable.imgcofre);
                } else if (dungeon[i][j] == 'c') {
                    backgroundImage.setImageResource(R.drawable.imgcofreabierto);
                }
                cell.addView(backgroundImage);
    
                // Add walls if needed
                if (dungeon[i][j] == '#') {
                    checkAndPlaceWall(cell, dungeon, i, j, 0);
                    checkAndPlaceWall(cell, dungeon, i, j, 90);
                    checkAndPlaceWall(cell, dungeon, i, j, 180);
                    checkAndPlaceWall(cell, dungeon, i, j, 270);
                }
    
                // Add player and enemies
                Point playerPos = player.getPosition();
                if (i == playerPos.x && j == playerPos.y) {
                    addCharacterToCell(cell, R.drawable.caballero);
                }
    
                for (Enemy enemy : enemies) {
                    if (enemy.isAlive() && enemy.getPosition().x == i && enemy.getPosition().y == j) {
                        addCharacterToCell(cell, R.drawable.slime);
                    }
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
    private void addCharacterToCell(FrameLayout cell, int drawableResource) {
        ImageView image = new ImageView(this);
        image.setImageResource(drawableResource);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        
        // Si es el caballero, aplicar rotación
        if (drawableResource == R.drawable.caballero) {
            if (inBattle && currentEnemy != null) {
                // Calcular ángulo hacia el enemigo durante el combate
                float angle = calculateAngleToEnemy(player.getPosition(), currentEnemy.getPosition());
                image.setRotation(angle);
            } else {
                image.setRotation(player.getRotation());
            }
        }
        
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(4, 4, 4, 4);
        image.setLayoutParams(params);
        cell.addView(image);
    }
    
    private float calculateAngleToEnemy(Point playerPos, Point enemyPos) {
        float dx = enemyPos.y - playerPos.y;
        float dy = enemyPos.x - playerPos.x;
        return (float) (Math.toDegrees(Math.atan2(dy, dx)));
    }
    private void handleEnemyTurns() {
        if (!Enemy.shouldMove()) {
            Enemy.incrementTurn();
            return;
        }

        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                // Check for adjacent position to player before moving
                Point playerPos = player.getPosition();
                Point enemyPos = enemy.getPosition();
                
                if (isAdjacent(enemyPos, playerPos)) {
                    handleCombat(enemy);
                } else {
                    Point prevPos = enemy.getPosition();
                    enemy.moveTowards(playerPos, dungeon);
                    Point newPos = enemy.getPosition();
                    
                    if (newPos.equals(playerPos)) {
                        handleCombat(enemy);
                        if (enemy.isAlive()) {
                            enemy.setPosition(prevPos);
                        }
                    }
                }
            }
        }
        Enemy.incrementTurn();
    }
    private boolean isAdjacent(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) <= 1 && Math.abs(p1.y - p2.y) <= 1;
    }
}

