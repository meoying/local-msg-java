package com.meoying.localmessage;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

@Configuration
@AutoConfigureAfter({LocalMessageStarterAutoConfiguration.class})
@EntityScan({"com.meoying.localmessage.repository.entity"})
@EnableJpaRepositories({"com.meoying.localmessage.repository.impl.jpa"})
public class LocalMessageJpaAutoConfiguration {

}
