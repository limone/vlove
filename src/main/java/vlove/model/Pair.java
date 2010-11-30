/**
 * vlove - web based virtual machine management
 * Copyright (C) 2010 Limone Fresco Limited
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package vlove.model;

import java.io.Serializable;

/**
 * A Tuple!
 * 
 * @author Michael Laccetti
 *
 * @param <T>
 * @param <U>
 */
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