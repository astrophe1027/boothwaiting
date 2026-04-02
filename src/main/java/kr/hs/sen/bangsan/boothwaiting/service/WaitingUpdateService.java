package kr.hs.sen.bangsan.boothwaiting.service;

import kr.hs.sen.bangsan.boothwaiting.domain.Account;
import kr.hs.sen.bangsan.boothwaiting.domain.Waiting;
import kr.hs.sen.bangsan.boothwaiting.repository.AccountRepository;
import kr.hs.sen.bangsan.boothwaiting.repository.WaitingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
public class WaitingUpdateService {

    @Autowired
    private WaitingRepository waitingRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TaskScheduler taskScheduler;

    final WaitingUpdateService waitingUpdateService = this;

    protected static final Map<Integer, ScheduledFuture<?>> waitingCancelTimers = new HashMap<>();

    public void updateWaiting() {
        int currentNumber = accountRepository.findAllByStatus(Account.AccountStatus.ENTERED).size() + accountRepository.findAllByStatus(Account.AccountStatus.WAITING).size() + accountRepository.findAllByStatus(Account.AccountStatus.TEMPORARILY_EXIT).size();
        int maxNumber = 20;
        for (int i = 0; i < maxNumber - currentNumber; i++) {
            Waiting waiting;
            Account account;
            if(waitingRepository.count() != 0) {
                waiting = waitingRepository.findAll().stream().min(Comparator.comparing(Waiting::getId)).orElseThrow();
            } else {break;}
            if(accountRepository.existsByStudentID(waiting.getStudentID())) {
                account = accountRepository.findByStudentID(waiting.getStudentID());
            } else {
                account = new Account(waiting.getStudentID(), waiting.getName());
                accountRepository.save(account);
            }
            final int studentID = account.getStudentID();
            ScheduledFuture<?> scheduledTask = taskScheduler.schedule(() -> {
                Account currentAccount = accountRepository.findByStudentID(studentID);
                if (currentAccount != null && currentAccount.getStatus() == Account.AccountStatus.WAITING) {
                    currentAccount.cancelEntry();
                    waitingUpdateService.updateWaiting();
                    waitingCancelTimers.remove(studentID);
                    // TODO: 입장 취소 메세지 전송
                }

            }, account.getEnterTime().atZone(ZoneId.systemDefault()).plusMinutes(3).toInstant());
            //입장된 사람한테 메세지 발송
            //순번 n번 남은 사람에게 메세지 발송
        }
    }
}
