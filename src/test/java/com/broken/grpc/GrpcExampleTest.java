package com.broken.grpc;

import com.google.common.collect.Maps;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;
import io.grpc.stub.StreamObserver;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest
class GrpcExampleTest {
    private static final Logger log = LoggerFactory.getLogger(GrpcExampleTest.class);

    @Inject
    EmbeddedApplication<?> application;

    private final Map<String, GrpcExampleReply> finishedDocs = Maps.newConcurrentMap();

    AtomicInteger counter = new AtomicInteger();

    @Test
    void testItWorks() {
        Assertions.assertTrue(application.isRunning());
    }

    @Inject
    GrpcExampleServiceGrpc.GrpcExampleServiceBlockingStub endpoint;

    @Inject
    GrpcExampleServiceGrpc.GrpcExampleServiceStub endpoint2;

    StreamObserver<GrpcExampleReply> streamObserver = new StreamObserver<>() {
        @Override
        public void onNext(GrpcExampleReply reply) {
            try {
                log.info("RESPONSE, returning embeddings: {}", JsonFormat.printer().print(
                        reply));
                finishedDocs.put("example" + counter.incrementAndGet(), reply);
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onError(Throwable throwable) {

            log.error("Not implemented", throwable);
        }

        @Override
        public void onCompleted() {
            log.info("Finished");
        }

        // Override OnError ...
    };


    @Test
    void testServerEndpoint() throws InvalidProtocolBufferException {

        GrpcExampleRequest request = GrpcExampleRequest.newBuilder()
                .setName("hello you micronaut service").build();
        GrpcExampleReply reply = endpoint.send(request);
        assertNotNull(reply);
        assertNotNull(reply.getMessage());
        JsonFormat.printer().print(reply);

    }

    @Test
    void testAsyncEndpoint() {

        GrpcExampleRequest request = GrpcExampleRequest.newBuilder()
                .setName("async test").build();
        endpoint2.send(request, streamObserver);

        await().atMost(3, SECONDS).until(() -> finishedDocs.size() == 1);
    }

}
