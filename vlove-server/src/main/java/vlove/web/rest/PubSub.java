package vlove.web.rest;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Schedule;
import org.atmosphere.annotation.Suspend;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.jersey.Broadcastable;
import org.atmosphere.jersey.JerseyBroadcaster;
import org.atmosphere.util.StringFilterAggregator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vlove.model.JAXBBean;

@Path("/{topic}")
@Produces("text/plain;charset=ISO-8859-1")
public class PubSub {
  private static final Logger logger = LoggerFactory.getLogger(PubSub.class);

  @PreDestroy
  public void destroy() {
    logger.info("Testing the @PreDestroy");
  }

  /**
   * Inject a {@link Broadcaster} based on @Path
   */
  private @PathParam("topic")
  Broadcaster topic;

  /**
   * Suspend the response, and register a
   * {@link AtmosphereResourceEventListener} that get notified when events
   * occurs like client disconnection, broadcast or when the response get
   * resumed.
   * 
   * @return A {@link Broadcastable} used to broadcast events.
   */
  @GET
  @Suspend(listeners = { EventsLogger.class })
  public Broadcastable subscribe() {
    return new Broadcastable(topic);
  }

  /**
   * Suspend the response, and register a
   * {@link AtmosphereResourceEventListener} that get notified when events
   * occurs like client disconnection, broadcast or when the response get
   * resumed.
   * 
   * @return A {@link Broadcastable} used to broadcast events.
   */
  @GET
  @Path("subscribeAndUsingExternalThread")
  @Suspend(resumeOnBroadcast = true, listeners = { EventsLogger.class })
  public String subscribeAndResumeUsingExternalThread(final @PathParam("topic") String newTopic) {
    Executors.newSingleThreadExecutor().submit(new Runnable() {
      @Override
      public void run() {
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          // empty
        }
        BroadcasterFactory.getDefault().lookup(JerseyBroadcaster.class, newTopic).broadcast("\nEcho: " + newTopic);
      }
    });
    return "foo";
  }

  /**
   * Suspend the response, and tell teh framework to resume the response \ when
   * the first @Broadcast operation occurs.
   * 
   * @return A {@link Broadcastable} used to broadcast events.
   */
  @GET
  @Suspend(resumeOnBroadcast = true, listeners = { EventsLogger.class })
  @Path("subscribeAndResume")
  public Broadcastable subscribeAndResume() {
    return new Broadcastable(topic);
  }

  /**
   * ' Broadcast XML data using JAXB
   * 
   * @param message
   *          A String from an HTML form
   * @return A {@link Broadcastable} used to broadcast events.
   */
  @POST
  @Produces("application/xml")
  @Broadcast
  public Broadcastable publishWithXML(@FormParam("message") String message) {
    return new Broadcastable(new JAXBBean(message), topic);
  }

  /**
   * Broadcast messahge to this server and also to other server using JGroups
   * 
   * @param message
   *          A String from an HTML form
   * @return A {@link Broadcastable} used to broadcast events.
   */
  @POST
  @Broadcast
  /*
   * @Cluster( name="chat", value= JGroupsFilter.class )
   */
  public Broadcastable publish(@FormParam("message") String message) {
    return broadcast(message);
  }

  /**
   * Retain Broadcast events until we have enough data. See the
   * {@link StringFilterAggregator} to configure the amount of data buffered
   * before the events gets written back to the set of suspended response.
   * 
   * @param message
   *          A String from an HTML form
   * @return A {@link Broadcastable} used to broadcast events.
   */
  @POST
  @Broadcast(value = { StringFilterAggregator.class })
  @Path("aggregate")
  public Broadcastable aggregate(@FormParam("message") String message) {
    return broadcast(message);
  }

  /**
   * Execute periodic {@link Broadcaster#broadcast(java.lang.Object)} operation
   * and resume the suspended connection after the first broadcast operation.
   * 
   * @param message
   *          A String from an HTML form
   * @return A {@link Broadcastable} used to broadcast events.
   */
  @Schedule(period = 5, resumeOnBroadcast = true)
  @POST
  @Path("scheduleAndResume")
  public Broadcastable scheduleAndResume(@FormParam("message") String message) {
    return broadcast(message);
  }

  /**
   * Wait 5 seconds and then execute periodic
   * {@link Broadcaster#broadcast(java.lang.Object)} operations.
   * 
   * @param message
   *          A String from an HTML form
   * @return A {@link Broadcastable} used to broadcast events.
   */
  @Schedule(period = 10, waitFor = 5)
  @POST
  @Path("delaySchedule")
  public Broadcastable delaySchedule(@FormParam("message") String message) {
    return broadcast(message);
  }

  /**
   * Execute periodic {@link Broadcaster#broadcast(java.lang.Object)} operation.
   * 
   * @param message
   *          A String from an HTML form
   * @return A {@link Broadcastable} used to broadcast events.
   */
  @Schedule(period = 5)
  @POST
  @Path("schedule")
  public Broadcastable schedule(@FormParam("message") String message) {
    return broadcast(message);
  }

  /**
   * Delay for 5 seconds the executionof
   * {@link Broadcaster#broadcast(java.lang.Object)} operation
   * 
   * @param message
   *          A String from an HTML form
   * @return A {@link Broadcastable} used to broadcast events.
   */
  @Broadcast(delay = 5)
  @POST
  @Path("delay")
  public Broadcastable delayPublish(@FormParam("message") String message) {
    return broadcast(message);
  }

  @Broadcast(delay = 5, resumeOnBroadcast = true)
  @POST
  @Path("delayAndResume")
  public Broadcastable delayPublishAndResume(@FormParam("message") String message) {
    return broadcast(message);
  }

  /**
   * Buffer the first broadcast events until the second one happens.
   * 
   * @param message
   *          A String from an HTML form
   * @return A {@link Broadcastable} used to broadcast events.
   */
  @Path("buffer")
  @POST
  @Broadcast(delay = 0)
  public Broadcastable buffer(@FormParam("message") String message) {
    return broadcast(message);
  }

  /**
   * Use the {@link Broadcaster#delayBroadcast(java.lang.Object)} directly
   * instead of using the annotation.
   * 
   * @param message
   *          A String from an HTML form
   * @return A {@link Broadcastable} used to broadcast events.
   */
  @POST
  @Path("broadcast")
  public String manualDelayBroadcast(@FormParam("message") String message) {
    topic.delayBroadcast(message, 10, TimeUnit.SECONDS);
    return message;
  }

  /**
   * Timeout the resource
   * 
   * @return A {@link Broadcastable} used to broadcast events.
   */
  @GET
  @Suspend(period = 60, timeUnit = TimeUnit.SECONDS, listeners = { EventsLogger.class })
  @Path("timeout")
  public Broadcastable timeout() {
    return new Broadcastable(topic);
  }

  /**
   * Create a new {@link Broadcastable}.
   * 
   * @param m
   * @return
   */
  Broadcastable broadcast(String m) {
    return new Broadcastable(m + "\n", topic);
  }
}