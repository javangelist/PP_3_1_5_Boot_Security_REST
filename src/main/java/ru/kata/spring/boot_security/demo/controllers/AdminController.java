package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.RegistrationService;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.UserValidator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RegistrationService registrationService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final UserValidator userValidator;

    @Autowired
    public AdminController(UserService userService, RegistrationService registrationService, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserValidator userValidator) {
        this.userService = userService;
        this.registrationService = registrationService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userValidator = userValidator;
    }

    @GetMapping()
    public String index(Model model) {
        List<User> users = userService.findAll();
        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("users", users);
        return "admin";
    }

    @GetMapping("/new")
    public String create(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "new";
    }

    @PostMapping("/new")
    public String newUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if(bindingResult.hasErrors()){
            return "profile";
        }
        registrationService.register(user);
        return "redirect:/profile";
    }

    @GetMapping("/edit")
    public String editUser(Model model, @RequestParam(value = "userId", required = false) Integer userId) {
        User user = userService.findById(userId).get();
        Set<Role> roles = user.getRoles();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        return "edit";
    }

    @PostMapping("/edit")
    public String update(HttpServletRequest request, @RequestParam(value = "userId", required = false) Integer userId) {
        User userToUpdate = userService.findById(userId).get();
        String firstName = request.getParameter("first-name");
        String lastName = request.getParameter("last-name");
        int age = Integer.parseInt(request.getParameter("age"));
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        int role = Integer.parseInt(request.getParameter("role"));

        userToUpdate.setFirstName(firstName);
        userToUpdate.setLastName(lastName);
        userToUpdate.setAge(age);
        userToUpdate.setEmail(email);


        if(!password.isEmpty()){
            userToUpdate.setPassword(passwordEncoder.encode(password));
        }


        //Изменение ролей
        if(userToUpdate.getRoles().contains(roleRepository.getById(role))){
//            System.out.println("Нечего менять");
        } else {
            Role role1 = roleRepository.findById(role).get();
            userToUpdate.getRoles().add(role1);
            if(role == 1){
                userToUpdate.getRoles().remove(roleRepository.findById(2).get());
            } else {
                userToUpdate.getRoles().remove(roleRepository.findById(1).get());
            }
        }

        userService.update(userId, userToUpdate);
        return "redirect:/profile";
    }

//    @PostMapping("/edit")
//    public String update(@ModelAttribute("firstUser") @Valid User updatedUser, BindingResult bindingResult, @ModelAttribute("roles") Set<Role> roles,
//                         @RequestParam(value = "userId", required = false) Integer userId) {
//        userValidator.validate(updatedUser, bindingResult);
//        if(bindingResult.hasErrors()){
//            return "edit";
//        }
//        User user = userService.findById(userId).get();
//        user.setName(updatedUser.getName());
//        user.setRoles(roles);
//        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
//        userService.update(userId, user);
//        return "redirect:/admin";
//    }

    @PostMapping("/delete")
    public String delete(Model model, @RequestParam(value = "userId", required = false) Integer userId) {
        userService.deleteById(userId);
        return "redirect:/profile";
    }

}
