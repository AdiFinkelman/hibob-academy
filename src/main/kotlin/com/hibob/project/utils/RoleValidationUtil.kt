package com.hibob.project.utils

import com.hibob.project.dao.Role

object RoleValidationUtil {
    fun isRoleValid(role: Role, allowedRoles: Set<Role>): Boolean {
        return allowedRoles.contains(role)
    }
}