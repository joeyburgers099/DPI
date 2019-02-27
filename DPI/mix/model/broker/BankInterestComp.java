package model.broker;

import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;

import java.util.Comparator;

public class BankInterestComp implements Comparator<BankInterestReply> {
    @Override
    public int compare(BankInterestReply a, BankInterestReply b) {
        if (a.getInterest() < b.getInterest())
            return -1; // highest value first
        if (a.getInterest() == b.getInterest())
            return 0;
        return 1;
    }
}