package com.pedektech.pedek_apis.config

import jakarta.persistence.EntityManagerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource



@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = ["com.pedektech.pedek_apis.pedek_catering.repositories"],
    entityManagerFactoryRef = "cateringEntityManagerFactory",
    transactionManagerRef = "cateringTransactionManager"
)
class CateringDbConfig {

    @Bean(name = ["cateringEntityManagerFactory"])
    fun cateringEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
        @Qualifier("cateringDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        return builder
            .dataSource(dataSource)
            .packages("com.pedektech.pedek_apis.pedek_catering.models")
            .persistenceUnit("catering")
            .properties(jpaProperties())
            .build()
    }

    @Bean(name = ["cateringTransactionManager"])
    fun cateringTransactionManager(
        @Qualifier("cateringEntityManagerFactory") entityManagerFactory: LocalContainerEntityManagerFactoryBean
    ): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory.`object`!!)
    }

    private fun jpaProperties(): Map<String, Any> {
        return mapOf(
            "hibernate.hbm2ddl.auto" to "update",
            "hibernate.dialect" to "org.hibernate.dialect.MySQLDialect",
            "hibernate.show_sql" to "true",
            "hibernate.default_schema" to "catering_schema"
        )
    }
}
//
//
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//    basePackages = ["com.pedektech.pedek_apis.pedek_catering.repositories"],
//    entityManagerFactoryRef = "entityManagerFactory",
//    transactionManagerRef = "transactionManager"
//)
//class CateringDbConfig {
//
//    @Primary
//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource.catering")
//    fun cateringDataSource(): DataSource =
//        DataSourceBuilder.create().build()
//
//    @Primary
//    @Bean(name = ["entityManagerFactory"])
//    fun entityManagerFactory(
//        builder: EntityManagerFactoryBuilder
//    ): LocalContainerEntityManagerFactoryBean =
//        builder
//            .dataSource(cateringDataSource())
//            .packages("com.pedektech.pedek_apis.pedek_catering.models")
//            .persistenceUnit("cateringPU")
//            .build()
//
//    @Primary
//    @Bean(name = ["transactionManager"])
//    fun transactionManager(
//        @Qualifier("entityManagerFactory") emf: EntityManagerFactory
//    ): PlatformTransactionManager =
//        JpaTransactionManager(emf)
//}
