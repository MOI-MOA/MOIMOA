package com.b110.jjeonchongmu.domain.account.service;

import com.b110.jjeonchongmu.domain.account.dto.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonalAccountService {

    public String transfer(TransferRequestDTO requestDto) {
        // 송금 로직 구현
        return null;
    }

    public Boolean checkPassword(PasswordCheckRequestDTO requestDto) {
        // 비밀번호 확인 로직 구현
        return true;
    }

    public void addAutoPayment() {
        // 자동이체 추가 로직 구현
    }

    public List<AddAutoPaymentRequestDTO> getAutoPayments() {
        // 자동이체 조회 로직 구현
        return new ArrayList<>();
    }

    public void deleteAccount(DeleteRequestDTO requestDTO) {
        // 계좌 삭제 로직 구현
    }
}
