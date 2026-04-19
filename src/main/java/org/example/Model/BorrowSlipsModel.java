package org.example.Model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "member_id", nullable = false)
   private MemberModel member;

   @Column(name = "borrow_at")
   private LocalDateTime borrowAt;

   @Column(name = "due_at")
   private LocalDateTime dueAt;

   @Column(name = "status")
   private String status;

   @OneToMany(mappedBy = "borrowSlip", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<BorrowItemsModel> items;
}