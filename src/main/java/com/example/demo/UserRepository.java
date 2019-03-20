package com.example.demo;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
    Long countByEmail(String email);
    User findByEmail(String email);
    Long countByUsername(String username);
}
