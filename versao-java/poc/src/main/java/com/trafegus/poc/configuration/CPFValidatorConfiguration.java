package com.trafegus.poc.configuration;

import br.com.caelum.stella.validation.CPFValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CPFValidatorConfiguration {

    @Bean
    public CPFValidator cpfValidator() {
        return new CPFValidator();
    }

}
