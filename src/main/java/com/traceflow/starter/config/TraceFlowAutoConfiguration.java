package com.traceflow.starter.config;

import com.traceflow.starter.filter.JtiBaggageFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(TraceFlowProperties.class)
@ConditionalOnProperty(prefix = "traceflow", name = "enabled", matchIfMissing = true)
public class TraceFlowAutoConfiguration {

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public FilterRegistrationBean<JtiBaggageFilter> jtiBaggageFilter(TraceFlowProperties properties) {
        FilterRegistrationBean<JtiBaggageFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JtiBaggageFilter(properties.getJtiHeader()));
        registrationBean.addUrlPatterns("/*");
        // Asegurar que se ejecuta muy temprano en la cadena para que los spans anidados capturen el baggage
        registrationBean.setOrder(-100); 
        return registrationBean;
    }
}
