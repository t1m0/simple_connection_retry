package com.t1m0.test;


public interface IConnectionProvider {

    class ConnectionFailedException extends Exception {
        public ConnectionFailedException(String s) {super(s);}
        public ConnectionFailedException(String s, Throwable throwable) {super(s, throwable);}
    }

    class Response{
        private final boolean success;
        private final String message;

        public Response(boolean success, String message) {
            this.success = success;
            this.message = message;
}
        public boolean isSuccess() {return success;}
        public String getMessage() {return message;}
    }

    Response sendData(String data) throws ConnectionFailedException;

    boolean verifyConnection();

}
