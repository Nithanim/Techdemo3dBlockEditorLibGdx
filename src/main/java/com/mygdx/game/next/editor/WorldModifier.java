package com.mygdx.game.next.editor;

import java.util.function.Function;

import com.badlogic.gdx.Input;
import com.mygdx.game.ModernInputProcessor;
import com.mygdx.game.World;
import com.mygdx.game.next.blocks.Block;
import com.mygdx.game.next.blocks.BlockOrientation;
import com.mygdx.game.next.blocks.BlockProperties;
import com.mygdx.game.next.util.Vector3i;
import com.mygdx.game.next.util.VectorUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorldModifier implements ModernInputProcessor {
  private final World world;

  private final SelectionThingy selectionThingy;

  boolean shiftPressed;
  boolean ctrlPressed;

  @Override
  public boolean keyDown(int keycode) {
    switch (keycode) {
      case Input.Keys.SHIFT_LEFT:
      case Input.Keys.SHIFT_RIGHT:
        shiftPressed = true;
        return false;
      case Input.Keys.CONTROL_LEFT:
      case Input.Keys.CONTROL_RIGHT:
        ctrlPressed = true;
        return false;
      case Input.Keys.R:
        rotate();
        return true;
      case Input.Keys.FORWARD_DEL:
        delete();
        return true;
      case Input.Keys.C:
        if (ctrlPressed) {
          copy();
        }
        return true;
      case Input.Keys.V:
        if (ctrlPressed) {
          paste();
        }
        return true;
      default:
        return false;
    }
  }

  @Override
  public boolean keyUp(int keycode) {
    switch (keycode) {
      case Input.Keys.SHIFT_LEFT:
      case Input.Keys.SHIFT_RIGHT:
        shiftPressed = false;
        return false;
      case Input.Keys.CONTROL_LEFT:
      case Input.Keys.CONTROL_RIGHT:
        ctrlPressed = false;
        return false;
      default:
        return false;
    }
  }

  private void rotate() {
    Vector3i selection = selectionThingy.getSelectionPrimary();
    if (selection != null) {
      Block chunk = world.getBlockAt(selection);
      if (chunk != null) {
        Function<BlockOrientation, BlockOrientation> f =
            shiftPressed ? BlockOrientation::turnCounterClockwise : BlockOrientation::turnClockwise;

        BlockProperties oldProps = chunk.getProperties();
        var newProps = oldProps.withOrientation(f.apply(oldProps.getOrientation()));
        chunk.setProperties(newProps);
      }
    }
  }

  private void delete() {
    Vector3i selectionPrimary = selectionThingy.getSelectionPrimary();
    if (selectionPrimary != null) {

      var minimum =
          VectorUtil.getMinimum(selectionPrimary, selectionThingy.getSelectionSecondary());
      var area =
          VectorUtil.getAreaVector(selectionPrimary, selectionThingy.getSelectionSecondary());

      for (int x = minimum.x; x <= minimum.x + area.x; x++) {
        for (int y = minimum.y; y <= minimum.y + area.y; y++) {
          for (int z = minimum.z; z <= minimum.z + area.z; z++) {
            world.setBlockAt(x, y, z, null);
          }
        }
      }
    }
  }

  private Block copy;

  private void copy() {
    Vector3i selection = selectionThingy.getSelectionPrimary();
    if (selection != null) {
      Block block = world.getBlockAt(selection);
      if (block != null) {
        this.copy = block;
      }
    }
  }

  private void paste() {
    if (copy == null) {
      return;
    }
    Vector3i selection = selectionThingy.getSelectionPrimary();
    if (selection != null) {
      var minimum = VectorUtil.getMinimum(selection, selectionThingy.getSelectionSecondary());
      var area = VectorUtil.getAreaVector(selection, selectionThingy.getSelectionSecondary());

      for (int x = minimum.x; x <= minimum.x + area.x; x++) {
        for (int y = minimum.y; y <= minimum.y + area.y; y++) {
          for (int z = minimum.z; z <= minimum.z + area.z; z++) {
            Block block = new Block(new Vector3i(x, y, z), copy.getProperties());
            world.setBlockAt(x, y, z, block);
          }
        }
      }
    }
  }
}
