package com.rzb.pms.log;

/**
 * Annotation processor for @Log
 * This class will help to inject appropriate class at runtime
 * 
 * @author Rajib Rath
 */

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

/**
 * @author Rajib.Rath
 *
 */
@Component
public class CustomLogProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) {
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String beanName) {
		ReflectionUtils.doWithFields(bean.getClass(), new FieldCallback() {
			public void doWith(Field field) throws IllegalAccessException {
				if (field.getAnnotation(Log.class) != null) {
					Logger log = LoggerFactory.getLogger(bean.getClass());
					field.setAccessible(true);
					field.set(bean, log);
					field.setAccessible(false);
				}
			}
		});
		return bean;
	}

}
