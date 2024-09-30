package com.pedektech.pedek_catering

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan(basePackages = arrayOf("com.pedektech.pedek_catering"))
@EnableJpaRepositories(basePackages = arrayOf("com.pedektech.pedek_catering.repositories"))
class PedekCateringApplication

fun main(args: Array<String>) {
	runApplication<PedekCateringApplication>(*args)
}
