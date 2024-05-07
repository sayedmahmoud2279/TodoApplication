package org.oril.services;

import com.example.aspect.config.JwtUtil;
import com.example.aspect.config.UserVO;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.oril.entities.AuthRequest;
import org.oril.entities.AuthResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class AuthService {

    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil = new JwtUtil();
    private static final String FIXED_SALT = "$2a$10$abcdefghabcdefghabcdefghabcde";

    public AuthResponse register(AuthRequest request) {
        //do validation if user exists in DB
        request.setPassword(BCrypt.hashpw(request.getPassword(), FIXED_SALT));
        System.out.println(request);
        UserVO registeredUser = restTemplate.postForObject("http://user/users/register", request, UserVO.class);
        System.out.println("User is back here : " + registeredUser);
        String accessToken = jwtUtil.generate(registeredUser, "ACCESS");
        String refreshToken = jwtUtil.generate(registeredUser, "REFRESH");

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(AuthRequest request) {
        //do validation if user exists in DB
        request.setPassword(BCrypt.hashpw(request.getPassword(), FIXED_SALT));
        UserVO registeredUser = restTemplate.postForObject("http://user/users/login", request, UserVO.class);

        String accessToken = jwtUtil.generate(registeredUser, "ACCESS");
        String refreshToken = jwtUtil.generate(registeredUser, "REFRESH");

        return new AuthResponse(accessToken, refreshToken);
    }

}
