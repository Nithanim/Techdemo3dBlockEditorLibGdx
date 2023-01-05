package com.mygdx.game.next.shapes;

import java.util.List;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.next.blocks.BlockOrientation;
import com.mygdx.game.next.blocks.BlockProperties;
import com.mygdx.game.next.blocks.Face;

import static com.mygdx.game.next.blocks.Block.S;

public class FaceGenerator {

  public static final Vector3 FRONT_BOTTOM_LEFT = new Vector3(0, 0, S);
  public static final Vector3 FRONT_BOTTOM_RIGHT = new Vector3(S, 0, S);
  public static final Vector3 BACK_BOTTOM_LEFT = new Vector3(0, 0, 0);
  public static final Vector3 BACK_BOTTOM_RIGHT = new Vector3(S, 0, 0);
  public static final Vector3 FRONT_TOP_LEFT = new Vector3(0, S, S);
  public static final Vector3 FRONT_TOP_RIGHT = new Vector3(S, S, S);
  public static final Vector3 BACK_TOP_LEFT = new Vector3(0, S, 0);
  public static final Vector3 BACK_TOP_RIGHT = new Vector3(S, S, 0);

  public static final Vector3 NORMAL_LEFT = new Vector3(-1, 0, 0);
  public static final Vector3 NORMAL_RIGHT = new Vector3(1, 0, 0);
  public static final Vector3 NORMAL_BACK = new Vector3(0, 0, -1);
  public static final Vector3 NORMAL_FRONT = new Vector3(0, 0, 1);
  public static final Vector3 NORMAL_TOP = new Vector3(0, 1, 0);

  public static List<Face> get(BlockProperties properties) {
    var sides = properties.getSides();
    var blockOrientation = properties.getOrientation();
    var baseHeight = properties.getH1();
    var topHeight = properties.getH2();
    return switch (properties.getType()) {
      case QUBE -> new QubeGenerator().get(sides, blockOrientation);
        // case SLOPE_QUARTER -> getSlope(0, 0.5f, sides, blockOrientation);
      case PILLAR_CENTER -> new PillarCenterGenerator().get(sides, blockOrientation);
      case PILLAR_CORNER -> new PillarCornerGenerator().get(sides, blockOrientation);
      case SLOPE_HALF -> new SlopeGenerator().get(sides, blockOrientation, baseHeight, topHeight);
      default -> throw new UnsupportedOperationException();
    };
  }

  private static Vector3[] shiftArray(Vector3[] src, int steps) {
    Vector3[] vectors = new Vector3[4];
    System.arraycopy(src, 0, vectors, steps, 4 - steps);
    System.arraycopy(src, 4 - steps, vectors, 0, steps);
    return vectors;
  }

  private static int getRotationSteps(BlockOrientation blockOrientation) {
    return switch (blockOrientation) {
      case NORTH -> 0;
      case EAST -> 1;
      case SOUTH -> 2;
      case WEST -> 3;
    };
  }

  private static boolean isElevated(
      LogicalPosition logicalPosition, BlockOrientation blockOrientation) {
    return switch (logicalPosition) {
      case TOP_LEFT -> switch (blockOrientation) {
        case NORTH, WEST -> true;
        case EAST, SOUTH -> false;
      };
      case TOP_RIGHT -> switch (blockOrientation) {
        case NORTH, EAST -> true;
        case SOUTH, WEST -> false;
      };
      case BOTTOM_RIGHT -> switch (blockOrientation) {
        case EAST, SOUTH -> true;
        case NORTH, WEST -> false;
      };
      case BOTTOM_LEFT -> switch (blockOrientation) {
        case SOUTH, WEST -> true;
        case NORTH, EAST -> false;
      };
    };
  }

  /** If looked on a quad/triangle from the front, specifies the corner. */
  private enum LogicalPosition {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_RIGHT,
    BOTTOM_LEFT
  }
}
