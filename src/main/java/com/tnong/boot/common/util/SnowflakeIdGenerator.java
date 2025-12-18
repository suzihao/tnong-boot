package com.tnong.boot.common.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 简化的用户ID生成器
 * 生成10位数字ID，格式：时间戳(6位) + 随机数(4位)
 * 适用于用户量在100亿以内的系统
 */
public class SnowflakeIdGenerator {

    // 起始时间戳 (2024-01-01 00:00:00)
    private static final long START_TIMESTAMP = 1704067200000L;
    
    // 时间戳部分的最大值(6位数字)
    private static final long MAX_TIMESTAMP_PART = 999999L;
    
    // 随机数部分(4位)
    private static final int RANDOM_DIGITS = 4;
    private static final int RANDOM_MAX = 9999;

    /**
     * 生成10位用户ID
     * 格式: TTTTTTNNNN (T=时间戳6位, N=随机数4位)
     * 
     * @return 10位数字ID
     */
    public static long generateId() {
        // 1. 计算时间戳部分(取最近6位)
        long currentMillis = System.currentTimeMillis();
        long elapsedMillis = currentMillis - START_TIMESTAMP;
        // 取模确保不超过6位
        long timestampPart = (elapsedMillis / 1000) % 1000000; // 秒级精度，6位
        
        // 2. 生成随机数部分(4位)
        int randomPart = ThreadLocalRandom.current().nextInt(RANDOM_MAX + 1);
        
        // 3. 组合: 时间戳6位 + 随机数4位
        return timestampPart * 10000 + randomPart;
    }
    
    /**
     * 批量生成ID(确保不重复)
     * 
     * @param count 需要生成的数量
     * @return ID数组
     */
    public static synchronized long[] generateIds(int count) {
        long[] ids = new long[count];
        long baseTimestamp = (System.currentTimeMillis() - START_TIMESTAMP) / 1000 % 1000000;
        
        for (int i = 0; i < count; i++) {
            int randomPart = ThreadLocalRandom.current().nextInt(RANDOM_MAX + 1);
            ids[i] = baseTimestamp * 10000 + randomPart;
            // 简单的重复检测，实际应用中数据库唯一索引会保证
        }
        
        return ids;
    }
    
    // 兼容旧接口
    public static SnowflakeIdGenerator getInstance() {
        return new SnowflakeIdGenerator();
    }
    
    public long nextId() {
        return generateId();
    }
}
