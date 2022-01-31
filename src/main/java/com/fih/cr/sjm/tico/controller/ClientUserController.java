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
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/client")
public class ClientUserController {
    private final UserService userService;

    public ClientUserController(
            final UserService userService
    ) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(
            @RequestBody final LoginUserRequest user
    ) {
        try {
            final Session login = this.userService.login(user.getUserId(), user.getPassword(), UserTypeEnum.CLIENT);
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
    public ResponseEntity logon(
            @RequestBody final AddUserRequest addUserRequest
    ) {
        try {
            if (StringUtils.isNullOrEmpty(addUserRequest.getPassword())
                    || StringUtils.isNullOrEmpty(addUserRequest.getConfirmPassword())
                    || !addUserRequest.getPassword().equals(addUserRequest.getConfirmPassword())
            ) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
            }

            final PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parseAndKeepRawInput(addUserRequest.getUserId(), null);
            if (!PhoneNumberUtil.getInstance().isPossibleNumber(phoneNumber)) {
                return ResponseEntity.badRequest().build();
            }
            final User newUser = this.userService.logon(
                    addUserRequest.getUserId(),
                    addUserRequest.getName(),
                    addUserRequest.getPassword(),
                    UserTypeEnum.CLIENT,
                    true
            );
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(newUser);
        } catch (NumberParseException e) {
            return ResponseEntity
                    .status(HttpStatus.PRECONDITION_FAILED)
                    .build();
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
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestBody final ResetPasswordRequest resetPasswordRequest
    ) {
        if (StringUtils.isNullOrEmpty(resetPasswordRequest.getNewPassword())
                || StringUtils.isNullOrEmpty(resetPasswordRequest.getConfirmPassword())
                || StringUtils.isNullOrEmpty(resetPasswordRequest.getCurrentPassword())
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
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getResponseBody());
        }
    }

    @GetMapping("/isValid")
    public ResponseEntity<Void> isTokenValid() {
        final Optional<Boolean> isValid = this.userService.isValid(UserTypeEnum.CLIENT);

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
