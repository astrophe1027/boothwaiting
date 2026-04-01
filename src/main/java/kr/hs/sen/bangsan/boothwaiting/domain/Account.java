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
    private Integer studentID;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer token;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime enterTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    public Account(Integer studentID, String name) {
        this.studentID = studentID;
        this.name = name;
        this.token = 0;
        this.status = AccountStatus.WAITING;
    }

    public void completeEntry() {
        this.status = AccountStatus.ENTERED;
        this.enterTime = LocalDateTime.now();
    }

    public void cancelEntry() {
        this.status = AccountStatus.CANCELED;
    }

    public void exit() {
        this.status = AccountStatus.EXITED;
    }

    public void reregister() {
        this.status = AccountStatus.WAITING;
    }

    public void temporarilyExit() {
        this.status = AccountStatus.TEMPORARILY_EXIT;
    }

    public enum AccountStatus {
        WAITING,
        ENTERED,
        EXITED,
        TEMPORARILY_EXIT,
        CANCELED
    }
}