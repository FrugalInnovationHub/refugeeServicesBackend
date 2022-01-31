package com.fih.cr.sjm.tico.service;

import com.amazonaws.util.StringUtils;
import com.fih.cr.sjm.tico.mongodb.documents.Session;
import com.fih.cr.sjm.tico.mongodb.repository.SessionRepository;
import com.fih.cr.sjm.tico.mongodb.structures.UserTypeEnum;
import com.fih.cr.sjm.tico.threadlocal.UserDetailThreadLocal;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;

    public SessionService(
            final SessionRepository sessionRepository
    ) {
        this.sessionRepository = sessionRepository;
    }

    public Optional<Session> getSessionDetails(
            final String token
    ) {
        if (StringUtils.isNullOrEmpty(token)) {
            return Optional.empty();
        }
        return this.sessionRepository.findById(token);
    }

    public Session newSession(
            final Session session
    ) {
        return this.sessionRepository.newSession(session);
    }

    public void logoutUserSessionEverywhere(
            final String userId,
            final UserTypeEnum userType
    ) {
        this.sessionRepository.logoutUserSessionEverywhere(userId, userType);
    }

    public Optional<Boolean> logSessionOut() {
        final Session currentSession = UserDetailThreadLocal.getCurrentUserDetails();

        if (currentSession != null && !StringUtils.isNullOrEmpty(currentSession.getToken())
                && this.sessionRepository.existsById(currentSession.getToken())) {
            this.sessionRepository.deleteById(currentSession.getToken());
            return Optional.of(true);
        } else {
            return Optional.empty();
        }
    }

    public Optional<Boolean> isSessionValid(
            final UserTypeEnum userType
    ) {
        final Session currentSession = UserDetailThreadLocal.getCurrentUserDetails();

        if (currentSession != null
                && !StringUtils.isNullOrEmpty(currentSession.getToken())
                && this.sessionRepository.existsById(currentSession.getToken())
                && currentSession.getUserType().equals(userType)) {
            return Optional.of(true);
        } else {
            return Optional.empty();
        }
    }
}
