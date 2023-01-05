package com.mygdx.game.next.blocks;

import com.mygdx.game.next.Vertex;

import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
public class Face {
  Vertex[] vertices;
  short[] indices;
}
