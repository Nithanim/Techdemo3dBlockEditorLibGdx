package com.mygdx.game.next.shapes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.next.blocks.BlockOrientation;
import com.mygdx.game.next.blocks.Face;
import com.mygdx.game.next.blocks.Side;
import com.mygdx.game.next.Vertex;

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
import static com.mygdx.game.next.shapes.FaceGenerator.NORMAL_TOP;

public class PillarCornerGenerator {
  private static final float WIDTH = 1f / 3;
  private static final Matrix4 T = new Matrix4().scale(WIDTH, 1, WIDTH);

  public static Face leftFaceFull() {
    return new Face(
        new Vertex[] {
          new Vertex(BACK_TOP_LEFT.cpy().mul(T), NORMAL_LEFT.cpy(), new Vector2(0, 0)),
          new Vertex(FRONT_TOP_LEFT.cpy().mul(T), NORMAL_LEFT.cpy(), new Vector2(WIDTH, 0)),
          new Vertex(FRONT_BOTTOM_LEFT.cpy().mul(T), NORMAL_LEFT.cpy(), new Vector2(WIDTH, 1)),
          new Vertex(BACK_BOTTOM_LEFT.cpy().mul(T), NORMAL_LEFT.cpy(), new Vector2(0, 1)),
        },
        new short[] {0, 3, 1 /**/, 3, 2, 1});
  }

  public static Face rightFaceFull() {
    return new Face(
        new Vertex[] {
          new Vertex(FRONT_TOP_RIGHT.cpy().mul(T), NORMAL_RIGHT.cpy(), new Vector2(1 - WIDTH, 0)),
          new Vertex(BACK_TOP_RIGHT.cpy().mul(T), NORMAL_RIGHT.cpy(), new Vector2(1, 0)),
          new Vertex(BACK_BOTTOM_RIGHT.cpy().mul(T), NORMAL_RIGHT.cpy(), new Vector2(1, 1)),
          new Vertex(
              FRONT_BOTTOM_RIGHT.cpy().mul(T), NORMAL_RIGHT.cpy(), new Vector2(1 - WIDTH, 1)),
        },
        new short[] {0, 3, 1 /**/, 3, 2, 1});
  }

  public static Face frontFaceFull() {
    return new Face(
        new Vertex[] {
          new Vertex(FRONT_TOP_LEFT.cpy().mul(T), NORMAL_FRONT.cpy(), new Vector2(0, 0)),
          new Vertex(FRONT_TOP_RIGHT.cpy().mul(T), NORMAL_FRONT.cpy(), new Vector2(WIDTH, 0)),
          new Vertex(FRONT_BOTTOM_RIGHT.cpy().mul(T), NORMAL_FRONT.cpy(), new Vector2(WIDTH, 1)),
          new Vertex(FRONT_BOTTOM_LEFT.cpy().mul(T), NORMAL_FRONT.cpy(), new Vector2(0, 1)),
        },
        new short[] {0, 3, 1 /**/, 3, 2, 1});
  }

  public static Face backFaceFull() {
    return new Face(
        new Vertex[] {
          new Vertex(BACK_TOP_RIGHT.cpy().mul(T), NORMAL_BACK.cpy(), new Vector2(1 - WIDTH, 0)),
          new Vertex(BACK_TOP_LEFT.cpy().mul(T), NORMAL_BACK.cpy(), new Vector2(1, 0)),
          new Vertex(BACK_BOTTOM_LEFT.cpy().mul(T), NORMAL_BACK.cpy(), new Vector2(1, 1)),
          new Vertex(BACK_BOTTOM_RIGHT.cpy().mul(T), NORMAL_BACK.cpy(), new Vector2(1 - WIDTH, 1)),
        },
        new short[] {0, 3, 1 /**/, 3, 2, 1});
  }

  public static Face topFaceFull() {
    return new Face(
        new Vertex[] {
          new Vertex(BACK_TOP_LEFT.cpy().mul(T), NORMAL_TOP.cpy(), new Vector2(0, 0)),
          new Vertex(BACK_TOP_RIGHT.cpy().mul(T), NORMAL_TOP.cpy(), new Vector2(WIDTH, 0)),
          new Vertex(FRONT_TOP_RIGHT.cpy().mul(T), NORMAL_TOP.cpy(), new Vector2(WIDTH, WIDTH)),
          new Vertex(FRONT_TOP_LEFT.cpy().mul(T), NORMAL_TOP.cpy(), new Vector2(0, WIDTH)),
        },
        new short[] {0, 3, 1 /**/, 3, 2, 1});
  }

  public List<Face> get(Set<Side> sides, BlockOrientation blockOrientation) {
    var nsides = Rotator.offsetSides(sides, blockOrientation);

    List<Face> faces = new ArrayList<>();
    if (nsides.contains(Side.TOP)) {
      faces.add(topFaceFull());
    }
    if (nsides.contains(Side.FRONT)) {
      faces.add(frontFaceFull());
    }
    if (nsides.contains(Side.BACK)) {
      faces.add(backFaceFull());
    }
    if (nsides.contains(Side.LEFT)) {
      faces.add(leftFaceFull());
    }
    if (nsides.contains(Side.RIGHT)) {
      faces.add(rightFaceFull());
    }
    Rotator.rotate(faces, blockOrientation);
    return faces;
  }
}
