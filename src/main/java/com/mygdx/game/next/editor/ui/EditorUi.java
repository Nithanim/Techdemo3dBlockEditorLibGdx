package com.mygdx.game.next.editor.ui;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.mygdx.game.World;
import com.mygdx.game.next.editor.BlockSelectionChangedEvent;
import com.mygdx.game.next.events.EventReceiver;

import lombok.Getter;

public class EditorUi extends ApplicationAdapter {
  @Getter private Stage stage;
  private MenuBar menuBar;
  private final EventReceiver<BlockSelectionChangedEvent> rec;
  private final World world;

  public EditorUi(EventReceiver<BlockSelectionChangedEvent> rec, World world) {
    this.rec = rec;
    this.world = world;
  }

  @Override
  public void create() {
    VisUI.load(VisUI.SkinScale.X1);

    stage = new Stage(new ScreenViewport());
    final Table root = new Table();
    root.setFillParent(true);
    stage.addActor(root);

    menuBar = new MenuBar();
    menuBar.setMenuListener(
        new MenuBar.MenuBarListener() {
          @Override
          public void menuOpened(Menu menu) {
            System.out.println("Opened menu: " + menu.getTitle());
          }

          @Override
          public void menuClosed(Menu menu) {
            System.out.println("Closed menu: " + menu.getTitle());
          }
        });
    root.add(menuBar.getTable()).expandX().fillX().row();
    root.add().expand().fill();

    createMenus();

    stage.addActor(new BlockWindow(rec, world));
  }

  private void createMenus() {
    Menu fileMenu = new Menu("File");
    Menu editMenu = new Menu("Edit");
    Menu windowMenu = new Menu("Window");
    Menu helpMenu = new Menu("Help");

    fileMenu.addItem(createTestsMenu());
    fileMenu.addItem(new MenuItem("menuitem #1"));
    fileMenu.addItem(new MenuItem("menuitem #2").setShortcut("f1"));
    fileMenu.addItem(new MenuItem("menuitem #3").setShortcut("f2"));
    fileMenu.addItem(new MenuItem("menuitem #4").setShortcut("alt + f4"));

    MenuItem subMenuItem = new MenuItem("submenu #1");
    subMenuItem.setShortcut("alt + insert");
    subMenuItem.setSubMenu(createSubMenu());
    fileMenu.addItem(subMenuItem);

    MenuItem subMenuItem2 = new MenuItem("submenu #2");
    subMenuItem2.setSubMenu(createSubMenu());
    fileMenu.addItem(subMenuItem2);

    MenuItem subMenuItem3 = new MenuItem("submenu disabled");
    subMenuItem3.setDisabled(true);
    subMenuItem3.setSubMenu(createSubMenu());
    fileMenu.addItem(subMenuItem3);

    // ---

    editMenu.addItem(new MenuItem("menuitem #5"));
    editMenu.addItem(new MenuItem("menuitem #6"));
    editMenu.addSeparator();
    editMenu.addItem(new MenuItem("menuitem #7"));
    editMenu.addItem(new MenuItem("menuitem #8"));
    editMenu.addItem(createDoubleNestedMenu());

    MenuItem disabledItem = new MenuItem("disabled menuitem");
    disabledItem.setDisabled(true);
    MenuItem disabledItem2 = new MenuItem("disabled menuitem shortcut").setShortcut("alt + f4");
    disabledItem2.setDisabled(true);

    editMenu.addItem(disabledItem);
    editMenu.addItem(disabledItem2);

    windowMenu.addItem(new MenuItem("menuitem #9"));
    windowMenu.addItem(new MenuItem("menuitem #10"));
    windowMenu.addItem(new MenuItem("menuitem #11"));
    windowMenu.addSeparator();
    windowMenu.addItem(new MenuItem("menuitem #12"));

    helpMenu.addItem(
        new MenuItem(
            "about",
            new ChangeListener() {
              @Override
              public void changed(ChangeEvent event, Actor actor) {
                Dialogs.showOKDialog(stage, "about", "visui test application");
              }
            }));

    menuBar.addMenu(fileMenu);
    menuBar.addMenu(editMenu);
    menuBar.addMenu(windowMenu);
    menuBar.addMenu(helpMenu);
  }

  private MenuItem createDoubleNestedMenu() {
    MenuItem doubleNestedMenuItem = new MenuItem("submenu nested x2");
    doubleNestedMenuItem.setSubMenu(createSubMenu());

    PopupMenu nestedMenu = new PopupMenu();
    nestedMenu.addItem(doubleNestedMenuItem);
    nestedMenu.addItem(new MenuItem("single nested"));

    MenuItem menuItem = new MenuItem("submenu nested");
    menuItem.setSubMenu(nestedMenu);
    return menuItem;
  }

  private MenuItem createTestsMenu() {
    MenuItem item = new MenuItem("start test");

    PopupMenu menu = new PopupMenu();

    item.setSubMenu(menu);
    return item;
  }

  private PopupMenu createSubMenu() {
    PopupMenu menu = new PopupMenu();
    menu.addItem(new MenuItem("submenuitem #1"));
    menu.addItem(new MenuItem("submenuitem #2"));
    menu.addSeparator();
    menu.addItem(new MenuItem("submenuitem #3"));
    menu.addItem(new MenuItem("submenuitem #4"));
    return menu;
  }

  @Override
  public void resize(int width, int height) {
    if (width == 0 && height == 0)
      return; // see https://github.com/libgdx/libgdx/issues/3673#issuecomment-177606278
    stage.getViewport().update(width, height, true);
    PopupMenu.removeEveryMenu(stage);
    /*
    WindowResizeEvent resizeEvent = new WindowResizeEvent();
    for (Actor actor : stage.getActors()) {
      actor.fire(resizeEvent);
    }*/
  }

  @Override
  public void render() {
    stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
    stage.draw();
  }

  @Override
  public void dispose() {
    VisUI.dispose();
    stage.dispose();
  }
}
