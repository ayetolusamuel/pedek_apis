package com.pedektech.pedek_apis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan(basePackages = ["com.pedektech.pedek_apis"])
//@EnableJpaRepositories(basePackages = ["com.pedektech.pedek_apis.pedek_catering.repositories", "com.pedektech.pedek_apis.gnosis.repositories"])
class PedekApisApplication

fun main(args: Array<String>) {
	runApplication<PedekApisApplication>(*args)
}
