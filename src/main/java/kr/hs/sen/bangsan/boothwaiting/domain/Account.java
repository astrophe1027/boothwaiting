package kr.hs.sen.bangsan.boothwaiting.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String studentID;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime enterTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    public Account(String studentID, String name, String token) {
        this.studentID = studentID;
        this.name = name;
        this.token = token;
        this.status = AccountStatus.WAITING; // 초기값은 항상 대기
    }

    // 상태 변경 및 입장 시간 기록을 위한 비즈니스 메서드
    public void completeEntry() {
        this.status = AccountStatus.ENTERED;
        this.enterTime = LocalDateTime.now();
    }

    public enum AccountStatus {
        WAITING,
        ENTERED,
        EXITED,
        TEMPORARILY_EXIT,
        CANCELED
    }
}