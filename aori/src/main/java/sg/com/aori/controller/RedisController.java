package sg.com.aori.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sg.com.aori.service.RedisService;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisService redisService;

    /**
     * 测试 Redis 连接 - 相当于 C# 示例的 run() 方法
     * GET /redis/test
     */
    @GetMapping("/test")
    public String testRedis() {
        redisService.run();
        return "Redis test completed! Check console for output.";
    }

    /**
     * 设置键值对
     * POST /redis/set?key=foo&value=bar
     */
    @PostMapping("/set")
    public String setValue(@RequestParam String key, @RequestParam String value) {
        redisService.setString(key, value);
        return "Set " + key + " = " + value;
    }

    /**
     * 获取键对应的值
     * GET /redis/get?key=foo
     */
    @GetMapping("/get")
    public String getValue(@RequestParam String key) {
        String value = redisService.getString(key);
        return "Value for " + key + " is: " + value;
    }

    /**
     * 删除键
     * DELETE /redis/delete?key=foo
     */
    @DeleteMapping("/delete")
    public String deleteKey(@RequestParam String key) {
        boolean deleted = redisService.deleteKey(key);
        return deleted ? "Key " + key + " deleted successfully" : "Key " + key + " not found";
    }
}