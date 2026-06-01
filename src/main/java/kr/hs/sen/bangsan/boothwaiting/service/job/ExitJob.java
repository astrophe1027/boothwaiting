package kr.hs.sen.bangsan.boothwaiting.service.job;

import kr.hs.sen.bangsan.boothwaiting.domain.Account;
import kr.hs.sen.bangsan.boothwaiting.repository.AccountRepository;
import kr.hs.sen.bangsan.boothwaiting.service.WaitingUpdateService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ExitJob extends QuartzJobBean {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WaitingUpdateService waitingUpdateService;

    @Override
    @Transactional
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // JobDataMap에서 저장된 데이터 추출
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        int studentId = dataMap.getInt("studentId");

        Account currentAccount = accountRepository.findByStudentId(studentId);

        // 취소 시키기
        if (currentAccount != null && (currentAccount.getStatus() == Account.AccountStatus.TEMPORARILY_EXIT || currentAccount.getStatus() == Account.AccountStatus.ENTERED)) {
            currentAccount.exit();

            System.out.println(currentAccount.getStudentId() + " 퇴장되었습니다.");

            waitingUpdateService.updateWaiting();
        }
    }
}