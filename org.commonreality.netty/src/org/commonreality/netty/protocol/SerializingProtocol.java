/*
 * Created on Feb 22, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.commonreality.netty.protocol;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

 
import org.slf4j.LoggerFactory;
import org.commonreality.net.protocol.IProtocolConfiguration;
import org.commonreality.netty.impl.LoggingHandler;

/**
 * @author developer
 */
public class SerializingProtocol implements IProtocolConfiguration
{
  /**
   * logger definition
   */
  static private final org.slf4j.Logger LOGGER = LoggerFactory
                                      .getLogger(SerializingProtocol.class);

  @Override
  public void configure(Object session)
  {
    Channel channel = (Channel) session;

    ChannelPipeline pipeline = channel.pipeline();

    /*
     * does order actually matter?
     */
    // if (channel instanceof ServerChannel)
    // pipeline.addLast(
    // new ObjectDecoder(ClassResolvers.cacheDisabled(getClass()
    // .getClassLoader())),
    // new ObjectEncoder());
    // else
    if (LOGGER.isDebugEnabled()) pipeline.addLast(new LoggingHandler("wire"));

    pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(getClass()
        .getClassLoader())));
    pipeline.addLast(new ObjectEncoder());

    if (LOGGER.isDebugEnabled()) pipeline.addLast(new LoggingHandler("app"));
  }

}
