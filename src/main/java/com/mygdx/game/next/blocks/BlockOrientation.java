package com.mygdx.game.next.blocks;

public enum BlockOrientation {
  NORTH(0),
  SOUTH(2),
  EAST(1),
  WEST(3);

  private static final BlockOrientation[] arr =
      new BlockOrientation[BlockOrientation.values().length];

  static {
    for (BlockOrientation value : values()) {
      arr[value.getRotation()] = value;
    }
  }

  public static BlockOrientation fromRotation(int rotation) {
    return arr[rotation];
  }

  private final int rotation;

  BlockOrientation(int rotation) {
    this.rotation = rotation;
  }

  public int getRotation() {
    return rotation;
  }

  public BlockOrientation turnClockwise() {
    return fromRotation((rotation + 1) % 4);
  }

  public BlockOrientation turnCounterClockwise() {
    return fromRotation((rotation - 1 + 4) % 4);
  }
}
