package com.example.recommend_lunch_menu.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.userId = :userId")
    Optional<User> findUserByUserId(@Param("userId") Long userId);

    @Query("select count(u) from User u where u.email = :email")
    Integer findByEmailCount(@Param("email") String email);

    @Query("select u.accessToken from User u where u.userId = :userId")
    Optional<String> findAccessTokenByUserId(@Param("userId") Long userId);

    @Query("SELECT u FROM User u WHERE u.isAdmin = true")
    Optional<User> findAdminUser();

    @Query("select u.accessToken from User u where u.isAdmin = true")
    Optional<String> findAccessTokenForAdmin();

    Optional<User> findByEmail(String email);
}
