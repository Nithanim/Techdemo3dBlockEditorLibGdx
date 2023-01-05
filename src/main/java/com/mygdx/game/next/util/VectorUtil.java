package com.mygdx.game.next.util;

import com.mygdx.game.next.util.Vector3i;

import lombok.experimental.UtilityClass;

@UtilityClass
public class VectorUtil {

  public static Vector3i getMinimum(Vector3i a, Vector3i b) {
    return new Vector3i(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z));
  }

  public static Vector3i getAreaVector(Vector3i a, Vector3i b) {
    var difference = b.cpy().sub(a);
    difference.x = Math.abs(difference.x);
    difference.y = Math.abs(difference.y);
    difference.z = Math.abs(difference.z);
    return difference;
  }
}
