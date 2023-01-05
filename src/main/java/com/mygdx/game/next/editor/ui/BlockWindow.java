package com.mygdx.game.next.editor.ui;

import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.mygdx.game.World;
import com.mygdx.game.next.editor.BlockSelectionChangedEvent;
import com.mygdx.game.next.editor.ui.components.BlockClearComponent;
import com.mygdx.game.next.editor.ui.components.BlockTextureComponent;
import com.mygdx.game.next.events.EventReceiver;

public class BlockWindow extends VisWindow {

  private final EventReceiver<BlockSelectionChangedEvent> blockSelectionEventReceiver;
  private final World world;

  public BlockWindow(
      EventReceiver<BlockSelectionChangedEvent> blockSelectionEventReceiver, World world) {
    super("Block Window");
    this.blockSelectionEventReceiver = blockSelectionEventReceiver;
    this.world = world;

    TableUtils.setSpacingDefaults(this);
    columnDefaults(0).left();

    addWidgets();

    pack();
    setPosition(0, 0);
  }

  private void addWidgets() {
    TableUtils.setSpacingDefaults(this);

    add(new BlockTextureComponent(blockSelectionEventReceiver, world)).row();
    add(new BlockClearComponent(blockSelectionEventReceiver, world)).row();
  }
}
