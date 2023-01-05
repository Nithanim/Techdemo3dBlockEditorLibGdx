package com.mygdx.game.next.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.next.Vertex;
import com.mygdx.game.next.shapes.FaceGenerator;
import com.mygdx.game.next.util.Vector3i;

import lombok.Getter;

public class Block implements Disposable, RenderableProvider {

  public static final float S = 10;
  @Getter private final Texture texture;
  private Model model;
  private ModelInstance modelInstance;
  @Getter private Vector3i coords;
  @Getter private BlockProperties properties;

  public Block(Vector3i coords, BlockProperties properties) {
    this.coords = coords;
    this.properties = properties;
    this.texture = new Texture(Gdx.files.internal("FloorStreets0078_1_350.jpg"));
    update();
  }

  public void setProperties(BlockProperties properties) {
    this.properties = properties;
    update();
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    modelInstance.getRenderables(renderables, pool);
  }

  @Override
  public void dispose() {
    model.dispose();
  }

  public void update() {
    Material material = new Material(TextureAttribute.createDiffuse(texture));
    int attributes =
        VertexAttributes.Usage.Position
            | VertexAttributes.Usage.Normal
            | VertexAttributes.Usage.TextureCoordinates;
    int primitive = GL20.GL_TRIANGLES;

    if (model != null) {
      model.dispose();
    }
    var faces = FaceGenerator.get(properties);

    ModelBuilder mb = new ModelBuilder();
    // model = mb.createBox(S, S, S, material, attributes);
    var m = new MeshBuilder();
    m.begin(attributes);
    for (Face face : faces) {
      toMesh(m, face);
    }

    mb.begin();
    mb.part("t", m.end(), primitive, material);
    model = mb.end();

    float cx = (coords.x * S);
    float cy = (coords.y * S);
    float cz = (coords.z * S);
    modelInstance = new ModelInstance(model, new Matrix4().translate(cx, cy, cz));
  }

  public void toMesh(MeshBuilder meshBuilder, Face face) {
    var vertices = face.vertices();
    var indices = face.indices();
    int l = 3 + 3 + 2;
    float[] all = new float[l * vertices.length];
    for (int i = 0; i < vertices.length; i++) {
      System.arraycopy(toArray(vertices[i]), 0, all, l * i, l);
    }
    meshBuilder.addMesh(all, indices);
  }

  private float[] toArray(Vertex v) {
    return new float[] {
      v.getPosition().x,
      v.getPosition().y,
      v.getPosition().z,
      v.getNormal().x,
      v.getNormal().y,
      v.getNormal().z,
      v.getTextureCoordinates().x,
      v.getTextureCoordinates().y
    };
  }
}
