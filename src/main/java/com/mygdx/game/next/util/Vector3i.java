package com.mygdx.game.next.util;

import java.io.Serializable;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;

public class Vector3i implements Serializable, Vector<Vector3i> {
  private static final long serialVersionUID = 3840054589595372522L;

  /** the x-component of this vector * */
  public int x;
  /** the y-component of this vector * */
  public int y;
  /** the z-component of this vector * */
  public int z;

  public static final Vector3i X = new Vector3i(1, 0, 0);
  public static final Vector3i Y = new Vector3i(0, 1, 0);
  public static final Vector3i Z = new Vector3i(0, 0, 1);
  public static final Vector3i Zero = new Vector3i(0, 0, 0);

  private static final Matrix4 tmpMat = new Matrix4();

  /** Constructs a vector at (0,0,0) */
  public Vector3i() {}

  /**
   * Creates a vector with the given components
   *
   * @param x The x-component
   * @param y The y-component
   * @param z The z-component
   */
  public Vector3i(int x, int y, int z) {
    this.set(x, y, z);
  }

  /**
   * Creates a vector from the given vector
   *
   * @param vector The vector
   */
  public Vector3i(final Vector3i vector) {
    this.set(vector);
  }

  /**
   * Creates a vector from the given array. The array must have at least 3 elements.
   *
   * @param values The array
   */
  public Vector3i(final int[] values) {
    this.set(values[0], values[1], values[2]);
  }

  /**
   * Sets the vector to the given components
   *
   * @param x The x-component
   * @param y The y-component
   * @param z The z-component
   * @return this vector for chaining
   */
  public Vector3i set(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
    return this;
  }

  @Override
  public Vector3i set(final Vector3i vector) {
    return this.set(vector.x, vector.y, vector.z);
  }

  /**
   * Sets the components from the array. The array must have at least 3 elements
   *
   * @param values The array
   * @return this vector for chaining
   */
  public Vector3i set(final int[] values) {
    return this.set(values[0], values[1], values[2]);
  }

  @Override
  public Vector3i setToRandomDirection() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Vector3i cpy() {
    return new Vector3i(this);
  }

  @Override
  public Vector3i add(final Vector3i vector) {
    return this.add(vector.x, vector.y, vector.z);
  }

  /**
   * Adds the given vector to this component
   *
   * @param x The x-component of the other vector
   * @param y The y-component of the other vector
   * @param z The z-component of the other vector
   * @return This vector for chaining.
   */
  public Vector3i add(int x, int y, int z) {
    return this.set(this.x + x, this.y + y, this.z + z);
  }

  /**
   * Adds the given value to all three components of the vector.
   *
   * @param values The value
   * @return This vector for chaining
   */
  public Vector3i add(int values) {
    return this.set(this.x + values, this.y + values, this.z + values);
  }

  @Override
  public Vector3i sub(final Vector3i a_vec) {
    return this.sub(a_vec.x, a_vec.y, a_vec.z);
  }

  /**
   * Subtracts the other vector from this vector.
   *
   * @param x The x-component of the other vector
   * @param y The y-component of the other vector
   * @param z The z-component of the other vector
   * @return This vector for chaining
   */
  public Vector3i sub(int x, int y, int z) {
    return this.set(this.x - x, this.y - y, this.z - z);
  }

  /**
   * Subtracts the given value from all components of this vector
   *
   * @param value The value
   * @return This vector for chaining
   */
  public Vector3i sub(int value) {
    return this.set(this.x - value, this.y - value, this.z - value);
  }

  @Override
  public Vector3i scl(float scalar) {
    throw new UnsupportedOperationException();
  }

  public Vector3i scl(int scalar) {
    return this.set(this.x * scalar, this.y * scalar, this.z * scalar);
  }

  @Override
  public Vector3i scl(final Vector3i other) {
    return this.set(x * other.x, y * other.y, z * other.z);
  }

  /**
   * Scales this vector by the given values
   *
   * @param vx X value
   * @param vy Y value
   * @param vz Z value
   * @return This vector for chaining
   */
  public Vector3i scl(int vx, int vy, int vz) {
    return this.set(this.x * vx, this.y * vy, this.z * vz);
  }

  @Override
  public Vector3i mulAdd(Vector3i vec, Vector3i mulVec) {
    this.x += vec.x * mulVec.x;
    this.y += vec.y * mulVec.y;
    this.z += vec.z * mulVec.z;
    return this;
  }

