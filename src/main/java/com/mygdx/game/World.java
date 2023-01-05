package com.mygdx.game;

import java.util.EnumSet;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.next.blocks.Block;
import com.mygdx.game.next.blocks.BlockOrientation;
import com.mygdx.game.next.blocks.BlockProperties;
import com.mygdx.game.next.blocks.Side;
import com.mygdx.game.next.blocks.Type;
import com.mygdx.game.next.util.Vector3i;

import lombok.Getter;

public class World implements RenderableProvider, Disposable {
  private final Block[] blocks;
  @Getter private final int w;
  @Getter private final int h;

  @Getter private final int d;

  public World(int w, int h, int d) {
    this.w = w;
    this.d = d;
    this.h = h;
    blocks = new Block[w * this.h * d];

    for (int x = 0; x < w; x++) {
      for (int z = 0; z < d; z++) {
        var coords = new Vector3i(x, 0, z);
        setBlockAt(
            coords,
            new Block(
                coords,
                new BlockProperties(
                    Type.QUBE, BlockOrientation.NORTH, EnumSet.allOf(Side.class), 0, 1)));
      }
    }
    {
      Vector3i p = new Vector3i(0, 1, 0);
      EnumSet<Side> sides = EnumSet.allOf(Side.class);
      sides.remove(Side.LEFT);
      setBlockAt(
          p,
          new Block(
              p, new BlockProperties(Type.SLOPE_HALF, BlockOrientation.EAST, sides, 0, 0.5f)));
    }

    {
      Vector3i p = new Vector3i(1, 1, 0);
      EnumSet<Side> sides = EnumSet.allOf(Side.class);
      sides.remove(Side.LEFT);
      setBlockAt(
          p,
          new Block(
              p, new BlockProperties(Type.SLOPE_HALF, BlockOrientation.EAST, sides, 0.5f, 1.0f)));
    }

    {
      Vector3i p = new Vector3i(2, 1, 0);
      EnumSet<Side> sides = EnumSet.allOf(Side.class);
      sides.remove(Side.LEFT);
      setBlockAt(
          p,
          new Block(
              p, new BlockProperties(Type.SLOPE_HALF, BlockOrientation.NORTH, sides, 0.5f, 1.0f)));
    }

    for (Block chunk : blocks) {
      if (chunk != null) {
        chunk.update();
      }
    }
  }

  public void setBlockAt(Vector3i coords, Block block) {
    setBlockAt(coords.x, coords.y, coords.z, block);
  }

  public void setBlockAt(int x, int y, int z, Block block) {
    ensureValidCoordinates(x, y, z);
    blocks[toArrIndex(x, y, z)] = block;
  }

  public Block getBlockAt(Vector3i coords) {
    return getBlockAt(coords.x, coords.y, coords.z);
  }

  public Block getBlockAt(int x, int y, int z) {
    ensureValidCoordinates(x, y, z);
    return blocks[toArrIndex(x, y, z)];
  }

  private int toArrIndex(Vector3i coords) {
    return toArrIndex(coords.x, coords.y, coords.z);
  }

  private int toArrIndex(int x, int y, int z) {
    // https://stackoverflow.com/a/72590610/2060704
    return (z * w * h) + (y * w) + x;
  }

  public void ensureValidCoordinates(Vector3i coords) {
    ensureValidCoordinates(coords.x, coords.y, coords.z);
  }

  public void ensureValidCoordinates(int x, int y, int z) {
    ensureValidX(x);
    ensureValidY(y);
    ensureValidZ(z);
  }

  private void ensureValidZ(int z) {
    if (!(0 <= z && z < d)) {
      throw new IllegalStateException();
    }
  }

  private void ensureValidY(int y) {
    if (!(0 <= y && y < h)) {
      throw new IllegalStateException();
    }
  }

  private void ensureValidX(int x) {
    if (!(0 <= x && x < w)) {
      throw new IllegalStateException();
    }
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    var tracker = new IdentityMap<>();
    for (Block block : blocks) {
      if (block != null) {
        if (tracker.put(block, Void.class) == null) {
          block.getRenderables(renderables, pool);
        }
      }
    }
  }

  @Override
  public void dispose() {
    var tracker = new IdentityMap<>();
    for (Block block : blocks) {
      if (block != null) {
        if (tracker.put(block, Void.class) == null) {
          block.dispose();
        }
      }
    }
  }
}
