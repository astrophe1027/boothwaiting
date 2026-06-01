package kr.hs.sen.bangsan.boothwaiting.service;

import kr.hs.sen.bangsan.boothwaiting.controller.MonitorController;
import kr.hs.sen.bangsan.boothwaiting.domain.Account;
import kr.hs.sen.bangsan.boothwaiting.repository.AccountRepository;
import kr.hs.sen.bangsan.boothwaiting.repository.WaitingRepository;
import kr.hs.sen.bangsan.boothwaiting.service.job.ExitJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

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

    @Value("${booth.exit-timeout-minutes}")
    private int exitTimeoutMinutes;

    public boolean isCalled(int studentId) {
        return accountRepository.existsByStudentId(studentId)
                && accountRepository.findByStudentId(studentId).getStatus() == Account.AccountStatus.CALLED;
    }

    @Transactional
    public String enter(int studentId) {
        if (this.isCalled(studentId)) {
            Account account = accountRepository.findByStudentId(studentId);
            account.completeEntry();

            // 호출 취소 타이머 제거
            cancelScheduledJob(Objects.toString(studentId), "cancel-group");

            // 퇴장 타이머 등록
            scheduleExitJob(studentId);

            monitorController.broadcastCurrentAccounts();
            return "입장되셨습니다.";
        }
        if (accountRepository.existsByStudentId(studentId)
                && accountRepository.findByStudentId(studentId).getStatus() == Account.AccountStatus.CANCELED) {
            return "부재로 인해 만료된 입장권입니다.";
        } else if (waitingRepository.existsByStudentId(studentId)) {
            return "아직 호출되지 않은 순번입니다.";
        }
        return "알수없는 입장권입니다.";
    }

    @Transactional
    public String exit(int studentId) {
        if (accountRepository.existsByStudentId(studentId)) {
            Account account = accountRepository.findByStudentId(studentId);
            if (account.getStatus() == Account.AccountStatus.ENTERED
                    || account.getStatus() == Account.AccountStatus.TEMPORARILY_EXIT) {
                account.exit();

                // 퇴장 타이머 제거
                cancelScheduledJob(studentId + "-exit", "exit-group");

                waitingUpdateService.updateWaiting(); // 내부에서 broadcast 호출됨
                return "퇴장되었습니다.";
            }
        }
        return "잘못된 학번입니다.";
    }

    private void scheduleExitJob(int studentId) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("studentId", studentId);

        JobDetail jobDetail = JobBuilder.newJob(ExitJob.class)
                .withIdentity(studentId + "-exit", "exit-group")
                .usingJobData(jobDataMap)
                .build();

        Date runTime = Date.from(
                LocalDateTime.now()
                        .atZone(ZoneId.systemDefault())
                        .plusMinutes(exitTimeoutMinutes)
                        .toInstant()
        );

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(studentId + "-exit-trigger", "exit-group")
                .startAt(runTime)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withMisfireHandlingInstructionFireNow())
                .build();

        try {
            cancelScheduledJob(studentId + "-exit", "exit-group");
            scheduler.scheduleJob(jobDetail, trigger);
            System.out.println(studentId + " 퇴장 타이머 등록 (" + exitTimeoutMinutes + "분 후)");
        } catch (SchedulerException e) {
            System.out.println("퇴장 타이머 등록 실패");
            e.printStackTrace(System.out);
        }
    }

    private void cancelScheduledJob(String jobName, String groupName) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, groupName);
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
        } catch (SchedulerException e) {
            System.out.println("타이머 취소 중 오류: " + jobName);
            e.printStackTrace(System.out);
        }
    }
}