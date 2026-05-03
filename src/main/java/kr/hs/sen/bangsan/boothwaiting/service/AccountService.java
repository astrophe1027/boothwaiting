package kr.hs.sen.bangsan.boothwaiting.service;

import kr.hs.sen.bangsan.boothwaiting.domain.Account;
import kr.hs.sen.bangsan.boothwaiting.dto.AccountCheckResponse;
import kr.hs.sen.bangsan.boothwaiting.repository.AccountRepository;
import kr.hs.sen.bangsan.boothwaiting.repository.WaitingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sqids.Sqids;

import java.util.Collections;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private WaitingRepository waitingRepository;
    @Autowired
    private AccountRepository accountRepository;

    private final Sqids sqids = Sqids.builder().minLength(4).alphabet("").build();

    public String getEncryptedId(int studentId) {
        return sqids.encode(Collections.singletonList(Long.valueOf(accountRepository.findByStudentId(studentId).getId())));
    }

    public AccountCheckResponse existsByEncryptedId(String encryptedId) {
        Optional<Account> account = accountRepository.findById((int) (long) sqids.decode(encryptedId).get(0));
        if (account.isEmpty()) {
            return new AccountCheckResponse(false, "찾을 수 없습니다.");
        }
        if (account.get().getStatus() == Account.AccountStatus.CALLED) {
            return new AccountCheckResponse( true, "계정이 확인되었습니다.");
        }
        return new AccountCheckResponse(false, "만료되었습니다.");
    }
}
