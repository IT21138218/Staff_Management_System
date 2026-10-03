package com.sliit.sms.config;

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers the Hibernate5 Jackson module with FORCE_LAZY_LOADING enabled, so
 * a still-uninitialized lazy association (e.g. Employee.department) is loaded
 * and serialized with its real data rather than coming out as null. This is
 * safe because spring.jpa.open-in-view=true keeps the Hibernate session open
 * through response serialization, so the extra query lands inside a live
 * session instead of throwing LazyInitializationException.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Hibernate5JakartaModule hibernate5JakartaModule() {
        Hibernate5JakartaModule module = new Hibernate5JakartaModule();
        module.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING, true);
        return module;
    }
}
