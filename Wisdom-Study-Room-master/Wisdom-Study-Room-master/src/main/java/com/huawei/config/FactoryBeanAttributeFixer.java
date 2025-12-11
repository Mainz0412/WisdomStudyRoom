package com.huawei.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FactoryBeanAttributeFixer implements BeanFactoryPostProcessor, BeanDefinitionRegistryPostProcessor, Ordered {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] names = beanFactory.getBeanDefinitionNames();
        ClassLoader cl = beanFactory.getBeanClassLoader();
        for (String name : names) {
            BeanDefinition bd = beanFactory.getBeanDefinition(name);
            Object attr = bd.getAttribute("factoryBeanObjectType");
            if (attr instanceof String) {
                String className = (String) attr;
                try {
                    Class<?> cls = (cl != null) ? cl.loadClass(className) : Class.forName(className);
                    bd.setAttribute("factoryBeanObjectType", cls);
                } catch (ClassNotFoundException ignored) {
                }
            }
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        String[] names = registry.getBeanDefinitionNames();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        for (String name : names) {
            BeanDefinition bd = registry.getBeanDefinition(name);
            Object attr = bd.getAttribute("factoryBeanObjectType");
            if (attr instanceof String) {
                String className = (String) attr;
                try {
                    Class<?> cls = (cl != null) ? cl.loadClass(className) : Class.forName(className);
                    bd.setAttribute("factoryBeanObjectType", cls);
                } catch (ClassNotFoundException ignored) {
                }
            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
