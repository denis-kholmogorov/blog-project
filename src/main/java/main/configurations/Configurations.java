package main.configurations;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

public class Configurations
{
    @Configuration
    public class AppConfig {

        @Bean
        public ModelMapper modelMapper() {
            return new ModelMapper();
        }
    }
}
