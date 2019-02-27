package model.broker;

import model.bank.BankInterestReply;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BrokerObject {
    private int numberOut;
    private List<BankInterestReply> replyList;
    private String Correlation;

    public BrokerObject(int nummer, String cor){
        replyList = new ArrayList<>();
        numberOut = nummer;
        Correlation = cor;

    }

    public boolean add(BankInterestReply reply){
        replyList.add(reply);
        System.out.println("Expected returns: " + numberOut);
        System.out.println("Current Returns: " + replyList.size());
        if(replyList.size() == numberOut){
            System.out.println("Returning true, sending to client after");
            return true;
        }
        else{
            System.out.println("Returning False, try again");
            return false;
        }
    }
    public String getCorrelation() {
        return Correlation;
    }
    public BankInterestReply getReply(){
        BankInterestReply finalReply = Collections.max(replyList, new BankInterestComp());
        return finalReply;
    }
}