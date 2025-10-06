package sg.com.aori.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 相当于 C# 示例中的 run() 方法
     * 设置和获取 Redis 中的字符串值
     */
    public void run() {
        // 相当于 db.StringSet("foo", "bar");
        redisTemplate.opsForValue().set("foo", "bar");

        // 相当于 RedisValue result = db.StringGet("foo");
        String result = redisTemplate.opsForValue().get("foo");

        // 相当于 Console.WriteLine(result); // >>> bar
        System.out.println(result); // >>> bar
    }

    /**
     * 设置键值对
     */
    public void setString(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 获取键对应的值
     */
    public String getString(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 检查键是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 删除键
     */
    public boolean deleteKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }
}