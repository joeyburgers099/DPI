package interfaces;

import messaging.requestreply.RequestReply;
import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;
import model.loan.LoanRequest;

public interface IsenderGateway {
    void messageSomeOne(RequestReply reply, String correlation, String Queue);
    void messageSomeOne(LoanRequest request, String Queue);
    void messageSomeOne(BankInterestRequest request, String correlation,String Queue);
    void messageSomeOne(BankInterestReply request, String correlation, String Queue);
}