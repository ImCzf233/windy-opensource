package space.emptiness.utils.vec;



public class Vector2<T extends Number> extends Vector<Number> {

	public Vector2(Number x, Number y) {
		super(x, y, 0);
	}

	public Vector3<T> toVector3() {
		return new Vector3<>((T) getX(), ((T) getY()), ((T) getZ()));
	}
}
