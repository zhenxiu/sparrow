package com.sparrow.rocketmq.impl;

import com.sparrow.container.Container;
import com.sparrow.mq.MQEvent;
import com.sparrow.mq.MQPublisher;
import com.sparrow.mq.MQ_CLIENT;
import com.sparrow.rocketmq.MessageConverter;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.UUID;

/**
 * Created by harry on 2017/6/14.
 */
public class SparrowRocketMQPublisher implements MQPublisher {
    private static Logger logger= LoggerFactory.getLogger(SparrowRocketMQPublisher.class);
    private String nameServerAddress;
    private String group;
    private String topic;
    private String tag;

    private MQProducer producer;
    private MessageConverter messageConverter;
    private Integer retryTimesWhenSendAsyncFailed;

    public String getNameServerAddress() {
        return nameServerAddress;
    }

    public void setNameServerAddress(String nameServerAddress) {
        this.nameServerAddress = nameServerAddress;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTopic() {
        return topic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public MessageConverter getMessageConverter() {
        return messageConverter;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }


    public Integer getRetryTimesWhenSendAsyncFailed() {
        return retryTimesWhenSendAsyncFailed;
    }

    public void setRetryTimesWhenSendAsyncFailed(Integer retryTimesWhenSendAsyncFailed) {
        this.retryTimesWhenSendAsyncFailed = retryTimesWhenSendAsyncFailed;
    }

    @Override
    public void publish(MQEvent event) throws Throwable {
        Message msg = this.messageConverter.createMessage(topic, tag, event);
        String key = UUID.randomUUID().toString();
        msg.setKeys(Collections.singletonList(key));
        SendResult sendResult = producer.send(msg);
        if (!sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
            throw new Throwable(sendResult.toString());
        }
    }

    public void start() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(group);
        producer.setNamesrvAddr(nameServerAddress);
        producer.setInstanceName(MQ_CLIENT.INSTANCE_NAME);
        if (this.retryTimesWhenSendAsyncFailed!=null&&this.retryTimesWhenSendAsyncFailed > 0) {
            producer.setRetryTimesWhenSendAsyncFailed(retryTimesWhenSendAsyncFailed);
            producer.setRetryTimesWhenSendFailed(retryTimesWhenSendAsyncFailed);
        }
        producer.setCreateTopicKey(this.getTopic());

        int maxMessageSize = 1024000;
        producer.setMaxMessageSize(maxMessageSize);
        this.producer = producer;
        producer.start();
    }

    @Override
    public void aware(Container container, String beanName) {
        try {
            this.start();
        } catch (MQClientException e) {
           logger.error("mq client exception",e);
        }
    }
}
