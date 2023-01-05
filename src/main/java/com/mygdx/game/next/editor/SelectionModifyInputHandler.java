package com.mygdx.game.next.editor;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.World;
import com.mygdx.game.next.blocks.Block;
import com.mygdx.game.next.util.ModernPool;
import com.mygdx.game.next.util.Vector3i;
import com.mygdx.game.next.util.VectorUtil;

import lombok.RequiredArgsConstructor;

import static com.mygdx.game.next.raycast.RayCastUtil.getRay;

@RequiredArgsConstructor
public class SelectionModifyInputHandler extends InputAdapter {
  private final SelectionThingy selectionThingy;
  private final World world;
  private final Camera camera;

  private SelectionSide currentSide;
  private Vector3 startingPoint;
  private Plane hitPlane;

  private final Pool<Vector3> vecPool = new ModernPool<>(Vector3::new);
  private final Pool<BoundingBox> bbPool = new ModernPool<>(BoundingBox::new);

  private boolean shiftPressed;

  private final Map<SelectionSide, BoundingBox> boundingBoxCache =
      new EnumMap<>(SelectionSide.class);

  @Override
  public boolean keyDown(int keycode) {
    if (keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT) {
      shiftPressed = true;
    }
    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    if (keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT) {
      shiftPressed = false;
    }
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    if (button != 0) {
      return false;
    }
    Ray ray = getRay(screenX, screenY, camera);
    if (selectionThingy.getSelectionPrimary() != null) {
      var result = castSelection(ray);
      if (result.isEmpty()) {
        return false;
      }

      Map.Entry<SelectionSide, Vector3> nearest = getNearest(result);
      currentSide = nearest.getKey();
      startingPoint = nearest.getValue();

      if (startingPoint != null) {
        switch (currentSide) {
          case TOP, BOTTOM -> {
            var nvec = vecPool.obtain().set(ray.direction);
            nvec.y = 0;
            hitPlane = new Plane(nvec, startingPoint);
            vecPool.free(nvec);
          }
          case LEFT, RIGHT, FRONT, BACK -> hitPlane = new Plane(Vector3.Y, startingPoint);
        }
        return true;
      }
    }
    return false;
  }

  private Map.Entry<SelectionSide, Vector3> getNearest(Map<SelectionSide, Vector3> result) {
    return result.entrySet().stream()
        .min(
            (e1, e2) -> {
              var tmp = vecPool.obtain();
              float d1 = tmp.set(e1.getValue()).dst2(camera.position);
              float d2 = tmp.set(e2.getValue()).dst2(camera.position);
              return Float.compare(d1, d2) * (shiftPressed ? -1 : 1);
            })
        .orElse(null);
  }

  private Map<SelectionSide, Vector3> castSelection(Ray ray) {
    updateBoundingBoxCache();

    Map<SelectionSide, Vector3> map = new EnumMap<>(SelectionSide.class);
    var tmp = vecPool.obtain();
    if (Intersector.intersectRayBounds(ray, boundingBoxCache.get(SelectionSide.TOP), tmp)) {
      map.put(SelectionSide.TOP, tmp.cpy());
    }
    if (Intersector.intersectRayBounds(ray, boundingBoxCache.get(SelectionSide.BOTTOM), tmp)) {
      map.put(SelectionSide.BOTTOM, tmp.cpy());
    }
    if (Intersector.intersectRayBounds(ray, boundingBoxCache.get(SelectionSide.LEFT), tmp)) {
      map.put(SelectionSide.LEFT, tmp.cpy());
    }
    if (Intersector.intersectRayBounds(ray, boundingBoxCache.get(SelectionSide.RIGHT), tmp)) {
      map.put(SelectionSide.RIGHT, tmp.cpy());
    }
    if (Intersector.intersectRayBounds(ray, boundingBoxCache.get(SelectionSide.FRONT), tmp)) {
      map.put(SelectionSide.FRONT, tmp.cpy());
    }
    if (Intersector.intersectRayBounds(ray, boundingBoxCache.get(SelectionSide.BACK), tmp)) {
      map.put(SelectionSide.BACK, tmp.cpy());
    }
    vecPool.free(tmp);
    return map;
  }

