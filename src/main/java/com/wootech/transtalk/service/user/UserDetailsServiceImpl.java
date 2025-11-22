package com.wootech.transtalk.service.user;

import com.wootech.transtalk.dto.auth.AuthUser;
import com.wootech.transtalk.entity.User;
import com.wootech.transtalk.exception.custom.NotFoundException;
import com.wootech.transtalk.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.wootech.transtalk.exception.ErrorMessages.WITHDRAWN_USER_ERROR;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(WITHDRAWN_USER_ERROR, HttpStatusCode.valueOf(404)));

        AuthUser authUser = new AuthUser(user.getId(), user.getEmail(), user.getName(), user.getUserRole());
        if (!authUser.isEnabled()) {
            throw new NotFoundException(WITHDRAWN_USER_ERROR, HttpStatusCode.valueOf(404));
        }
        return authUser;
    }
}
