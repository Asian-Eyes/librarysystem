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

    @Column(name = "book_id")
    private int bookId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "returned_qty")
    private int returnedQty;
}