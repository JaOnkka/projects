package wsuv.bounce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class WalkingAnimation {
    private Animation<TextureRegion> walkAnimation;
    private TextureRegion currentFrame;
    private float stateTime;
    private boolean isWalking;
    public boolean isFacingLeft;
    public WalkingAnimation(Texture walkSheet, int frameCols, int frameRows, float frameDuration) {
        TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth() / frameCols, walkSheet.getHeight() / frameRows);
        Array<TextureRegion> walkFrames = new Array<>();

        for (int i = 0; i < frameRows; i++) {
            for (int j = 0; j < frameCols; j++) {
                walkFrames.add(tmp[i][j]);
            }
        }

        walkAnimation = new Animation<>(frameDuration, walkFrames, Animation.PlayMode.LOOP);
        isWalking = false;
        isFacingLeft = false;

    }
    public void setDirection(boolean isFacingLeft) {
        this.isFacingLeft = isFacingLeft;
    }
    public void startWalking() {
        isWalking = true;
    }

    public void stopWalking() {
        isWalking = false;
    }

    public void update(float deltaTime) {
        if (isWalking) {
            stateTime += deltaTime;
            currentFrame = walkAnimation.getKeyFrame(stateTime);
        }
    }

    public boolean draw(Batch batch, float posx, float posy) {
        if (isWalking) {
            if (isFacingLeft) {
                batch.draw(currentFrame, posx + currentFrame.getRegionWidth(), posy, -currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
            } else {
                batch.draw(currentFrame, posx, posy);
            }
            return true;
        }
        return false;
    }
}

