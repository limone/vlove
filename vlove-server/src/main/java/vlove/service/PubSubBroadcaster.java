package vlove.service;

import java.util.concurrent.atomic.AtomicBoolean;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereServlet.AtmosphereConfig;
import org.atmosphere.jersey.JerseyBroadcaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PubSubBroadcaster extends JerseyBroadcaster {
  private static final Logger logger = LoggerFactory.getLogger(PubSubBroadcaster.class);

  private final AtomicBoolean isSet  = new AtomicBoolean(false);

  public PubSubBroadcaster(String id, AtmosphereConfig config) {
    super(id, config);
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected void broadcast(final AtmosphereResource<?,?> r, final AtmosphereResourceEvent e) {
    if (!isSet.getAndSet(true)) {
      logger.info("This is just an example that demonstrate " + "how a Broadcaster can be customized using atmosphere.xml or by " + "defining it inside web.xml");
    }

    super.broadcast(r, e);
  }
}