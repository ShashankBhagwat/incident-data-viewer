package com.example.demo;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.model.HttpApiV2ProxyRequest;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * ✅ Correct handler for AWS Lambda Function URL (HTTP API v2)
 * ✅ Fixes the "200 OK but empty body" issue
 * ✅ Works with Spring Boot + Thymeleaf
 */
public class UnifiedLambdaHandler
        implements RequestHandler<HttpApiV2ProxyRequest, AwsProxyResponse> {

    private static final SpringBootLambdaContainerHandler<
            HttpApiV2ProxyRequest, AwsProxyResponse> handler;

    static {
        try {
            handler = SpringBootLambdaContainerHandler
                    .getHttpApiV2ProxyHandler(DemoApplication.class);
        } catch (ContainerInitializationException e) {
            throw new RuntimeException("❌ Failed to initialize Spring Boot application", e);
        }
    }

    @Override
    public AwsProxyResponse handleRequest(HttpApiV2ProxyRequest event, Context context) {

        System.out.println("✅ HTTP EVENT RECEIVED VIA LAMBDA FUNCTION URL");
        System.out.println("➡️ Path: " + event.getRawPath());
        System.out.println("➡️ Method: " + event.getRequestContext().getHttp().getMethod());

        return handler.proxy(event, context);
    }
}
