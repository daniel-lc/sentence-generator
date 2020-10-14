package com.gooddata.app.core.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InjectionPoint
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import javax.validation.Validator

@Configuration
class Configuration {

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    fun logger(ip: InjectionPoint): Logger = LoggerFactory.getLogger(ip.member.declaringClass)

    @Bean
    fun validator(): Validator = LocalValidatorFactoryBean()

    @Bean
    fun forbiddenWords(): List<String> = ResourceReader.resourceToList("forbidden-words.txt")

    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper().registerModule(KotlinModule()).registerModule(Jdk8Module())
}
