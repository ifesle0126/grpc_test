package my.test.grpc;

import io.grpc.stub.StreamObserver;
import my.test.proto.*;

import java.util.stream.Stream;

public class StudentServiceImpl extends StudentServiceGrpc.StudentServiceImplBase {


    @Override
    public void getRealNameByUserName(MyRequest request, StreamObserver<MyResponse> responseObserver) {
        System.out.println("接受到客户端请求1");
        MyResponse response = MyResponse.newBuilder().setRealname("real name: " + request.getUsername()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getStudentsByAge(StudentRequest request, StreamObserver<StudentResponse> responseObserver) {
        System.out.println("接受到客户端请求2");
        StudentResponse response1 = StudentResponse.newBuilder()
                .setName("aaa").setAge(20).setCity("111").build();
        StudentResponse response2 = StudentResponse.newBuilder()
                .setName("bbb").setAge(21).setCity("222").build();
        StudentResponse response3 = StudentResponse.newBuilder()
                .setName("ccc").setAge(22).setCity("333").build();
        responseObserver.onNext(response1);
        responseObserver.onNext(response2);
        responseObserver.onNext(response3);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<StudentRequest> getStudentsByAges(StreamObserver<StudentResponseList> responseObserver) {
        return new StreamObserver<StudentRequest>() {
            @Override
            public void onNext(StudentRequest value) {
                System.out.println("接收到客户端请求: " + value.getAge());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                StudentResponse st1 = StudentResponse.newBuilder()
                        .setName("aaa").setAge(20).setCity("111").build();
                StudentResponse st2 = StudentResponse.newBuilder()
                        .setName("bbb").setAge(21).setCity("222").build();
                StudentResponseList list = StudentResponseList.newBuilder().addStudentResponse(st1).addStudentResponse(st2).build();
                responseObserver.onNext(list);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<StreamRequest> biTalk(StreamObserver<StreamResponse> responseObserver) {
        return new StreamObserver<StreamRequest>() {
            @Override
            public void onNext(StreamRequest value) {
                System.out.println("onNext: " + value.getRequestInfo());
                int n = Integer.valueOf(value.getRequestInfo());
                StreamResponse streamResponse = StreamResponse.newBuilder()
                        .setResponseInfo(String.valueOf(n + 1)).build();
                responseObserver.onNext(streamResponse);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
