package com.example.User.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.User.model.User;
import com.example.User.model.dto.UserRequest;
import com.example.User.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final UserRepository userRepo;

    public User register(UserRequest userReq){
        System.out.println("In user funcion " + userReq);
        String username = userReq.getUsername();
        String password = userReq.getPassword();
        Optional<User> optionalUser = userRepo.findByUsername(username);
        User user = User.builder().build();

        if (optionalUser.isPresent()) {
            return user;
        }


        user.setUsername(username);
        user.setPassword(password);
        userRepo.save(user);

        return user;
    }

    public User login(UserRequest userRequest) {
        System.out.println(userRequest);
        String username = userRequest.getUsername();
        String password = userRequest.getPassword();
        Optional<User> optionalUser = userRepo.findByUsernameAndPassword(username, password);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        return User.builder().build();
    }

//    public List<User> getAllUsers() {
//        return userRepo.findAll();
//    }
//
//    public User getUserById(Integer id) {
//        return userRepo.findById(id).orElse(null);
//    }
//
//    public User createUser(User user) {
//        return userRepo.save(user);
//    }
//    public User getUserByUsername(String username) {
//        return userRepo.findByUsername(username);
//    }
//
//    public List<String> getUsersByUsername(String username) {
//        List<User> users = userRepo.findAllByUsername(username);
//        return users.stream().map(User::getUserName).collect(Collectors.toList());
//    }
//
//    public User updateUser(Integer userId, User updatedUser) {
//        Optional<User> userOptional = userRepo.findById(userId);
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            if ("Admin".equals(user.getRole())) {
//                throw new RuntimeException("Admin user cannot be updated");
//            }
//            // Update user details
//            user.setUsername(updatedUser.getUserName());
//            userRepo.save(user);
//            return user;
//
//        } else {
//            throw new RuntimeException("User not found");
//        }
//    }
//
//    public void deleteUser(Integer userId) {
//        Optional<User> userOptional = userRepo.findById(userId);
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            if ("admin".equals(user.getRole())) {
//                throw new RuntimeException("Can't remove admin user");
//            } else {
//                userRepo.deleteById(userId);
//            }
//        } else {
//            throw new RuntimeException("User not found");
//        }
//    }

}
