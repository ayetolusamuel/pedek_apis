package com.pedektech.pedek_apis.config

import org.springframework.beans.factory.annotation.Qualifier
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
    basePackages = ["com.pedektech.pedek_apis.gnosis.repositories"],
    entityManagerFactoryRef = "gnosisEntityManagerFactory",
    transactionManagerRef = "gnosisTransactionManager"
)
class GnosisDbConfig {

    @Primary
    @Bean(name = ["gnosisEntityManagerFactory"])
    fun gnosisEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
        @Qualifier("gnosisDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        return builder
            .dataSource(dataSource)
            .packages("com.pedektech.pedek_apis.gnosis.models")
            .persistenceUnit("gnosis")
            .properties(jpaProperties())
            .build()
    }

    @Primary
    @Bean(name = ["gnosisTransactionManager"])
    fun gnosisTransactionManager(
        @Qualifier("gnosisEntityManagerFactory") entityManagerFactory: LocalContainerEntityManagerFactoryBean
    ): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory.`object`!!)
    }

    private fun jpaProperties(): Map<String, Any> {
        return mapOf(
            "hibernate.hbm2ddl.auto" to "update",
            "hibernate.dialect" to "org.hibernate.dialect.MySQLDialect",
            "hibernate.show_sql" to "true",
            "hibernate.default_schema" to "gnosis_schema"
        )
    }
}

//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//    basePackages = ["com.pedektech.pedek_apis.gnosis.repositories"],
//    entityManagerFactoryRef = "gnosisEntityManagerFactory",
//    transactionManagerRef = "gnosisTransactionManager"
//)
//class GnosisDbConfig {
//
//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource.gnosis")
//    fun gnosisDataSource(): DataSource =
//        DataSourceBuilder.create().build()
//
//    @Bean(name = ["gnosisEntityManagerFactory"])
//    fun gnosisEntityManagerFactory(
//        builder: EntityManagerFactoryBuilder,
//        @Qualifier("gnosisDataSource") dataSource: DataSource
//    ): LocalContainerEntityManagerFactoryBean =
//        builder
//            .dataSource(dataSource)
//            .packages("com.pedektech.pedek_apis.gnosis.models.Emotion")
//            .persistenceUnit("gnosisPU")
//            .build()
//
//    @Bean(name = ["gnosisTransactionManager"])
//    fun gnosisTransactionManager(
//        @Qualifier("gnosisEntityManagerFactory") emf: EntityManagerFactory
//    ): PlatformTransactionManager =
//        JpaTransactionManager(emf)
//}
