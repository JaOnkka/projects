package wsuv.bounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class Enemy extends Sprite{
    float x,y;
    float width,height;
    Rectangle brick;
    boolean isAlive;
    public int tileX, tileY, hp;
    private Texture texture;
    private float attackCooldown = 1.0f;
    private float currentCooldown = 0.0f;

    public Enemy(BounceGame game, float x, float y, int health){
        super(game.am.get("enemy1.png", Texture.class));
        texture = new Texture(Gdx.files.internal("enemy1.png"));
        this.x = x;
        this.y = y;
        this.width = getWidth();
        this.height = getHeight();
        brick = new Rectangle();
        tileX = (int) (this.x / 40);
        tileY = (int) (this.y / 40);
        hp = health;
        this.isAlive = true;
    }
    public boolean isAlive(){ return isAlive; }
    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }


    //need to update enemy to move, figure out where they are in tilemap
    public void move(TileMap tilemap, float Time, ArrayList<Towers> towers){
        if(hp <= 0){
            isAlive = false;
            return;
        }
        //int tileX = (int) (this.x / 40);
        //int tileY = (int) (this.y / 40);
        //int currentTile = tilemap.getTile(tileX,tileY); //the tile the enemy is currently on
        //System.out.println("Tile " + currentTile);
        int tileLeft = tileX - 1;
        int tileRight = tileX + 1;
        int tileTop = tileY + 1;
        int tileBottom = tileY - 1;

        int[][] dijkstraResults = tilemap.dijkstra(10, 10);


        int tileLeftValue = tilemap.getTileValue(dijkstraResults, tileLeft, tileY);
        int tileRightValue = tilemap.getTileValue(dijkstraResults, tileRight, tileY);
        int tileTopValue = tilemap.getTileValue(dijkstraResults,tileX, tileTop);
        int tileBottomValue = tilemap.getTileValue(dijkstraResults,tileX, tileBottom);
       // System.out.println("Left " + tileLeftValue + " Right " + tileRightValue + " Top " + tileTopValue + " Bottom " + tileBottomValue);
        //find lowest value, move in that direction
        int moveX = 100, moveY = 100;
        //move left
        if(tileLeftValue <= tileRightValue && tileLeftValue <= tileTopValue && tileLeftValue <= tileBottomValue){
            moveX = tileLeft;
            moveY = tileY;
            //System.out.println("Move Left");
        }
        //move right
        else if(tileRightValue <= tileLeftValue && tileRightValue <= tileTopValue && tileRightValue <= tileBottomValue){
            moveX = tileRight;
            moveY = tileY;
            //System.out.println("Move Right");

        }
        //move top
        else if(tileTopValue <= tileRightValue && tileTopValue <= tileLeftValue && tileTopValue <= tileBottomValue){
            moveX = tileX;
            moveY = tileTop;
            //System.out.println("Move Top");

        }
        //move down
        else if(tileBottomValue <= tileRightValue && tileBottomValue <= tileTopValue && tileBottomValue <= tileLeftValue){
            moveX = tileX;
            moveY = tileBottom;
            //System.out.println("Move Bottom");

        }
        else{ return; }
        //now we know tile to move to
        int nextTile = tilemap.getTile(moveX, moveY); //what is the next tile
        int nextHealth = tilemap.getTileHealth(moveX, moveY);
        float moveDeltaX = (moveX * 40 - x) * Time;
        float moveDeltaY = (moveY * 40 - y) * Time;

        if(nextTile == 0 && nextHealth == 0){  //if it's empty, just move
            if (currentCooldown <= 0) {
                tileX = moveX;
                tileY = moveY;
                x = tileX * 40;
                y = tileY * 40;
                //System.out.println("MOVING");
                //this.x += moveDeltaX;
                //this.y += moveDeltaY;
                currentCooldown = attackCooldown;
            }
        }
        else if(nextTile != 0 || nextHealth > 0){
            if (currentCooldown <= 0) {
                // Perform the attack on the wall
                System.out.println("Attacking " + nextTile + "HP " + nextHealth);
                if(nextTile == 20){     //special case if attacking a tower since it's not built directly into tilemap
                    for(Towers tower : towers){
                        if(tower.active) {
                            if(moveX == tower.tileX && moveY == tower.tileY) {
                                if(tower.health == 0){
                                    tower.active = false;
                                }
                                else{
                                    tower.health -= 1;
                                    //System.out.println("Tower health" + tower.health);
                                }
                            }
                        }
                    }
                }
                if (nextHealth == 1) {
                    tilemap.setTile(moveX, moveY, 0, 0);
                } else {
                    tilemap.setTile(moveX, moveY, nextTile, nextHealth - 1);
                }

                currentCooldown = attackCooldown;
            }

        }

        if (currentCooldown > 0) {
            currentCooldown -= Gdx.graphics.getDeltaTime();
        }

        /*
        else if(nextTile == 2){ //this is the base
            tilemap.setTile(moveX,moveY,0, nextHealth - 1);
        }
        else if(nextTile == 3){
            tilemap.setTile(moveX,moveY, nextTile - 3, nextHealth - 1);
        }
        else if(nextTile == 4 || nextTile == 5 || nextTile == 6){  //if it's a wall, attack it
            tilemap.setTile(moveX,moveY, nextTile, nextHealth - 1);
        }*/
        //System.out.println("Next tile " + nextTile);

    }
}
