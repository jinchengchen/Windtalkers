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

/**
 * Created by jinchengchen on 10/29/14.
 */
public enum StatusCode {

    OK(200, "200 OK"),
    BAD_REQUEST(400, "400 Bad request"),
    NOT_FOUND(404, "404 Not found"),
    REQUEST_TIMEOUT(408, "408 Request timeout"),
    SERVICE_ERROR(503, "503 Service error")
    ;

    private final int code;
    private final String description;

    private StatusCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
