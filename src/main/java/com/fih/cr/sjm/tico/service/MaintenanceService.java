package com.fih.cr.sjm.tico.service;

import com.fih.cr.sjm.tico.service.maintenance.MoveS3ObjectsService;
import org.springframework.stereotype.Service;

@Service
public class MaintenanceService {
    private final MoveS3ObjectsService moveS3ObjectsService;

    public MaintenanceService(
            final MoveS3ObjectsService moveS3ObjectsService
    ) {
        this.moveS3ObjectsService = moveS3ObjectsService;
    }

    public void upgradeCloudFrontURL(
            final String cloudFrontDomain
    ) {
        this.moveS3ObjectsService.moveS3Objects(cloudFrontDomain);
    }
}
