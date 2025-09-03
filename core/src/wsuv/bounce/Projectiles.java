package wsuv.bounce;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import java.util.List;

public class Projectiles {
    private float x, y;
    private float speed;
    private float damage;
    private Enemy target;
    private Sprite sprite;
    public boolean active;
    private Texture texture;


    public Projectiles(float x, float y, Enemy target, float damage, float speed) {
        this.x = x + 20;
        this.y = y + 20;
        this.target = target;
        this.damage = damage;
        this.speed = speed;

        texture = new Texture("bullet.png");
        sprite = new Sprite(texture);
        sprite.setPosition(x, y);
        sprite.setSize(20, 20);

        active = true;
    }
    public void update(float deltaTime) {
        if (active) {
            if (target != null && target.isAlive) {
                float targx = target.tileX * 40;
                float targy = target.tileY * 40;
                float deltaX = targx - x;
                float deltaY = targy - y;
                float length = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                float directionX = deltaX / length;
                float directionY = deltaY / length;

                x += directionX * speed * deltaTime;
                y += directionY * speed * deltaTime;
                if (collision()) {
                    System.out.println("HIT");
                    target.hp -= 1;
                    active = false;
                }
            }
           if(target == null || !target.isAlive){
                active = false;
            }
        }
    }
    private boolean collision() {
        if (target != null) {
            float targx = target.tileX * 40;
            float targy = target.tileY * 40;
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


