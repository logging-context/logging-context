package io.github.logcontext.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(value = "io.github.logcontext.spring.aop")
public class SpringAopLoggingContextConfiguration {}
