// mi_libreria.py
// Copyright (c) 2024 [Tu Nombre]
// Licencia: CC BY-NC-ND 4.0 - https://creativecommons.org/licenses/by-nc-nd/4.0/

package com.traceflow.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "traceflow")
public class TraceFlowProperties {
    private boolean enabled = true;
    private String jtiHeader = "Authorization";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getJtiHeader() {
        return jtiHeader;
    }

    public void setJtiHeader(String jtiHeader) {
        this.jtiHeader = jtiHeader;
    }
}
