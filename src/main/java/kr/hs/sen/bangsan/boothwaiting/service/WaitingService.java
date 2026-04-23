package kr.hs.sen.bangsan.boothwaiting.service;

import kr.hs.sen.bangsan.boothwaiting.domain.Account;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingCheckResponse;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingRegisterResponse;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingResisterRequest;
import kr.hs.sen.bangsan.boothwaiting.repository.AccountRepository;
import kr.hs.sen.bangsan.boothwaiting.repository.WaitingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WaitingService {

    @Autowired
    private WaitingRepository waitingRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public WaitingRegisterResponse registerWaiting(WaitingResisterRequest waitingResisterRequest) {
        Account account = accountRepository.findByStudentId(waitingResisterRequest.getStudentId());
        if (account != null && (account.getStatus() == Account.AccountStatus.CALLED || account.getStatus() == Account.AccountStatus.ENTERED || account.getStatus() == Account.AccountStatus.TEMPORARILY_EXIT)) {
            return new WaitingRegisterResponse(-1, "이미 호출 혹은 입장된 학번입니다.");
        }
        if (waitingRepository.existsByStudentId(waitingResisterRequest.getStudentId())) {
            return new WaitingRegisterResponse(-1, "이미 등록된 학번입니다.");
        }
        return new WaitingRegisterResponse(waitingRepository.save(waitingResisterRequest.toEntity()).getId(), "등록되었습니다.");
    }

    //*학번으로 받은 학생 앞에 대기중인 학생수(대기열에 없을 경우 -1)
    public WaitingCheckResponse checkWaiting(int studentID) {
        if(waitingRepository.existsByStudentId(studentID)) {
            return new WaitingCheckResponse((int) waitingRepository.findAll().stream().filter(account -> account.getId() < waitingRepository.findByStudentId(studentID).getId()).count(), "앞에서 대기중인 팀의 수를 정상적으로 불러왔습니다.");
        } else {
            return new WaitingCheckResponse(-1, "등록되지 않은 학번입니다.");
        }
    }

    @Transactional
    public String EnterWaiting(int studentID) {
        if(accountRepository.existsByStudentId(studentID) && !waitingRepository.existsByStudentId(studentID)) {
            Account account = accountRepository.findByStudentId(studentID);
            if(account.getStatus() == Account.AccountStatus.CALLED) {
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
