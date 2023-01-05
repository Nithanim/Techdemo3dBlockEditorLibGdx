package com.mygdx.game.next.raycast;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.mygdx.game.World;
import com.mygdx.game.next.blocks.Block;
import com.mygdx.game.next.util.Vector3i;

public class BlockCaster {

  private final World world;

  public BlockCaster(World world) {
    this.world = world;
  }

  public Vector3i cast(Ray ray, Camera camera) {
    List<BoundingBox> candidates = getCandidates(ray);

    return candidates.stream()
        .min(
            (a, b) ->
                Float.compare(
                    a.getCenter(new Vector3()).dst2(camera.position),
                    b.getCenter(new Vector3()).dst2(camera.position)))
        .map(
            bb -> {
              var v = bb.getMin(new Vector3());
              return new Vector3i(
                  (int) (v.x / Block.S), (int) (v.y / Block.S), (int) (v.z / Block.S));
            })
        .orElse(null);
  }

  private List<BoundingBox> getCandidates(Ray ray) {
    List<BoundingBox> candidates = new ArrayList<>();
    for (int x = 0; x < world.getW(); x++) {
      for (int z = 0; z < world.getD(); z++) {
        for (int y = 0; y < world.getH(); y++) {
          if (world.getBlockAt(x, y, z) != null) {
            BoundingBox box =
                new BoundingBox(
                    new Vector3(x * Block.S, y * Block.S, z * Block.S),
                    new Vector3((x + 1) * Block.S, (y + 1) * Block.S, (z + 1) * Block.S));
            boolean intersects = Intersector.intersectRayBoundsFast(ray, box);

            if (intersects) {
              candidates.add(box);
            }
          }
        }
      }
    }
    return candidates;
  }
}
