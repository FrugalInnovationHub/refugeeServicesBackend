package com.fih.cr.sjm.tico.utilities;

import com.fih.cr.sjm.tico.mongodb.documents.Session;
import com.fih.cr.sjm.tico.mongodb.structures.UserTypeEnum;
import com.fih.cr.sjm.tico.threadlocal.UserDetailThreadLocal;
import org.springframework.stereotype.Component;

@Component
public class RolesUtil {
    public boolean isRole(
            final UserTypeEnum userType
    ) {
        final Session currentUserDetails = UserDetailThreadLocal.getCurrentUserDetails();
        if (currentUserDetails != null && currentUserDetails.getUserType() != null) {
            return currentUserDetails.getUserType().equals(userType);
        }
        return false;
    }
}
