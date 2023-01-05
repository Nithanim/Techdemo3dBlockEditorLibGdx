package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.next.GameBox;
import com.mygdx.game.next.blocks.Block;
import com.mygdx.game.next.editor.BlockSelectionChangedEvent;
import com.mygdx.game.next.editor.SelectionModifyInputHandler;
import com.mygdx.game.next.editor.SelectionOpenInputHandler;
import com.mygdx.game.next.editor.SelectionThingy;
import com.mygdx.game.next.editor.WorldModifier;
import com.mygdx.game.next.editor.cam.EditorCamera;
import com.mygdx.game.next.editor.ui.EditorUi;
import com.mygdx.game.next.events.EventMagic;

public class MyGdxGame extends ApplicationAdapter {
  SpriteBatch batch;
  Texture img;
  private final List<Disposable> stuffToDispose = new ArrayList<>();
  private ModelInstance axis;
  private BitmapFont font;
  private EditorCamera camController;

  private SelectionThingy selectionThingy;
  private EditorUi editorUi;
  private GameBox gameBox;

  @Override
  public void create() {
    gameBox =
        new GameBox(
            modelBatch -> {
              modelBatch.render(axis);
              // modelBatch.render(selectionMarker);

              modelBatch.render(selectionThingy);
            },
            modelBatch -> {
              Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
              modelBatch.render(selectionThingy);
            });
    gameBox.create();

    // camController = new CameraInputController(cam);
    camController = new EditorCamera(gameBox.getCamera());
    var world = gameBox.getWorld();
    var camera = gameBox.getCamera();

    var selectionMagic = new EventMagic<BlockSelectionChangedEvent>();
    selectionThingy = new SelectionThingy(selectionMagic);
    var selectionOpenInputHandler = new SelectionOpenInputHandler(selectionThingy, world, camera);
    var selectionModifyInputHandler =
        new SelectionModifyInputHandler(selectionThingy, world, camera);
    var worldModifier = new WorldModifier(world, selectionThingy);

    stuffToDispose.add(world);

    {
      ModelBuilder modelBuilder = new ModelBuilder();
      Model axisModel =
          modelBuilder.createXYZCoordinates(
              5 * Block.S,
              new Material(),
              VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked);
      stuffToDispose.add(axisModel);
      axis = new ModelInstance(axisModel);
      axis.transform.translate(0, 0, 0);
    }

    batch = new SpriteBatch();
    img = new Texture("badlogic.jpg");

    font = new BitmapFont();
    stuffToDispose.add(font);

    editorUi = new EditorUi(selectionMagic, world);
    editorUi.create();

    Gdx.input.setInputProcessor(
        new InputMultiplexer(
            editorUi.getStage(),
            worldModifier,
            selectionModifyInputHandler,
            selectionOpenInputHandler,
            camController));
  }

  @Override
  public void render() {
    Gdx.graphics.getDeltaTime();

    camController.update();

    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1, true);

    gameBox.render();

    batch.begin();
    // batch.draw(img, 0, 0, 100, 100);
    // font.draw(batch, "Test", 100, 100);
    batch.end();

    editorUi.render();
  }

  @Override
  public void resize(int width, int height) {
    gameBox.resize(width, height);
    batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);

    editorUi.resize(width, height);
  }

  @Override
  public void dispose() {
    gameBox.dispose();
    batch.dispose();
    img.dispose();
    stuffToDispose.forEach(Disposable::dispose);

    editorUi.dispose();
  }
}
