package com.fih.cr.sjm.tico.configuration;

import com.fih.cr.sjm.tico.service.SessionService;
import com.fih.cr.sjm.tico.utilities.WebSecurityConfigUtil;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfigurationSource;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final SessionService sessionService;
    private final WebSecurityConfigUtil webSecurityConfigUtil;
    private final CorsConfigurationSource corsConfigurationSource;

    public WebSecurityConfig(
            final SessionService sessionService,
            final WebSecurityConfigUtil webSecurityConfigUtil,
            final CorsConfigurationSource corsConfigurationSource
    ) {
        this.sessionService = sessionService;
        this.webSecurityConfigUtil = webSecurityConfigUtil;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Override
    protected void configure(
            final HttpSecurity http
    ) throws Exception {
        webSecurityConfigUtil.defaultHttpSecurity(http, this.sessionService, this.corsConfigurationSource);
    }
}
