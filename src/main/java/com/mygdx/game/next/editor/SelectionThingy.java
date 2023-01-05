package com.mygdx.game.next.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.next.blocks.Block;
import com.mygdx.game.next.events.EventPublisher;
import com.mygdx.game.next.util.Vector3i;
import com.mygdx.game.next.util.VectorUtil;

import lombok.Getter;

public class SelectionThingy implements RenderableProvider, Disposable {
  private final ModelInstance selectionMarkerPrimary;
  private final ModelInstance selectionMarkerSecondary;

  private final List<Disposable> disposables;
  private final EventPublisher<BlockSelectionChangedEvent> blockSelectionChangedEventPublisher;
  private Model selectionMarkerVolumeModel;
  private ModelInstance selectionMarkerVolume;
  @Getter private Vector3i selectionPrimary;
  @Getter private Vector3i selectionSecondary;

  public SelectionThingy(
      EventPublisher<BlockSelectionChangedEvent> blockSelectionChangedEventPublisher) {
    this.blockSelectionChangedEventPublisher = blockSelectionChangedEventPublisher;
    disposables = new ArrayList<>();
    selectionMarkerPrimary =
        new ModelInstance(
            disposeLater(buildBlockSelectionModel(new Color(0, 1, 0, 0.1f), Block.S, 0.2f)));
    selectionMarkerSecondary =
        new ModelInstance(
            disposeLater(buildBlockSelectionModel(new Color(1, 1, 0, 0.1f), Block.S, 0.2f)));
  }

  private <T extends Disposable> T disposeLater(T disposable) {
    disposables.add(disposable);
    return disposable;
  }

  public void setPrimarySelection(Vector3i selection) {
    if (!Objects.equals(selectionPrimary, selection)) {
      setSelection(
          selection, s -> this.selectionPrimary = s, this.selectionMarkerPrimary.transform);
      updateArea();
      blockSelectionChangedEventPublisher.fire(
          new BlockSelectionChangedEvent(selection, selectionSecondary));
    }
  }

  public void setSecondarySelection(Vector3i selection) {
    if (!Objects.equals(selectionSecondary, selection)) {
      setSelection(
          selection, s -> this.selectionSecondary = s, this.selectionMarkerSecondary.transform);
      updateArea();
      blockSelectionChangedEventPublisher.fire(
          new BlockSelectionChangedEvent(selectionPrimary, selection));
    }
  }

  private void setSelection(Vector3i selection, Consumer<Vector3i> c, Matrix4 m) {
    if (selection != null) {
      c.accept(selection.cpy());
      m.setTranslation(selection.x * Block.S, selection.y * Block.S, selection.z * Block.S);
    } else {
      c.accept(null);
    }
  }

  private void updateArea() {
    if (selectionPrimary != null && selectionSecondary != null) {
      var areaVector = VectorUtil.getAreaVector(selectionPrimary, selectionSecondary);
      var minimum = VectorUtil.getMinimum(selectionPrimary, selectionSecondary);

      if (selectionMarkerVolumeModel != null) {
        selectionMarkerVolumeModel.dispose();
      }
      selectionMarkerVolumeModel =
          buildBlockSelectionModel(
              new Color(0, 0, 1, 0.1f),
              (areaVector.x + 1) * Block.S,
              (areaVector.y + 1) * Block.S,
              (areaVector.z + 1) * Block.S,
              0.1f);
      selectionMarkerVolume = new ModelInstance(selectionMarkerVolumeModel);
      selectionMarkerVolume
          .transform
          .idt()
          .translate(minimum.x * Block.S, minimum.y * Block.S, minimum.z * Block.S);
    }
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    if (selectionPrimary != null) {
      selectionMarkerPrimary.getRenderables(renderables, pool);
      if (selectionSecondary != null) {
        if (!selectionPrimary.equals(selectionSecondary)) {
          selectionMarkerSecondary.getRenderables(renderables, pool);
          selectionMarkerVolume.getRenderables(renderables, pool);
        }
      }
    }
  }

  private Model buildBlockSelectionModel(Color color, float s, float t) {
    return buildBlockSelectionModel(color, s, s, s, t);
  }

  private Model buildBlockSelectionModel(Color color, float sx, float sy, float sz, float t) {
    var modelBuilder = new ModelBuilder();
    var material = new Material(new ColorAttribute(ColorAttribute.Diffuse, color));
    int attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;

    modelBuilder.begin();
    var meshPartBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES, attributes, material);
    box(meshPartBuilder, -t / 2, -t / 2, 0 - t / 2, sx + t, t, t);
    box(meshPartBuilder, -t / 2, -t / 2, sz - t / 2, sx + t, t, t);
    box(meshPartBuilder, -t / 2, sy - t / 2, 0 - t / 2, sx + t, t, t);
    box(meshPartBuilder, -t / 2, sy - t / 2, sz - t / 2, sx + t, t, t);

    box(meshPartBuilder, -t / 2, -t / 2, -t / 2, t, t, sz + t);
    box(meshPartBuilder, sx - t / 2, -t / 2, -t / 2, t, t, sz + t);
    box(meshPartBuilder, -t / 2, sy - t / 2, -t / 2, t, t, sz + t);
    box(meshPartBuilder, sx - t / 2, sy - t / 2, -t / 2, t, t, sz + t);

    box(meshPartBuilder, -t / 2, -t / 2, -t / 2, t, sy + t, t);
    box(meshPartBuilder, sx - t / 2, -t / 2, -t / 2, t, sy + t, t);
    box(meshPartBuilder, -t / 2, -t / 2, sz - t / 2, t, sy + t, t);
    box(meshPartBuilder, sx - t / 2, -t / 2, sz - t / 2, t, sy + t, t);

    return modelBuilder.end();
  }

  private void box(
      MeshPartBuilder meshPartBuilder, float x, float y, float z, float w, float h, float d) {
    Vector3 o = new Vector3(x, y, z);
    BoxShapeBuilder.build(
        meshPartBuilder,
        o,
        o.cpy().add(0, h, 0),
        o.cpy().add(w, 0, 0),
        o.cpy().add(w, h, 0),
        o.cpy().add(0, 0, d),
        o.cpy().add(0, h, d),
        o.cpy().add(w, 0, d),
        o.cpy().add(w, h, d));
  }

  @Override
  public void dispose() {
    disposables.forEach(Disposable::dispose);
  }
}
