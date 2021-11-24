package com.yapp.sharefood.userflavor.repository;

import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.userflavor.domain.UserFlavor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserFlavorRepository extends JpaRepository<UserFlavor, Long>, UserFlavorQueryRepository {
    void deleteUserFlavorsByUser(User user);

    @Query("SELECT uf FROM UserFlavor uf JOIN FETCH uf.flavor WHERE uf.user = :user")
    List<UserFlavor> findByUser(@Param("user") User user);
}
