package main.security;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class FilterToken extends GenericFilterBean {


    private ProviderToken providerToken;

    public FilterToken(ProviderToken providerToken){
        this.providerToken = providerToken;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = providerToken.resolveToken((HttpServletRequest) servletRequest);
        if (token!=null && providerToken.validateToken(token)){
            Authentication authentication = providerToken.getAuthentification(token);
            if(authentication != null){
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
