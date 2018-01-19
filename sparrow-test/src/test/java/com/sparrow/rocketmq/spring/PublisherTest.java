package com.sparrow.rocketmq.spring;

import com.sparrow.mq.MQPublisher;
import com.sparrow.rocketmq.protocol.event.HelloEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by harry on 2017/6/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring_rocket_mq_product.xml"})
public class PublisherTest {

    @Autowired
    private MQPublisher mqPublisher;

    @Test
    public void publish() {
        HelloEvent helloEvent = new HelloEvent();
        helloEvent.setMessage("msg");
        try {
            while (true){
                mqPublisher.publish(helloEvent);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
