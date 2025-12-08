package com.example.demo.controller;

import com.example.demo.common.Result;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis 常用操作示例接口。
 * 依赖外部二方件提供的 Redis Starter（已在 pom 中引入）。
 */
@RestController
@RequestMapping("/redis-demo")
public class RedisDemoController {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisDemoController(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @GetMapping("/ping")
    public Result<String> ping() {
        stringRedisTemplate.opsForValue().set("demo:ping", "pong", Duration.ofSeconds(30));
        String val = stringRedisTemplate.opsForValue().get("demo:ping");
        return Result.success(val);
    }

    // ---------- String ----------
    @PostMapping("/string")
    public Result<Boolean> setString(@RequestParam String key,
                                     @RequestParam String value,
                                     @RequestParam(required = false) Long ttlSeconds) {
        if (ttlSeconds != null && ttlSeconds > 0) {
            stringRedisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
        } else {
            stringRedisTemplate.opsForValue().set(key, value);
        }
        return Result.success(true);
    }

    @GetMapping("/string")
    public Result<String> getString(@RequestParam String key) {
        return Result.success(stringRedisTemplate.opsForValue().get(key));
    }

    @DeleteMapping("/key")
    public Result<Boolean> deleteKey(@RequestParam String key) {
        Boolean res = stringRedisTemplate.delete(key);
        return Result.success(Boolean.TRUE.equals(res));
    }

    @PostMapping("/incr")
    public Result<Long> incr(@RequestParam String key,
                             @RequestParam(defaultValue = "1") long delta) {
        Long val = stringRedisTemplate.opsForValue().increment(key, delta);
        return Result.success(val);
    }

    // ---------- Hash ----------
    @PostMapping("/hash")
    public Result<Boolean> hashPut(@RequestParam String key,
                                   @RequestParam String field,
                                   @RequestParam String value) {
        stringRedisTemplate.opsForHash().put(key, field, value);
        return Result.success(true);
    }

    @GetMapping("/hash")
    public Result<Map<Object, Object>> hashGetAll(@RequestParam String key) {
        return Result.success(stringRedisTemplate.opsForHash().entries(key));
    }

    // ---------- List ----------
    @PostMapping("/list/push-left")
    public Result<Long> listPushLeft(@RequestParam String key,
                                     @RequestParam String value) {
        Long len = stringRedisTemplate.opsForList().leftPush(key, value);
        return Result.success(len);
    }

    @PostMapping("/list/push-right")
    public Result<Long> listPushRight(@RequestParam String key,
                                      @RequestParam String value) {
        Long len = stringRedisTemplate.opsForList().rightPush(key, value);
        return Result.success(len);
    }

    @GetMapping("/list")
    public Result<List<String>> listRange(@RequestParam String key,
                                          @RequestParam(defaultValue = "0") long start,
                                          @RequestParam(defaultValue = "-1") long end) {
        return Result.success(stringRedisTemplate.opsForList().range(key, start, end));
    }

    // ---------- Set ----------
    @PostMapping("/set")
    public Result<Long> setAdd(@RequestParam String key,
                               @RequestParam List<String> values) {
        Long count = stringRedisTemplate.opsForSet().add(key, values.toArray(new String[0]));
        return Result.success(count);
    }

    @GetMapping("/set")
    public Result<Set<String>> setMembers(@RequestParam String key) {
        return Result.success(stringRedisTemplate.opsForSet().members(key));
    }

    // ---------- ZSet ----------
    @PostMapping("/zset")
    public Result<Boolean> zsetAdd(@RequestParam String key,
                                   @RequestParam String member,
                                   @RequestParam double score) {
        Boolean ok = stringRedisTemplate.opsForZSet().add(key, member, score);
        return Result.success(Boolean.TRUE.equals(ok));
    }

    @GetMapping("/zset")
    public Result<Set<String>> zsetRange(@RequestParam String key,
                                         @RequestParam(defaultValue = "0") long start,
                                         @RequestParam(defaultValue = "-1") long end) {
        return Result.success(stringRedisTemplate.opsForZSet().range(key, start, end));
    }

    // ---------- Key & TTL ----------
    @PostMapping("/expire")
    public Result<Boolean> expire(@RequestParam String key,
                                  @RequestParam long ttlSeconds) {
        Boolean ok = stringRedisTemplate.expire(key, Duration.ofSeconds(ttlSeconds));
        return Result.success(Boolean.TRUE.equals(ok));
    }

    @GetMapping("/ttl")
    public Result<Long> ttl(@RequestParam String key) {
        Long ttl = stringRedisTemplate.getExpire(key);
        return Result.success(ttl);
    }
}

