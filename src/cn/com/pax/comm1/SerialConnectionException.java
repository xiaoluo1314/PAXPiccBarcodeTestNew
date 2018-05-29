package cn.com.pax.comm1;

@SuppressWarnings("serial")
public class SerialConnectionException extends Exception {

    public SerialConnectionException(String str) {
        super(str);
    }

    public SerialConnectionException() {
        super();
    }
}
