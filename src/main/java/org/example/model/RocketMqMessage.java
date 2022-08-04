package org.example.model;

import lombok.Data;

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
@Data
public class RocketMqMessage {

    private Long id;
    private String message;
    private String version;
    /**
     * LocalDate和LocalDateTime默认不支持，需要单独处理
     */
    private LocalDate currentDate;
    private LocalDateTime currentDateTime;

}
