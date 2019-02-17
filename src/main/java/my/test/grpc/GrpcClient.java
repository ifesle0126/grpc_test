package my.test.grpc;

import com.google.api.AuthRequirement;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import my.test.proto.*;

import java.util.Iterator;

public class GrpcClient {

    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("127.0.0.1", 8899)
                .usePlaintext().build();
        // 同步
        StudentServiceGrpc.StudentServiceBlockingStub blockingStub = StudentServiceGrpc.newBlockingStub(managedChannel);
        // 异步
        StudentServiceGrpc.StudentServiceStub stub = StudentServiceGrpc.newStub(managedChannel);


        MyResponse response = blockingStub.getRealNameByUserName(MyRequest.newBuilder().setUsername("zhujq").build());
        System.out.println(response.getRealname());
        System.out.println("=================================");

        Iterator<StudentResponse> iter = blockingStub.getStudentsByAge(StudentRequest.newBuilder().setAge(10).build());
        while (iter.hasNext()) {
            StudentResponse sr = iter.next();
            System.out.println(sr.getName() + ":" + sr.getAge() + ":" + sr.getCity());
        }
        System.out.println("=================================");


        StreamObserver<StudentResponseList> studentResponseListStreamObserver = new StreamObserver<StudentResponseList>() {
            @Override
            public void onNext(StudentResponseList value) {
                for (StudentResponse sr : value.getStudentResponseList()) {
                    System.out.println(sr.getName() + ":" + sr.getAge() + ":" + sr.getCity());
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("completed!");
            }
        };
        StreamObserver<StudentRequest> studentRequestStreamObserver = stub.getStudentsByAges(studentResponseListStreamObserver);
        studentRequestStreamObserver.onNext(StudentRequest.newBuilder().setAge(20).build());
        studentRequestStreamObserver.onNext(StudentRequest.newBuilder().setAge(21).build());
        studentRequestStreamObserver.onNext(StudentRequest.newBuilder().setAge(22).build());
        studentRequestStreamObserver.onNext(StudentRequest.newBuilder().setAge(23).build());
        studentRequestStreamObserver.onCompleted();

        try {
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("=================================");

        StreamObserver<StreamRequest> streamRequestStreamObserver = stub.biTalk(new StreamObserver<StreamResponse>() {
            @Override
            public void onNext(StreamResponse value) {
                System.out.println("onNext: " + value.getResponseInfo());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("completed");
            }
        });

        for (int i = 0; i < 10; i++) {
            streamRequestStreamObserver.onNext(StreamRequest.newBuilder().setRequestInfo(String.valueOf(i)).build());
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }

        System.out.println("=================================");

        managedChannel.shutdown();
    }
}
