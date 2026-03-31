package kr.hs.sen.bangsan.boothwaiting.service;

import kr.hs.sen.bangsan.boothwaiting.domain.Account;
import kr.hs.sen.bangsan.boothwaiting.domain.Waiting;
import kr.hs.sen.bangsan.boothwaiting.repository.AccountRepository;
import kr.hs.sen.bangsan.boothwaiting.repository.WaitingRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class WaitingUpdateService {

    private WaitingRepository waitingRepository;
    private AccountRepository accountRepository;

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
                account.completeEntry();
            } else {
                account = new Account(waiting.getStudentID(), waiting.getName());
                accountRepository.save(account);
            }
            //입장 취소 타이머 취소시키기
            //입장된 사람한테 메세지 발송
            //순번 n번 남은 사람에게 메세지 발송
        }
    }
}
