package com.physiotherapy.userservice.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.physiotherapy.userservice.model.UserModel;

public interface UserRepository extends JpaRepository<UserModel, Long> {
    UserModel findByUsername(String username);
}
