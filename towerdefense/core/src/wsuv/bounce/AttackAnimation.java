package wsuv.bounce;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

public class AttackAnimation {
    private Animation<TextureRegion> attackAnimation;
    private TextureRegion currentFrame;
    public float stateTime;
    public boolean isAttacking;


    public AttackAnimation(Texture attackSheet, int frameCols, int frameRows, float frameDuration) {
        TextureRegion[][] tmp = TextureRegion.split(attackSheet, attackSheet.getWidth() / frameCols, attackSheet.getHeight() / frameRows);
        Array<TextureRegion> attackFrames = new Array<>();

        for (int i = 0; i < frameRows; i++) {
            for (int j = 0; j < frameCols; j++) {
                attackFrames.add(tmp[i][j]);
            }
        }

        attackAnimation = new Animation<>(frameDuration, attackFrames, Animation.PlayMode.NORMAL);
        isAttacking = false;
    }

    public void startAttack() {
        isAttacking = true;
    }

    public void stopAttack() {
        isAttacking = false;
    }

    public void update(float deltaTime) {
        if (isAttacking) {
            stateTime += deltaTime;
            currentFrame = attackAnimation.getKeyFrame(stateTime);
        }
    }

    public boolean draw(Batch batch, float posx, float posy, boolean isFacingLeft) {
        if (isAttacking) {
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

