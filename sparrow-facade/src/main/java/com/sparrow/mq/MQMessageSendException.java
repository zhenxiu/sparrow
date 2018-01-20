package com.sparrow.mq;

/**
 * @author by harry
 */
public class MQMessageSendException extends RuntimeException {
    public MQMessageSendException(String error) {
        super(error);
    }

    public MQMessageSendException(String error,Throwable e){
        super(error,e);
    }
}
