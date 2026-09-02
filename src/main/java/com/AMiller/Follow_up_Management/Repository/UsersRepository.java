package com.AMiller.Follow_up_Management.Repository;

import com.AMiller.Follow_up_Management.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {

    Optional<Users> findByUserName(String name);

    List<Users> findByRole(String role);

    Integer userName(String userName);
}
