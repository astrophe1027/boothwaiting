package kr.hs.sen.bangsan.boothwaiting.service;

import kr.hs.sen.bangsan.boothwaiting.domain.Account;
import kr.hs.sen.bangsan.boothwaiting.domain.Waiting;
import kr.hs.sen.bangsan.boothwaiting.repository.AccountRepository;
import kr.hs.sen.bangsan.boothwaiting.repository.WaitingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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

    @Transactional
    public void updateWaiting() {
        int currentNumber = accountRepository.findAllByStatus(Account.AccountStatus.ENTERED).size() + accountRepository.findAllByStatus(Account.AccountStatus.CALLED).size() + accountRepository.findAllByStatus(Account.AccountStatus.TEMPORARILY_EXIT).size();
        //최대 수용 가능 인원수
        int maxNumber = 20;
        //현재 입장 인원과 최대 수용 인원수 비교
        for (int i = 0; i < maxNumber - currentNumber; i++) {
            Waiting waiting;
            Account account;
            //가장 앞에 사람 구하기
            if(waitingRepository.count() != 0) {
                waiting = waitingRepository.findAll().stream().min(Comparator.comparing(Waiting::getId)).orElseThrow();
            } else {break;}
            //입장 시킬 사람 계정 찾기 or 계정 생성
            if(accountRepository.existsByStudentId(waiting.getStudentId())) {
                account = accountRepository.findByStudentId(waiting.getStudentId());
            } else {
                account = new Account(waiting.getStudentId(), waiting.getName());
                accountRepository.save(account);
            }
            final String phoneNumber = waiting.getPhoneNumber();
            //입장 시킬 사람 waiting 삭제
            waitingRepository.delete(waiting);
            final int studentID = account.getStudentId();
            //입장 취소 타이머 생성
            ScheduledFuture<?> scheduledTask = taskScheduler.schedule(() -> {
                Account currentAccount = accountRepository.findByStudentId(studentID);
                if (currentAccount != null && currentAccount.getStatus() == Account.AccountStatus.CALLED) {
                    currentAccount.cancelEntry();
                    waitingUpdateService.updateWaiting();
                    waitingCancelTimers.remove(studentID);
                    // TODO: 입장 취소 메세지 전송
                    System.out.println(currentAccount.getStudentId()+" "+phoneNumber+" 입장이 취소되었습니다.");
                }
            }, account.getEnterTime().atZone(ZoneId.systemDefault()).plusMinutes(3).toInstant());
            waitingCancelTimers.put(account.getStudentId(), scheduledTask);
            // TODO: 호출된 사람한테 메세지 발송
            System.out.println(account.getStudentId()+" "+phoneNumber+" 호출되셨습니다.");

            int n = 3;
            //앞에 n명 남은사람에게 메세지 발송
            if(waitingRepository.findAll().size() >= n+1){
                List<Waiting> waitings = waitingRepository.findAll(Sort.by("id"));
                Waiting newWaiting = waitings.get(n);
                // TODO: 앞에 n명 남은 사람에게 메세지 발송
                System.out.println(newWaiting.getStudentId()+" "+newWaiting.getPhoneNumber()+" 부스 근처로 이동해주세요");
            }
        }
    }
}
