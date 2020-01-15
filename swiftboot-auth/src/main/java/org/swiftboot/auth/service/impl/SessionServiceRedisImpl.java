package org.swiftboot.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swiftboot.auth.SwiftbootAuthConfigBean;
import org.swiftboot.auth.service.Session;
import org.swiftboot.auth.service.SessionService;
import org.swiftboot.service.service.RedisService;
import org.swiftboot.web.exception.ErrMessageException;
import org.swiftboot.web.exception.ErrorCodeSupport;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 用 Redis 实现会话（Session）控制，客户端采用 Jedis
 * 当部署集群时或多服务器时启用，非集群时使用 {@code SessionServiceImpl}
 *
 * @author swiftech
 */
public class SessionServiceRedisImpl implements SessionService {

    private Logger log = LoggerFactory.getLogger(SessionServiceRedisImpl.class);

    private ObjectMapper mapper = new ObjectMapper();

    @Resource
    private RedisService redisService;

    @Resource
    private SwiftbootAuthConfigBean config;

    @PostConstruct
    public void init() {
    }

    @PreDestroy
    public void destroy() {
        try (Jedis jedis = redisService.getJedis()) {
            if (jedis != null && jedis.isConnected()) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public void addSession(String token, Session session) throws RuntimeException {
        if (isBlank(token) || session == null) {
            throw new RuntimeException("会话参数无效");
        }
        try {
            // Session 中的超时时间覆盖配置中的超时时间
            if (session.getExpireTime() == null) {
                if (config.getSession().getExpiresIn() > 0) {
                    session.setExpireTime(System.currentTimeMillis() + (config.getSession().getExpiresIn() * 1000));
                }
            }
            else {
                if (session.getExpireTime() <= 0) {
                    session.setExpireTime(null);
                }
            }
            byte[] bytes = mapper.writeValueAsBytes(session);
            try (Jedis jedis = redisService.getJedis()) {
                if (!isBlank(session.getGroup())) {
                    jedis.hset(session.getGroup(), token, new String(bytes));// group -> token ->  会话
                }
                else {
                    if (StringUtils.isNotBlank(config.getSession().getGroup())) {
                        jedis.hset(config.getSession().getGroup(), token, new String(bytes));// group -> token ->  会话
                    }
                    else {
                        jedis.set(token.getBytes(), bytes); // token ->  会话
                    }
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("Save session of %s failed", session.getUserId()));
        }
    }

    @Override
    public Session getSession(String token) {
        return getSession(config.getSession().getGroup(), token);
    }

    @Override
    public Session getSession(String group, String token) {
        if (isBlank(token)) {
            return null;
        }

        if (StringUtils.isBlank(group)) {
            group = config.getSession().getGroup();
        }
        log.debug(String.format("token: %s", token));

        byte[] bytes;
        try (Jedis jedis = redisService.getJedis()) {
            String jsonSession;
            if (StringUtils.isNotBlank(group)) {
                jsonSession = jedis.hget(group, token);
                if (isBlank(jsonSession)) {
                    return null;
                }
                else {
                    bytes = jsonSession.getBytes();
                }
            }
            else {
                bytes = jedis.get(token.getBytes());
                if (bytes == null || bytes.length == 0) {
                    return null;
                }
            }
            log.debug(new String(bytes));
            try {
                return mapper.readValue(bytes, Session.class);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Session is invalid");
            }
        } catch (Exception e) {
            log.error("Get session error, token=" + token, e);
            return null;
        }
    }


    @Override
    public void removeSession(String token) {
        this.removeSession(config.getSession().getGroup(), token);
    }

    @Override
    public void removeSession(String group, String token) {
        Long result;
        try (Jedis jedis = redisService.getJedis()) {
            if (StringUtils.isNotBlank(group)) {
                result = jedis.hdel(group, token);
            }
            else {
                if (StringUtils.isNotBlank(config.getSession().getGroup())) {
                    result = jedis.hdel(config.getSession().getGroup(), token);
                }
                else {
                    result = jedis.del(token);
                }
            }
        }
        if (result <= 0) {
            throw new RuntimeException("Remove session failed, token: " + token);
        }
    }

    @Override
    public Session verifySession(String token) {
        return this.verifySession(config.getSession().getGroup(), token);
    }

    @Override
    public Session verifySession(String group, String token) {
        Session session = null;
        try {
            session = this.getSession(group, token);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn(String.format("Retrieve session failed: %s", token));
            throw new ErrMessageException(ErrorCodeSupport.CODE_SYS_ERR);
        }

        if (session == null) {
            throw new ErrMessageException(ErrorCodeSupport.CODE_USER_SESSION_NOT_EXIST);
        }
        else if (session.getExpireTime() < System.currentTimeMillis()) {
            this.removeSession(group, token);
            throw new ErrMessageException(ErrorCodeSupport.CODE_SESSION_TIMEOUT);
        }
        else {
            return session;
        }
    }
}
