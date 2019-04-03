package org.commonreality.netty.impl;

/*
 * default logging
 */
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collection;

 
import org.slf4j.LoggerFactory;
import org.commonreality.net.transform.IMessageTransfromer;

public class NettyMessageTransformer extends ChannelHandlerAdapter
{
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger LOGGER = LoggerFactory
                                                .getLogger(NettyMessageTransformer.class);

  private final IMessageTransfromer  _filter;

  public NettyMessageTransformer(IMessageTransfromer filter)
  {
    _filter = filter;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg)
      throws Exception
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("TRansforming %s", msg));

    Collection<?> msgs = _filter.messageReceived(msg);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Transformed %s to %s", msg, msgs));

    for (Object message : msgs)
      ctx.fireChannelRead(message);
  }
}
