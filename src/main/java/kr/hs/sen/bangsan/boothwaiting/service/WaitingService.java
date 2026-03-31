package kr.hs.sen.bangsan.boothwaiting.service;

import jakarta.transaction.Transactional;
import kr.hs.sen.bangsan.boothwaiting.dto.WaitingResisterRequest;
import kr.hs.sen.bangsan.boothwaiting.repository.WaitingRepository;
import org.springframework.stereotype.Service;

@Service
public class WaitingService {

    private WaitingRepository waitingRepository;

    @Transactional
    public int registerWaiting(WaitingResisterRequest waitingResisterRequest) {
        if (waitingRepository.existsByStudentID(waitingResisterRequest.getStudentID())) {
            throw new IllegalStateException("이미 대기 등록된 학번입니다.");
        }
        return waitingRepository.save(waitingResisterRequest.toEntity()).getId();
    }

    public int checkWaiting(int studentID) {
        return (int) waitingRepository.findAll().stream().filter(account -> account.getId() < waitingRepository.findByStudentID(studentID).getId()).count();
    }
}
