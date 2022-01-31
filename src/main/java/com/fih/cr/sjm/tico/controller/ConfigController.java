package com.fih.cr.sjm.tico.controller;

import com.fih.cr.sjm.tico.mongodb.documents.Config;
import com.fih.cr.sjm.tico.mongodb.structures.UserTypeEnum;
import com.fih.cr.sjm.tico.service.ConfigService;
import com.fih.cr.sjm.tico.utilities.RolesUtil;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/config")
public class ConfigController {
    private final ConfigService configService;
    private final RolesUtil rolesUtil;

    public ConfigController(
            final ConfigService configService,
            final RolesUtil rolesUtil
    ) {
        this.configService = configService;
        this.rolesUtil = rolesUtil;
    }

    @GetMapping
    public ResponseEntity<List<Config>> getConfigs(
    ) {
        final List<Config> configs = configService.getConfigs();

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(configs);
    }

    @SneakyThrows
    @GetMapping(value = "/{key}")
    public ResponseEntity<Config> getConfig(
            @PathVariable("key") final String key
    ) {
        final Optional<Config> config = configService.getConfig(key);

        return config.map(config1 -> ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(config1)
        ).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @SneakyThrows
    @PostMapping(value = "/{key}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Config> addConfig(
            @PathVariable("key") final String key,
            @RequestBody final Config config
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final Config savedConfig = configService.addConfig(
                Config.builder()
                        .key(key)
                        .value(config.getValue())
                        .build());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedConfig);
    }

    @SneakyThrows
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<Config>> addConfigList(
            @RequestBody final List<Config> configList
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final List<Config> savedConfigs = configService.addConfigList(configList);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(savedConfigs);
    }
}
