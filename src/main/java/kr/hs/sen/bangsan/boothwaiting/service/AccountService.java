package kr.hs.sen.bangsan.boothwaiting.service;

import kr.hs.sen.bangsan.boothwaiting.domain.Account;
import kr.hs.sen.bangsan.boothwaiting.dto.AccountCheckResponse;
import kr.hs.sen.bangsan.boothwaiting.repository.AccountRepository;
import kr.hs.sen.bangsan.boothwaiting.repository.WaitingRepository;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sqids.Sqids;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private WaitingRepository waitingRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private Scheduler scheduler;

    private final Sqids sqids = Sqids.builder().minLength(4).alphabet("8as50tyuo2rjklgh6cv1zxdebm7w9fp34niq").build();

    public String getEncryptedId(int studentId) {
        return sqids.encode(Collections.singletonList(accountRepository.findByStudentId(studentId).getId().longValue()));
    }

    public AccountCheckResponse existsByEncryptedId(String encryptedId) {
        Optional<Account> account = accountRepository.findById(sqids.decode(encryptedId).get(0).intValue());
        if (account.isEmpty()) {
            return new AccountCheckResponse(false, "찾을 수 없습니다.");
        }
        if (account.get().getStatus() == Account.AccountStatus.CALLED) {
            return new AccountCheckResponse( true, "계정이 확인되었습니다.");
        }
        return new AccountCheckResponse(false, "만료되었습니다.");
    }

    public boolean isCalled(int studentId) {
        return accountRepository.existsByStudentId(studentId) && accountRepository.findByStudentId(studentId).getStatus() == Account.AccountStatus.CALLED;
    }

    @Transactional
    public String enter(int studentId) {
        if (this.isCalled(studentId)) {
            accountRepository.findByStudentId(studentId).completeEntry();

            try {
                JobKey jobKey = JobKey.jobKey(Objects.toString(studentId), "cancel-group");

                // 스케줄러 삭제
                if (scheduler.checkExists(jobKey)) {
                    scheduler.deleteJob(jobKey);
                }
            } catch (SchedulerException e) {
                System.out.println("타이머 취소중 오류");
                e.printStackTrace(System.out);
            }

            //TODO: 퇴장타이머 만들기

            return "입장되셨습니다.";
        } if (accountRepository.existsByStudentId(studentId) && accountRepository.findByStudentId(studentId).getStatus() == Account.AccountStatus.CANCELED) {
            return "부재로 인해 만료된 입장권입니다.";
        } else if(waitingRepository.existsByStudentId(studentId)) {
            return "아직 호출되지 않은 순번입니다.";
        }
        return "알수없는 입장권입니다.";
    }
}
