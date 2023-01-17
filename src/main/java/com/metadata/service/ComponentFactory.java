package com.metadata.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ComponentFactory<T> implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ComponentFactory.applicationContext = applicationContext;
    }

    public T getBean(String beanName){
        return (T) applicationContext.getBean(beanName);
    }
}
