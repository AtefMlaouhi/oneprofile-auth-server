package com.newlight77.oauth2.server.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

public class JwtAuthenticationTokenFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;


        // 1. get the authentication header. Tokens are supposed to be passed in the authentication header
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 2. validate the header and check the prefix
        if(header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);  		// If not valid, go to the next filter.
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // 3. Get the token
        Long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .setSubject(auth.getName())
                // Convert to list of strings.
                // This is important because it affects the way we get them back in the Gateway.
                .claim("authorities", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 3600 * 1000))  // in milliseconds
                .signWith(SignatureAlgorithm.HS512, "adminsecret")
                .compact();

        // Add token to header
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        // go to the next filter in the filter chain
        chain.doFilter(request, response);
//        SecurityContextHolder.getContext().setAuthentication(null); // Clean authentication after process

    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
    }
}
