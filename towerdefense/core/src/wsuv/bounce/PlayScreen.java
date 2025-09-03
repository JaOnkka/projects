package wsuv.bounce;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class PlayScreen extends ScreenAdapter {
    //private ArrayList<Walk> walking;
    WalkAnimationFrames waf;
    private Walk walkAnimation = null; // Initialize the animation outside of the key input checks

    public int healthbase = 10, healthwood = 3, healthstone = 5, healthgold = 7, healthobsidian = 10, healthtower = 6;
    public int tilebase = 2, tilewood = 3, tilestone = 4, tilegold = 5, tileobsidian = 6, tiletower = 20;
    public int costwood = 2, coststone = 5, costgold = 10, costobsidian = 20, costtowerbasic = 25;
    private enum SubState {READY, GAME_OVER, PLAYING, BUILD, WAVE}
    private enum State {NONE, WALL_WOOD, WALL_STONE, WALL_GOLD, WALL_OBSIDIAN, REMOVE, TOWER_BASIC}
    private State pick;
    private BounceGame bounceGame;
    private HUD hud;
    private SubState state;
    private float timer;
    private TileMap tilemap;
    private Integer level;

    public static BitmapFont font;
    boolean enemiesAlive = false;

    private SubState gamestate;
    public static Texture sidepanel, bullet;
    public static Texture emptytile, enemy;
    public static Texture enemytile;
    public static Texture walltile, field;
    public static Texture wallwood;
    public static Texture wallstone;
    public static Texture wallgold;
    public static Texture wallobsidian;
    public static Texture playertile;
    public static Texture basetile;
    public static Texture start, tower_basic, startendless;
    private ArrayList<Enemy> enemies;
    private ArrayList<Towers> towers;
    public List<Projectiles> projectiles;
    public List<Arrows> arrows;
    public int currency, towerLimit = 15, towersPlaced = 0;
    public float timesurvived;
    private float spawnCooldown = 2.0f;
    private float currentCooldown = 0;
    private float attackCooldown = 1.0f;
    private float currentAttackCooldown = 0;
    private Player player;
    boolean isWalking = false;
    private AttackAnimation myattacking;
    private WalkingAnimation mywalking;
    private Random random;
    private boolean showDijkstra = false;
    private boolean showTiles = false;
    Music music;
    private boolean isattacking = false;
    //screen is 1000 x 800; h x w ( x y)
    //tilemap is 840 x 800 (the right 200 is for the sidebar)
    //each tile is 40 x 40
    //tilemap is 21 x 20 in size, tile 10 x 10 is the base
    //enemies should not spawn on edges of the map
    //player cannot build on edges of map or too near to edge of map or too close to base
    private void addEnemies(Integer lvl){
        enemiesAlive = true;
        if (lvl == 1){
            enemies.add(new Enemy(bounceGame, 40, 720, 1));
            enemies.add(new Enemy(bounceGame, 120, 720, 1));

            enemies.add(new Enemy(bounceGame, 760, 720, 1));
            enemies.add(new Enemy(bounceGame, 680, 720, 1));


        }
        else if (lvl == 2){

            enemies.add(new Enemy(bounceGame, 40, 640, 1));
            enemies.add(new Enemy(bounceGame, 120, 640, 1));
            enemies.add(new Enemy(bounceGame, 40, 720, 1));
            enemies.add(new Enemy(bounceGame, 120, 720, 1));

            enemies.add(new Enemy(bounceGame, 760, 640, 1));
            enemies.add(new Enemy(bounceGame, 680, 640, 1));
            enemies.add(new Enemy(bounceGame, 760, 720, 1));
            enemies.add(new Enemy(bounceGame, 680, 720, 1));

            enemies.add(new Enemy(bounceGame, 760, 40, 3));
            enemies.add(new Enemy(bounceGame, 680, 40, 3));
        }
        else if (lvl == 3){
            enemies.add(new Enemy(bounceGame, 40, 640, 2));
            enemies.add(new Enemy(bounceGame, 120, 640, 2));
            enemies.add(new Enemy(bounceGame, 40, 720, 2));
            enemies.add(new Enemy(bounceGame, 120, 720, 2));

            enemies.add(new Enemy(bounceGame, 760, 640, 2));
            enemies.add(new Enemy(bounceGame, 680, 640, 2));
            enemies.add(new Enemy(bounceGame, 760, 720, 2));
            enemies.add(new Enemy(bounceGame, 680, 720, 2));

            enemies.add(new Enemy(bounceGame, 760, 40, 3));
            enemies.add(new Enemy(bounceGame, 680, 40, 3));
        }
        else if (level > 3 && level < 100){
            enemies.add(new Enemy(bounceGame, 40, 640, 3));
            enemies.add(new Enemy(bounceGame, 120, 640, 3));
            enemies.add(new Enemy(bounceGame, 40, 720, level));
            enemies.add(new Enemy(bounceGame, 120, 720, 3));

            enemies.add(new Enemy(bounceGame, 760, 640, 3));
            enemies.add(new Enemy(bounceGame, 680, 640, 3));
            enemies.add(new Enemy(bounceGame, 760, 720, level));
            enemies.add(new Enemy(bounceGame, 680, 720, 3));

            enemies.add(new Enemy(bounceGame, 760, 40, level));
            enemies.add(new Enemy(bounceGame, 680, 40, 3));

            enemies.add(new Enemy(bounceGame, 40, 40, level));
            enemies.add(new Enemy(bounceGame, 120, 40, 3));

            enemies.add(new Enemy(bounceGame, 40, 400, 3));
            enemies.add(new Enemy(bounceGame, 760, 400, 3));
        }
        else if (level == 100){
            if (currentCooldown <= 0) {
                Random random = new Random();
                int randomSpawn = random.nextInt(4) + 1;
                int randomHP = random.nextInt(4) + 1;

                //System.out.println(randomNumber);
                if(randomSpawn == 1){
                    enemies.add(new Enemy(bounceGame, 40, 40, (int) (randomHP * timesurvived) / 4));
                }
                if(randomSpawn == 2){
                    enemies.add(new Enemy(bounceGame, 40, 720, (int) (randomHP * timesurvived) / 4));
                }
                if(randomSpawn == 3){
                    enemies.add(new Enemy(bounceGame, 720, 720, (int) (randomHP * timesurvived) / 4));
                }
                if(randomSpawn == 4){
                    enemies.add(new Enemy(bounceGame, 720, 40, (int) (randomHP * timesurvived) / 4));
                }
                currentCooldown = spawnCooldown;
            }
            if (currentCooldown > 0) {
                currentCooldown -= Gdx.graphics.getDeltaTime();
            }
        }
    }

    public PlayScreen(BounceGame game) {
        timer = 0;
        bounceGame = game;
        player = new Player(game);
        hud = new HUD(bounceGame.am.get(BounceGame.RSC_MONO_FONT));
        tilemap = new TileMap(game,21,20);
        //walking = new ArrayList<>(10);
        waf = new WalkAnimationFrames(bounceGame.am.get(BounceGame.RSC_EXPLOSION_FRAMES));
        myattacking= new AttackAnimation(bounceGame.am.get(BounceGame.RSC_ATTACKING_FRAMES),14,1,1f/14f);
        //field = new Texture("FieldsTileset.png");
        field = bounceGame.am.get(bounceGame.RSC_FIELD);
        sidepanel = bounceGame.am.get(bounceGame.RSC_SIDEPANEL);
        mywalking = new WalkingAnimation(bounceGame.am.get(BounceGame.RSC_EXPLOSION_FRAMES),8,1,1f/15f);
        bullet = bounceGame.am.get(bounceGame.RSC_BULLET);
        tower_basic = bounceGame.am.get(bounceGame.RSC_TOWER_BASIC);
        emptytile = bounceGame.am.get(bounceGame.RSC_EMPTYTILE);
        //enemy = bounceGame.am.get(RSC_ENEMY);
        enemytile = bounceGame.am.get(bounceGame.RSC_ENEMYTILE);
        // we've loaded textures, but the explosion texture isn't quite ready to go--
        // we need to carve it up into frames.  All that work really
        // only needs to happen once.  Since we only use explosions in the PlayScreen,
        // we'll do it here, storing the work in a special object we'll use each time
        // a new Bang instance is created...

        music = bounceGame.am.get("Loyalty Freak Music - Slow Pogo.mp3");
        music.setLooping(true);
        music.setVolume(.5f);
        music.play();

        walltile = bounceGame.am.get(bounceGame.RSC_WALLTILE);
        playertile = bounceGame.am.get(bounceGame.RSC_PLAYERTILE);
        basetile = bounceGame.am.get(bounceGame.RSC_BASETILE);

        wallwood = bounceGame.am.get(bounceGame.RSC_WALLWOOD);
        wallstone = bounceGame.am.get(bounceGame.RSC_WALLSTONE);
        wallgold = bounceGame.am.get(bounceGame.RSC_WALLGOLD);
        wallobsidian = bounceGame.am.get(bounceGame.RSC_WALLOBSIDIAN);
        start = bounceGame.am.get(bounceGame.RSC_START);
        startendless = bounceGame.am.get(bounceGame.RSC_STARTENDLESS);
        // walltile = new Texture("wall.png");
        //playertile = new Texture("player.png");
       // basetile = new Texture("base.png");
        level = 0;
        //wallwood = new Texture("wall_wood.png");
        //wallstone = new Texture("wall_stone.png");
        //wallgold = new Texture("wall_gold.png");
        //wallobsidian = new Texture("wall_obsidian.png");

        //start = new Texture("start.png");
        //startendless = new Texture("startendless.png");
        enemies = new ArrayList<Enemy>();
        towers = new ArrayList<Towers>();
        projectiles = new ArrayList<>();
        arrows = new ArrayList<>();
        currency = 100;
        tilemap.setTile(10,10,tilebase, healthbase);
        // the HUD will show FPS always, by default.  Here's how
        // to use the HUD interface to silence it (and other HUD Data)
        hud.setDataVisibility(HUDViewCommand.Visibility.WHEN_OPEN);
        //BitmapFont font;
        font = new BitmapFont();
        // HUD Console Commands

        hud.registerAction("tiles", new HUDActionCommand() {
            static final String help = "Usage: tiles [toggles on or off the tiles visually]";

            @Override
            public String execute(String[] cmd) {
                if (showTiles) {
                    showTiles = false;
                }
                else{
                    showTiles = true;
                }
                return help;
            }

            public String help(String[] cmd) {
                return help;
            }
        });/////////////////////////


        hud.registerAction("dijkstra", new HUDActionCommand() {
            static final String help = "Usage: dijkstra [toggles on or off dijkstra visual\nNote: in each tile, top number is hp of tile, bottom is dijkstra value]";

            @Override
            public String execute(String[] cmd) {
                if (showDijkstra) {
                    showDijkstra = false;
                }
                else{
                    showDijkstra = true;
                }
                return help;
            }

            public String help(String[] cmd) {
                return help;
            }
        });/////////////////////////
        hud.registerAction("money", new HUDActionCommand() {
            static final String help = "Usage: money [gives you ample money for testing]";

            @Override
            public String execute(String[] cmd) {
                currency = 100000;
                return help;
            }

            public String help(String[] cmd) {
                return help;
            }
        });/////////////////////////
        hud.registerAction("base", new HUDActionCommand() {
            static final String help = "Usage: base <int> [sets the base health]";

            @Override
            public String execute(String[] cmd) {
                try {
                    int x = Integer.parseInt(cmd[1]);
                    tilemap.setTile(10,10,tilebase,x);
                    System.out.println("Game over, base destroyed");
                    if (x <= 0){
                        gamestate = SubState.BUILD;
                        state = SubState.GAME_OVER;
                    }
                    return "ok!";
                } catch (Exception e) {
                    return help;
                }
            }

            public String help(String[] cmd) {
                return help;
            }
        });/////////////////////////
        int[][] dijkstraResults = tilemap.dijkstra(10, 10);
        //Gdx.app.log("Short", Arrays.toString(shortestDistances));
 /*       for (int x = 0; x < dijkstraResults.length; x++) {
            for (int y = 0; y < dijkstraResults[x].length; y++) {
                System.out.print(dijkstraResults[x][y] + " ");
            }
            System.out.println(); // Start a new line for the next row
        }
*/


        // HUD Data
        // we're adding an input processor AFTER the HUD has been created,
        // so we need to be a bit careful here and make sure not to clobber
        // the HUD's input controls. Do that by using an InputMultiplexer
        InputMultiplexer multiplexer = new InputMultiplexer();
        // let the HUD's input processor handle things first....
        multiplexer.addProcessor(Gdx.input.getInputProcessor());
        // then pass input to our new handler...
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {   //capture left mouse clicks
                if (button == Input.Buttons.LEFT) {
                        y = Gdx.graphics.getHeight() - y;
                        int tileX = x / 40;
                        int tileY = y / 40;

                    if(gamestate == SubState.WAVE){
                        //System.out.println("In Wave Mode");
                        if (currentAttackCooldown <= 0) {
                            isattacking = true;
                            myattacking.startAttack();
                            System.out.println("Shooting");
                            //if user clicks, shoot a projectile from current location in the direction of mouse relative to position
                            float deltaX = x - player.x;
                            float deltaY = y - player.y;
                            float length = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                            float directionX = deltaX / length;
                            float directionY = deltaY / length;
                            System.out.println("Dirx + " + directionX + " Diry " + directionY);
                            //arrows = player.attack(enemies, arrows);
                            Arrows arrow = new Arrows(player.x, player.y, directionX, directionY, 400, enemies);
                            arrows.add(arrow);
                            currentAttackCooldown = attackCooldown;


                        }

                        //System.out.println(arrows.x);

                    }
                        if(gamestate == SubState.BUILD){
                            if(x > 850 && x < 890 && y < 740 && y > 700){
                                    System.out.println("Wall Wood Picked");
                                    pick = State.WALL_WOOD;

                            }
                            else if(x > 850 && x < 890 && y < 690 && y > 650){
                                    System.out.println("Wall Stone Picked");
                                    pick = State.WALL_STONE;

                            }
                            else if(x > 850 && x < 890 && y < 640 && y > 600){
                                    System.out.println("Wall Gold Picked");
                                    pick = State.WALL_GOLD;

                            }
                            else if(x > 850 && x < 890 && y < 590 && y > 550){
                                    System.out.println("Wall Obsidian Picked");
                                    pick = State.WALL_OBSIDIAN;

                            }
                            else if(x > 850 && x < 890 && y < 340 && y > 300){
                                System.out.println("Remove");
                                pick = State.REMOVE;
                            }
                            //build towers
                            else if(x > 850 && x < 890 && y < 540 && y > 500){

                                    System.out.println("Tower");
                                    pick = State.TOWER_BASIC;

                            }

                            //end build towers
                            else if(x > 880 && x < 960 && y < 140 && y > 100){
                                System.out.println("Start Wave");
                                pick = State.NONE;
                                gamestate = SubState.WAVE;
                                level++;
                                addEnemies(level);
                            }
                            else if(x > 880 && x < 960 && y < 80 && y > 40){
                                System.out.println("Endless Wave Good Luck");
                                pick = State.NONE;
                                gamestate = SubState.WAVE;
                                level = 100;
                                timesurvived = 0;
                            }

                            //placing on tiles
                            int tileType = tilemap.getTile(tileX,tileY);
                            if(tileType == 0) {
                                if (x < 680 && x > 140 && y < 640 && y > 80 && pick == State.WALL_WOOD) {
                                    if(currency >= costwood) {
                                        currency -= costwood;
                                        tilemap.setTile(tileX, tileY, tilewood, healthwood);     //setup to build wall
                                    }
                                } else if (x < 680 && x > 140 && y < 640 && y > 80 && pick == State.WALL_STONE) {
                                    if(currency >= coststone) {
                                        currency -= coststone;
                                        tilemap.setTile(tileX, tileY, tilestone, healthstone);     //setup to build wall
                                    }
                                } else if (x < 680 && x > 140 && y < 640 && y > 80 && pick == State.WALL_GOLD) {
                                    if(currency >= costgold) {
                                        currency -= costgold;
                                        tilemap.setTile(tileX, tileY, tilegold, healthgold);     //setup to build wall
                                    }
                                } else if (x < 680 && x > 140 && y < 640 && y > 80 && pick == State.WALL_OBSIDIAN) {
                                    if(currency >= costobsidian) {
                                        currency -= costobsidian;
                                        tilemap.setTile(tileX, tileY, tileobsidian, healthobsidian);     //setup to build wall
                                    }
                                } else if (x < 680 && x > 140 && y < 640 && y > 80 && pick == State.TOWER_BASIC){
                                    if(currency >= costtowerbasic && towersPlaced < towerLimit) {
                                        currency -= costtowerbasic;
                                        towersPlaced += 1;
                                        tilemap.setTile(tileX, tileY, tiletower, healthtower);
                                        towers.add(new Towers(bounceGame, tileX, tileY, 1, healthtower));
                                    }
                                    else if(towersPlaced >= towerLimit){
                                        System.out.println("You have reached the tower limit");
                                    }
                                }
                                else {
                                    System.out.println("Nothing Selected");
                                }
                                System.out.println(gamestate + " " + pick);
                            }
                            else if (x < 860 && pick == State.REMOVE) {
                                int deltile = tilemap.getTile(tileX,tileY);
                                if(deltile == tilebase){ System.out.println("Cannot sell base"); return false; }
                                else if(deltile == tilewood){ currency += costwood; }
                                else if(deltile == tilestone){ currency += coststone; }
                                else if(deltile == tilegold){ currency += costgold; }
                                else if(deltile == tileobsidian){ currency += costobsidian; }
                                tilemap.setTile(tileX, tileY, 0, 0);     //setup to build wall
                                for(Towers tower : towers){
                                    if(tower.active) {
                                        if(tileX == tower.tileX && tileY == tower.tileY) {
                                            tower.active = false;
                                            towersPlaced -= 1;
                                            currency += costtowerbasic;
                                        }
                                    }
                                }
                            }
                            else {
                                //System.out.println("Bad");
                            }
                        }

                    return true;
                }
                return false;
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void show() {
        Gdx.app.log("PlayScreen", "show");
        state = SubState.READY;

    }

    public void update(float delta) {
        timer += delta;
        if (state == SubState.PLAYING) {
            if(gamestate == SubState.BUILD){
               // System.out.println("In Build Mode");
                player.x = 400;
                player.y = 400;
            }
            else if(gamestate == SubState.WAVE){
                //System.out.println("In Wave Mode");
                player.update(tilemap);
                for(Arrows arrow : arrows){
                    if(arrow != null && arrow.active) {
                        arrow.update(delta);
                    }
                }
                if(isattacking = true){
                    myattacking.update(delta);
                }

                if(currentAttackCooldown > 0 && currentAttackCooldown < 0.1f){
                    myattacking.stopAttack();
                    isattacking = false;
                }

                if (currentAttackCooldown > 0) {
                    currentAttackCooldown -= Gdx.graphics.getDeltaTime();
                }

                // ignore key presses when console is open...
                //animate player walking when key is pressed
                if (!hud.isOpen()) {
                    if (Gdx.input.isKeyPressed(Input.Keys.W)) {     //normally player doesn't control ball, remove these in future
                        player.yVelocity = 100;

                    }
                    if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                        player.yVelocity = -100;
                    }
                    if (Gdx.input.isKeyPressed(Input.Keys.A)) {     //player movement for paddle, can only go left or right, no acceleration, paddle stops when player lets go of key
                        player.xVelocity = -100;
                        mywalking.setDirection(true);

                    }
                    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                        player.xVelocity = 100;
                        mywalking.setDirection(false);
                    }
                    if(!Gdx.input.isKeyPressed(Input.Keys.W) && !Gdx.input.isKeyPressed(Input.Keys.S) && !Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D)){
                        mywalking.stopWalking();
                    }
                    else{
                        mywalking.startWalking();
                    }

                }
                mywalking.update(delta);

                if(level == 100){
                    addEnemies(100);
                    timesurvived += delta;
                }
                if(!enemiesAlive){
                    //System.out.println("All enemies defeated");
                    gamestate = SubState.BUILD;
                    currency += level * 100;
                    tilemap.resetHP();
                }
                if(tilemap.getTile(10,10) <= 0){    //the base has been destroyed, restart game
                    System.out.println("Game over, base destroyed");
                    gamestate = SubState.BUILD;
                    state = SubState.GAME_OVER;

                }
                for(Enemy enemy: enemies){
                    if(enemy.isAlive()){
                        enemy.move(tilemap, delta, towers);
                        enemiesAlive = true;
                    }
                    else if(!enemy.isAlive()){
                        enemiesAlive = false;
                    }

                }
                for(Enemy enemy: enemies) {
                    if (enemy.isAlive()) {
                        enemy.move(tilemap, delta, towers);
                        enemiesAlive = true;
                    }
                }
                for(Towers tower: towers){
                    //projectiles = tower.attack(enemies);
                    if(tower.health <= 0){
                        tower.active = false;
                    }
                    if(tower.active) {
                        projectiles = tower.attack(enemies, projectiles);
                    }
                    //System.out.println("ATTACKING");
                    //projectiles.add
                }
                for(Projectiles projectile : projectiles){
                    if(projectile != null) {
                        projectile.update(delta);
                    }
                }
            }
        }
        //if game ends, reset everything, game is over if base is destroyed
        if (state == SubState.READY && Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            //state = SubState.PLAYING;
            //bounceGame.music.setVolume(bounceGame.music.getVolume() / 2);
            //gamestate = SubState.BUILD;
            //pick = State.NONE;
            System.out.println("GAME IS OVER");
            gamestate = SubState.BUILD;
            projectiles.removeAll(projectiles);
            towers.removeAll(towers);
            enemies.removeAll(enemies);
            arrows.removeAll(arrows);
            tilemap = new TileMap(bounceGame,21,20);
            tilemap.setTile(10,10,tilebase, healthbase);
            currency = 100;
            level = 0;
            towersPlaced = 0;
            state = SubState.PLAYING;
            System.out.println("Map Restarted");
            music.stop();

            music = bounceGame.am.get("Loyalty Freak Music - Slow Pogo.mp3");
            music.setLooping(true);
            music.setVolume(.5f);
            music.play();
        }
        if (state == SubState.GAME_OVER && timer > 3.0f) {
            state = SubState.READY;
        }

    }

    @Override
    public void render(float delta) {
        update(delta);
        int[][] dijkstraResults = tilemap.dijkstra(10, 10);


        ScreenUtils.clear(0, 0, 0, 1);
        bounceGame.batch.begin();

        //bounceGame.batch.draw(field,0,0);
        renderTilemap(bounceGame);
        bounceGame.batch.draw(wallwood, 850, 700);
        font.draw(bounceGame.batch, "Wood: " + costwood, 890, 725);

        bounceGame.batch.draw(wallstone, 850, 650);
        font.draw(bounceGame.batch, "Stone: " + coststone, 890, 675);

        bounceGame.batch.draw(wallgold, 850, 600);
        font.draw(bounceGame.batch, "Gold: " + costgold, 890, 625);

        bounceGame.batch.draw(wallobsidian, 850, 550);
        font.draw(bounceGame.batch, "Obsidian: " + costobsidian, 890, 575);


        bounceGame.batch.draw(tower_basic, 850, 500);    //tower
        font.draw(bounceGame.batch, "Cannon: " + costtowerbasic, 890, 525);


        bounceGame.batch.draw(enemytile, 850, 300);
        font.draw(bounceGame.batch, "Sell", 890, 325);

        bounceGame.batch.draw(start, 880, 100);
        bounceGame.batch.draw(startendless, 880, 40);

        font.draw(bounceGame.batch, "Currency: " + String.valueOf(currency), 860, 780);
        font.draw(bounceGame.batch, "Wave: " + String.valueOf(level), 860, 760);
        if(level == 100){
            font.draw(bounceGame.batch, "Time: " + timesurvived, 850, 15);
        }
        font.draw(bounceGame.batch, "Towers: " + towersPlaced + "/" + towerLimit, 850, 375);

        //font.draw(bounceGame.batch, "hi",40,40);
        if(gamestate == SubState.WAVE){
            for (Enemy enemy : enemies) {
                if (enemy.isAlive()) {
                    enemy.draw(bounceGame.batch);
                }
            }
            if(!mywalking.draw(bounceGame.batch, player.x - 20,player.y) && !myattacking.isAttacking){
                if(mywalking.isFacingLeft) {
                    player.draw(bounceGame.batch, true);
                }
                else{
                    player.draw(bounceGame.batch, false);
                }

            }
            if(myattacking.isAttacking){
                if(mywalking.isFacingLeft) {
                    myattacking.draw(bounceGame.batch,player.x - 20, player.y, true);
                }
                else{
                    myattacking.draw(bounceGame.batch,player.x - 20, player.y, false);
                }

            }
        }
        for(Towers tower : towers){
            if(tower.active) {
                tower.draw(bounceGame.batch);
            }
        }
        for(Projectiles projectile : projectiles){
            if(projectile.active) {
                projectile.draw(bounceGame.batch);
            }
        }
        for(Arrows arrow : arrows){
            if(arrow.active) {
                arrow.draw(bounceGame.batch);
            }
        }
        if(showDijkstra) {
            tilemap.renderDijkstraValues(dijkstraResults, bounceGame, font);
        }
        // this logic could also be pushed into a method on SubState enum
        switch (state) {
            case GAME_OVER:
                bounceGame.batch.draw(bounceGame.am.get(BounceGame.RSC_GAMEOVER_IMG, Texture.class), 200, 200);
                break;
            case READY:
                bounceGame.batch.draw(bounceGame.am.get(BounceGame.RSC_PRESSAKEY_IMG, Texture.class), 200, 200);
                break;
            case PLAYING:
                break;
        }


        hud.draw(bounceGame.batch);

        bounceGame.batch.end();
    }

    private void renderTilemap(BounceGame game) {
        int tileSize = tilemap.getTileSize();
        int mapWidth = tilemap.getMapWidth();
        int mapHeight = tilemap.getMapHeight();
        bounceGame.batch.draw(field,0,0);
        bounceGame.batch.draw(sidepanel,840,0);


        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                int tileType = tilemap.getTile(x, y);
                if (tileType == 0 && gamestate == SubState.BUILD || showTiles) {
                    //empty land
                    //tilemap.draw(bounceGame.batch, x, y);
                    game.batch.draw(emptytile,x * tileSize, y * tileSize);
                } if (tileType == 1) {
                    //wall
                   game.batch.draw(walltile,x * tileSize, y * tileSize);
                } if (tileType == 2) {
                    //base
                   game.batch.draw(basetile, x * tileSize, y * tileSize);
                }
                if (tileType == 3){
                    game.batch.draw(wallwood, x * tileSize, y * tileSize);
                }
                if (tileType == 4){
                    game.batch.draw(wallstone, x * tileSize, y * tileSize);

                }
                if (tileType == 5){
                    game.batch.draw(wallgold, x * tileSize, y * tileSize);

                }
                if (tileType == 6){
                    game.batch.draw(wallobsidian, x * tileSize, y * tileSize);

                }
            }
        }
    }
}
class WalkAnimationFrames {
    float halfW, halfH;
    TextureRegion[] frames;
    WalkAnimationFrames(Texture spritesheet) {
        // split the single spritesheet into an array of equally sized TextureRegions
        TextureRegion[][] tmp = TextureRegion.split(spritesheet,
                spritesheet.getWidth() / BounceGame.RSC_EXPLOSION_FRAMES_COLS,
                spritesheet.getHeight() / BounceGame.RSC_EXPLOSION_FRAMES_ROWS);

        halfW = (spritesheet.getWidth() / 2f) / BounceGame.RSC_EXPLOSION_FRAMES_COLS;
        halfH = (spritesheet.getHeight() / 2f) / BounceGame.RSC_EXPLOSION_FRAMES_ROWS;

        frames = new TextureRegion[BounceGame.RSC_EXPLOSION_FRAMES_COLS * BounceGame.RSC_EXPLOSION_FRAMES_ROWS];
        int index = 0;
        for (int i = 0; i < BounceGame.RSC_EXPLOSION_FRAMES_ROWS; i++) {
            for (int j = 0; j < BounceGame.RSC_EXPLOSION_FRAMES_COLS; j++) {
                frames[index++] = tmp[i][j];
            }
        }
    }
}

