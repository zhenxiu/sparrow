package com.sparrow.rocketmq.impl;

import com.sparrow.cache.CacheClient;
import com.sparrow.constant.cache.KEY;
import com.sparrow.mq.*;
import com.sparrow.rocketmq.MessageConverter;
import com.sparrow.support.redis.impl.RedisDistributedCountDownLatch;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by harry on 2017/6/14.
 */
public class SparrowRocketMQMessageListener implements MessageListenerConcurrently {

    public SparrowRocketMQMessageListener(){
        System.out.println("init spring rocket mq message listener");
    }
    private static Logger logger = LoggerFactory.getLogger(SparrowRocketMQMessageListener.class);

    private QueueHandlerMappingContainer queueHandlerMappingContainer = MQContainerProvider.getContainer();
    private MessageConverter messageConverter;
    private CacheClient cacheClient;

    public void setQueueHandlerMappingContainer(QueueHandlerMappingContainer queueHandlerMappingContainer) {
        this.queueHandlerMappingContainer = queueHandlerMappingContainer;
    }

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    protected void before(MQEvent event,String keys) {
        logger.info("starting sparrow consume {},keys {}...",event,keys);
    }

    protected void after(MQEvent event,String idempotentKey,String keys) {
        logger.info("ending sparrow consume {},keys {} ...",event,keys);
        if (idempotentKey == null) {
            return;
        }
        KEY idempotent=KEY.parse(idempotentKey);
        RedisDistributedCountDownLatch redisDistributedCountDownLatch = new RedisDistributedCountDownLatch(cacheClient, idempotent);
        redisDistributedCountDownLatch.consume(keys);
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext context) {
        MessageExt message = list.get(0);
        String type = message.getProperties().get(MQ_CLIENT.CLASS_NAME);
        try {
            if (logger.isInfoEnabled()) {
                logger.info("receive msg:" + message.toString());
            }
            MQHandler handler = queueHandlerMappingContainer.get(type);
            try {
                MQEvent event = messageConverter.fromMessage(message);
                this.before(event,message.getKeys());
                handler.handler(event);
                this.after(event,message.getProperties().get(MQ_CLIENT.IDEMPOTENT_KEY),message.getKeys());
            } catch (Throwable e) {
                logger.error("message error", e);
            }
        } catch (Throwable e) {
            logger.error("process failed, msg : " + message, e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
