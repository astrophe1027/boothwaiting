package kr.hs.sen.bangsan.boothwaiting.repository;

import kr.hs.sen.bangsan.boothwaiting.domain.Waiting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatingRepository extends JpaRepository<Waiting, Long> {
}
