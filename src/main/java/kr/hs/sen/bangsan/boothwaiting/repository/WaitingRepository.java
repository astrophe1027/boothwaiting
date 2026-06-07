package kr.hs.sen.bangsan.boothwaiting.repository;

import jakarta.persistence.LockModeType;
import kr.hs.sen.bangsan.boothwaiting.domain.Waiting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;

public interface WaitingRepository extends JpaRepository<Waiting, Integer> {

    boolean existsByStudentId(int studentId);

    Waiting findByStudentId(int studentId);

    void deleteByStudentId(int studentId);

    boolean existsByToken(String token);

    Waiting findByToken(String token);
}
