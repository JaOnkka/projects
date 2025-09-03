package wsuv.bounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;


public class Player extends Sprite {

    float xVelocity;
    float yVelocity;
    int tileX, tileY;
    float x, y;
    public boolean isWalking = false;

    private Texture texture;
    public Player(BounceGame game) {
        super(game.am.get("player.png", Texture.class));
        texture = new Texture(Gdx.files.internal("player.png"));

        xVelocity = 40;//game.random.nextFloat(80, 150);
        yVelocity = 40;//game.random.nextFloat(80, 150);
        if (game.random.nextBoolean()) xVelocity *= -1;
        if (game.random.nextBoolean()) yVelocity *= -1;
        tileX = 10;
        tileY = 10;
        x = tileX * 40;
        y = tileY * 40;
    }
    public void draw(SpriteBatch batch, boolean isFacingLeft) {
        if (isFacingLeft) {
            batch.draw(texture, x + getWidth(), y, -getWidth(), getHeight());
        } else {
            batch.draw(texture, x, y);
        }
        //batch.draw(texture, x, y);
    }


        /**
         * Update the ball's location based on time since last update and velocity.
         * update() should generally be called every frame...
         *
         * @return true iff the ball bounced in this last update.
         */
    public void update(TileMap tilemap) {
        //check general collision out of bounds
        if (x < 0 ) {
            Gdx.app.log("Player","Left");
            x = 1;
        }
        else if(x + getWidth() > 840){
            Gdx.app.log("Player","Right");
            x = 840 - 1 - getWidth();
        }
        else if (y < 0 ) {
            Gdx.app.log("Player","Bottom");
            y = 1;
        }
        else if(y + getHeight() > Gdx.graphics.getHeight()){
            Gdx.app.log("Player","Top");
            y = Gdx.graphics.getHeight() - 1 - getHeight();
        }
        float centerx = x + getWidth() / 2f;
        float centery = y + getHeight() / 2f;
        //now check collision with walls
        tileX = (int) (centerx / 40);
        tileY = (int) (centery / 40);
        int tileAbove = tilemap.getTile(tileX, tileY + 1);
        int tileBelow = tilemap.getTile(tileX, tileY - 1);
        int tileRight = tilemap.getTile(tileX + 1, tileY);
        int tileLeft = tilemap.getTile(tileX - 1, tileY);

        int tileAboveLeft = tilemap.getTile(tileX - 1, tileY + 1);
        int tileAboveRight = tilemap.getTile(tileX + 1, tileY + 1);
        int tileBelowLeft = tilemap.getTile(tileX - 1, tileY - 1);
        int tileBelowRight = tilemap.getTile(tileX + 1, tileY - 1);

        float time = Gdx.graphics.getDeltaTime();

        if(tileAbove > 2 && y + getHeight() >= (tileY + 1) * 40 && yVelocity > 0){
            y = y;
        }
        else if(tileBelow > 2 && y <= (tileY) * 40 && yVelocity < 0){
            y = y;
        }
        else if(tileLeft > 2 && x <= (tileX) * 40 && xVelocity < 0){
            x = x;
        }
        else if(tileRight > 2 && x + getWidth() >= (tileX + 1) * 40 && xVelocity > 0){
            x = x;
        }
        //check corners now
        /*
        else if(tileAboveLeft != 0 && x <= tileX * 40 && y + getHeight() >= (tileY + 1) * 40 && xVelocity < 0){
            x = x;
        }
        else if(tileAboveLeft != 0 && x <= tileX * 40 && y + getHeight() >= (tileY + 1) * 40 && yVelocity > 0){
            y = y;
        }

        else if(tileAboveRight != 0 && x + getWidth() >= (tileX + 1) * 40 && y + getHeight() >= (tileY + 1) * 40 && xVelocity > 0){
            x = x;
        }
        else if(tileAboveRight != 0 && x + getWidth() >= (tileX + 1) * 40 && y + getHeight() >= (tileY + 1) * 40 && yVelocity > 0){
            y = y;
        }
        */
        else{
            y += time * yVelocity;
            x += time * xVelocity;

        }

        //float time = Gdx.graphics.getDeltaTime();
        //setX(x + time * xVelocity);
        //setY(y + time * yVelocity);
        //x += time * xVelocity;
        //y += time * yVelocity;
        xVelocity = 0;      //update velocity to 0, this effectively stops the paddle after the user is done pressing movement key
        yVelocity = 0;      //update velocity to 0, this effectively stops the paddle after the user is done pressing movement key
        isWalking = false;


    }
}
