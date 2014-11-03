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

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;

/**
 * Created by jinchengchen on 10/29/14.
 */
public class Issue extends RuntimeException implements Understandable {

    private StatusCode statusCode;

    private String className;

    private String methodName;

    private String reason;

    private Issue(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public static Issue of() {
        return new Issue(StatusCode.SERVICE_ERROR);
    }

    public static Issue of(StatusCode statusCode, String className, String methodName, String reason) {
        Preconditions.checkNotNull(statusCode);
        Preconditions.checkNotNull(className);
        Preconditions.checkNotNull(methodName);
        Preconditions.checkNotNull(reason);
        Issue issue = new Issue(statusCode);
        issue.className = className;
        issue.methodName = methodName;
        issue.reason = reason;
        return issue;
    }

    public ObjectNode translate() {
        ObjectNode errorsNode = new ObjectNode(JsonNodeFactory.instance);

        errorsNode.put("statusCode", statusCode.getDescription());

        if (className != null) {
            errorsNode.put("className", className);
            errorsNode.put("methodName", methodName);
            errorsNode.put("reason", reason);
        }

        return errorsNode;
    }

    public StatusCode getStatusCode() {
        return this.statusCode;
    }
}
