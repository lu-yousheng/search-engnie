package com.example.demo.common;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class TimeConversionService {

    public static String convertUtcToLocal(String utcTime) {
        // 解析UTC时间
        LocalDateTime utcDateTime = LocalDateTime.parse(utcTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 获取本地时区
        ZoneId localZoneId = ZoneId.of("Asia/Shanghai"); // 替换成你需要的本地时区

        // 转换为本地时区时间
        ZonedDateTime localDateTime = utcDateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(localZoneId);

        // 格式化本地时区时间
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
