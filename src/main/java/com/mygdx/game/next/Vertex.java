package com.mygdx.game.next;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import lombok.Value;

@Value
public class Vertex {
  Vector3 position;
  Vector3 normal;
  Vector2 textureCoordinates;
}
