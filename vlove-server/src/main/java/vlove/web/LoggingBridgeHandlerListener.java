package vlove.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class LoggingBridgeHandlerListener implements ServletContextListener {
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
    
    Logger log = LoggerFactory.getLogger(LoggingBridgeHandlerListener.class);
    log.debug("Bridge handler listener fired.");
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    // nothing going on here
  }
}