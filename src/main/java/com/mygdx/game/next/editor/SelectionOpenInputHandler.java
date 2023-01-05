package com.mygdx.game.next.editor;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.collision.Ray;
import com.mygdx.game.World;
import com.mygdx.game.next.raycast.BlockCaster;
import com.mygdx.game.next.util.Vector3i;

import lombok.RequiredArgsConstructor;

import static com.mygdx.game.next.raycast.RayCastUtil.getRay;

@RequiredArgsConstructor
public class SelectionOpenInputHandler extends InputAdapter {
  private final SelectionThingy selectionThingy;
  private final World world;
  private final Camera camera;

  private boolean selectionOpeningDragActive;

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    if (button == 0) {
      Ray ray = getRay(screenX, screenY, camera);
      Vector3i selection = new BlockCaster(world).cast(ray, camera);

      if (selection == null) {
        selectionThingy.setPrimarySelection(null);
        selectionThingy.setSecondarySelection(null);
      } else if (selection.equals(selectionThingy.getSelectionPrimary())) {
        selectionOpeningDragActive = true;
      } else if (selection.equals(selectionThingy.getSelectionSecondary())) {
        selectionThingy.setSecondarySelection(selectionThingy.getSelectionPrimary());
        selectionThingy.setPrimarySelection(selection);
        selectionOpeningDragActive = true;
      } else {
        selectionThingy.setPrimarySelection(selection);
        selectionThingy.setSecondarySelection(selection);
        selectionOpeningDragActive = true;
      }

      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    if (selectionOpeningDragActive) {
      Ray ray = getRay(screenX, screenY, camera);
      Vector3i selection = new BlockCaster(world).cast(ray, camera);
      selectionThingy.setPrimarySelection(selection);
    }
    return false; // Allows further processing for other drag event of other handlers.
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    if (button == 0) {
      selectionOpeningDragActive = false;
      return true;
    } else {
      return false;
    }
  }
}