  private void updateBoundingBoxCache() {
    var mini =
        VectorUtil.getMinimum(
            selectionThingy.getSelectionPrimary(), selectionThingy.getSelectionSecondary());
    var area =
        VectorUtil.getAreaVector(
            selectionThingy.getSelectionPrimary(), selectionThingy.getSelectionSecondary());
    var maxi = mini.cpy().add(area);

    var min = vecPool.obtain().set(mini.x * Block.S, mini.y * Block.S, mini.z * Block.S);
    var max =
        vecPool
            .obtain()
            .set((maxi.x + 1) * Block.S, (maxi.y + 1) * Block.S, (maxi.z + 1) * Block.S);

    if (!boundingBoxCache.isEmpty()) {
      bbPool.free(boundingBoxCache.get(SelectionSide.TOP));
      bbPool.free(boundingBoxCache.get(SelectionSide.BOTTOM));
      bbPool.free(boundingBoxCache.get(SelectionSide.FRONT));
      bbPool.free(boundingBoxCache.get(SelectionSide.BACK));
      bbPool.free(boundingBoxCache.get(SelectionSide.LEFT));
      bbPool.free(boundingBoxCache.get(SelectionSide.RIGHT));
    }
    boundingBoxCache.clear();
    var tmp = vecPool.obtain();
    {
      tmp.set(min);
      tmp.y = max.y - 0.1f;
      boundingBoxCache.put(SelectionSide.TOP, getBoundingBox(tmp, max));
    }
    {
      tmp.set(max);
      tmp.y = min.y - 0.1f;
      boundingBoxCache.put(SelectionSide.BOTTOM, getBoundingBox(min, tmp));
    }
    {
      tmp.set(max);
      tmp.x = min.x + 0.1f;
      boundingBoxCache.put(SelectionSide.LEFT, getBoundingBox(min, tmp));
    }
    {
      tmp.set(min);
      tmp.x = max.x - 0.1f;
      boundingBoxCache.put(SelectionSide.RIGHT, getBoundingBox(tmp, max));
    }
    {
      tmp.set(min);
      tmp.z = max.z - 0.1f;
      boundingBoxCache.put(SelectionSide.FRONT, getBoundingBox(tmp, max));
    }
    {
      tmp.set(max);
      tmp.z = min.z + 0.1f;
      boundingBoxCache.put(SelectionSide.BACK, getBoundingBox(min, tmp));
    }
    vecPool.free(tmp);
    vecPool.free(min);
    vecPool.free(max);
  }

