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

import java.util.Set;
import java.util.concurrent.Executors;

import com.beust.jcommander.Parameter;
import com.chenchengzhi.windtalkers.core.MessageFactory;
import com.chenchengzhi.windtalkers.core.Talker;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;


/**
 * Created by jinchengchen on 11/1/14.
 */
public class WindServerConfiguration extends AbstractModule {

    @Parameter(names = "--port", description = "The port on which the server should listen")
    private int port = 3280;

    @Parameter(names = "--verbose", description = "Enables verbose logging")
    private boolean verbose = false;

    @Parameter(names = "--basePath", description = "")
    private String basePath = "/windtalkers/";

    @Override
    public void configure() {
        bindConstant().annotatedWith(Names.named("port")).to(port);
        bindConstant().annotatedWith(Names.named("verbose")).to(verbose);
        bindConstant().annotatedWith(Names.named("basePath")).to(basePath);

        bind(MessageFactory.class).to(WindMessageFactory.class);
        bind(IdleStateAwareChannelHandler.class).to(WindChannelHandler.class);

        // Services
        Multibinder<Service> services = Multibinder.newSetBinder(binder(), Service.class);
        services.addBinding().to(HttpService.class);

        // Talkers
        Multibinder<Talker> talkers = Multibinder.newSetBinder(binder(), Talker.class);
        talkers.addBinding().to(TalkerLeader.class);
    }

    @Provides @Singleton
    ServiceManager getServiceManager(Set<Service> services) {
        return new ServiceManager(services);
    }

    @Provides
    @Named("talkerExecutor")
    private ListeningExecutorService getAgentsExecutor() {
        return MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    }
}
