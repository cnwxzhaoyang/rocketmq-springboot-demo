package org.example.consumer;

/**
 * description:
 *
 * @author MorningSun
 * @version 1.0
 * @since JDK1.8
 * date 2022/8/4
 */

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.example.constants.RocketMqBizConstant;
import org.example.model.RocketMqMessage;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @RocketMQMessageListener(
 *         topic = "${service.topic}",
 *         consumerGroup = "${service.group}",
 *         selectorExpression = "${service.tag}"
 * )
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = RocketMqBizConstant.SOURCE_TOPIC,
        consumerGroup = RocketMqBizConstant.SOURCE_GROUP,
        selectorExpression = RocketMqBizConstant.SOURCE_TAG
)
public class RocketEntityMessageListener implements RocketMQListener<RocketMqMessage> {


    @Override
    public void onMessage(RocketMqMessage rocketMqMessage) {
        log.info("收到消息【{}】", JSONObject.toJSON(rocketMqMessage));
        try {
            // 方法执行完成之后会自动进行进行ack，后续会分享源码解读
            TimeUnit.SECONDS.sleep(3);
            // 出现异常，将会自动进入重试队列
            // int ex = 10 / 0;
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        log.info("休眠了3s后消费完成");
    }

}
