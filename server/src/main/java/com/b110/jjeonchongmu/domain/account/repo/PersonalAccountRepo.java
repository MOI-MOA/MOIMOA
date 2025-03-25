package com.b110.jjeonchongmu.domain.account.repo;

import com.b110.jjeonchongmu.domain.account.entity.PersonalAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalAccountRepo extends JpaRepository<PersonalAccount, Long> {

}