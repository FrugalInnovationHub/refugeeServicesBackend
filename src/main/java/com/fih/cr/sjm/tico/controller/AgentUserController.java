package com.fih.cr.sjm.tico.controller;

import com.amazonaws.util.StringUtils;
import com.fih.cr.sjm.tico.exception.TicoException;
import com.fih.cr.sjm.tico.mongodb.documents.Session;
import com.fih.cr.sjm.tico.mongodb.documents.User;
import com.fih.cr.sjm.tico.mongodb.structures.UserTypeEnum;
import com.fih.cr.sjm.tico.requestbody.AddUserRequest;
import com.fih.cr.sjm.tico.requestbody.LoginUserRequest;
import com.fih.cr.sjm.tico.requestbody.ResetPasswordRequest;
import com.fih.cr.sjm.tico.service.UserService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Validated
@RestController
@RequestMapping("/agent")
public class AgentUserController {
    private final UserService userService;

    public AgentUserController(
            final UserService userService
    ) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(
            @RequestBody final LoginUserRequest user
    ) {
        try {
            final Session login = this.userService.login(user.getUserId(), user.getPassword(), UserTypeEnum.AGENT);

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(login);
        } catch (TicoException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getResponseBody());
        }

    }

    @PostMapping("/logon")
    public ResponseEntity<Object> logon(
            @RequestBody final AddUserRequest addUserRequest
    ) {
        if (StringUtils.isNullOrEmpty(addUserRequest.getPassword())
                || StringUtils.isNullOrEmpty(addUserRequest.getConfirmPassword())
                || !addUserRequest.getPassword().equals(addUserRequest.getConfirmPassword())
        ) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }
        if (!EmailValidator.getInstance(true).isValid(addUserRequest.getUserId())) {
            return ResponseEntity.badRequest().build();
        }
        try {
            final User newUser = this.userService.logon(addUserRequest.getUserId(), addUserRequest.getName(), addUserRequest.getPassword(), UserTypeEnum.AGENT);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(newUser);
        } catch (TicoException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getResponseBody());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        final Optional<Boolean> isLogoutSuccess = this.userService.logout();

        return isLogoutSuccess
                .<ResponseEntity<Void>>map(aBoolean -> ResponseEntity
                        .noContent()
                        .build()
                )
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .build()
                );
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Object> resetPassword(
            @RequestBody final ResetPasswordRequest resetPasswordRequest
    ) {
        if (!StringUtils.isNullOrEmpty(resetPasswordRequest.getNewPassword())
                || !StringUtils.isNullOrEmpty(resetPasswordRequest.getConfirmPassword())
                || !StringUtils.isNullOrEmpty(resetPasswordRequest.getCurrentPassword())
                || !resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmPassword())
        ) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }

        try {
            this.userService.resetPassword(resetPasswordRequest);
            return ResponseEntity
                    .ok()
                    .build();
        } catch (TicoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getResponseBody());
        }
    }

    @GetMapping("/isValid")
    public ResponseEntity<Void> isTokenValid() {
        final Optional<Boolean> isValid = this.userService.isValid(UserTypeEnum.AGENT);

        return isValid
                .<ResponseEntity<Void>>map(aBoolean -> ResponseEntity
                        .ok()
                        .build()
                )
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .build()
                );
    }
}
