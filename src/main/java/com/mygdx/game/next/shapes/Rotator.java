package com.mygdx.game.next.shapes;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.next.Vertex;
import com.mygdx.game.next.blocks.BlockOrientation;
import com.mygdx.game.next.blocks.Face;
import com.mygdx.game.next.blocks.Side;

import lombok.experimental.UtilityClass;

import static com.mygdx.game.next.blocks.Block.S;

@UtilityClass
public class Rotator {

  public static void rotate(List<Face> faces, BlockOrientation blockOrientation) {
    for (Face face : faces) {
      rotate(face, blockOrientation);
    }
  }

  public static void rotate(Face face, BlockOrientation blockOrientation) {
    Matrix4 t =
        new Matrix4()
            .translate(S / 2, 0, S / 2)
            .rotate(0f, 1f, 0, -90f * blockOrientation.getRotation())
            .translate(-S / 2, 0, -S / 2);
    for (Vertex v : face.vertices()) {
      v.getPosition().mul(t);
    }
  }

  public static EnumSet<Side> offsetSides(Set<Side> sides, BlockOrientation blockOrientation) {
    EnumSet<Side> n = EnumSet.noneOf(Side.class);
    for (Side side : sides) {
      if (side == Side.TOP) {
        n.add(Side.TOP);
      } else {
        n.add(turn(side, blockOrientation));
      }
    }
    return n;
  }

  private static Side turn(Side side, BlockOrientation blockOrientation) {
    int n =
        switch (side) {
          case BACK -> 0;
          case RIGHT -> 1;
          case FRONT -> 2;
          case LEFT -> 3;
          case TOP -> throw new UnsupportedOperationException();
        };
    int a = (n - blockOrientation.getRotation() + 4) % 4;

    return switch (a) {
      case 0 -> Side.BACK;
      case 1 -> Side.RIGHT;
      case 2 -> Side.FRONT;
      case 3 -> Side.LEFT;
      default -> throw new UnsupportedOperationException();
    };
  }

  public static void rotate(Vertex[] vertices, int steps) {
    Matrix4 t =
        new Matrix4()
            .translate(-0.5f, 0, -0.5f)
            .rotate(0, 1, 0, 90 * steps)
            .translate(0.5f, 0, 0.5f);

    for (Vertex vertex : vertices) {
      vertex.getPosition().mul(t);
    }
  }

  private static Vector3[] turn(Vector3[] src, int steps) {
    Matrix4 t =
        new Matrix4()
            .translate(-0.5f, 0, -0.5f)
            .rotate(0, 1, 0, 90 * steps)
            .translate(0.5f, 0, 0.5f);
    Arrays.stream(src).forEach(v -> v.mul(t));
    return shiftArray(src, steps);
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
}
