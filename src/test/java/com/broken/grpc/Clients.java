package com.broken.grpc;

import io.grpc.ManagedChannel;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.grpc.annotation.GrpcChannel;
import io.micronaut.grpc.server.GrpcServerChannel;

@Factory
public class Clients {

    @Bean
    GrpcExampleServiceGrpc.GrpcExampleServiceBlockingStub blockingStub(
            @GrpcChannel(GrpcServerChannel.NAME)
            ManagedChannel channel) {
        return GrpcExampleServiceGrpc.newBlockingStub(
                channel
        );
    }

    @Bean
    GrpcExampleServiceGrpc.GrpcExampleServiceStub embeddingsStub(
            @GrpcChannel(GrpcServerChannel.NAME)
            ManagedChannel channel) {
        return GrpcExampleServiceGrpc.newStub(
                channel
        );
    }
}
