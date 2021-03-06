package org.commonreality.netty.transport;

import java.util.concurrent.ThreadFactory;
import java.util.function.BiFunction;

import org.slf4j.LoggerFactory;

/*
 * default logging
 */
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;

public class NettyConfig
{
  /**
   * Logger definition
   */
  static private final transient org.slf4j.Logger                  LOGGER = LoggerFactory
                                                                              .getLogger(NettyConfig.class);

  private final BiFunction<Integer, ThreadFactory, EventLoopGroup> _serverSupplier;

  private final BiFunction<Integer, ThreadFactory, EventLoopGroup> _clientSupplier;

  private final Class<? extends ServerChannel>                     _serverClass;

  private final Class<? extends Channel>                           _clientClass;

  public NettyConfig(Class<? extends ServerChannel> serverClass,
      BiFunction<Integer, ThreadFactory, EventLoopGroup> serverSupplier,
      Class<? extends Channel> clientClass,
      BiFunction<Integer, ThreadFactory, EventLoopGroup> clientSupplier)
  {
    _serverSupplier = serverSupplier;
    _clientSupplier = clientSupplier;
    _clientClass = clientClass;
    _serverClass = serverClass;
  }

  public BiFunction<Integer, ThreadFactory, EventLoopGroup> getServerSupplier()
  {
    return _serverSupplier;
  }

  public BiFunction<Integer, ThreadFactory, EventLoopGroup> getClientSupplier()
  {
    return _clientSupplier;
  }

  public Class<? extends ServerChannel> getServerClass()
  {
    return _serverClass;
  }

  public Class<? extends Channel> getClientClass()
  {
    return _clientClass;
  }

}
