package com.fih.cr.sjm.tico.threadlocal;

import com.fih.cr.sjm.tico.mongodb.documents.Session;

public class UserDetailThreadLocal {
    private static final ThreadLocal<Session> USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUserThreadLocal(
            final Session session
    ) {
        USER_THREAD_LOCAL.set(session);
    }

    public static Session getCurrentUserDetails(
    ) {
        return USER_THREAD_LOCAL.get();
    }

    public static void shredDetails(
    ) {
        USER_THREAD_LOCAL.remove();
    }
}
