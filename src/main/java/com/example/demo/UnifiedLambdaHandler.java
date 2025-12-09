package com.example.demo;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UnifiedLambdaHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {

    private static final SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    static {
        try {
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(DemoApplication.class);
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize Spring Boot handler", e);
        }
    }

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest request, Context context) {

        try {
            System.out.println("RAW EVENT PAYLOAD:\n" + new ObjectMapper().writeValueAsString(request));
        } catch (Exception e) {}

        System.out.println("Method: " + request.getHttpMethod());
        System.out.println("Path: " + request.getPath());

        return handler.proxy(request, context);
    }

}
