package com.dfsv.servicioCatalogo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Filtro que protege los endpoints /api/products/**
 *
 * - Permite las peticiones OPTIONS (pre‑flight CORS)
 * - Permite cualquier petición cuyo Origin sea http://localhost:4200  (front en dev)
 * - Para el resto, exige cabecera X‑API‑KEY == expectedKey; de lo contrario ⇒ 401
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${internal.api-key}")
    private String expectedKey;

    @Override
    protected void doFilterInternal(HttpServletRequest  req,
                                    HttpServletResponse res,
                                    FilterChain         chain)
            throws ServletException, IOException {

        /* 1. Pre‑flight (OPTIONS) → pasa sin validar */
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(req, res);
            return;
        }

        /* 2. Peticiones desde el front Angular (origin 4200) → permitidas */
        String origin = req.getHeader("Origin");
        if ("http://localhost:4200".equals(origin)) {
            chain.doFilter(req, res);
            return;
        }

        /* 3. Resto: validar API‑Key */
        String key = req.getHeader("X-API-KEY");
        if (expectedKey.equals(key)) {
            chain.doFilter(req, res);            // ✔ clave correcta
        } else {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        }
    }

    /** Aplica solo a la API interna */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        return !req.getServletPath().startsWith("/api/products");
    }
}
