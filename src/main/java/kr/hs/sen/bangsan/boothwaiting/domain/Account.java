package kr.hs.sen.bangsan.boothwaiting.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private Integer studentId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer coin;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime time;

    @Column(nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    public Account(Integer studentID, String name, String token) {
        this.studentId = studentID;
        this.name = name;
        this.coin = 0;
        this.status = AccountStatus.CALLED;
        this.token = token;
    }

    public void completeEntry() {
        this.status = AccountStatus.ENTERED;
        this.time = LocalDateTime.now();
    }

    public void cancelEntry() {
        this.status = AccountStatus.CANCELED;
        this.time = LocalDateTime.now();
    }

    public void exit() {
        this.status = AccountStatus.EXITED;
        this.time = LocalDateTime.now();
    }

    public void recall(String token) {
        this.status = AccountStatus.CALLED;
        this.time = LocalDateTime.now();
        this.token = token;
    }

    public void temporarilyExit() {
        this.status = AccountStatus.TEMPORARILY_EXIT;
    }

    public enum AccountStatus {
        CALLED,
        ENTERED,
        EXITED,
        TEMPORARILY_EXIT,
        CANCELED
    }
}