package org.example.Service;

import java.util.List;

import org.example.Model.MemberModel;
import org.example.Repository.BorrowSlipRepo;
import org.example.Repository.FineRepo;
import org.example.Repository.MemberRepo;

public class MemberService {
    private final MemberRepo memberRepo = new MemberRepo();
    private final BorrowSlipRepo borrowSlipRepo = new BorrowSlipRepo();
    private final FineRepo fineRepo = new FineRepo();

    public boolean createMember(MemberModel member) {
        if (member == null || isBlank(member.getName()) || isBlank(member.getContact())) {
            return false;
        }

        if (isBlank(member.getStatus())) {
            member.setStatus("ACTIVE");
        }

        member.setName(member.getName().trim());
        member.setContact(member.getContact().trim());
        return memberRepo.createMember(member);
    }

    public boolean updateMember(int id, String name, String contact) {
        if (id <= 0 || isBlank(name) || isBlank(contact)) {
            return false;
        }

        return memberRepo.updateMember(id, name.trim(), contact.trim());
    }

    public boolean blockMember(int id) {
        return memberRepo.updateStatus(id, "BLOCKED");
    }

    public boolean unblockMember(int id) {
        return memberRepo.updateStatus(id, "ACTIVE");
    }

    public List<MemberModel> getAllMembers() {
        return memberRepo.getAllMembers();
    }

    public MemberModel getMemberById(int id) {
        return memberRepo.getMemberById(id);
    }

    public long getActiveBorrowCount(int memberId) {
        return borrowSlipRepo.getActiveBorrowCount(memberId);
    }

    public double getUnpaidFineTotal(int memberId) {
        return fineRepo.getUnpaidFineTotal(memberId);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
