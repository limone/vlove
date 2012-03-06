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
package vlove.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import vlove.dao.ConfigDao;
import vlove.dao.GenericDao;
import vlove.model.ConfigItem;

@Repository
public class ConfigDaoImpl implements ConfigDao {
  @Autowired
  private GenericDao gd;

  public ConfigDaoImpl() {
    // empty
  }

  /**
   * @see vlove.dao.ConfigDao#getConfigItem(String)
   */
  @Override
  public ConfigItem getConfigItem(String key) {
    Map<String, Object> params = new HashMap<>();
    params.put("key", key);
    final ConfigItem configItem = gd.namedQuerySingle("ConfigItem.GetByKey", params);
    return configItem == null ? new ConfigItem(key, null) : configItem;
  }

  /**
   * @see vlove.dao.ConfigDao#saveConfigItem(ConfigItem)
   */
  @Override
  public void saveConfigItem(ConfigItem item) {
    ConfigItem tmp = getConfigItem(item.getKey());
    if (tmp == null) {
      gd.persist(item);
    } else {
      tmp.setValue(item.getValue());
      gd.merge(tmp);
    }
  }
}