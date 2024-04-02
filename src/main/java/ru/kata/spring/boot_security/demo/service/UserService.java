package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.util.UserValidator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
//    private final PasswordEncoder passwordEncoder;
    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, /*PasswordEncoder passwordEncoder,*/ UserValidator userValidator) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
//        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByEmail(username);
        if(user.isEmpty()){
            throw new UsernameNotFoundException("User not found");
        }
        return user.get();
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public Optional<User> findById(int id){
        return userRepository.findById(id);
    }

    public Optional<User> findUserByName(String name){
        return userRepository.findUserByEmail(name);
    }
    @Transactional
    public User save(User user){
        return userRepository.save(user);
    }
    @Transactional
    public User update(int id, User updatedUser){
        updatedUser.setId(id);
        return userRepository.save(updatedUser);
    }
    @Transactional
    public void deleteById(int id){
        userRepository.deleteById(id);
    }
}
