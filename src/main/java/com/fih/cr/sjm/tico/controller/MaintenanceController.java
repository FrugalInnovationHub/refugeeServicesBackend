package com.fih.cr.sjm.tico.controller;

import com.fih.cr.sjm.tico.mongodb.structures.UserTypeEnum;
import com.fih.cr.sjm.tico.service.MaintenanceService;
import com.fih.cr.sjm.tico.utilities.RolesUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {
    private final MaintenanceService maintenanceService;
    private final RolesUtil rolesUtil;

    public MaintenanceController(
            final MaintenanceService maintenanceService,
            final RolesUtil rolesUtil
    ) {
        this.maintenanceService = maintenanceService;
        this.rolesUtil = rolesUtil;
    }

    @PostMapping("/s3CloudFrontURL")
    public ResponseEntity upgradeS3CloudFrontURL(
            @RequestBody final String cloudFrontDomain
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        this.maintenanceService.upgradeCloudFrontURL(cloudFrontDomain);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .build();
    }
}
