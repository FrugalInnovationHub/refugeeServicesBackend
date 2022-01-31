package com.fih.cr.sjm.tico.controller;

import com.amazonaws.util.StringUtils;
import com.fih.cr.sjm.tico.exception.TicoException;
import com.fih.cr.sjm.tico.mongodb.documents.Session;
import com.fih.cr.sjm.tico.mongodb.documents.User;
import com.fih.cr.sjm.tico.mongodb.structures.UserTypeEnum;
import com.fih.cr.sjm.tico.requestbody.AddUserRequest;
import com.fih.cr.sjm.tico.requestbody.ApproveUserRequest;
import com.fih.cr.sjm.tico.requestbody.LoginUserRequest;
import com.fih.cr.sjm.tico.requestbody.ResetPasswordRequest;
import com.fih.cr.sjm.tico.service.UserService;
import com.fih.cr.sjm.tico.utilities.RolesUtil;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Validated
@RestController
@RequestMapping("/admin")
public class AdminUserController {
    private final UserService userService;
    private final RolesUtil rolesUtil;

    public AdminUserController(
            final UserService userService,
            final RolesUtil rolesUtil
    ) {
        this.userService = userService;
        this.rolesUtil = rolesUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(
            @RequestBody final LoginUserRequest user
    ) {
        try {
            final Session login = this.userService.login(user.getUserId(), user.getPassword(), UserTypeEnum.ADMIN);

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
            final User newUser = this.userService.logon(addUserRequest.getUserId(), addUserRequest.getName(), addUserRequest.getPassword(), UserTypeEnum.ADMIN);
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

    @PostMapping("/changePassword")
    public ResponseEntity<Object> changePassword(
            @RequestBody final User user
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            this.userService.changePassword(user.getUserId(), user.getPassword(), user.getUserType());
            return ResponseEntity
                    .ok()
                    .build();
        } catch (TicoException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getResponseBody());
        }
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
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getResponseBody());
        }
    }

    @GetMapping("/isValid")
    public ResponseEntity<Void> isTokenValid() {
        final Optional<Boolean> isValid = this.userService.isValid(UserTypeEnum.ADMIN);

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

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getUserList(
            @RequestParam(value = "page", defaultValue = "0") final Integer page,
            @RequestParam(value = "size", defaultValue = "10") final Integer size
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final Page<User> allDocumentation = this.userService.getPagedUserList(page, size);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(allDocumentation);
    }

    @PostMapping("/users")
    public ResponseEntity<Object> addNewUser(
            @RequestBody final AddUserRequest addUserRequest
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            if (UserTypeEnum.CLIENT.equals(addUserRequest.getUserType())) {
                final PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parseAndKeepRawInput(addUserRequest.getUserId(), null);
                if (!PhoneNumberUtil.getInstance().isPossibleNumber(phoneNumber)) {
                    return ResponseEntity.badRequest().build();
                }
            } else {
                if (!EmailValidator.getInstance(true).isValid(addUserRequest.getUserId())) {
                    return ResponseEntity.badRequest().build();
                }
            }
            final User newUser = this.userService.logon(
                    addUserRequest.getUserId(),
                    addUserRequest.getName(),
                    addUserRequest.getPassword(),
                    addUserRequest.getUserType());
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

    @DeleteMapping("/users/{_id}")
    public ResponseEntity<Object> deleteUser(
            @PathVariable("_id") final String id
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            this.userService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (TicoException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getResponseBody());
        }
    }

    @PostMapping("users/approve")
    public ResponseEntity<Object> approveUsers(
            @RequestBody final ApproveUserRequest approveUserRequest
    ) {
        if (!this.rolesUtil.isRole(UserTypeEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            final User newUser = this.userService.approveAddUserRequest(approveUserRequest.getUserId(), approveUserRequest.getUserType());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(newUser);
        } catch (TicoException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getResponseBody());
        }
    }
}
