package io.roach.spring.identity.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

//@Entity
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AccountEntity<ID> extends AbstractEntity<ID> {
    @Column(name = "balance")
    private double balance;

    @Column(name = "creation_time", nullable = true, insertable = false)
    private LocalDateTime creationTime;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "AccountEntity{" + "id=" + getId() + ", balance=" + balance + ", creationTime=" + creationTime + '}';
    }
}
