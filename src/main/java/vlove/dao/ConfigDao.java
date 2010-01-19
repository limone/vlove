package vlove.dao;

import vlove.model.ConfigItem;

public interface ConfigDao {
	public ConfigItem getConfigItem(String key);
	public void saveConfigItem(ConfigItem item);
}