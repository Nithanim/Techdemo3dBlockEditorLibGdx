package com.mygdx.game.next.blocks;

import java.util.Set;

import lombok.Value;
import lombok.With;

@Value
@With
public class BlockProperties {
  Type type;
  BlockOrientation orientation;
  Set<Side> sides;
  float h1;
  float h2;
}
