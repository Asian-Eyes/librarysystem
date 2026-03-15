package org.example.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_slips")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowSlipsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "slip_no")
    private String slipNo;

    @Column(name = "member_id")
    private int memberId;

    @Column(name = "borrow_at")
    private LocalDateTime borrowAt;

    @Column(name = "due_at")
    private LocalDateTime dueAt;

    @Column(name = "status")
    private String status;
}