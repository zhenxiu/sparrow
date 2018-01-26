package com.sparrow.mq;

import com.sparrow.container.Container;

/**
 * @author harry
 * @date 2018/1/19
 */
public abstract class AbstractMQHandler<T extends MQEvent> implements MQHandler<T> {
    private QueueHandlerMappingContainer queueHandlerMappingContainer;

    public void setQueueHandlerMappingContainer(QueueHandlerMappingContainer queueHandlerMappingContainer) {
        this.queueHandlerMappingContainer = queueHandlerMappingContainer;
    }

    @Override
    public void aware(Container container, String beanName) {
        if (MQHandler.class.isAssignableFrom(this.getClass())) {
            queueHandlerMappingContainer.put(this);
        }
    }
}
