package org.example.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.example.constants.RocketMqBizConstant;
import org.example.constants.RocketMqDelayLevel;
import org.example.model.RocketMqMessage;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * description:
 *
 * @author MorningSun
 * @version 1.0
 * @since JDK1.8
 * date 2022/8/4
 */
@RestController
@RequestMapping("/rocketmq")
@Slf4j
public class RocketMqController {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 通过实体类发送消息，发送注意事项请参考实体类
     * http://localhost:8888/rocketmq/entity/message
     */
    @RequestMapping("/entity/message")
    public Object sendMessage() {
        // 目的：topic:tag，如果不指定则发往配置的默认地址
        String destination = RocketMqBizConstant.SOURCE_TOPIC + ":" + RocketMqBizConstant.SOURCE_TAG;
        RocketMqMessage message = new RocketMqMessage();
        message.setId(System.currentTimeMillis());
        message.setMessage("当前消息发送时间为：" + LocalDateTime.now());
        // Java时间字段需要单独处理，否则会序列化失败
        message.setCurrentDate(LocalDate.now());
        message.setCurrentDateTime(LocalDateTime.now());
        message.setVersion("1.0");

        /// 发送同步消息，消息成功发送到Broker时才返回，message可以入参批量消息
        // 通过SendResult来处理发送结果
        // SendResult sendResult = rocketMqTemplate.syncSend(destination, message);

        /// 发送时指定业务key
        /*Message<RocketMqMessage> buildMessage = MessageBuilder.withPayload(message)
                // 设置keys
                .setHeader(RocketMQHeaders.KEYS, message.getId())
                .build();
        SendResult sendResult = rocketMqTemplate.syncSend(destination, buildMessage);*/

        /// 发送延迟消息
        Message<RocketMqMessage> buildMessage = MessageBuilder.withPayload(message).build();
        SendResult sendResult = rocketMQTemplate.syncSend(destination, buildMessage, 3000, RocketMqDelayLevel.FIVE_SECOND);

        /// 发送同步有序消息，需要指定hashKey，可以用业务唯一键
        // rocketMqTemplate.syncSendOrderly(destination, message, message.getId().toString());

        /// 发送异步消息，消息发送后及时返回，然后通过回调方法通知
        // rocketMqTemplate.asyncSend(destination, message, new SendCallback() {
        //     @Override
        //     public void onSuccess(SendResult sendResult) {
        //         log.info("消息发送成功【{}】", JSONObject.toJSONString(sendResult));
        //     }
        //
        //     @Override
        //     public void onException(Throwable e) {
        //         log.error("消息发送失败【{}】", e.getMessage());
        //     }
        // });

        /// 发送异步有序消息，需要指定hashKey，可以用业务唯一键
        // rocketMqTemplate.asyncSendOrderly(destination, message, message.getId().toString(), new SendCallback() {
        //     @Override
        //     public void onSuccess(SendResult sendResult) {
        //         log.info("消息发送成功【{}】", JSONObject.toJSONString(sendResult));
        //     }
        //
        //     @Override
        //     public void onException(Throwable e) {
        //         log.error("消息发送失败【{}】", e.getMessage());
        //     }
        // });

        /// 发送单向消息
        // rocketMqTemplate.sendOneWay(destination, message);

        /// 发送单向有序消息，通过MessageBuilder构建
        // Message<RocketMqMessage> buildMessage = MessageBuilder.withPayload(message).build();
        // rocketMqTemplate.sendOneWayOrderly(destination, buildMessage, message.getId().toString());

        /// 发送和接收回调消息，需要实现 RocketMQReplyListener 监听器类才可以，否则将会超时错误
        // rocketMqTemplate.sendAndReceive(destination, message, new RocketMQLocalRequestCallback<String>() {
        //     @Override
        //     public void onSuccess(String message) {
        //         log.info("消息发送成功，消息类型【{}】", message);
        //     }
        //
        //     @Override
        //     public void onException(Throwable e) {
        //         log.error("消息发送失败", e);
        //     }
        // });

        /// 调用抽象类方法发送，最终也是syncSend
        // rocketMqTemplate.convertAndSend(destination, "convertAndSend");

        // 转换消息和发送，底层使用的是syncSend(destination, message)，将会被RocketEntityMessageListener消费
        // Message<RocketMqMessage> buildMessage = MessageBuilder.withPayload(message)
        //         // 设置请求头
        //         .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE)
        //         .build();
        // 将会被RocketEntityMessageListener03消费
        // Message<Object> buildMessage = MessageBuilder.withPayload(new Object()).build();
        // rocketMqTemplate.send(destination, buildMessage);

        /// 发送批量消息，批量消息最终转为单挑进行发送
        // List<Message<String>> msgList = new ArrayList<>();
        // for (int i = 0; i < 10; i++) {
        //     msgList.add(MessageBuilder.withPayload("消息:" + i).build());
        // }
        // rocketMqTemplate.syncSend(destination, msgList);
        return message;
    }

    /**
     * 直接将对象进行传输，也可以自己进行json转化后传输
     */
    @RequestMapping("/messageExt/message")
    public SendResult convertAndSend() {
        String destination = "rocketmq_source_code_topic:rocketmq_source_code_ext_tag";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "messageExt");
        return rocketMQTemplate.syncSend(destination, jsonObject);
    }
}
