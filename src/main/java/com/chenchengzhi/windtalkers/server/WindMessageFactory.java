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
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.google.common.base.Charsets;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Logger;

/**
 * Created by jinchengchen on 10/28/14.
 */
public class WindMessageFactory implements MessageFactory {

    private static final Logger logger = Logger.getLogger(WindMessageFactory.class.getCanonicalName());

    private final String basePath;
    private final JsonFactory jsonFactory;

    @Inject
    public WindMessageFactory(@Named("basePath") String basePath) {
        this.basePath = basePath.toLowerCase();
        jsonFactory = new ObjectMapper().getJsonFactory();
    }

    @Override
    public Message parse(HttpRequest httpRequest) {
        WindTalkerID talkerID = pathResolve(httpRequest);
        if (talkerID == null) {
            throw Issue.of(StatusCode.NOT_FOUND, WindMessageFactory.class.getCanonicalName(),
                    "parse", "Talker not found!");
        }

        Message request = new Message(talkerID);

        ChannelBuffer buffer = httpRequest.getContent();
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getJsonFactory();
        byte[] rawRequestBytes = buffer.array();
        String content = new String(rawRequestBytes, Charsets.UTF_8);

        try {
            JsonNode body = mapper.readTree(factory.createJsonParser(content));
            request.setRequestBody(body);
        } catch (JsonProcessingException jpe) {
            throw Issue.of(StatusCode.BAD_REQUEST, WindMessageFactory.class.getCanonicalName(),
                    "parse", "JsonProcessingException");
        } catch (IOException ioe) {
            throw Issue.of(StatusCode.BAD_REQUEST, WindMessageFactory.class.getCanonicalName(),
                    "parse", "IOException");
        }

        return request;
    }

    @Override
    public HttpResponse serialize(Message message) throws IOException {
        Issue issue = message.get(Issue.class);

        if (issue != null) {
            return throwError(issue);
        }

        HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK);
        ObjectNode response = message.getResponseBody();

        if (response != null) {
            httpResponse.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/json");
            StringWriter writer = new StringWriter();
            JsonGenerator generator = jsonFactory.createJsonGenerator(writer);
            generator.setPrettyPrinter(new DefaultPrettyPrinter());

            response.serialize(generator, new DefaultSerializerProvider.Impl());
            generator.flush();
            httpResponse.setContent(ChannelBuffers.copiedBuffer(writer.getBuffer(), Charsets.UTF_8));
        }

        httpResponse.setHeader(HttpHeaders.Names.CONTENT_LENGTH, httpResponse.getContent()
                .readableBytes());
        return httpResponse;
    }

    protected HttpResponse throwError(Issue issue) throws IOException {
        HttpResponse errorResponse =new DefaultHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.valueOf(issue.getStatusCode().getCode()));

        ObjectNode errorNode = issue.translate();
        StringWriter writer = new StringWriter();
        JsonGenerator generator = jsonFactory.createJsonGenerator(writer);
        generator.setPrettyPrinter(new DefaultPrettyPrinter());
        errorNode.serialize(generator, new DefaultSerializerProvider.Impl());
        generator.flush();

        errorResponse.setContent(ChannelBuffers.copiedBuffer(writer.getBuffer(), Charsets.UTF_8));
        errorResponse.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/json");
        errorResponse.setHeader(HttpHeaders.Names.CONTENT_LENGTH, errorResponse.getContent().readableBytes());
        return errorResponse;
    }

    protected WindTalkerID pathResolve(HttpRequest httpRequest) {
        QueryStringDecoder decoder = new QueryStringDecoder(httpRequest.getUri(), Charsets.UTF_8);
        String fullPath = decoder.getPath().toLowerCase();

        if (!fullPath.startsWith(basePath)) {
            return null;
        }

        String path = fullPath.substring(basePath.length());
        return WindTalkerID.getByName(path);
    }
}
