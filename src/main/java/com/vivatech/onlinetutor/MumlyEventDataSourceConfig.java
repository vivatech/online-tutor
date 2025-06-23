package com.vivatech.onlinetutor;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.vivatech.mumly_event.notification.repository",
        entityManagerFactoryRef = "mumlyEventEntityManager",
        transactionManagerRef = "mumlyEventTransactionManager"
)
public class MumlyEventDataSourceConfig {

    @Bean
    @ConfigurationProperties("mumly.datasource") // custom prefix
    public DataSourceProperties mumlyEventDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource mumlyEventDataSource() {
        return mumlyEventDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean mumlyEventEntityManager(
            EntityManagerFactoryBuilder builder) {

        Map<String, Object> jpaProperties = new HashMap<>();
        // 👇 Naming strategies
        jpaProperties.put("hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        jpaProperties.put("hibernate.implicit_naming_strategy",
                "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");

        return builder
                .dataSource(mumlyEventDataSource())
                .packages("com.vivatech.mumly_event.notification.model")
                .persistenceUnit("mumlyEvent")
                .properties(jpaProperties)
                .build();
    }

    @Bean
    public PlatformTransactionManager mumlyEventTransactionManager(
            @Qualifier("mumlyEventEntityManager") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

}
