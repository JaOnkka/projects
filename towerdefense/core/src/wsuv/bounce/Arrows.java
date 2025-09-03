package wsuv.bounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;


public class Arrows {
    public float x, y;
    private float speed;
    private float damage;
    private Sprite sprite;
    public boolean active;
    private Texture texture;
    private ArrayList<Enemy> enemies;

    private float directionX, directionY;
    private float attackCooldown = 2.0f;
    private float currentAttackCooldown = 0;

    public Arrows(float x, float y, float directionx, float directiony, float speed, ArrayList<Enemy> enemiesgiven) {

            this.x = x + 20;
            this.y = y + 20;
            //this.target = target;
            this.damage = damage;
            this.speed = speed;
            directionX = directionx;
            directionY = directiony;
            texture = new Texture("bullet.png");
            sprite = new Sprite(texture);
            sprite.setPosition(x, y);
            sprite.setSize(20, 20);
            enemies = enemiesgiven;
            active = true;
            System.out.println("Bullet made");


    }

    public void update(float deltaTime) {
        System.out.println("Arrow update");
        for (Enemy enemy : enemies) {
            if(enemy.isAlive) {
                float targx = enemy.tileX * 40;
                float targy = enemy.tileY * 40;
                //float deltaX = targx - x;
                //float deltaY = targy - y;
                //float length = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                //float directionX = deltaX / length;
                //float directionY = deltaY / length;

                x += directionX * speed * deltaTime;
                y += directionY * speed * deltaTime;
                if (collision(enemy)) {
                    System.out.println("HIT");
                    enemy.hp -= 1;
                    active = false;
                }
                else if(x < 0 || x > 860 || y < 0 || y > 800){
                    active = false;
                }
            }
        }

    }
    private boolean collision(Enemy enemy) {
        if (enemy != null) {
            float targx = enemy.tileX * 40;
            float targy = enemy.tileY * 40;
            float targxright = targx + 40;
            float targytop = targy + 40;

            float projcenterx = x + 10;
            float projcentery = y + 10;
            if(projcenterx <= targxright && projcenterx >= targx && projcentery <= targytop && projcentery >= targy){
                return true;
            }
        }
        return false;
    }
    public void draw(SpriteBatch batch) {
        if (active) {
            batch.draw(texture, x, y);
        }
    }
}



