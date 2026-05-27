package com.ilsegreto.backend.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.ilsegreto.backend.model.User;
import com.ilsegreto.backend.repository.UserRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String providerId = oAuth2User.getName(); 

        if (email != null) {
            Optional<User> userOptional = userRepository.findByEmail(email);
            
            if (userOptional.isEmpty()) {
                User newUser = new User(name, email, provider, providerId);
                userRepository.save(newUser);
                System.out.println("Nuovo utente registrato via " + provider + ": " + email);
            }
        }

        return oAuth2User;
    }
}