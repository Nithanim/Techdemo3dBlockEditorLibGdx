package com.mygdx.game.next.editor.ui.components;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mygdx.game.World;
import com.mygdx.game.next.blocks.Block;
import com.mygdx.game.next.blocks.Side;
import com.mygdx.game.next.editor.BlockSelectionChangedEvent;
import com.mygdx.game.next.events.EventReceiver;

public class BlockClearComponent extends VisTable {
  private final Map<Side, VisTextButton> map;
  private final World world;
  private Block block;

  public BlockClearComponent(EventReceiver<BlockSelectionChangedEvent> rec, World world) {
    this.world = world;
    TableUtils.setSpacingDefaults(this);

    map = new EnumMap<>(Side.class);

    add(new VisLabel("Visibility:")).row();
    VisTable table = new VisTable(true);
    table.add(new Actor());
    table.add(create(Side.BACK));
    table.add(new Actor());
    table.row();
    table.add(create(Side.LEFT));
    table.add(create(Side.TOP));
    table.add(create(Side.RIGHT));
    table.row();
    table.add(new Actor());
    table.add(create(Side.FRONT));
    table.add(new Actor());
    table.row();
    add(table);

    rec.addListener(this::onSelectionChanged);
    setBlock(null);
  }

  private void onSelectionChanged(BlockSelectionChangedEvent blockSelectionChangedEvent) {
    if (blockSelectionChangedEvent.a() != null) {
      setBlock(world.getBlockAt(blockSelectionChangedEvent.a()));
    } else {
      setBlock(null);
    }
  }

  private Actor create(Side side) {
    var textButton = new VisTextButton("");

    Function<Set<Side>, Set<Side>> toggleSide =
        sides -> {
          var n = EnumSet.copyOf(sides);
          if (!n.remove(side)) {
            n.add(side);
          }
          return n;
        };

    textButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            if (block != null) {
              block.setProperties(
                  block
                      .getProperties()
                      .withSides(toggleSide.apply(block.getProperties().getSides())));
              update();
            }
          }
        });
    map.put(side, textButton);
    return textButton;
  }

  public void setBlock(Block block) {
    this.block = block;
    update();
  }

  private void update() {
    for (Side value : Side.values()) {
      var button = map.get(value);
      if (block != null) {
        if (block.getProperties().getSides().contains(value)) {
          button.setText("X");
        } else {
          button.setText("O");
        }
      } else {
        button.setText("-");
      }
    }
  }
}
