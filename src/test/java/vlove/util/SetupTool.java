package vlove.util;

import static org.junit.Assert.assertNotNull;

import org.aspectj.lang.annotation.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import vlove.dao.ConfigDao;
import vlove.model.ConfigItem;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-config.xml")
@Transactional
@TransactionConfiguration(defaultRollback = false)
public class SetupTool {
  private transient final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private ConfigDao              cd;

  @Test
  public void populateConfig() {
    log.debug("Creating vlove URL.");
    ConfigItem vloveUrl = new ConfigItem("libvirt.url", "test:///default");
    cd.saveConfigItem(vloveUrl);
    log.debug("vlove URL saved.");
  }

  @Test
  @After("populateConfig")
  public void checkConfig() {
    log.debug("Checking for vlove URL.");
    ConfigItem vloveUrl = cd.getConfigItem("libvirt.url");
    assertNotNull("Config item not found.", vloveUrl);
    assertNotNull("Config item found, but value was empty.", vloveUrl.getValue());
  }
}