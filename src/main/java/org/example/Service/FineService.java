package org.example.Service;

import org.example.Model.FinesModel;
import org.example.Repository.FineRepo;

import java.time.LocalDateTime;
import java.util.List;

public class FineService {

    private static final double FINE_PER_DAY = 5.0;
    private final FineRepo fineRepo = new FineRepo();

    public double computeFine(long overdueDays, int quantity) {
        if (overdueDays <= 0) return 0.0;
        return overdueDays * FINE_PER_DAY * quantity;
    }

    public boolean createFine(int slipId, int memberId, double amount, String reason) {
        if (fineRepo.fineExistsForSlip(slipId)) return false;
        FinesModel fine = new FinesModel();
        fine.setSlipId(slipId);
        fine.setMemberId(memberId);
        fine.setAmount(amount);
        fine.setReason(reason);
        fine.setPaid(false);
        fine.setCreatedAt(LocalDateTime.now());
        return fineRepo.saveFine(fine);
    }

    public List<FinesModel> getFinesByMember(int memberId) {
        return fineRepo.getFinesByMember(memberId);
    }

    public List<FinesModel> getUnpaidFinesByMember(int memberId) {
        return fineRepo.getUnpaidFinesByMember(memberId);
    }

    public double getUnpaidFineTotal(int memberId) {
        return fineRepo.getUnpaidFineTotal(memberId);
    }
}