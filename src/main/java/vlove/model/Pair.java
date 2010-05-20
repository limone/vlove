package vlove.model;

import java.io.Serializable;

public class Pair<T,U> implements Serializable {
	public T _1;
	public U _2;
	
	public Pair() {
		// empty;
	}
	
	public Pair(T _1, U _2) {
		this._1 = _1;
		this._2 = _2;
	}
}