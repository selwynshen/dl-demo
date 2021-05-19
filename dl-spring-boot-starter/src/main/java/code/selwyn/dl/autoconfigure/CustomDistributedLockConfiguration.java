/**
 * Hrfax Inc.
 * Copyright (c) 2020-2066 All Rights Reserved.
 */
package code.selwyn.dl.autoconfigure;

import com.github.alturkovic.lock.advice.LockBeanPostProcessor;
import com.github.alturkovic.lock.advice.LockTypeResolver;
import com.github.alturkovic.lock.interval.BeanFactoryAwareIntervalConverter;
import com.github.alturkovic.lock.interval.IntervalConverter;
import com.github.alturkovic.lock.key.KeyGenerator;
import com.github.alturkovic.lock.key.SpelKeyGenerator;
import com.github.alturkovic.lock.retry.DefaultRetriableLockFactory;
import com.github.alturkovic.lock.retry.DefaultRetryTemplateConverter;
import com.github.alturkovic.lock.retry.RetriableLockFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author Selwyn
 * @since 2021/5/17
 */
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class CustomDistributedLockConfiguration {

    @Bean("dlConversionService")
    //@ConditionalOnMissingBean
    public ConversionService conversionService() {
        return new DefaultConversionService();
    }

    @Bean
    @ConditionalOnMissingBean
    public LockBeanPostProcessor lockBeanPostProcessor(final KeyGenerator keyGenerator,
                                                       final ConfigurableBeanFactory configurableBeanFactory,
                                                       final IntervalConverter intervalConverter,
                                                       final RetriableLockFactory retriableLockFactory,
                                                       @Autowired(required = false) final TaskScheduler taskScheduler) {
        final LockBeanPostProcessor processor = new LockBeanPostProcessor(keyGenerator, configurableBeanFactory::getBean, intervalConverter, retriableLockFactory, taskScheduler);
        processor.setBeforeExistingAdvisors(true);
        return processor;
    }

    @Bean
    @ConditionalOnMissingBean
    public IntervalConverter intervalConverter(final ConfigurableBeanFactory configurableBeanFactory) {
        return new BeanFactoryAwareIntervalConverter(configurableBeanFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public RetriableLockFactory retriableLockFactory(final IntervalConverter intervalConverter) {
        return new DefaultRetriableLockFactory(new DefaultRetryTemplateConverter(intervalConverter));
    }

    @Bean
    @ConditionalOnMissingBean
    public KeyGenerator spelKeyGenerator(@Qualifier("dlConversionService") final ConversionService conversionService) {
        return new SpelKeyGenerator(conversionService);
    }

    @Bean
    @ConditionalOnMissingBean
    public LockTypeResolver lockTypeResolver(final ConfigurableBeanFactory configurableBeanFactory) {
        return configurableBeanFactory::getBean;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "com.github.alturkovic.lock.task-scheduler.default", name = "enabled", havingValue = "true", matchIfMissing = true)
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
