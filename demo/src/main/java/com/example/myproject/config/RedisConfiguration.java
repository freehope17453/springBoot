package com.example.myproject.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
@EnableCaching
public class RedisConfiguration extends CachingConfigurerSupport{

	private Logger logger = LoggerFactory.getLogger(RedisConfiguration.class);
	
	@Autowired
	private JedisConnectionFactory jedisConnectionFactory;
	
	public KeyGenerator keyGenerator() {
		
		return (target, method, params) -> {
	          StringBuilder sb = new StringBuilder();
	          sb.append(target.getClass().getName());
	          sb.append(":");
	          sb.append(method.getName());
	          for (Object obj : params) {
	              sb.append(":" + String.valueOf(obj));
	          }
	          String rsToUse = String.valueOf(sb);
	          logger.info("�Զ�����Redis Key -> [{}]", rsToUse);
	          return rsToUse;
	      };
	}
	
	@Bean
	  @Override
	  public CacheManager cacheManager() {
	      // ��ʼ����������������������ǿ��Ի�����������ʱ��ʲô�ģ�������Ĭ��û������
	      logger.info("��ʼ�� -> [{}]", "CacheManager RedisCacheManager Start");
	      RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager
	              .RedisCacheManagerBuilder
	              .fromConnectionFactory(jedisConnectionFactory);
	      return builder.build();
	  }


	  @Bean
	  public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory ) {
	      //�������л�
	      Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
	      ObjectMapper om = new ObjectMapper();
	      om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
	      om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
	      jackson2JsonRedisSerializer.setObjectMapper(om);
	      // ����redisTemplate
	      RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
	      redisTemplate.setConnectionFactory(jedisConnectionFactory);
	      RedisSerializer stringSerializer = new StringRedisSerializer();
	      redisTemplate.setKeySerializer(stringSerializer); // key���л�
	      redisTemplate.setValueSerializer(jackson2JsonRedisSerializer); // value���л�
	      redisTemplate.setHashKeySerializer(stringSerializer); // Hash key���л�
	      redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer); // Hash value���л�
	      redisTemplate.afterPropertiesSet();
	      return redisTemplate;
	  }

	  @Override
	  @Bean
	  public CacheErrorHandler errorHandler() {
	      // �쳣������Redis�����쳣ʱ����ӡ��־�����ǳ���������
	      logger.info("��ʼ�� -> [{}]", "Redis CacheErrorHandler");
	      CacheErrorHandler cacheErrorHandler = new CacheErrorHandler() {
	          @Override
	          public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
	              logger.error("Redis occur handleCacheGetError��key -> [{}]", key, e);
	          }

	          @Override
	          public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
	              logger.error("Redis occur handleCachePutError��key -> [{}]��value -> [{}]", key, value, e);
	          }

	          @Override
	          public void handleCacheEvictError(RuntimeException e, Cache cache, Object key)    {
	              logger.error("Redis occur handleCacheEvictError��key -> [{}]", key, e);
	          }

	          @Override
	          public void handleCacheClearError(RuntimeException e, Cache cache) {
	              logger.error("Redis occur handleCacheClearError��", e);
	          }
	      };
	      return cacheErrorHandler;
	  }

	  /**
	   * ���ڲ�����ǰ�yml���������ݣ����ж�ȡ������JedisConnectionFactory��JedisPool���Թ��ⲿ���ʼ�����������ʹ��
	   * ���˽��ͬѧ����ȥ��@ConfigurationProperties��@Value������
	   *
	   */
	  @ConfigurationProperties
	  class DataJedisProperties{
	      @Value("${spring.redis.host}")
	      private  String host;
	      @Value("${spring.redis.password}")
	      private  String password;
	      @Value("${spring.redis.port}")
	      private  int port;
	      @Value("${spring.redis.timeout}")
	      private  int timeout;
	      @Value("${spring.redis.jedis.pool.max-idle}")
	      private int maxIdle;
	      @Value("${spring.redis.jedis.pool.max-wait}")
	      private long maxWaitMillis;

	      @Bean
	      JedisConnectionFactory jedisConnectionFactory() {
	          logger.info("Create JedisConnectionFactory successful");
	          JedisConnectionFactory factory = new JedisConnectionFactory();
	          factory.setHostName(host);
	          factory.setPort(port);
	          factory.setTimeout(timeout);
	          factory.setPassword(password);
	          return factory;
	      }
	      @Bean
	      public JedisPool redisPoolFactory() {
	          logger.info("JedisPool init successful��host -> [{}]��port -> [{}]", host, port);
	          JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
	          jedisPoolConfig.setMaxIdle(maxIdle);
	          jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);

	          JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
	          return jedisPool;
	      }
	  }
	  
}
