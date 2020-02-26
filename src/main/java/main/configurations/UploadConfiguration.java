package main.configurations;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.MultipartConfigElement;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class UploadConfiguration
{
    @Bean
    MultipartConfigElement multipartConfigElement(){
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.parse("5MB"));
        factory.setMaxRequestSize(DataSize.parse("5MB"));
        return factory.createMultipartConfig();
    }

    @Bean(name = "commonsMultipartResolver")
    MultipartResolver multipartResolver(){
        return new StandardServletMultipartResolver();
    }

}
