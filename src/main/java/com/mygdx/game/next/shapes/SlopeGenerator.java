package com.mygdx.game.next.shapes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.next.blocks.BlockOrientation;
import com.mygdx.game.next.blocks.Face;
import com.mygdx.game.next.blocks.Side;
import com.mygdx.game.next.Vertex;

import static com.mygdx.game.next.blocks.Block.S;
import static com.mygdx.game.next.shapes.FaceGenerator.BACK_BOTTOM_LEFT;
import static com.mygdx.game.next.shapes.FaceGenerator.BACK_BOTTOM_RIGHT;
import static com.mygdx.game.next.shapes.FaceGenerator.BACK_TOP_LEFT;
import static com.mygdx.game.next.shapes.FaceGenerator.BACK_TOP_RIGHT;
import static com.mygdx.game.next.shapes.FaceGenerator.FRONT_BOTTOM_LEFT;
import static com.mygdx.game.next.shapes.FaceGenerator.FRONT_BOTTOM_RIGHT;
import static com.mygdx.game.next.shapes.FaceGenerator.FRONT_TOP_LEFT;
import static com.mygdx.game.next.shapes.FaceGenerator.FRONT_TOP_RIGHT;
import static com.mygdx.game.next.shapes.FaceGenerator.NORMAL_BACK;
import static com.mygdx.game.next.shapes.FaceGenerator.NORMAL_FRONT;
import static com.mygdx.game.next.shapes.FaceGenerator.NORMAL_LEFT;
import static com.mygdx.game.next.shapes.FaceGenerator.NORMAL_RIGHT;

public class SlopeGenerator {
  private static final Matrix4 T = new Matrix4();

  public static Face leftFaceFull(float baseHeight, float topHeight) {
    // TODO: Remove face if base = 0
    Matrix4 topT3 = new Matrix4().scl(1, topHeight, 1);
    Matrix4 baseT3 = new Matrix4().scale(1, baseHeight, 1);
    return new Face(
        new Vertex[] {
          new Vertex(BACK_TOP_LEFT.cpy().mul(topT3), NORMAL_LEFT, new Vector2(0, 1 - topHeight)),
          new Vertex(FRONT_TOP_LEFT.cpy().mul(baseT3), NORMAL_LEFT, new Vector2(1, 1 - baseHeight)),
          new Vertex(FRONT_BOTTOM_LEFT.cpy(), NORMAL_LEFT, new Vector2(1, 1)),
          new Vertex(BACK_BOTTOM_LEFT.cpy(), NORMAL_LEFT, new Vector2(0, 1)),
        },
        new short[] {0, 3, 1 /**/, 3, 2, 1});
  }

  public static Face rightFaceFull(float baseHeight, float topHeight) {
    Matrix4 topT3 = new Matrix4().scl(1, topHeight, 1);
    Matrix4 baseT3 = new Matrix4().scale(1, baseHeight, 1);
    return new Face(
        new Vertex[] {
          new Vertex(
              FRONT_TOP_RIGHT.cpy().mul(baseT3), NORMAL_RIGHT, new Vector2(0, 1 - baseHeight)),
          new Vertex(BACK_TOP_RIGHT.cpy().mul(topT3), NORMAL_RIGHT, new Vector2(1, 1 - topHeight)),
          new Vertex(BACK_BOTTOM_RIGHT.cpy(), NORMAL_RIGHT, new Vector2(1, 1)),
          new Vertex(FRONT_BOTTOM_RIGHT.cpy(), NORMAL_RIGHT, new Vector2(0, 1)),
        },
        new short[] {0, 3, 1 /**/, 3, 2, 1});
  }

