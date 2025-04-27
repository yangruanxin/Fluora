package org.whu.fleetingtime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //设置连接超时时间10s
        factory.setConnectTimeout(10000);
        //设置读取时间10s
        factory.setReadTimeout(10000);
        return new RestTemplate(factory);
    }
}
