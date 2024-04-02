package com.cqcst.util;

import com.cqcst.entity.Admin;

//TODO: 鉴权有问题
public class AdminContext {

    private static ThreadLocal<Admin> adminThreadLocal = new ThreadLocal<>();

    public static Admin getAdmin() {
        return adminThreadLocal.get();
    }

    public static void setAdmin(Admin admin) {
        admin.setPassword(null);
        adminThreadLocal.set(admin);
        System.out.println("adminThreadLocal.get() = " + adminThreadLocal.get());
    }

    public static void removeAdmin() {
        adminThreadLocal.remove();
    }
}
