package com.broken.grpc;

import io.grpc.stub.StreamObserver;
import jakarta.inject.Singleton;

@Singleton
public class GrpcExampleService extends GrpcExampleServiceGrpc.GrpcExampleServiceImplBase {

    @Override
    public void send(GrpcExampleRequest req, StreamObserver<GrpcExampleReply> responseObserver) {
        GrpcExampleReply reply = GrpcExampleReply.newBuilder().setMessage("I sure hope this works! " + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

}