  private BoundingBox getBoundingBox(Vector3 a, Vector3 b) {
    var bb = bbPool.obtain();
    bb.min.set(a);
    bb.max.set(b);
    bb.update();
    return bb;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    if (startingPoint != null) {
      var ray = getRay(screenX, screenY, camera);

      var tmp = vecPool.obtain();
      if (Intersector.intersectRayPlane(ray, hitPlane, tmp)) {
        if (currentSide == SelectionSide.TOP) {
          Supplier<Vector3i> getter;
          Consumer<Vector3i> setter;
          if (selectionThingy.getSelectionPrimary().y > selectionThingy.getSelectionSecondary().y) {
            getter = selectionThingy::getSelectionPrimary;
            setter = selectionThingy::setPrimarySelection;
          } else {
            getter = selectionThingy::getSelectionSecondary;
            setter = selectionThingy::setSecondarySelection;
          }

          var v = getter.get().cpy();
          v.y = (int) (tmp.y / Block.S);
          setter.accept(clampToWorld(v));
        } else if (currentSide == SelectionSide.BOTTOM) {
          Supplier<Vector3i> getter;
          Consumer<Vector3i> setter;
          if (selectionThingy.getSelectionPrimary().y < selectionThingy.getSelectionSecondary().y) {
            getter = selectionThingy::getSelectionPrimary;
            setter = selectionThingy::setPrimarySelection;
          } else {
            getter = selectionThingy::getSelectionSecondary;
            setter = selectionThingy::setSecondarySelection;
          }

          var v = getter.get().cpy();
          v.y = (int) (tmp.y / Block.S);
          setter.accept(clampToWorld(v));
        } else if (currentSide == SelectionSide.LEFT) {
          Supplier<Vector3i> getter;
          Consumer<Vector3i> setter;
          if (selectionThingy.getSelectionPrimary().x < selectionThingy.getSelectionSecondary().x) {
            getter = selectionThingy::getSelectionPrimary;
            setter = selectionThingy::setPrimarySelection;
          } else {
            getter = selectionThingy::getSelectionSecondary;
            setter = selectionThingy::setSecondarySelection;
          }

          var v = getter.get().cpy();
          v.x = (int) (tmp.x / Block.S);
          setter.accept(clampToWorld(v));
        } else if (currentSide == SelectionSide.RIGHT) {
          Supplier<Vector3i> getter;
          Consumer<Vector3i> setter;
          if (selectionThingy.getSelectionPrimary().x > selectionThingy.getSelectionSecondary().x) {
            getter = selectionThingy::getSelectionPrimary;
            setter = selectionThingy::setPrimarySelection;
          } else {
            getter = selectionThingy::getSelectionSecondary;
            setter = selectionThingy::setSecondarySelection;
          }

          var v = getter.get().cpy();
          v.x = (int) (tmp.x / Block.S);
          setter.accept(clampToWorld(v));
        } else if (currentSide == SelectionSide.BACK) {
          Supplier<Vector3i> getter;
          Consumer<Vector3i> setter;
          if (selectionThingy.getSelectionPrimary().z < selectionThingy.getSelectionSecondary().z) {
            getter = selectionThingy::getSelectionPrimary;
            setter = selectionThingy::setPrimarySelection;
          } else {
            getter = selectionThingy::getSelectionSecondary;
            setter = selectionThingy::setSecondarySelection;
          }

          var v = getter.get().cpy();
          v.z = (int) (tmp.z / Block.S);
          setter.accept(clampToWorld(v));
        } else if (currentSide == SelectionSide.FRONT) {
          Supplier<Vector3i> getter;
          Consumer<Vector3i> setter;
          if (selectionThingy.getSelectionPrimary().z > selectionThingy.getSelectionSecondary().z) {
            getter = selectionThingy::getSelectionPrimary;
            setter = selectionThingy::setPrimarySelection;
          } else {
            getter = selectionThingy::getSelectionSecondary;
            setter = selectionThingy::setSecondarySelection;
          }

          var v = getter.get().cpy();
          v.z = (int) (tmp.z / Block.S);
          setter.accept(clampToWorld(v));
        }
      }
      vecPool.free(tmp);

      return true;
    }
    return false;
  }

  private Vector3i clampToWorld(Vector3i v) {
    if (v.y < 0) {
      v.y = 0;
    }
    if (v.y >= world.getH()) {
      v.y = world.getH() - 1;
    }
    if (v.x < 0) {
      v.x = 0;
    }
    if (v.x >= world.getW()) {
      v.x = world.getW() - 1;
    }
    if (v.z < 0) {
      v.z = 0;
    }
    if (v.z >= world.getD()) {
      v.z = world.getD() - 1;
    }
    return v;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    if (button != 0) {
      return false;
    }
    if (currentSide != null) {
      currentSide = null;
      startingPoint = null;
      hitPlane = null;
      return true;
    } else {
      return false;
    }
  }

  enum SelectionSide {
    LEFT,
    RIGHT,
    FRONT,
    BACK,
    TOP,
    BOTTOM
  }
}
