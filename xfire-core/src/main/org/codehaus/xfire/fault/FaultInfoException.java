package org.codehaus.xfire.fault;

public class FaultInfoException extends Exception {

    public FaultInfoException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public FaultInfoException(String arg0) {
        super(arg0);
    }

    public FaultInfoException(Throwable arg0) {
        super(arg0);
    }

    public FaultInfoException() {
        super();
    }

}
