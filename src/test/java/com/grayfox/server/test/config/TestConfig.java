package com.grayfox.server.test.config;

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.foursquare4j.FoursquareApi;
import com.grayfox.server.config.MainConfig;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:test-config.properties")
@Import({MainConfig.DataConfig.class, TestConfig.BeanConfig.class})
public class TestConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @EnableAsync
    @Configuration
    @EnableTransactionManagement
    @ComponentScan(basePackages = { 
            "com.grayfox.server.test.dao.*",
            "com.grayfox.server.dao.jdbc",
            "com.grayfox.server.service"})
    public static class BeanConfig {

        private MockWebServer mockWebServer;

        @PostConstruct
        private void init() throws Exception {
            mockWebServer = new MockWebServer();
            mockWebServer.start();
        }

        @Bean
        public FoursquareApi foursquareApi() {
            FoursquareApi foursquareApi = mock(FoursquareApi.class, CALLS_REAL_METHODS);
            String mockUrl = mockWebServer.getUrl("/").toString();
            when(foursquareApi.authUrl()).thenReturn(mockUrl);
            when(foursquareApi.apiUrl()).thenReturn(mockUrl);
            return foursquareApi;
        }

        @Bean
        public PropertiesFactoryBean queries() {
            PropertiesFactoryBean bean = new PropertiesFactoryBean();
            bean.setLocation(new ClassPathResource("/queries.xml"));
            return bean;
        }

        @Bean
        public MockWebServer mockWebServer() {
            return mockWebServer;
        }

        @PreDestroy
        private void destory() throws Exception {
            mockWebServer.shutdown();
        }
    }
}