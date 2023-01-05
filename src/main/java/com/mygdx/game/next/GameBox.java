package com.mygdx.game.next;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.World;
import com.mygdx.game.next.blocks.Block;

import static com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA;
import static com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA;

public class GameBox extends ApplicationAdapter {
  private final Consumer<ModelBatch> onRenderWorld;
  private final Consumer<ModelBatch> onPostRenderWorld;
  private final List<Disposable> stuffToDispose = new ArrayList<>();
  private PerspectiveCamera camera;
  private ModelBatch modelBatch;
  private Environment environment;
  private World world;

  public GameBox(Consumer<ModelBatch> onRenderWorld, Consumer<ModelBatch> onPostRenderWorld) {
    this.onRenderWorld = onRenderWorld;
    this.onPostRenderWorld = onPostRenderWorld;
  }

  @Override
  public void create() {
    world = new World(10, 5, 10);

    camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    camera.position.set(0f, Block.S * world.getH() + 10f, 40f);
    camera.lookAt(20, Block.S * 2 + world.getH(), 20);
    camera.near = 1f;
    camera.far = 500f;
    camera.update(true);

    stuffToDispose.add(world);

    RenderContext rc = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.LRU, 1));
    rc.setDepthMask(false);
    rc.setBlending(true, GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    modelBatch = new ModelBatch();

    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
    environment.add(new PointLight().set(0.6f, 0.4f, 0.3f, 8f, Block.S * 5 + 10f, -6f, 200));
  }

  @Override
  public void render() {
    modelBatch.begin(camera);
    modelBatch.render(world, environment);
    onRenderWorld.accept(modelBatch);
    modelBatch.end();

    modelBatch.begin(camera);
    onPostRenderWorld.accept(modelBatch);
    modelBatch.end();
  }

  @Override
  public void resize(int width, int height) {
    camera.viewportWidth = width;
    camera.viewportHeight = height;
    camera.update(true);
  }

  @Override
  public void dispose() {
    stuffToDispose.forEach(Disposable::dispose);
  }

  public PerspectiveCamera getCamera() {
    return camera;
  }

  public World getWorld() {
    return world;
  }
}
