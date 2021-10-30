package com.yapp.sharefood.userflavor.repository;

import com.yapp.sharefood.userflavor.domain.UserFlavor;

import java.util.List;

public interface UserFlavorQueryRepository {
    List<UserFlavor> findUserFlavorByUserId(Long userId);
}
