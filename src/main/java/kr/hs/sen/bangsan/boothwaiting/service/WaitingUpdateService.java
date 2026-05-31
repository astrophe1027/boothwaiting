package kr.hs.sen.bangsan.boothwaiting.service;

import kr.hs.sen.bangsan.boothwaiting.domain.Account;
import kr.hs.sen.bangsan.boothwaiting.domain.Waiting;
import kr.hs.sen.bangsan.boothwaiting.repository.AccountRepository;
import kr.hs.sen.bangsan.boothwaiting.repository.WaitingRepository;
import kr.hs.sen.bangsan.boothwaiting.service.job.CancelEntryJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.*;

@Service
public class WaitingUpdateService {

    @Autowired
    private WaitingRepository waitingRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private Scheduler scheduler;


    final WaitingUpdateService waitingUpdateService = this;

    @Transactional
    public void updateWaiting() {
        int currentNumber = accountRepository.findAllByStatus(Account.AccountStatus.ENTERED).size() + accountRepository.findAllByStatus(Account.AccountStatus.CALLED).size() + accountRepository.findAllByStatus(Account.AccountStatus.TEMPORARILY_EXIT).size();
        // 최대 수용 가능 인원수
        int maxNumber = 2;
        // 현재 입장 인원과 최대 수용 인원수 비교
        for (int i = 0; i < maxNumber - currentNumber; i++) {
            Waiting waiting;
            Account account;
            // 가장 앞에 사람 구하기
            if(waitingRepository.count() != 0) {
                waiting = waitingRepository.findAll().stream().min(Comparator.comparing(Waiting::getId)).orElseThrow();
            } else {break;}
            // 입장 시킬 사람 계정 찾기 or 계정 생성
            if(accountRepository.existsByStudentId(waiting.getStudentId())) {
                account = accountRepository.findByStudentId(waiting.getStudentId());
                account.recall(waiting.getToken());
            } else {
                account = new Account(waiting.getStudentId(), waiting.getName(), waiting.getToken());
                accountRepository.save(account);
            }
            String phoneNumber = waiting.getPhoneNumber();
            // 입장 시킬 사람 waiting 삭제
            waitingRepository.delete(waiting);
            int studentId = account.getStudentId();


            // 입장 취소 타이머 생성
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("studentId", studentId);
            jobDataMap.put("phoneNumber", phoneNumber);

            // JobDetail 생성
            JobDetail jobDetail = JobBuilder.newJob(CancelEntryJob.class)
                    .withIdentity(Objects.toString(studentId), "cancel-group") // studentID를 식별자로 사용
                    .usingJobData(jobDataMap)
                    .build();

            // 트리거
            // x분뒤 실행
            int x = 3;
            Date runTime = Date.from(account.getTime()
                    .atZone(ZoneId.systemDefault())
                    //취소 유예시간
                    .plusMinutes(x)
                    .toInstant());
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(studentId + "-trigger", "cancel-group")
                    .startAt(runTime)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withMisfireHandlingInstructionFireNow()) // 서버가 꺼져서 놓친 작업은 켜지자마자 실행
                    .build();

            try {
                // 중복방지
                if (scheduler.checkExists(jobDetail.getKey())) {
                    scheduler.deleteJob(jobDetail.getKey());
                }
                scheduler.scheduleJob(jobDetail, trigger);
            } catch (SchedulerException e) {
                System.out.println("Quartz 스케줄링 실패");
                e.printStackTrace(System.out);
            }

            // TODO: 호출된 사람한테 메세지 발송
            System.out.println(account.getStudentId()+" "+phoneNumber+" 호출되셨습니다.");

            int n = 3;
            // 앞에 n명 남은사람에게 메세지 발송
            if(waitingRepository.findAll().size() >= n+1){
                List<Waiting> waiters = waitingRepository.findAll(Sort.by("id"));
                Waiting newWaiting = waiters.get(n);
                // TODO: 앞에 n명 남은 사람에게 메세지 발송
                System.out.println(newWaiting.getStudentId()+" "+newWaiting.getPhoneNumber()+" 부스 근처로 이동해주세요");
            }
        }
    }
}
