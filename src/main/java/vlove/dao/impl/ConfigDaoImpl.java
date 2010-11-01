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
	
	@Override
	public ConfigItem getConfigItem(String key) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("key", key);
		final ConfigItem configItem = gd.namedQuerySingle("ConfigItem.GetByKey", params);
		return configItem == null ? new ConfigItem(key, null) : configItem;
	}

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