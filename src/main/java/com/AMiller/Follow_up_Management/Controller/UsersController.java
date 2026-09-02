package com.AMiller.Follow_up_Management.Controller;

import com.AMiller.Follow_up_Management.DTO.UsersDTO;
import com.AMiller.Follow_up_Management.Repository.UsersRepository;
import com.AMiller.Follow_up_Management.Service.UsersService;
import com.AMiller.Follow_up_Management.entities.Users;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping
    public List<Users> findAll() {
        return usersService.findAllUsers();
    }

    @GetMapping("/{id}")
    public Users findById(@PathVariable int id) {

        return usersService.findUserById(id);

    }

    @PostMapping
    public Users addUser(@RequestBody UsersDTO usersDTO) {

        return usersService.addUser(usersDTO.getUserName(),usersDTO.getPassword(), usersDTO.getRole(),usersDTO.isActive());
    }

    @PutMapping("/{id}")
    public Users updateUser(@PathVariable int id, @RequestBody UsersDTO usersDTO) {

        return usersService.updateUser(id, usersDTO.getUserName(), usersDTO.getPassword(), usersDTO.getRole(), usersDTO.isActive());

    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        usersService.deleteUser(id);
    }

    @GetMapping("/search")
    public List<Users> searchByRole(@RequestParam String role) {
        return usersService.searchByRole(role);

    }

    @PutMapping("/{id}/active")
    public Users updateUserActiveStatus(@PathVariable int id, @RequestParam boolean active) {
        return usersService.setUserActive(id, active);
    }
}
