package com.chenchengzhi.windtalkers.server;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import com.beust.jcommander.JCommander;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.Injector;
import com.google.inject.Stage;


/**
 * Created by jinchengchen on 10/28/14.
 */

public class WindServer implements Runnable {
    static final Logger logger = Logger.getLogger(WindServer.class.getCanonicalName());

    private final ServiceManager serviceManager;

    @Inject
    public WindServer(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void run() {
        logger.info("Start");
        serviceManager.addListener(new ServerListener(), MoreExecutors.sameThreadExecutor());
        serviceManager.startAsync();
    }

    private class ServerListener implements ServiceManager.Listener {

        private final AtomicReference<Boolean> liveness = new AtomicReference<Boolean>(false);

        @Override
        public void healthy() {
            logger.info("Server is healthy, it is ready to process requests!");
            liveness.set(true);
        }

        @Override
        public void stopped() {

        }

        @Override
        public void failure(Service service) {
            logger.severe(String.format("Service %s has failed to start up, aborting",
                    service.getClass().getSimpleName()));
        }

    }

    public static void main(String[] args) {
        WindServerConfiguration configuration = new WindServerConfiguration();
        new JCommander(configuration, args);
        Injector injector = Guice.createInjector(Stage.DEVELOPMENT, configuration);
        WindServer server = injector.getInstance(WindServer.class);
        server.run();
    }
}
