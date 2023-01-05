package com.mygdx.game.next.editor.ui.components;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mygdx.game.World;
import com.mygdx.game.next.blocks.Block;
import com.mygdx.game.next.blocks.Side;
import com.mygdx.game.next.editor.BlockSelectionChangedEvent;
import com.mygdx.game.next.events.EventReceiver;

public class BlockTextureComponent extends VisTable implements Disposable {

  private static final int IMAGE_SIZE = 64;
  private final Map<Side, VisImageButton> map;
  private final Texture blankTexture;
  private final TextureRegionDrawable blank;

  private final World world;
  private Block block;
  private TextureRegionDrawable current;

  public BlockTextureComponent(EventReceiver<BlockSelectionChangedEvent> rec, World world) {
    this.world = world;
    TableUtils.setSpacingDefaults(this);
    map = new EnumMap<>(Side.class);
    blankTexture = new Texture(IMAGE_SIZE, IMAGE_SIZE, Pixmap.Format.RGB888);
    blank = new TextureRegionDrawable(blankTexture);

    add(new VisLabel("Texture:")).row();
    VisTable table = new VisTable(true);

    table.add(new Actor());
    Actor b = create(Side.BACK);
    table.add(b);
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
    // table.debug();
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
    var button = new VisImageButton(blank);
    map.put(side, button);
    return button;
  }

  public void setBlock(Block block) {
    this.block = block;
    if (block != null) {
      current = new TextureRegionDrawable(block.getTexture());
      current.setMinWidth(IMAGE_SIZE);
      current.setMinHeight(IMAGE_SIZE);
    } else {
      current = blank;
    }
    for (Side side : Side.values()) {
      updateButton(map.get(side));
    }
  }

  private void updateButton(VisImageButton b) {
    b.getStyle().imageUp = current;
    b.setStyle(b.getStyle());
  }

  @Override
  public void dispose() {
    blankTexture.dispose();
  }
}
