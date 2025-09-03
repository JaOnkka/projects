package wsuv.bounce;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Towers extends Sprite {
    private int towers;
    public int health, damage;
    public int tileX, tileY;
    public boolean active;
    private Texture texture, bullettexture;
    private float x, y;
    private float attackCooldown = 2.0f;
    private float currentCooldown = 0;
    private Sound boomSfx;


    public Towers(BounceGame game, int tilex, int tiley, int tower, int givenhealth) {
        super(game.am.get("tower_basic.png", Texture.class));
        texture = new Texture(Gdx.files.internal("tower_basic.png"));
        bullettexture = new Texture("bullet.png");
        boomSfx = game.am.get(BounceGame.RSC_EXPLOSION_SFX);

        towers = tower;
        health = givenhealth;
        tileX = tilex;
        tileY = tiley;
        x = tilex * 40;
        y = tiley * 40;
        damage = 1;
        active = true;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public List<Projectiles> attack(List<Enemy> enemies, List<Projectiles> projectiles) {   //return list of bullets

        Enemy enemy = findClosestEnemy(enemies);

        if (currentCooldown <= 0) {
            launchProjectile(enemy, projectiles);
            currentCooldown = attackCooldown;
        }
        if (currentCooldown > 0) {
            currentCooldown -= Gdx.graphics.getDeltaTime();
        }
        return projectiles;
    }

    private Enemy findClosestEnemy(List<Enemy> enemies) {
        float closestDistance = Float.MAX_VALUE;
        Enemy closestEnemy = null;

        for (Enemy enemy : enemies) {
            if(enemy.isAlive) {
                float distance = (float) ((tileX - enemy.tileX) * (tileX - enemy.tileX) + (tileY - enemy.tileY) * (tileY - enemy.tileY));
                //System.out.println(distance);
                if (distance < closestDistance && distance < 50) {
                    closestDistance = distance;
                    closestEnemy = enemy;
                }
            }
        }

        return closestEnemy;
    }

    private void launchProjectile(Enemy target, List<Projectiles> projectiles) {
        Projectiles projectile = new Projectiles(x, y, target, damage, 400);
        projectiles.add(projectile);
        boomSfx.play();

    }
}
