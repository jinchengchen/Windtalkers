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

import com.chenchengzhi.windtalkers.core.Message;
import com.chenchengzhi.windtalkers.core.Talker;
import com.chenchengzhi.windtalkers.core.WindTalkerID;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by jinchengchen on 11/2/14.
 */
public class TalkerLeader implements Talker {

    @Override
    public Message reply(Message request) {
        ObjectNode responseNode =  new ObjectNode(JsonNodeFactory.instance);
        ArrayNode talkers =  new ArrayNode(JsonNodeFactory.instance);
        for (WindTalkerID id : WindTalkerID.values()) {
            talkers.add(id.getName());
        }
        responseNode.put("Windtalkers", talkers);
        request.setResponseBody(responseNode);
        return request;
    }

    @Override
    public WindTalkerID getID() {
        return WindTalkerID.TALKER_LEADER;
    }
}
