package vlove.web.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventsLogger implements AtmosphereResourceEventListener {
  private static final Logger log = LoggerFactory.getLogger(EventsLogger.class);

  public EventsLogger() {
    // empty
  }

  @Override
  public void onSuspend(final AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event) {
    log.info("onResume: " + event);
  }

  @Override
  public void onResume(AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event) {
    log.info("onResume: " + event);
  }

  @Override
  public void onDisconnect(AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event) {
    log.info("onDisconnect: " + event);
  }

  @Override
  public void onBroadcast(AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event) {
    log.info("onBroadcast: " + event);
  }

  @Override
  public void onThrowable(AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event) {
    event.throwable().printStackTrace(System.err);
  }
}