  public static Face frontFaceFull(float baseHeight, float topHeight) {
    Matrix4 topT = new Matrix4().translate(0, baseHeight * S, 0);
    Vector3 frontTopLeft = FRONT_BOTTOM_LEFT.cpy().mul(T).mul(topT);
    Vector3 frontTopRight = FRONT_BOTTOM_RIGHT.cpy().mul(T).mul(topT);
    return new Face(
        new Vertex[] {
          new Vertex(frontTopLeft, NORMAL_FRONT.cpy(), new Vector2(0, 1 - baseHeight)),
          new Vertex(frontTopRight, NORMAL_FRONT.cpy(), new Vector2(1, 1 - baseHeight)),
          new Vertex(FRONT_BOTTOM_RIGHT.cpy().mul(T), NORMAL_FRONT.cpy(), new Vector2(1, 1)),
          new Vertex(FRONT_BOTTOM_LEFT.cpy().mul(T), NORMAL_FRONT.cpy(), new Vector2(0, 1)),
        },
        new short[] {0, 3, 1 /**/, 3, 2, 1});
  }

  public static Face backFaceFull(float baseHeight, float topHeight) {
    Matrix4 topT3 = new Matrix4().scl(1, topHeight, 1);
    return new Face(
        new Vertex[] {
          new Vertex(
              BACK_TOP_RIGHT.cpy().mul(T).mul(topT3),
              NORMAL_BACK.cpy(),
              new Vector2(0, 1 - topHeight)),
          new Vertex(
              BACK_TOP_LEFT.cpy().mul(T).mul(topT3),
              NORMAL_BACK.cpy(),
              new Vector2(1, 1 - topHeight)),
          new Vertex(BACK_BOTTOM_LEFT.cpy().mul(T), NORMAL_BACK.cpy(), new Vector2(1, 1)),
          new Vertex(BACK_BOTTOM_RIGHT.cpy().mul(T), NORMAL_BACK.cpy(), new Vector2(0, 1)),
        },
        new short[] {0, 3, 1 /**/, 3, 2, 1});
  }

  public static Face topFaceFull(float baseHeight, float topHeight) {
    Matrix4 topT = new Matrix4().translate(0, topHeight * S, 0);
    Matrix4 bottomT = new Matrix4().translate(0, baseHeight * S, 0);
    Vector3 backTopLeft = BACK_BOTTOM_LEFT.cpy().mul(T).mul(topT);
    Vector3 backTopRight = BACK_BOTTOM_RIGHT.cpy().mul(T).mul(topT);
    Vector3 frontTopRight = FRONT_BOTTOM_RIGHT.cpy().mul(T).mul(bottomT);
    Vector3 frontTopLeft = FRONT_BOTTOM_LEFT.cpy().mul(T).mul(bottomT);
    Vector3 nVec = backTopLeft.cpy().sub(backTopRight).crs(frontTopRight.cpy().sub(backTopRight));
    return new Face(
        new Vertex[] {
          new Vertex(backTopLeft, nVec, new Vector2(0, 0)),
          new Vertex(backTopRight, nVec, new Vector2(1, 0)),
          new Vertex(frontTopRight, nVec, new Vector2(1, 1)),
          new Vertex(frontTopLeft, nVec, new Vector2(0, 1)),
        },
        new short[] {0, 3, 1 /**/, 3, 2, 1});
  }

  public List<Face> get(
      Set<Side> sides, BlockOrientation blockOrientation, float baseHeight, float topHeight) {
    var nsides = Rotator.offsetSides(sides, blockOrientation);

    List<Face> faces = new ArrayList<>();
    if (nsides.contains(Side.TOP)) {
      faces.add(topFaceFull(baseHeight, topHeight));
    }
    if (nsides.contains(Side.FRONT) && baseHeight > 0) {
      faces.add(frontFaceFull(baseHeight, topHeight));
    }
    if (nsides.contains(Side.BACK)) {
      faces.add(backFaceFull(baseHeight, topHeight));
    }
    if (nsides.contains(Side.LEFT)) {
      faces.add(leftFaceFull(baseHeight, topHeight));
    }
    if (nsides.contains(Side.RIGHT)) {
      faces.add(rightFaceFull(baseHeight, topHeight));
    }
    Rotator.rotate(faces, blockOrientation);
    return faces;
  }
}
