package vlove.spring.config;

import java.util.Properties;

import org.springframework.beans.factory.config.PropertiesFactoryBean;

public class MutablePropertiesFactoryBean extends PropertiesFactoryBean {
	private Properties properties;

	public Properties getProperties() {
		return properties;
	}

	@Override
	public void setProperties(Properties properties) {
		super.setProperties(properties);
		this.properties = properties;
	}
}