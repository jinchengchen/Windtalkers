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

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Created by jinchengchen on 11/2/14.
 */
public interface Talker {

    /**
     * A Wind talker.
     *
     * @param request the request to respond to
     * @return the response
     */
    public Message reply(Message request);

    /**
     * The unique name of this Wind talker.
     * Wind Server will also take this name
     * as the unique path to access this talker.
     *
     * @return the name
     */
    public WindTalkerID getID();
}
