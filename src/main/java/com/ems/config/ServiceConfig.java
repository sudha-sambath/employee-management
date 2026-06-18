package com.ems.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix="services")
public class ServiceConfig {

    private String payrollUrl;

    public String getPayrollUrl() {
        return payrollUrl;
    }

    public void setPayrollUrl(String payrollUrl) {
        this.payrollUrl = payrollUrl;
    }
}
