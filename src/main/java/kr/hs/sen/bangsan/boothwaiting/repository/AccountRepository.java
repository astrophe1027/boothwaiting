package kr.hs.sen.bangsan.boothwaiting.repository;

import kr.hs.sen.bangsan.boothwaiting.domain.Account;
import kr.hs.sen.bangsan.boothwaiting.domain.Waiting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    List<Account> findAllByStatus(Account.AccountStatus status);

    boolean existsByStudentID(int studentID);

    Account findByStudentID(int studentID);
}
