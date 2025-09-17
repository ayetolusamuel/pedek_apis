package com.pedektech.pedek_apis.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@Configuration
class DataSourceConfig {

    @Primary
    @Bean(name = ["cateringDataSource"])
    @ConfigurationProperties(prefix = "spring.datasource.catering")
    fun cateringDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean(name = ["gnosisDataSource"])
    @ConfigurationProperties(prefix = "spring.datasource.gnosis")
    fun gnosisDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }
//
//    @Bean(name = ["thirdDataSource"])
//    @ConfigurationProperties(prefix = "spring.datasource.third")
//    fun thirdDataSource(): DataSource {
//        return DataSourceBuilder.create().build()
//    }
}