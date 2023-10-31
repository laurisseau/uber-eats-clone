package com.example.uber.service;

import com.example.uber.model.UserRole;
import com.example.uber.model.Users;
import com.example.uber.repository.AuthenticationRepository;
import com.example.uber.utils.EmailValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.example.uber.model.UserRole.USER;

@AllArgsConstructor
@Service
public class AuthenticationService implements UserDetailsService {


    private final AuthenticationRepository authenticationRepository;
    private final EmailValidator emailValidator;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return authenticationRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Map<String, Object> userInfo(String username, String token) throws UsernameNotFoundException {

        Users user = authenticationRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        int id = user.getId();
        String currentUsername = user.getUsername();
        String email = user.getEmail();
        String number = user.getNumber();
        UserRole role = user.getUserRole();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", id);
        userInfo.put("username", currentUsername);
        userInfo.put("role", role);
        userInfo.put("email", email);
        userInfo.put("number", number);
        userInfo.put("token", token);

        return userInfo;
    }

    public void signUp(Users user){

        boolean isValidEmail = emailValidator.test(user.getEmail());
        if(!isValidEmail){
            throw new IllegalStateException("Email not valid");
        }

        boolean userEmailExists = authenticationRepository.findByEmail(user.getEmail()).isPresent();
        boolean userUsernameExists = authenticationRepository.findByUsername(user.getUsername()).isPresent();
        if(userEmailExists || userUsernameExists){
            throw new IllegalStateException("User already Exists.");
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setUserRole(USER);

        authenticationRepository.save(user);

    }



}
