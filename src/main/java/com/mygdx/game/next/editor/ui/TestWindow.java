package com.mygdx.game.next.editor.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisRadioButton;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.spinner.ArraySpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.FloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

public class TestWindow extends VisWindow {

  public TestWindow() {
    super("test window");

    TableUtils.setSpacingDefaults(this);
    columnDefaults(0).left();

    addVisWidgets();

    pack();
    setPosition(234, 280);
  }

  private void addVisWidgets() {
    VisLabel label = new VisLabel("label");
    final VisLabel labelWithTooltip = new VisLabel("label with tooltip");
    labelWithTooltip.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            labelWithTooltip.remove();
          }
        });
    new Tooltip.Builder("this label has a tooltip").target(labelWithTooltip).build();

    TableUtils.setSpacingDefaults(this);

    VisTable labelTable = new VisTable(true);
    labelTable.add(label);
    labelTable.add(labelWithTooltip);
    // ---

    VisTextButton normalButton = new VisTextButton("button");
    VisTextButton normalBlueButton = new VisTextButton("button blue", "blue");
    VisTextButton disabledButton = new VisTextButton("disabled");
    VisTextButton toggleButton = new VisTextButton("toggle", "toggle");
    disabledButton.setDisabled(true);

    VisTable buttonTable = new VisTable(true);
    buttonTable.add(normalButton);
    buttonTable.add(normalBlueButton);
    buttonTable.add(disabledButton);
    buttonTable.add(toggleButton);

    // ---

    VisCheckBox normalCheckbox = new VisCheckBox("checkbox");
    VisCheckBox disabledCheckbox = new VisCheckBox("disabled");
    VisCheckBox disabledCheckedCheckbox = new VisCheckBox("disabled checked");
    disabledCheckbox.setDisabled(true);
    disabledCheckedCheckbox.setDisabled(true);
    disabledCheckedCheckbox.setChecked(true);

    VisTable checkboxTable = new VisTable(true);
    checkboxTable.add(normalCheckbox);
    checkboxTable.add(disabledCheckbox);
    checkboxTable.add(disabledCheckedCheckbox);

    // ---
    VisRadioButton normalRadio = new VisRadioButton("radio");
    VisRadioButton disabledRadio = new VisRadioButton("disabled");
    VisRadioButton disabledCheckedRadio = new VisRadioButton("disabled checked");
    disabledRadio.setDisabled(true);
    disabledCheckedRadio.setDisabled(true);
    disabledCheckedRadio.setChecked(true);

    VisTable radioTable = new VisTable(true);
    radioTable.add(normalRadio);
    radioTable.add(disabledRadio);
    radioTable.add(disabledCheckedRadio);

    // ---

    VisTextField normalTextField = new VisTextField("textbox");
    VisTextField disabledTextField = new VisTextField("disabled");
    VisTextField passwordTextField = new VisTextField("password");
    VisTextField invalidTextField = new VisTextField("invalid");
    disabledTextField.setDisabled(true);
    passwordTextField.setPasswordMode(true);
    invalidTextField.setInputValid(false);

    VisTable textFieldTable = new VisTable(true);
    textFieldTable.defaults().width(120);
    textFieldTable.add(normalTextField);
    textFieldTable.add(disabledTextField);
    textFieldTable.add(passwordTextField);
    textFieldTable.add(invalidTextField);

    // ---

    VisProgressBar progressbar = new VisProgressBar(0, 100, 1, false);
    VisSlider slider = new VisSlider(0, 100, 1, false);
    VisSlider sliderDisabled = new VisSlider(0, 100, 1, false);

    progressbar.setValue(50);
    slider.setValue(50);
    sliderDisabled.setValue(50);
    sliderDisabled.setDisabled(true);

    VisTable progressbarTable = new VisTable(true);
    progressbarTable.add(progressbar);
    progressbarTable.add(slider);
    progressbarTable.add(sliderDisabled);

    // ---

    VisTable listSpinnerTable = new VisTable();
    VisList<String> list = new VisList<String>();
    list.setItems("item 1", "item 2", "item 3", "item 4");

    listSpinnerTable.add(new VisLabel("list: "));
    listSpinnerTable.add(list);

    Array<String> stringArray = new Array<String>();
    stringArray.add("a");
    stringArray.add("b");
    stringArray.add("c");
    stringArray.add("d");
    stringArray.add("e");
    final ArraySpinnerModel<String> arrayModel = new ArraySpinnerModel<String>(stringArray);
    Spinner arraySpinner = new Spinner("array", arrayModel);

    final IntSpinnerModel intModel = new IntSpinnerModel(10, -5, 20, 1);
    Spinner intSpinner = new Spinner("int", intModel);

    VisTable spinnerTable = new VisTable(true);
    spinnerTable.add(new VisLabel("spinners")).colspan(2).row();
    spinnerTable.add(intSpinner);
    spinnerTable.add(arraySpinner).row();
    spinnerTable.add(
        new Spinner("simple float", new SimpleFloatSpinnerModel(10f, -5f, 20f, 1.5f, 3)));
    spinnerTable.add(new Spinner("float", new FloatSpinnerModel("1", "-5", "10", "0.5", 2)));

    arraySpinner.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            System.out.println("changed array spinner to: " + arrayModel.getCurrent());
          }
        });

    intSpinner.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            System.out.println("changed int spinner to: " + intModel.getValue());
          }
        });

    listSpinnerTable.add(spinnerTable).padLeft(10f);

    // ---

    VisTable selectorsTable = new VisTable(true);
    VisSelectBox<String> selectBox = new VisSelectBox<String>();
    selectBox.setItems("item 1", "item 2", "item 3", "item 4");

    selectorsTable.add(new VisLabel("select box: "));
    selectorsTable.add(selectBox);

    // ---

    VisTable linkTable = new VisTable(true);
    linkTable.add(new VisLabel("link label:"));
    linkTable.add(new LinkLabel("https://github.com/kotcrab/vis-ui")).row();

    VisTable linkTable2 = new VisTable(true);
    linkTable2.add(new VisLabel("link label with custom text:"));
    linkTable2.add(new LinkLabel("kotcrab's blog", "http://kotcrab.com")).row();

    // ---

    add(labelTable).row();
    add(buttonTable).row();
    add(checkboxTable).row();
    add(radioTable).row();
    add(textFieldTable).row();
    add(progressbarTable).row();
    add(listSpinnerTable).row();
    add(selectorsTable).row();
    add(linkTable).row();
    add(linkTable2).padBottom(3).row();
  }
}
