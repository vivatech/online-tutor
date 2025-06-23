package com.vivatech.onlinetutor;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = {"com.vivatech.onlinetutor.repository",
                "com.vivatech.onlinetutor.webchat.repository"},
        entityManagerFactoryRef = "onlineTutorEntityManagerFactory",
        transactionManagerRef = "onlineTutorTransactionManager"
)
public class OnlineTutorDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource") // default source
    public DataSourceProperties onlineTutorDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource onlineTutorDataSource() {
        return onlineTutorDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean onlineTutorEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        // 👇 Naming strategies
        jpaProperties.put("hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        jpaProperties.put("hibernate.implicit_naming_strategy",
                "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
        return builder
                .dataSource(onlineTutorDataSource())
                .packages("com.vivatech.onlinetutor.model", "com.vivatech.onlinetutor.webchat.model")
                .persistenceUnit("onlineTutor")
                .properties(jpaProperties)
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager onlineTutorTransactionManager(
            @Qualifier("onlineTutorEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

}
