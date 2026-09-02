package com.AMiller.Follow_up_Management.Service;

import com.AMiller.Follow_up_Management.Repository.UsersRepository;
import com.AMiller.Follow_up_Management.entities.Users;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;



import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    private final PasswordEncoder passwordEncoder;
    UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository,PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;

        this.passwordEncoder = passwordEncoder;
    }

    public List<Users> findAllUsers() {
        return usersRepository.findAll();
    }

    public Users findUserById(Integer id) {
        Optional<Users> user = usersRepository.findById(id);
        if (user.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found"
            );
        }
        return user.get();
    }

    public Users addUser(String userName, String password, String role, boolean active) {

        Users user = new Users();

        user.setUserName(userName);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setActive(active);
        return usersRepository.save(user);

    }

    public Users updateUser(int id, String userName, String password, String role, boolean active) {
        Optional<Users> existingUser = usersRepository.findById(id);
        if (existingUser.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found"
            );
        }
        Users user = existingUser.get();
        user.setUserName(userName);
        if(password != null && !password.isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(password));
        }
        user.setRole(role);
        user.setActive(active);
        return usersRepository.save(user);
    }

    public void deleteUser(int id) {
        Optional<Users> user = usersRepository.findById(id);
        if (user.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found"
            );
        }
        usersRepository.deleteById(id);

    }

    public Users searchUser(String userName) {
        Optional<Users> existingUser = usersRepository.findByUserName(userName);
        if (existingUser.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found"
            );

        }
        return existingUser.get();
    }

    public List<Users> searchByRole(String role) {
        List<Users> existingUser = usersRepository.findByRole(role);
        if (existingUser.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User/role not found"
            );
        }
        return existingUser;
    }

    public Users setUserActive(int id, boolean active) {
        Optional<Users> optionalUser = usersRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found"
            );
        }

        Users user = optionalUser.get();
        user.setActive(active);

        return usersRepository.save(user);
    }


}
