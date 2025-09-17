//package com.pedektech.pedek_apis.config
//
//import jakarta.persistence.EntityManagerFactory
//import org.springframework.beans.factory.annotation.Qualifier
//import org.springframework.boot.context.properties.ConfigurationProperties
//import org.springframework.boot.jdbc.DataSourceBuilder
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.context.annotation.Primary
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories
//import org.springframework.orm.jpa.JpaTransactionManager
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
//import org.springframework.transaction.PlatformTransactionManager
//import org.springframework.transaction.annotation.EnableTransactionManagement
//import javax.sql.DataSource
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
