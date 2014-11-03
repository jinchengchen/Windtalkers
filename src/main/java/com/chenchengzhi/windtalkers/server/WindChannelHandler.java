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

import com.chenchengzhi.windtalkers.core.*;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by jinchengchen on 11/2/14.
 */
public class WindChannelHandler extends IdleStateAwareChannelHandler {

    private final Set<Talker> talkers;
    private final MessageFactory messageFactory;
    private final ListeningExecutorService executor;

    @Inject
    public WindChannelHandler(Set<Talker> talkers, MessageFactory messageFactory,
            @Named("talkerExecutor") ListeningExecutorService executor) {
        this.talkers = talkers;
        this.messageFactory = messageFactory;
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) {
        HttpRequest request = (HttpRequest) event.getMessage();
        final Message message = messageFactory.parse(request);
        WindTalkerID talkerId = message.getTalkerID();
        for (final Talker talker : talkers) {
            if (talker.getID().equals(talkerId)) {
                Callable<Message> callable = new Callable<Message>() {

                    @Override
                    public Message call() throws Exception {
                        return talker.reply(message);
                    }

                };
                ListenableFuture<Message> response =  executor.submit(callable);

                Futures.addCallback(response, new ResponseCallback(event, message));
                return;
            }
        }
        event.getChannel().write(new DefaultHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.NOT_FOUND));
    }

    /**
     * Handles the return path of a response.
     *
     * <p>Instances of this class are attached as listeners to the future
     * result of a request processing.
     *
     */
    private class ResponseCallback implements FutureCallback<Message> {

        private Message message;
        private MessageEvent event;

        private ResponseCallback(MessageEvent event, Message message) {
            this.message = message;
            this.event = event;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onFailure(Throwable throwable) {
            message.put(Issue.class, Issue.of());
            HttpResponse httpResponse;
            try {
                httpResponse = messageFactory.serialize(message);
                event.getChannel().write(httpResponse);
            } catch (IOException e) {
                Throwables.propagate(e);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onSuccess(Message response) {
            HttpResponse httpResponse;
            try {
                httpResponse = messageFactory.serialize(message);
                event.getChannel().write(httpResponse);
            } catch (IOException e) {
                Throwables.propagate(e);
            }
        }
    }
}
