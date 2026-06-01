package kr.hs.sen.bangsan.boothwaiting.service;

import kr.hs.sen.bangsan.boothwaiting.controller.MonitorController;
import kr.hs.sen.bangsan.boothwaiting.domain.Account;
import kr.hs.sen.bangsan.boothwaiting.domain.Waiting;
import kr.hs.sen.bangsan.boothwaiting.repository.AccountRepository;
import kr.hs.sen.bangsan.boothwaiting.repository.WaitingRepository;
import kr.hs.sen.bangsan.boothwaiting.service.job.CancelEntryJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private MonitorController monitorController;
    @Autowired
    private Scheduler scheduler;

    @Value("${booth.max-capacity}")
    private int maxCapacity;

    @Value("${booth.call-timeout-minutes}")
    private int callTimeoutMinutes;

    @Value("${booth.approach-notify-count}")
    private int approachNotifyCount;

    final WaitingUpdateService waitingUpdateService = this;

    @Transactional
    public void updateWaiting() {
        int currentNumber = accountRepository.findAllByStatus(Account.AccountStatus.ENTERED).size()
                + accountRepository.findAllByStatus(Account.AccountStatus.CALLED).size()
                + accountRepository.findAllByStatus(Account.AccountStatus.TEMPORARILY_EXIT).size();

        for (int i = 0; i < maxCapacity - currentNumber; i++) {
            Waiting waiting;
            Account account;

            if (waitingRepository.count() != 0) {
                waiting = waitingRepository.findAll().stream()
                        .min(Comparator.comparing(Waiting::getId)).orElseThrow();
            } else { break; }

            if (accountRepository.existsByStudentId(waiting.getStudentId())) {
                account = accountRepository.findByStudentId(waiting.getStudentId());
                account.recall(waiting.getToken());
            } else {
                account = new Account(waiting.getStudentId(), waiting.getName(), waiting.getToken());
                accountRepository.save(account);
            }
            String phoneNumber = waiting.getPhoneNumber();
            waitingRepository.delete(waiting);
            int studentId = account.getStudentId();

            // 호출 취소 타이머 등록
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("studentId", studentId);
            jobDataMap.put("phoneNumber", phoneNumber);

            JobDetail jobDetail = JobBuilder.newJob(CancelEntryJob.class)
                    .withIdentity(Objects.toString(studentId), "cancel-group")
                    .usingJobData(jobDataMap)
                    .build();

            Date runTime = Date.from(account.getTime()
                    .atZone(ZoneId.systemDefault())
                    .plusMinutes(callTimeoutMinutes)
                    .toInstant());

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(studentId + "-trigger", "cancel-group")
                    .startAt(runTime)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withMisfireHandlingInstructionFireNow())
                    .build();

            try {
                if (scheduler.checkExists(jobDetail.getKey())) {
                    scheduler.deleteJob(jobDetail.getKey());
                }
                scheduler.scheduleJob(jobDetail, trigger);
            } catch (SchedulerException e) {
                System.out.println("Quartz 스케줄링 실패");
                e.printStackTrace(System.out);
            }

            // TODO: 호출된 사람한테 메세지 발송
            System.out.println(account.getStudentId() + " " + phoneNumber + " 호출되셨습니다.");

            // 앞에 approachNotifyCount명 남은 사람에게 이동 안내
            if (waitingRepository.findAll().size() >= approachNotifyCount + 1) {
                List<Waiting> waiters = waitingRepository.findAll(Sort.by("id"));
                Waiting newWaiting = waiters.get(approachNotifyCount);
                // TODO: 앞에 n명 남은 사람에게 메세지 발송
                System.out.println(newWaiting.getStudentId() + " " + newWaiting.getPhoneNumber() + " 부스 근처로 이동해주세요");
            }
        }

        monitorController.broadcastCurrentAccounts();
    }
}