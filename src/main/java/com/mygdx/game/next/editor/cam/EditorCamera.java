package com.mygdx.game.next.editor.cam;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;

public class EditorCamera extends GestureDetector {

  /** The button for rotating the camera. */
  public int rotateButton = Buttons.RIGHT;
  /** The angle to rotate when moved the full width or height of the screen. */
  public float rotateAngle = 360f;
  /** The button for translating the camera along the up/right plane */
  public int translateButton = Buttons.LEFT;
  /** The units to translate the camera when moved the full width or height of the screen. */
  public float translateUnits = 40f;
  /** The button for translating the camera along the direction axis */
  public int forwardButton = Buttons.MIDDLE;
  /**
   * The key which must be pressed to activate rotate, translate and forward or 0 to always
   * activate.
   */
  public int activateKey = 1;

  private final Set<KeyboardAction> keyboardPressedKey = EnumSet.noneOf(KeyboardAction.class);

  /** Indicates if the activateKey is currently being pressed. */
  protected boolean activatePressed;
  /**
   * Whether scrolling requires the activeKey to be pressed (false) or always allow scrolling
   * (true).
   */
  public boolean alwaysScroll = true;
  /** The weight for each scrolled amount. */
  public float scrollFactor = -0.1f;
  /** World units per screen size */
  public float pinchZoomFactor = 10f;
  /** Whether to update the camera after it has been changed. */
  public boolean autoUpdate = true;

  private final IntMap<KeyboardAction> keyboardKeyAssignments = new IntMap<>();

  {
    keyboardKeyAssignments.put(Keys.W, KeyboardAction.FORWARD);
    keyboardKeyAssignments.put(Keys.S, KeyboardAction.BACKWARD);
    keyboardKeyAssignments.put(Keys.A, KeyboardAction.LEFT);
    keyboardKeyAssignments.put(Keys.D, KeyboardAction.RIGHT);
  }

  public Camera camera;
  /** The current (first) button being pressed. */
  protected int button = -1;

  private int dragStartX, dragStartY;
  private final Vector3 tmp1 = new Vector3();
  private final Vector3 tmp2 = new Vector3();

  protected static class CameraGestureListener extends GestureAdapter {
    public EditorCamera controller;
    private float previousZoom;

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
      previousZoom = 0;
      return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
      float newZoom = distance - initialDistance;
      float amount = newZoom - previousZoom;
      previousZoom = newZoom;
      float w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
      return controller.pinchZoom(amount / Math.min(w, h));
    }
  }

  protected final CameraGestureListener gestureListener;

  protected EditorCamera(final CameraGestureListener gestureListener, final Camera camera) {
    super(gestureListener);
    this.gestureListener = gestureListener;
    this.gestureListener.controller = this;
    this.camera = camera;
  }

  public EditorCamera(final Camera camera) {
    this(new CameraGestureListener(), camera);
  }

  public void update() {
    if (!keyboardPressedKey.isEmpty()) {
      final float delta = Gdx.graphics.getDeltaTime();
      for (KeyboardAction keyboardAction : keyboardPressedKey) {
        keyboardActionHandlers.get(keyboardAction).onAction(delta);
      }
      if (autoUpdate) {
        camera.update();
      }
    }
  }

  private final EnumMap<KeyboardAction, KeyboardActionHandler> keyboardActionHandlers =
      new EnumMap<>(KeyboardAction.class);

  {
    keyboardActionHandlers.put(
        KeyboardAction.FORWARD,
        (delta) -> camera.translate(tmp1.set(camera.direction).scl(delta * translateUnits)));
    keyboardActionHandlers.put(
        KeyboardAction.BACKWARD,
        (delta) -> camera.translate(tmp1.set(camera.direction).scl(-delta * translateUnits)));
    keyboardActionHandlers.put(
        KeyboardAction.LEFT,
        (delta) ->
            camera.translate(
                tmp1.set(camera.up).crs(camera.direction).nor().scl(delta * translateUnits)));
    keyboardActionHandlers.put(
        KeyboardAction.RIGHT,
        (delta) ->
            camera.translate(
                tmp1.set(camera.direction).crs(camera.up).nor().scl(delta * translateUnits)));
  }

  @FunctionalInterface
  private interface KeyboardActionHandler {
    void onAction(float timeDelta);
  }

  private int touched;
  private boolean multiTouch;

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    touched |= (1 << pointer);
    multiTouch = !MathUtils.isPowerOfTwo(touched);
    if (multiTouch) {
      this.button = -1;
    } else if (this.button < 0 && (activateKey == button || activatePressed)) {
      dragStartX = screenX;
      dragStartY = screenY;
      this.button = button;
      Gdx.input.setCursorCatched(true);
    }

    return super.touchDown(screenX, screenY, pointer, button)
        || (activateKey == 0 || activatePressed);
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    touched &= ~(1 << pointer);
    multiTouch = !MathUtils.isPowerOfTwo(touched);
    if (button == this.button) {
      this.button = -1;
    }
    Gdx.input.setCursorCatched(false);
    return super.touchUp(screenX, screenY, pointer, button) || activatePressed;
  }

  protected boolean process(float deltaX, float deltaY, int button) {
    if (button == rotateButton) {
      tmp1.set(camera.direction).crs(camera.up).y = 0f;
      tmp1.nor();
      camera.rotate(tmp1, deltaY * rotateAngle);
      camera.rotate(Vector3.Y, deltaX * -rotateAngle);
    } else if (button == translateButton) {
      camera.translate(
          tmp1.set(camera.direction).crs(camera.up).nor().scl(-deltaX * translateUnits));
      camera.translate(tmp2.set(camera.up).scl(-deltaY * translateUnits));
    } else if (button == forwardButton) {
      camera.translate(tmp1.set(camera.direction).scl(deltaY * translateUnits));
    }
    if (autoUpdate) camera.update();
    return true;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    boolean result = super.touchDragged(screenX, screenY, pointer);
    if (result || this.button < 0) return result;
    final float deltaX = (screenX * 1f - dragStartX) / Gdx.graphics.getWidth();
    final float deltaY = (dragStartY * 1f - screenY) / Gdx.graphics.getHeight();
    dragStartX = screenX;
    dragStartY = screenY;
    return process(deltaX, deltaY, button);
  }

  @Override
  public boolean scrolled(float amountX, float amountY) {
    return zoom(amountY * scrollFactor * translateUnits);
  }

  public boolean zoom(float amount) {
    if (!alwaysScroll && activateKey != 0 && !activatePressed) return false;
    camera.translate(tmp1.set(camera.direction).scl(amount));
    if (autoUpdate) camera.update();
    return true;
  }

  protected boolean pinchZoom(float amount) {
    return zoom(pinchZoomFactor * amount);
  }

  @Override
  public boolean keyDown(int keycode) {
    if (keycode == activateKey) {
      activatePressed = true;
    }
    var action = keyboardKeyAssignments.get(keycode);
    if (action != null) {
      keyboardPressedKey.add(action);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean keyUp(int keycode) {
    if (keycode == activateKey) {
      activatePressed = false;
      button = -1;
    }
    var action = keyboardKeyAssignments.get(keycode);
    if (action != null) {
      keyboardPressedKey.remove(action);
      return true;
    } else {
      return false;
    }
  }

  private enum KeyboardAction {
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT
  }

  private enum MouseAction {
    ROTATE
  }
}
