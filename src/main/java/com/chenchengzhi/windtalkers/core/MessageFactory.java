/*
 * Copyright (C) 2014
 * Author: chen jincheng <loocalvinci@gmail.com>
 * Web: http://chenchengzhi.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.chenchengzhi.windtalkers.core;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

import java.io.IOException;

/**
 * Define the operation for creating a message.
 *
 * @author jchen
 */
public interface MessageFactory {

    public Message parse(HttpRequest message);

    public HttpResponse serialize(Message message) throws IOException;

}
