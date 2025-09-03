package wsuv.bounce;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

import java.util.Random;

public class BounceGame extends Game {
    public static final int RSC_EXPLOSION_FRAMES_ROWS = 1;
    public static final int RSC_EXPLOSION_FRAMES_COLS = 8;
    public static final String RSC_EXPLOSION_FRAMES = "Walk.png";
    public static final String RSC_ATTACKING_FRAMES = "Shot_1.png";


    public static final String RSC_GAMEOVER_IMG = "gameover.png";
    public static final String RSC_PRESSAKEY_IMG = "pressakey.png";
    public static final String RSC_MONO_FONT_FILE = "JetBrainsMono-Regular.ttf";
    public static final String RSC_MONO_FONT = "JBM.ttf";
    public static final String RSC_EMPTYTILE = "emptytile.png";
    public static final String RSC_ENEMYTILE = "enemy.png";
    public static final String RSC_ENEMY = "enemy1.png";
    public static final String RSC_FIELD = "FieldsTileset.png";
    public static final String RSC_WALLTILE = "wall.png";
    public static final String RSC_PLAYERTILE = "player.png";
    public static final String RSC_BASETILE = "base.png";
    public static final String RSC_TOWER_BASIC = "tower_basic.png";
    public static final String RSC_EXPLOSION_SFX = "explosion7s.wav";
    public static final String RSC_SIDEPANEL = "sidepanel.png";
    public static final String RSC_BULLET = "bullet.png";

    public static final String RSC_WALLWOOD = "wall_wood.png";
    public static final String RSC_WALLSTONE = "wall_stone.png";
    public static final String RSC_WALLGOLD = "wall_gold.png";
    public static final String RSC_WALLOBSIDIAN = "wall_obsidian.png";
    public static final String RSC_START = "start.png";
    public static final String RSC_STARTENDLESS = "startendless.png";

    public static final String RSC_Music_MP3 = "Loyalty Freak Music - Slow Pogo.mp3";

    //public static Texture emptytile;
    //public static Texture enemytile;
    //public static Texture walltile;
    //public static Texture playertile;
  //  public static Texture basetile;

    AssetManager am;  // AssetManager provides a single source for loaded resources
    SpriteBatch batch;

    Random random = new Random();

    Music music;
    @Override
    public void create() {
        am = new AssetManager();

		/* True Type Fonts are a bit of a pain. We need to tell the AssetManager
           a bit more than simply the file name in order to get them into an
           easily usable (BitMap) form...
		 */
        FileHandleResolver resolver = new InternalFileHandleResolver();
        am.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        am.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        FreetypeFontLoader.FreeTypeFontLoaderParameter myFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        myFont.fontFileName = RSC_MONO_FONT_FILE;
        myFont.fontParameters.size = 14;
        am.load(RSC_MONO_FONT, BitmapFont.class, myFont);

        // Load Textures after the font...
        am.load(RSC_GAMEOVER_IMG, Texture.class);
        am.load(RSC_PRESSAKEY_IMG, Texture.class);
        am.load(RSC_EXPLOSION_FRAMES, Texture.class);
        am.load(RSC_ATTACKING_FRAMES, Texture.class);
        am.load(RSC_FIELD, Texture.class);
        am.load(RSC_SIDEPANEL, Texture.class);

        am.load(RSC_BULLET, Texture.class);
        am.load(RSC_TOWER_BASIC, Texture.class);
        am.load(RSC_EMPTYTILE, Texture.class);
        am.load(RSC_SIDEPANEL, Texture.class);
        am.load(RSC_START, Texture.class);
        am.load(RSC_STARTENDLESS, Texture.class);





        am.load(RSC_EMPTYTILE, Texture.class);
        am.load(RSC_ENEMYTILE, Texture.class);
        am.load(RSC_ENEMY, Texture.class);

        am.load(RSC_WALLTILE, Texture.class);
        am.load(RSC_PLAYERTILE, Texture.class);
        am.load(RSC_BASETILE, Texture.class);
        am.load(RSC_WALLWOOD, Texture.class);
        am.load(RSC_WALLSTONE, Texture.class);
        am.load(RSC_WALLGOLD, Texture.class);
        am.load(RSC_WALLOBSIDIAN, Texture.class);
        am.load(RSC_Music_MP3, Music.class);


        am.load(RSC_TOWER_BASIC, Texture.class);

        // Load Sounds
        am.load(RSC_EXPLOSION_SFX, Sound.class);

        batch = new SpriteBatch();
        setScreen(new LoadScreen(this));

        // start the music right away.
        // this one we'll only reference via the GameInstance, and it's streamed
        // so, no need to add it to the AssetManager...
        //music = Gdx.audio.newMusic(Gdx.files.internal("sadshark.mp3"));
        //music.setLooping(true);
        //music.setVolume(.5f);
        //music.play();
    }

    @Override
    public void dispose() {
        batch.dispose();
        am.dispose();
    }
}