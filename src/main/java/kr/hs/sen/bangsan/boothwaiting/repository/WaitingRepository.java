package kr.hs.sen.bangsan.boothwaiting.repository;

import kr.hs.sen.bangsan.boothwaiting.domain.Waiting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaitingRepository extends JpaRepository<Waiting, Integer> {
    boolean existsByStudentID(int studentID);
}
