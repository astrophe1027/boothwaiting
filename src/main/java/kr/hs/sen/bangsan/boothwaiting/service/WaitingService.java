package kr.hs.sen.bangsan.boothwaiting.service;

import kr.hs.sen.bangsan.boothwaiting.controller.MonitorController;
import kr.hs.sen.bangsan.boothwaiting.domain.Account;
import kr.hs.sen.bangsan.boothwaiting.domain.Waiting;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingNumberCheckResponse;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingRegisterRequest;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingRegisterResponse;
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
    private MonitorController monitorController;
    @Autowired
    private Scheduler scheduler;

    @Transactional
    public WaitingRegisterResponse registerWaiting(WaitingRegisterRequest waitingRegisterRequest) {
        int studentId = Integer.parseInt(waitingRegisterRequest.getStudentId());
        Account account = accountRepository.findByStudentId(studentId);
        if (account != null && (account.getStatus() == Account.AccountStatus.CALLED || account.getStatus() == Account.AccountStatus.ENTERED || account.getStatus() == Account.AccountStatus.TEMPORARILY_EXIT)) {
            return new WaitingRegisterResponse(-1, "이미 호출 혹은 입장된 학번입니다.");
        }
        if (waitingRepository.existsByStudentId(studentId)) {
            return new WaitingRegisterResponse(-1, "이미 등록된 학번입니다.");
        }
        Waiting waiting = waitingRepository.save(waitingRegisterRequest.toEntity());
        waitingUpdateService.updateWaiting();
        return new WaitingRegisterResponse(waiting.getId(), "등록되었습니다.");
    }

    //학번으로 받은 학생 앞에 대기중인 학생 수
    public WaitingNumberCheckResponse checkWaiting(int studentID) {
        if(waitingRepository.existsByStudentId(studentID)) {
            return new WaitingNumberCheckResponse((int) waitingRepository.findAll().stream().filter(account -> account.getId() < waitingRepository.findByStudentId(studentID).getId()).count(), "대기 팀의 수를 정상적으로 불러왔습니다.");
        } else {
            if(accountRepository.existsByStudentId(studentID) && accountRepository.findByStudentId(studentID).getStatus() == Account.AccountStatus.CALLED) {
                return new WaitingNumberCheckResponse(-1, "호출되셨습니다.");
            }
            return new WaitingNumberCheckResponse(-1, "등록되지 않은 학번입니다.");
        }
    }

    public int getIdByStudentId(int studentId) {
        if(waitingRepository.existsByStudentId(studentId)) {
            return waitingRepository.findByStudentId(studentId).getId();
        } else {
            return -1;
        }
    }

    public String getToken(int studentId) {
        if(waitingRepository.existsByStudentId(studentId)) {
            return waitingRepository.findByStudentId(studentId).getToken();
        } else {
            if(accountRepository.existsByStudentId(studentId)) {
                return accountRepository.findByStudentId(studentId).getToken();
            }
        }
        return "";
    }

    public int getStudentIdByToken(String token) {
            if(waitingRepository.existsByToken(token)) {
                return waitingRepository.findByToken(token).getStudentId();
            } else if (accountRepository.existsByToken(token)) {
                if(accountRepository.findByToken(token).getStatus() == Account.AccountStatus.CALLED) {
                    return accountRepository.findByToken(token).getStudentId();
                } else {
                    return 0;
                }
            } else{
                return 0;
            }
    }

    @Transactional
    public String cancelWaiting(int studentId) {
        if(waitingRepository.existsByStudentId(studentId)) {
            waitingRepository.deleteByStudentId(studentId);

            return "정상적으로 취소 되었습니다.";
        } else if (accountRepository.existsByStudentId(studentId) && accountRepository.findByStudentId(studentId).getStatus() == Account.AccountStatus.CALLED) {
            accountRepository.findByStudentId(studentId).cancelEntry();

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
            monitorController.broadcastCurrentAccounts();
            return "정상적으로 취소 되었습니다.";
        }else {
            return "계정을 찾을 수 없습니다.";
        }
    }
}
