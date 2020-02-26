package main.configurations;

import main.security.FilterToken;
import main.security.ProviderToken;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class ConfigurationFilter extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>
{
    private ProviderToken providerToken;

    public ConfigurationFilter(ProviderToken providerToken) {
        this.providerToken = providerToken;
    }

    public void configure(HttpSecurity httpSecurity) throws Exception{
        FilterToken filterToken = new FilterToken(providerToken);
        httpSecurity.addFilterBefore(filterToken, UsernamePasswordAuthenticationFilter.class);
    }
}

