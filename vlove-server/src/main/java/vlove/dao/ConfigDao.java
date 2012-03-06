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
package vlove.dao;

import vlove.model.ConfigItem;

/**
 * Simple interface that defines common Config DAO functionality.
 * 
 * @author Michael Laccetti
 */
public interface ConfigDao {
	/**
	 * Retrieve a config item for the specified key.
	 * @param key The key to search by.
	 * @return The config item - will never be null.
	 */
	public ConfigItem getConfigItem(String key);
	
	/**
	 * Persiste the specified ConfigItem.
	 * @param item
	 */
	public void saveConfigItem(ConfigItem item);
}