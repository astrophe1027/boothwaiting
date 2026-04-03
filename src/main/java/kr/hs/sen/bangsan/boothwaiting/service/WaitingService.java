package kr.hs.sen.bangsan.boothwaiting.service;

import jakarta.transaction.Transactional;
import kr.hs.sen.bangsan.boothwaiting.domain.Account;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingRegisterResponse;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingResisterRequest;
import kr.hs.sen.bangsan.boothwaiting.repository.AccountRepository;
import kr.hs.sen.bangsan.boothwaiting.repository.WaitingRepository;
import org.springframework.stereotype.Service;

@Service
public class WaitingService {

    private WaitingRepository waitingRepository;
    private AccountRepository accountRepository;

    @Transactional
    public WaitingRegisterResponse registerWaiting(WaitingResisterRequest waitingResisterRequest) {
        Account account = accountRepository.findByStudentID(waitingResisterRequest.getStudentID());
        if (account != null && (account.getStatus() == Account.AccountStatus.WAITING || account.getStatus() == Account.AccountStatus.ENTERED || account.getStatus() == Account.AccountStatus.TEMPORARILY_EXIT)) {
            return new WaitingRegisterResponse(-1, "이미 호출 혹은 입장된 학번입니다.");
        }
        if (waitingRepository.existsByStudentID(waitingResisterRequest.getStudentID())) {
            return new WaitingRegisterResponse(-1, "이미 등록된 학번입니다.");
        }
        return new WaitingRegisterResponse(waitingRepository.save(waitingResisterRequest.toEntity()).getId(), "등록되었습니다.");
    }

    //*학번으로 받은 학생 앞에 대기중인 학생수(대기열에 없을 경우 -1)
    public int checkWaiting(int studentID) {
        if(waitingRepository.existsByStudentID(studentID)) {
            return (int) waitingRepository.findAll().stream().filter(account -> account.getId() < waitingRepository.findByStudentID(studentID).getId()).count();
        } else {
            return -1;
        }
    }

    public String EnterWaiting(int studentID) {
        if(accountRepository.existsByStudentID(studentID) && !waitingRepository.existsByStudentID(studentID)) {
            Account account = accountRepository.findByStudentID(studentID);
            if(account.getStatus() == Account.AccountStatus.WAITING) {
                account.completeEntry();
                return "입장되었습니다.";
            } else if (account.getStatus() == Account.AccountStatus.CANCELED) {
                return "부재로 인해 이미 입장 취소되었습니다.";
            } else if (account.getStatus() == Account.AccountStatus.ENTERED) {
                return "이미 입장되어 있습니다.";
            }
        }
        return "등록되지 않았습니다";
    }
}
