package com.mygdx.game.next.raycast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import lombok.experimental.UtilityClass;

@UtilityClass public
class RayCastUtil {

  public static Ray getRay(int screenX, int screenY, Camera camera) {
    float x = ((float) screenX / Gdx.graphics.getWidth()) * 2.0f - 1.0f;
    float y = 1.0f - ((float) screenY / Gdx.graphics.getHeight()) * 2.0f;
    float z = -1.0f;
    var pointProjectionPlane = new Vector3(x, y, z).mul(camera.invProjectionView);
    var pointCamera = camera.position;

    return new Ray(pointProjectionPlane, pointProjectionPlane.cpy().sub(pointCamera).nor());
  }
}
