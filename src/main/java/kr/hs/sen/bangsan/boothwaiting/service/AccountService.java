package kr.hs.sen.bangsan.boothwaiting.service;

import kr.hs.sen.bangsan.boothwaiting.controller.MonitorController;
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

import java.util.Objects;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private WaitingRepository waitingRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private WaitingUpdateService waitingUpdateService;
    @Autowired
    private MonitorController monitorController;
    @Autowired
    private Scheduler scheduler;

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
            monitorController.broadcastCurrentAccounts();
            return "입장되셨습니다.";
        } if (accountRepository.existsByStudentId(studentId) && accountRepository.findByStudentId(studentId).getStatus() == Account.AccountStatus.CANCELED) {
            return "부재로 인해 만료된 입장권입니다.";
        } else if(waitingRepository.existsByStudentId(studentId)) {
            return "아직 호출되지 않은 순번입니다.";
        }
        return "알수없는 입장권입니다.";
    }
    @Transactional
    public String exit(int studentId) {
        if (accountRepository.existsByStudentId(studentId)) {
            Account account = accountRepository.findByStudentId(studentId);
            if(account.getStatus() == Account.AccountStatus.ENTERED || account.getStatus() == Account.AccountStatus.TEMPORARILY_EXIT) {
                account.exit();
                //TODO: 퇴장타이머 취소
                waitingUpdateService.updateWaiting();
                return "퇴장되었습니다.";
            }
        }
        return "잘못된 학번입니다.";
    }
}
