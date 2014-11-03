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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.inject.Key;
import com.google.inject.name.Names;

/**
 * Created by jinchengchen on 10/28/14.
 */
public class Message extends BindingMap{

    public static final Key<WindTalkerID> SERVICE_ID = Key.get(WindTalkerID.class);
    public static final Key<JsonNode> REQUEST_BODY = Key.get(JsonNode.class, Names.named("requestBody"));
    public static final Key<ObjectNode> RESPONSE_BODY = Key.get(ObjectNode.class, Names.named("responseBody"));

    public Message(WindTalkerID talkers) {
        Preconditions.checkNotNull(talkers);
        put(SERVICE_ID, talkers);
    }
    //TODO: Process the header

    public WindTalkerID getTalkerID() {
        return get(SERVICE_ID);
    }

    public JsonNode getRequestBody() {
        return get(REQUEST_BODY);
    }

    public void setRequestBody(JsonNode request) {
        if (request == null) {
            return;
        }
        put(REQUEST_BODY, request);
    }

    public ObjectNode getResponseBody() {
        return get(RESPONSE_BODY);
    }

    public void setResponseBody(ObjectNode response) {
        if (response == null) {
            return;
        }
        put(RESPONSE_BODY, response);
    }
}
