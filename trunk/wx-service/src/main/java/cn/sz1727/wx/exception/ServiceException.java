package cn.sz1727.wx.exception;

public class ServiceException extends Exception {
    
    private static final long serialVersionUID = 1803521469744438980L;

    public ServiceException() {
    }

    public ServiceException(Exception e) {
        super(e);
    }

    public ServiceException(String s) {
        super(s);
    }
}
