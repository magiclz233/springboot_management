package com.cnpc.repository;

import com.cnpc.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User,String> {
    Page<User> findAll(Pageable pageable);

    User findByUserNameAndEmail(String userName,String email);

    User findByUserName(String userName);

    User findByEmail(String email);

    Optional<User> findById(String id);

    void deleteById(String id);
}
