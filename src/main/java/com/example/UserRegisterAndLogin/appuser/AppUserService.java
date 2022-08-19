package com.example.UserRegisterAndLogin.appuser;

import com.example.UserRegisterAndLogin.registration.token.ConfirmationToken;
import com.example.UserRegisterAndLogin.registration.token.ConfirmationTokenService;
import com.example.UserRegisterAndLogin.seciurity.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, ConfirmationTokenService confirmationTokenService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenService = confirmationTokenService;
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

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
                );
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        //TODO: SEND EMAIL
        return  token;
    }
    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }
}
