package com.t1m0.test;


/** The IConnectionProvider interface provides an abstraction for the network access. */
public interface IConnectionProvider {

    class ConnectionFailedException extends Exception {
        public ConnectionFailedException(String s) {super(s);}
        public ConnectionFailedException(String s, Throwable throwable) {super(s, throwable);}
    }

    /** Wraps the response of the different implementations to check, if the call was successful. */
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

    /** Sends the given {@link String} to the defined network location */
    Response sendData(String data) throws ConnectionFailedException;

    /** Verifies if the defined network location can be accessed or not */
    boolean verifyConnection();

}
