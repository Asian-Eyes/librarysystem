package org.example.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "borrow_items")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class BorrowItemsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "slip_id")
    private int slipId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BooksModel book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrow_slip_id", nullable = false)
    private BorrowSlipsModel borrowSlip;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "returned_qty")
    private int returnedQty;
}