/*
 * Copyright (C) 2014
 * Author: chen jincheng <loocalvinci@gmail.com>
 * Web: http://chenchengzhi.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */

package com.chenchengzhi.windtalkers.server;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.logging.InternalLogLevel;
import org.jboss.netty.util.HashedWheelTimer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by jinchengchen on 11/2/14.
 */
public class HttpService extends AbstractIdleService {

    private static final Logger logger = Logger.getLogger(HttpService.class
            .getCanonicalName());

    private final int port;
    private final boolean verbose;
    private final IdleStateAwareChannelHandler channelHandler;
    private final ServerBootstrap serverBootstrap;
    private final ChannelPipelineFactory pipelineFactory;
    private final ChannelGroup httpChannels = new DefaultChannelGroup("wind-http");

    @Inject
    public HttpService(@Named("port") int port, @Named("verbose") boolean verbose,
            IdleStateAwareChannelHandler channelHandler) {
        this.port = port;
        this.verbose = verbose;
        this.channelHandler = channelHandler;
        this.serverBootstrap = createServerBootstrap();
        this.pipelineFactory = createPipelineFactory();
    }

    @Override
    protected void startUp() {
        logger.info("HTTP service binding to port " + port);
        serverBootstrap.setPipelineFactory(pipelineFactory);
        Channel serverChannel = serverBootstrap.bind(new InetSocketAddress(port));
        httpChannels.add(serverChannel);
    }

    @Override
    protected void shutDown() {
        httpChannels.close().awaitUninterruptibly();
        serverBootstrap.releaseExternalResources();
    }

    private ServerBootstrap createServerBootstrap() {
        Executor bossThreadsExecutor = Executors.newCachedThreadPool(new ThreadFactory() {

            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "Axon Boss Thread");
            }
        });

        Executor workerThreadsExecutor = Executors.newCachedThreadPool(new ThreadFactory() {

            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "Axon HTTP Server Worker Thread");
            }
        });

        ChannelFactory channelFactory = new NioServerSocketChannelFactory(bossThreadsExecutor,
                workerThreadsExecutor);
        return new ServerBootstrap(channelFactory);
    }

    private ChannelPipelineFactory createPipelineFactory() {
        return new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                if (verbose) {
                    pipeline.addLast("logging", new LoggingHandler(InternalLogLevel.INFO));
                }
                pipeline.addLast("requestDecoder", new HttpRequestDecoder());
                pipeline.addLast("responseEncoder", new HttpResponseEncoder());
                pipeline.addLast("chunkAggregator", new HttpChunkAggregator(10485760));
                pipeline.addLast("compressor", new HttpContentCompressor());
                pipeline.addLast("decompressor", new HttpContentDecompressor());
                pipeline.addLast("idleDetector",
                        new IdleStateHandler(new HashedWheelTimer(), 0, 0, 0, TimeUnit.SECONDS));
                pipeline.addLast("windserver", channelHandler);

                return pipeline;
            }
        };
    }
}
