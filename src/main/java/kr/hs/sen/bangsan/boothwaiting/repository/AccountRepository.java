package kr.hs.sen.bangsan.boothwaiting.repository;

import kr.hs.sen.bangsan.boothwaiting.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    List<Account> findAllByStatus(Account.AccountStatus status);

    boolean existsByStudentId(int studentId);

    Account findByStudentId(int studentId);

    boolean existsByToken(String token);

    Account findByToken(String token);
}