  /**
   * @return The euclidean length
   */
  public static float len(final int x, final int y, final int z) {
    return (float) Math.sqrt(x * x + y * y + z * z);
  }

  @Override
  public float len() {
    return (float) Math.sqrt(x * x + y * y + z * z);
  }

  /**
   * @return The squared euclidean length
   */
  public static float len2(final float x, final float y, final float z) {
    return x * x + y * y + z * z;
  }

  @Override
  public float len2() {
    return x * x + y * y + z * z;
  }

  /**
   * @param vector The other vector
   * @return Whether this and the other vector are equal
   */
  public boolean idt(final Vector3i vector) {
    return x == vector.x && y == vector.y && z == vector.z;
  }

  /**
   * @return The euclidean distance between the two specified vectors
   */
  public static float dst(
      final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
    final int a = x2 - x1;
    final int b = y2 - y1;
    final int c = z2 - z1;
    return (float) Math.sqrt(a * a + b * b + c * c);
  }

  @Override
  public float dst(final Vector3i vector) {
    final int a = vector.x - x;
    final int b = vector.y - y;
    final int c = vector.z - z;
    return (float) Math.sqrt(a * a + b * b + c * c);
  }

  /**
   * @return the distance between this point and the given point
   */
  public float dst(int x, int y, int z) {
    final int a = x - this.x;
    final int b = y - this.y;
    final int c = z - this.z;
    return (float) Math.sqrt(a * a + b * b + c * c);
  }

  /**
   * @return the squared distance between the given points
   */
  public static float dst2(
      final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
    final int a = x2 - x1;
    final int b = y2 - y1;
    final int c = z2 - z1;
    return a * a + b * b + c * c;
  }

  @Override
  public float dst2(Vector3i point) {
    final int a = point.x - x;
    final int b = point.y - y;
    final int c = point.z - z;
    return a * a + b * b + c * c;
  }

  /**
   * Returns the squared distance between this point and the given point
   *
   * @param x The x-component of the other point
   * @param y The y-component of the other point
   * @param z The z-component of the other point
   * @return The squared distance
   */
  public float dst2(int x, int y, int z) {
    final int a = x - this.x;
    final int b = y - this.y;
    final int c = z - this.z;
    return a * a + b * b + c * c;
  }

  @Override
  public Vector3i nor() {
    throw new UnsupportedOperationException();
  }

  /**
   * @return The dot product between the two vectors
   */
  public static float dot(int x1, int y1, int z1, int x2, int y2, int z2) {
    return x1 * x2 + y1 * y2 + z1 * z2;
  }

  @Override
  public float dot(final Vector3i vector) {
    return x * vector.x + y * vector.y + z * vector.z;
  }

  /**
   * Returns the dot product between this and the given vector.
   *
   * @param x The x-component of the other vector
   * @param y The y-component of the other vector
   * @param z The z-component of the other vector
   * @return The dot product
   */
  public float dot(int x, int y, int z) {
    return this.x * x + this.y * y + this.z * z;
  }

  /**
   * Sets this vector to the cross product between it and the other vector.
   *
   * @param vector The other vector
   * @return This vector for chaining
   */
  public Vector3i crs(final Vector3i vector) {
    return this.set(
        y * vector.z - z * vector.y, z * vector.x - x * vector.z, x * vector.y - y * vector.x);
  }

