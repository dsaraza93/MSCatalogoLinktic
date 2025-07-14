package com.dfsv.servicioCatalogo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;         // ← añadido
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyFilterTest {

    @InjectMocks
    private ApiKeyFilter filter;          // clase bajo prueba

    @Mock private HttpServletRequest  request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain         chain;

    /*──────────────────────────────────────────────────────────────*/
    @Test
    @DisplayName("Permite OPTIONS (pre-flight CORS) sin validar")
    void preflightOptionsIsAllowed() throws ServletException, IOException {
        when(request.getMethod()).thenReturn("OPTIONS");
        when(request.getServletPath()).thenReturn("/api/products");

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    /*──────────────────────────────────────────────────────────────*/
    @Test
    @DisplayName("Permite peticiones desde Origin http://localhost:4200")
    void requestFromAngularOriginIsAllowed() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Origin")).thenReturn("http://localhost:4200");
        when(request.getServletPath()).thenReturn("/api/products/1");

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    /*──────────────────────────────────────────────────────────────*/
    @Test
    @DisplayName("Permite cuando X-API-KEY coincide con la esperada")
    void acceptsRequestWithCorrectApiKey() throws Exception {
        ReflectionTestUtils.setField(filter, "expectedKey", "secret123");

        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("X-API-KEY")).thenReturn("secret123");
        when(request.getServletPath()).thenReturn("/api/products/list");

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    /*──────────────────────────────────────────────────────────────*/
    @Test
    @DisplayName("Rechaza cuando falta o no coincide la X-API-KEY")
    void rejectsRequestWithWrongOrMissingKey() throws Exception {
        ReflectionTestUtils.setField(filter, "expectedKey", "secret123");

        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("X-API-KEY")).thenReturn("wrongKey");
        when(request.getServletPath()).thenReturn("/api/products");

        filter.doFilterInternal(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(chain, never()).doFilter(any(), any());
    }

    /*──────────────────────────────────────────────────────────────*/
    @Test
    @DisplayName("shouldNotFilter devuelve true fuera de /api/products")
    void shouldNotFilterOutsideProductsPath() {
        when(request.getServletPath()).thenReturn("/api/health");

        boolean result = filter.shouldNotFilter(request);

        assertTrue(result);
    }
}
