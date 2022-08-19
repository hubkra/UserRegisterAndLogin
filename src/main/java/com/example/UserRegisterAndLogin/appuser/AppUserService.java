package com.example.UserRegisterAndLogin.appuser;

import com.example.UserRegisterAndLogin.seciurity.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with that email"+email+"not exists"));
    }

    public String singUpUser(AppUser appUser){
        boolean userExist = appUserRepository.findByEmail(appUser.getEmail()).isPresent();
        if(userExist){
            throw new IllegalStateException("Email already taken");
        }
        String encodedPassword = passwordEncoder.bCryptPasswordEncoder().encode((appUser.getPassword()));

        appUser.setPassword(encodedPassword);

        appUserRepository.save(appUser);

        //TODO: SEND CONFIRMATION TOKEN

        return  "it works!";
    }
}
