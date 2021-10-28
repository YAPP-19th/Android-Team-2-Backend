package com.yapp.sharefood.userflavor.repository;

import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.userflavor.domain.UserFlavor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFlavorRepository extends JpaRepository<UserFlavor, Long> {
    void deleteUserFlavorsByUser(User user);
}