  /**
   * Sets this vector to the cross product between it and the other vector.
   *
   * @param x The x-component of the other vector
   * @param y The y-component of the other vector
   * @param z The z-component of the other vector
   * @return This vector for chaining
   */
  public Vector3i crs(int x, int y, int z) {
    return this.set(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
  }

  /**
   * Left-multiplies the vector by the given 4x3 column major matrix. The matrix should be composed
   * by a 3x3 matrix representing rotation and scale plus a 1x3 matrix representing the translation.
   *
   * @param matrix The matrix
   * @return This vector for chaining
   */
  public Vector3i mul4x3(float[] matrix) {
    throw new UnsupportedOperationException();
  }

  /**
   * Left-multiplies the vector by the given matrix, assuming the fourth (w) component of the vector
   * is 1.
   *
   * @param matrix The matrix
   * @return This vector for chaining
   */
  public Vector3i mul(final Matrix4 matrix) {
    throw new UnsupportedOperationException();
  }

  /**
   * Multiplies the vector by the transpose of the given matrix, assuming the fourth (w) component
   * of the vector is 1.
   *
   * @param matrix The matrix
   * @return This vector for chaining
   */
  public Vector3i traMul(final Matrix4 matrix) {
    throw new UnsupportedOperationException();
  }

  /**
   * Left-multiplies the vector by the given matrix.
   *
   * @param matrix The matrix
   * @return This vector for chaining
   */
  public Vector3i mul(Matrix3 matrix) {
    throw new UnsupportedOperationException();
  }

  /**
   * Multiplies the vector by the transpose of the given matrix.
   *
   * @param matrix The matrix
   * @return This vector for chaining
   */
  public Vector3i traMul(Matrix3 matrix) {
    throw new UnsupportedOperationException();
  }

  /**
   * Multiplies the vector by the given {@link Quaternion}.
   *
   * @return This vector for chaining
   */
  public Vector3i mul(final Quaternion quat) {
    throw new UnsupportedOperationException();
  }

  /**
   * Multiplies this vector by the given matrix dividing by w, assuming the fourth (w) component of
   * the vector is 1. This is mostly used to project/unproject vectors via a perspective projection
   * matrix.
   *
   * @param matrix The matrix.
   * @return This vector for chaining
   */
  public Vector3i prj(final Matrix4 matrix) {
    throw new UnsupportedOperationException();
  }

  /**
   * Multiplies this vector by the first three columns of the matrix, essentially only applying
   * rotation and scaling.
   *
   * @param matrix The matrix
   * @return This vector for chaining
   */
  public Vector3i rot(final Matrix4 matrix) {
    throw new UnsupportedOperationException();
  }

  /**
   * Multiplies this vector by the transpose of the first three columns of the matrix. Note: only
   * works for translation and rotation, does not work for scaling. For those, use {@link
   * #rot(Matrix4)} with {@link Matrix4#inv()}.
   *
   * @param matrix The transformation matrix
   * @return The vector for chaining
   */
  public Vector3i unrotate(final Matrix4 matrix) {
    throw new UnsupportedOperationException();
  }

  /**
   * Translates this vector in the direction opposite to the translation of the matrix and the
   * multiplies this vector by the transpose of the first three columns of the matrix. Note: only
   * works for translation and rotation, does not work for scaling. For those, use {@link
   * #mul(Matrix4)} with {@link Matrix4#inv()}.
   *
   * @param matrix The transformation matrix
   * @return The vector for chaining
   */
  public Vector3i untransform(final Matrix4 matrix) {
    throw new UnsupportedOperationException();
  }

  /**
   * Rotates this vector by the given angle in degrees around the given axis.
   *
   * @param degrees the angle in degrees
   * @param axisX the x-component of the axis
   * @param axisY the y-component of the axis
   * @param axisZ the z-component of the axis
   * @return This vector for chaining
   */
  public Vector3i rotate(float degrees, float axisX, float axisY, float axisZ) {
    throw new UnsupportedOperationException();
  }

  /**
   * Rotates this vector by the given angle in radians around the given axis.
   *
   * @param radians the angle in radians
   * @param axisX the x-component of the axis
   * @param axisY the y-component of the axis
   * @param axisZ the z-component of the axis
   * @return This vector for chaining
   */
  public Vector3i rotateRad(float radians, float axisX, float axisY, float axisZ) {
    throw new UnsupportedOperationException();
  }

  /**
   * Rotates this vector by the given angle in degrees around the given axis.
   *
   * @param axis the axis
   * @param degrees the angle in degrees
   * @return This vector for chaining
   */
  public Vector3i rotate(final Vector3i axis, float degrees) {
    throw new UnsupportedOperationException();
  }

  /**
   * Rotates this vector by the given angle in radians around the given axis.
   *
   * @param axis the axis
   * @param radians the angle in radians
   * @return This vector for chaining
   */
  public Vector3i rotateRad(final Vector3i axis, float radians) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isUnit() {
    return isUnit(0.000000001f);
  }

  @Override
  public boolean isUnit(final float margin) {
    return Math.abs(len2() - 1f) < margin;
  }

  @Override
  public boolean isZero() {
    return x == 0 && y == 0 && z == 0;
  }

  @Override
  public boolean isZero(final float margin) {
    return len2() < margin;
  }

  @Override
  public boolean isOnLine(Vector3i other, float epsilon) {
    return len2(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)
        <= epsilon;
  }

  @Override
  public boolean isOnLine(Vector3i other) {
    return len2(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)
        <= MathUtils.FLOAT_ROUNDING_ERROR;
  }

  @Override
  public boolean isCollinear(Vector3i other, float epsilon) {
    return isOnLine(other, epsilon) && hasSameDirection(other);
  }

  @Override
  public boolean isCollinear(Vector3i other) {
    return isOnLine(other) && hasSameDirection(other);
  }

  @Override
  public boolean isCollinearOpposite(Vector3i other, float epsilon) {
    return isOnLine(other, epsilon) && hasOppositeDirection(other);
  }

  @Override
  public boolean isCollinearOpposite(Vector3i other) {
    return isOnLine(other) && hasOppositeDirection(other);
  }

  @Override
  public boolean isPerpendicular(Vector3i vector) {
    return MathUtils.isZero(dot(vector));
  }

  @Override
  public boolean isPerpendicular(Vector3i vector, float epsilon) {
    return MathUtils.isZero(dot(vector), epsilon);
  }

  @Override
  public boolean hasSameDirection(Vector3i vector) {
    return dot(vector) > 0;
  }

  @Override
  public boolean hasOppositeDirection(Vector3i vector) {
    return dot(vector) < 0;
  }

  @Override
  public Vector3i lerp(final Vector3i target, float alpha) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Vector3i interpolate(Vector3i target, float alpha, Interpolation interpolator) {
    throw new UnsupportedOperationException();
  }

  /**
   * Converts this {@code Vector3} to a string in the format {@code (x,y,z)}.
   *
   * @return a string representation of this object.
   */
  @Override
  public String toString() {
    return "(" + x + "," + y + "," + z + ")";
  }

  /**
   * Sets this {@code Vector3} to the value represented by the specified string according to the
   * format of {@link #toString()}.
   *
   * @param v the string.
   * @return this vector for chaining
   */
  public Vector3i fromString(String v) {
    int s0 = v.indexOf(',', 1);
    int s1 = v.indexOf(',', s0 + 1);
    if (s0 != -1 && s1 != -1 && v.charAt(0) == '(' && v.charAt(v.length() - 1) == ')') {
      try {
        int x = Integer.parseInt(v.substring(1, s0));
        int y = Integer.parseInt(v.substring(s0 + 1, s1));
        int z = Integer.parseInt(v.substring(s1 + 1, v.length() - 1));
        return this.set(x, y, z);
      } catch (NumberFormatException ex) {
        // Throw a GdxRuntimeException
      }
    }
    throw new GdxRuntimeException("Malformed Vector3: " + v);
  }

  @Override
  public Vector3i limit(float limit) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Vector3i limit2(float limit2) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Vector3i setLength(float len) {
    return setLength2(len * len);
  }

  @Override
  public Vector3i setLength2(float len2) {
    float oldLen2 = len2();
    return (oldLen2 == 0 || oldLen2 == len2) ? this : scl((float) Math.sqrt(len2 / oldLen2));
  }

  @Override
  public Vector3i clamp(float min, float max) {
    final float len2 = len2();
    if (len2 == 0f) return this;
    float max2 = max * max;
    if (len2 > max2) return scl((float) Math.sqrt(max2 / len2));
    float min2 = min * min;
    if (len2 < min2) return scl((float) Math.sqrt(min2 / len2));
    return this;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + NumberUtils.floatToIntBits(x);
    result = prime * result + NumberUtils.floatToIntBits(y);
    result = prime * result + NumberUtils.floatToIntBits(z);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Vector3i other = (Vector3i) obj;
    if (x != other.x) return false;
    if (y != other.y) return false;
    if (z != other.z) return false;
    return true;
  }

  @Override
  public boolean epsilonEquals(final Vector3i other, float epsilon) {
    if (other == null) return false;
    if (Math.abs(other.x - x) > epsilon) return false;
    if (Math.abs(other.y - y) > epsilon) return false;
    if (Math.abs(other.z - z) > epsilon) return false;
    return true;
  }

  @Override
  public Vector3i mulAdd(Vector3i v, float scalar) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Vector3i setZero() {
    this.x = 0;
    this.y = 0;
    this.z = 0;
    return this;
  }
}
