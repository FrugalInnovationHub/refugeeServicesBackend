package com.fih.cr.sjm.tico.utilities;

import com.fih.cr.sjm.tico.filter.ThreadLocalPopulateFilter;
import com.fih.cr.sjm.tico.service.SessionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class WebSecurityConfigUtil {
    public void defaultHttpSecurity(
            final HttpSecurity httpSecurity,
            final SessionService sessionService,
            final CorsConfigurationSource corsConfigurationSource
    ) throws Exception {
        httpSecurity
                .cors().configurationSource(corsConfigurationSource).and()
                .csrf().disable()
                .addFilterAfter(new ThreadLocalPopulateFilter(sessionService), BasicAuthenticationFilter.class);
    }
}
