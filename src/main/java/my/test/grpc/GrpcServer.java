package my.test.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer {


    private Server server;

    private void start() throws IOException {
        server = ServerBuilder.forPort(8899).addService(new StudentServiceImpl()).build().start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                GrpcServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
        System.out.println("server started!");
    }


    private void stop() {
        System.out.println("call stop");
        if (this.server != null) {
            server.shutdownNow();
        }
    }

    private void awaitTermination() throws InterruptedException {
        if (this.server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception {

        GrpcServer grpcServer = new GrpcServer();
        grpcServer.start();
        grpcServer.awaitTermination();

    }


}
