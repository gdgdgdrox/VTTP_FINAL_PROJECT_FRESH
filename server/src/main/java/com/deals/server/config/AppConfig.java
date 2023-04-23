package com.deals.server.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.deals.server.model.Deal;

@Configuration
public class AppConfig {
    
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Value("${spaces.access.key}")
    private String accessKey;
    @Value("${spaces.secret.key}")
    private String secretKey;
    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private Integer redisPort;
    @Value("${spring.redis.password}")
    private String redisPassword;

    @Bean
    public AmazonS3 getS3Client(){
        BasicAWSCredentials cred = new BasicAWSCredentials(accessKey, secretKey);

        EndpointConfiguration epConfig = new EndpointConfiguration("sgp1.digitaloceanspaces.com", "sgp1");

        return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(cred))
                                                .withEndpointConfiguration(epConfig)
                                                .build();
    }

    @Bean
    public RedisTemplate<String, Object> initRedisTemplate() {
        // Configure the Redis database
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        redisConfig.setPassword(redisPassword);

        // Create an instance of the Jedis driver
        JedisClientConfiguration jedisConfig = JedisClientConfiguration.builder().build();

        // Create a factory for jedis connection
        JedisConnectionFactory jedisFac = new JedisConnectionFactory(redisConfig, jedisConfig);
        jedisFac.afterPropertiesSet();

        // Create RedisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisFac);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        RedisSerializer<Object> serializer = new JdkSerializationRedisSerializer(getClass().getClassLoader());
        // Jackson2JsonRedisSerializer<Deal> jackson2JsonJsonSerializer = 
        //                     new Jackson2JsonRedisSerializer<Deal>(Deal.class);
        redisTemplate.setValueSerializer(serializer);
        return redisTemplate;
    }
}
