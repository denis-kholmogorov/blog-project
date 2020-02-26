package main.configurations;

import main.security.ProviderToken;
import org.apache.catalina.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableConfigurationProperties
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{

    private final ProviderToken providerToken;

    @Autowired
    public WebSecurityConfig(ProviderToken providerToken) {
        this.providerToken = providerToken;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }


    @Autowired
    PasswordEncoder passwordEncoder;

    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().and()
                .antMatcher("/api/calendar/").authorizeRequests().anyRequest().authenticated()
                .and()
                .antMatcher("/api/**").authorizeRequests().anyRequest().permitAll()
                .and()
                .apply(new ConfigurationFilter(providerToken));
    }
}
