package com.meoying.localmessage;

import com.meoying.localmessage.configuration.LocalMessageProperties;
import com.meoying.localmessage.service.HelloWorldService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(LocalMessageProperties.class)
//@ConditionalOnBean(DataSource.class)
//@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@ConditionalOnProperty(
        prefix = "com.meoying.localmessage",
        name = "enable", havingValue = "true", matchIfMissing = true
)
public class LocalMessageStarterAutoConfiguration {

    @Bean
    public HelloWorldService helloWorldService() {
        return new HelloWorldService();
    }
}
