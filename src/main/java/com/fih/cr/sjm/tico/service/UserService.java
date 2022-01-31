package com.fih.cr.sjm.tico.service;

import com.fih.cr.sjm.tico.exception.TicoException;
import com.fih.cr.sjm.tico.mongodb.documents.Session;
import com.fih.cr.sjm.tico.mongodb.documents.User;
import com.fih.cr.sjm.tico.mongodb.documents.User.UserBuilder;
import com.fih.cr.sjm.tico.mongodb.repository.UserRepository;
import com.fih.cr.sjm.tico.mongodb.structures.UserTypeEnum;
import com.fih.cr.sjm.tico.requestbody.ResetPasswordRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            final UserRepository userRepository,
            final SessionService sessionService,
            final PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.sessionService = sessionService;
        this.passwordEncoder = passwordEncoder;
    }

    public Session login(
            final String userId,
            final String password,
            final UserTypeEnum userType
    ) throws TicoException {
        final Optional<User> userForPasswordCheck = this.userRepository.findUser(userId, userType);

        final User user = userForPasswordCheck.orElseThrow(() -> new TicoException("Failed to Authenticate"));

        if (user.isActive() && this.passwordEncoder.matches(password, user.getPassword())) {
            return this.sessionService.newSession(
                    Session.builder()
                            .userId(user.getUserId())
                            .id(user.getId())
                            .userType(user.getUserType())
                            .build()
            );
        } else {
            throw new TicoException("Failed to Authenticate");
        }
    }

    public User logon(
            final String userId,
            final String userName,
            final String password,
            final UserTypeEnum userType,
            final boolean isActive
    ) throws TicoException {
        if (this.userRepository.userExists(userId, userType)) {
            throw new TicoException("User already exists");
        }
        final UserBuilder userBuilder = User.builder();

        userBuilder.userId(userId);
        userBuilder.userType(userType);
        userBuilder.isActive(isActive);
        userBuilder.password(passwordEncoder.encode(password));
        userBuilder.name(userName);

        final User addedUser = this.userRepository.save(userBuilder.build());
        addedUser.setPassword(null);

        return addedUser;
    }

    public User logon(
            final String userId,
            final String userName,
            final String password,
            final UserTypeEnum userType
    ) throws TicoException {
        return this.logon(userId, userName, password, userType, false);
    }

    public User approveAddUserRequest(
            final String userId,
            final UserTypeEnum userType
    ) throws TicoException {
        final Optional<User> foundUser = this.userRepository.findUser(userId, userType);

        final User user = foundUser.orElseThrow(() -> new TicoException("User does not exist"));

        if (user.isActive()) {
            throw new TicoException("User already active");
        }
        user.setActive(true);

        final User addedUser = this.userRepository.save(user);
        addedUser.setPassword(null);

        return addedUser;
    }

    public Optional<Boolean> logout() {
        return this.sessionService.logSessionOut();
    }

    public Optional<Boolean> isValid(
            final UserTypeEnum userType
    ) {
        return this.sessionService.isSessionValid(userType);
    }

    public void resetPassword(
            final ResetPasswordRequest resetPasswordRequest
    ) throws TicoException {
        final Optional<User> userForPasswordCheck = this.userRepository.findUser(resetPasswordRequest.getUserId(), resetPasswordRequest.getUserType());

        final User user = userForPasswordCheck.orElseThrow(() -> new TicoException("Failed to Authenticate"));

        if (this.passwordEncoder.matches(resetPasswordRequest.getCurrentPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
            this.userRepository.insert(user);
        } else {
            throw new TicoException("Failed to Authenticate");
        }
    }

    public void changePassword(
            final String userId,
            final String password,
            final UserTypeEnum userType
    ) throws TicoException {
        final Optional<User> userForPasswordChange = this.userRepository.findUser(userId, userType);

        final User foundUser = userForPasswordChange.orElseThrow(() -> new TicoException("User not found!"));

        foundUser.setPassword(passwordEncoder.encode(password));
        this.userRepository.insert(foundUser);
    }

    public Page<User> getPagedUserList(
            final Integer page,
            final Integer size
    ) {
        final Pageable pageable = PageRequest.of(page, size);
        final Page<User> allUsers = this.userRepository.findAll(pageable);
        return allUsers.map(user -> {
            user.setPassword(null);
            return user;
        });
    }

    public void deleteUser(
            final String id
    ) throws TicoException {
        final User user = this.userRepository.findById(id).orElseThrow(() -> new TicoException("User not found!"));
        this.sessionService.logoutUserSessionEverywhere(user.getUserId(), user.getUserType());
        this.userRepository.deleteById(id);
    }
}
