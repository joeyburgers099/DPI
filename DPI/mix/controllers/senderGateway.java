package controllers;

import interfaces.IsenderGateway;
import messaging.requestreply.RequestReply;
import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;
import model.loan.LoanRequest;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class senderGateway implements IsenderGateway {
    Connection connection; // to connect to the ActiveMQ
    Session session; // session for creating messages, producers and

    Destination sendDestination; // reference to a queue/topic destination
    MessageProducer producer; // for sending messages
    public void senderGateway(){

    }
    public void messageSomeOne(BankInterestReply request, String correlation, String Queue){

        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

            props.put(("queue." + Queue), Queue);

            Context jndiContext = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext
                    .lookup("ConnectionFactory");
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);


            sendDestination = (Destination) jndiContext.lookup(Queue);
            producer = session.createProducer(sendDestination);


            ObjectMessage msg = session.createObjectMessage(request);
            msg.setJMSCorrelationID(correlation);

            producer.send(msg);

        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }
    public void messageSomeOne(BankInterestRequest request, String correlation, String Queue){

        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,					                  "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

            props.put(("queue." + Queue), Queue);

            Context jndiContext = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext
                    .lookup("ConnectionFactory");
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);


            sendDestination = (Destination) jndiContext.lookup(Queue);
            producer = session.createProducer(sendDestination);


            ObjectMessage msg = session.createObjectMessage(request);
            msg.setJMSCorrelationID(correlation);

            producer.send(msg);

        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }
    public void messageSomeOne(RequestReply reply, String correlation, String Queue){
        Connection connection; // to connect to the ActiveMQ
        Session session; // session for creating messages, producers and

        Destination sendDestination; // reference to a queue/topic destination
        MessageProducer producer; // for sending messages

        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,					                  "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

            // connect to the Destination called “myFirstChannel”
            // queue or topic: “queue.myFirstDestination” or “topic.myFirstDestination”
            props.put(("queue." + Queue), Queue);

            Context jndiContext = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext
                    .lookup("ConnectionFactory");
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // connect to the sender destination
            sendDestination = (Destination) jndiContext.lookup(Queue);
            producer = session.createProducer(sendDestination);


            ObjectMessage msg = session.createObjectMessage(reply);
            msg.setJMSCorrelationID(correlation);
            // send the message
            producer.send(msg);

        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }
    public void messageSomeOne(LoanRequest request, String Queue){
        Connection connection; // to connect to the ActiveMQ
        Session session; // session for creating messages, producers and

        Destination sendDestination; // reference to a queue/topic destination
        MessageProducer producer; // for sending messages

        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

            // connect to the Destination called “myFirstChannel”
            // queue or topic: “queue.myFirstDestination” or “topic.myFirstDestination”
            props.put(("queue." + Queue), Queue);

            Context jndiContext = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext
                    .lookup("ConnectionFactory");
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // connect to the sender destination
            sendDestination = (Destination) jndiContext.lookup(Queue);
            producer = session.createProducer(sendDestination);

            String body = "Hello, this is my first message!"; //or serialize an object!
            // create a text message
            ObjectMessage msg = session.createObjectMessage(request);
            // send the message
            producer.send(msg);

        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }
}