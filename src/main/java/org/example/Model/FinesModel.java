package org.example.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "fines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinesModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "slip_id")
    private int slipId;

    @Column(name = "member_id")
    private int memberId;

    @Column(name = "amount")
    private double amount;

    @Column(name = "reason")
    private String reason;

    @Column(name = "paid")
    private boolean paid;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}