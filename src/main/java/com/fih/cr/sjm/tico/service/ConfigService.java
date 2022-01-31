package com.fih.cr.sjm.tico.service;

import com.fih.cr.sjm.tico.mongodb.documents.Config;
import com.fih.cr.sjm.tico.mongodb.repository.ConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConfigService {
    private final ConfigRepository configRepository;

    public ConfigService(
            final ConfigRepository configRepository
    ) {
        this.configRepository = configRepository;
    }

    public Config addConfig(
            final Config config
    ) {
        return this.configRepository.save(config);
    }

    public List<Config> addConfigList(
            final List<Config> configList
    ) {
        return configList.parallelStream().map(this::addConfig).collect(Collectors.toList());
    }

    public Optional<Config> getConfig(
            final String key
    ) {
        return this.configRepository.findById(key);
    }

    public List<Config> getConfigs(
    ) {
        return this.configRepository.findAll();
    }
}
