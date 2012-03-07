package vlove.server;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vlove.protobuf.VloveProtoMessage.Vlove;
import vlove.protobuf.VloveProtoMessage.Vlove.Operation;

public class VloveHandler extends SimpleChannelUpstreamHandler {
  private static final Logger log = LoggerFactory.getLogger(VloveHandler.class);

  @Override
  public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
    if (e instanceof ChannelStateEvent) {
      log.info(e.toString());
    }
    super.handleUpstream(ctx, e);
  }

  @Override
  public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
    Vlove message = (Vlove) e.getMessage();
    
    log.debug("Message received: {}", message.toString());
    
    Vlove.Builder builder = Vlove.newBuilder();
    builder.setId(-1).setOp(Operation.UPDATE);
    e.getChannel().write(builder.build());
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
    log.warn("Unexpected exception from downstream.", e.getCause());
    e.getChannel().close();
  }
}