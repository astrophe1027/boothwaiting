package kr.hs.sen.bangsan.boothwaiting.service;

import kr.hs.sen.bangsan.boothwaiting.domain.Account;
import kr.hs.sen.bangsan.boothwaiting.domain.Waiting;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingCheckResponse;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingRegisterResponse;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingRegisterRequest;
import kr.hs.sen.bangsan.boothwaiting.repository.AccountRepository;
import kr.hs.sen.bangsan.boothwaiting.repository.WaitingRepository;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class WaitingService {

    @Autowired
    private WaitingRepository waitingRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private WaitingUpdateService waitingUpdateService;
    @Autowired
    private Scheduler scheduler;

    @Transactional
    public WaitingRegisterResponse registerWaiting(WaitingRegisterRequest waitingRegisterRequest) {
        Account account = accountRepository.findByStudentId(waitingRegisterRequest.getStudentId());
        if (account != null && (account.getStatus() == Account.AccountStatus.CALLED || account.getStatus() == Account.AccountStatus.ENTERED || account.getStatus() == Account.AccountStatus.TEMPORARILY_EXIT)) {
            return new WaitingRegisterResponse(-1, "이미 호출 혹은 입장된 학번입니다.");
        }
        if (waitingRepository.existsByStudentId(waitingRegisterRequest.getStudentId())) {
            return new WaitingRegisterResponse(-1, "이미 등록된 학번입니다.");
        }
        Waiting waiting = waitingRepository.save(waitingRegisterRequest.toEntity());
        waitingUpdateService.updateWaiting();
        return new WaitingRegisterResponse(waiting.getId(), "등록되었습니다.");
    }

    //*학번으로 받은 학생 앞에 대기중인 학생수(대기열에 없을 경우 -1)
    public WaitingCheckResponse checkWaiting(int studentID) {
        if(waitingRepository.existsByStudentId(studentID)) {
            return new WaitingCheckResponse((int) waitingRepository.findAll().stream().filter(account -> account.getId() < waitingRepository.findByStudentId(studentID).getId()).count(), "대기 팀의 수를 정상적으로 불러왔습니다.");
        } else {
            return new WaitingCheckResponse(-1, "등록되지 않은 학번입니다.");
        }
    }

    @Transactional
    public String EnterWaiting(int studentId) {
        if(accountRepository.existsByStudentId(studentId) && !waitingRepository.existsByStudentId(studentId)) {
            Account account = accountRepository.findByStudentId(studentId);
            if(account.getStatus() == Account.AccountStatus.CALLED) {
                account.completeEntry();

                try {
                    // 1. 학번과 그룹명을 이용해 JobKey 생성
                    JobKey jobKey = JobKey.jobKey(Objects.toString(studentId), "cancel-group");

                    // 2. 해당 JobKey가 스케줄러에 존재하는지 확인 후 삭제
                    if (scheduler.checkExists(jobKey)) {
                        scheduler.deleteJob(jobKey);
                    }
                } catch (SchedulerException e) {
                    System.out.println("타이머 취소중 오류");
                    e.printStackTrace(System.out);
                }

                // TODO:이용제한 시간 타이머 만들기
                return "입장되었습니다.";
            } else if (account.getStatus() == Account.AccountStatus.CANCELED) {
                return "부재로 인해 이미 입장 취소되었습니다.";
            } else if (account.getStatus() == Account.AccountStatus.ENTERED) {
                return "이미 입장되어 있습니다.";
            }
        }
        return "등록되지 않았습니다";
    }

    public int getId(int studentId) {
        if(waitingRepository.existsByStudentId(studentId)) {
            return waitingRepository.findByStudentId(studentId).getId();
        } else {
            return -1;
        }
    }
}
