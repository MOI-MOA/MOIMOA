package com.b110.jjeonchongmu.domain.user.service;

import com.b110.jjeonchongmu.domain.user.dto.SignupRequestDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public void signup(@Valid SignupRequestDTO request) {
    }
}
