// mi_libreria.py
// Copyright (c) 2024 [Tu Nombre]
// Licencia: CC BY-NC-ND 4.0 - https://creativecommons.org/licenses/by-nc-nd/4.0/

package com.traceflow.starter.filter;

import com.nimbusds.jwt.SignedJWT;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JtiBaggageFilter extends OncePerRequestFilter {

    private final String headerName;

    public JtiBaggageFilter(String headerName) {
        this.headerName = headerName;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(headerName);
        String jti = extractJti(authHeader);

        if (jti != null && !jti.isEmpty()) {
            // Es el punto de entrada (Gateway o primer servicio)
            Baggage baggage = Baggage.current().toBuilder()
                    .put("jti", jti)
                    .build();

            Span.current().setAttribute("jti", jti);

            try (Scope scope = Context.current().with(baggage).makeCurrent()) {
                filterChain.doFilter(request, response);
            }
        } else {
            // Podría ser un servicio downstream, intentamos leer el Baggage propagado por OTEL (W3C Baggage)
            String propagatedJti = Baggage.current().getEntryValue("jti");
            if (propagatedJti != null && !propagatedJti.isEmpty()) {
                Span.current().setAttribute("jti", propagatedJti);
            }
            filterChain.doFilter(request, response);
        }
    }

    private String extractJti(String header) {
        if (header == null || header.isEmpty()) return null;
        try {
            String token = header;
            if (header.toLowerCase().startsWith("bearer ")) {
                token = header.substring(7);
            }
            // Fallback for simple mock tokens in POC
            if (!token.contains(".")) {
                return token;
            }
            com.nimbusds.jwt.SignedJWT jwt = com.nimbusds.jwt.SignedJWT.parse(token);
            return jwt.getJWTClaimsSet().getJWTID();
        } catch (Exception e) {
            return header; // Fallback
        }
    }
}
