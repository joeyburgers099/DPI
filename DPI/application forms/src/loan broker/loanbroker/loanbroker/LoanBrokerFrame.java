package loanbroker;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.*;

import javax.jms.*;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import controllers.receiverGateway;
import controllers.senderGateway;
import interfaces.IsenderGateway;
import messaging.requestreply.RequestReply;
import model.broker.BrokerObject;
import model.bank.*;
import model.loan.LoanReply;
import model.loan.LoanRequest;


public class LoanBrokerFrame extends JFrame implements Observer {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DefaultListModel<JListLine> listModel = new DefaultListModel<JListLine>();
	private JList<JListLine> list;
	private List<RequestReply<LoanRequest, String>> waitingForReply;
	private IsenderGateway sendergateway;
	private receiverGateway receivergatewayreply;
	private receiverGateway receivergatewayrequest;
	private List<BrokerObject> registeredReturns;
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES","*");
					LoanBrokerFrame frame = new LoanBrokerFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LoanBrokerFrame() {
		receivergatewayreply = new receiverGateway("ReplyToBroker");
		receivergatewayrequest = new receiverGateway("myFirstDestination");
		receivergatewayreply.addObserver(this::update);
		receivergatewayrequest.addObserver(this::update);
		sendergateway = new senderGateway();
		registeredReturns = new ArrayList<>();
		setTitle("Loan Broker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{46, 31, 86, 30, 89, 0};
		gbl_contentPane.rowHeights = new int[]{233, 23, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 7;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);
		waitingForReply = new ArrayList<>();
		list = new JList<JListLine>(listModel);
		scrollPane.setViewportView(list);
	}

	private JListLine getRequestReply(LoanRequest request){

		for (int i = 0; i < listModel.getSize(); i++){
			JListLine rr =listModel.get(i);
			if (rr.getLoanRequest() == request){
				return rr;
			}
		}

		return null;
	}

	public void add(LoanRequest loanRequest){
		listModel.addElement(new JListLine(loanRequest));
	}


	public void add(LoanRequest loanRequest, BankInterestReply bankReply){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankReply != null){
			rr.setBankReply(bankReply);
			list.repaint();
		}
	}

	private void addbrokerobject(String correlation, LoanRequest request){
		BankInterestRequest newrequest = new BankInterestRequest(request.getAmount(),request.getTime());
		int count = 0;
		if (request.getAmount() <= 100000 && request.getTime() <= 10) // to ING
		{
			count++;
			sendergateway.messageSomeOne(newrequest, correlation, "toING");
		}
		if((200000 < request.getAmount()) && (request.getAmount() < 300000) && (request.getTime() <= 20))
		{
			count++;
			sendergateway.messageSomeOne(newrequest, correlation, "toABN");
		}
		if(request.getAmount() <= 250000 && request.getTime() <=15){
			count++;
			sendergateway.messageSomeOne(newrequest, correlation, "toRABO");
		}

		if(count!=0){
			System.out.println("this is the count" + count);
			add(request);
			registeredReturns.add(new BrokerObject(count, correlation));
			waitingForReply.add(new RequestReply<>(request,correlation));
		}
		else{
			sendergateway.messageSomeOne(new RequestReply<>(request, new LoanReply(0, "Error, no banks meet criteria")), correlation, "ReplyToClient");
		}


	}
	@Override
	public void update(Observable o, Object msg) {
		try {
			if (((ObjectMessage) msg).getObject() instanceof BankInterestReply) {
				String correlation = ((ObjectMessage)msg).getJMSCorrelationID();
				System.out.println("Received message");

				for(BrokerObject object : registeredReturns) {
					if(object.getCorrelation().equals(correlation)) {
						if(object.add((BankInterestReply) ((ObjectMessage)msg).getObject()) == true){
							finalMessage(object.getReply(), correlation);
							break;
						}
						else{
							break;
						}

					}
				}


			} else if (((ObjectMessage) msg).getObject() instanceof LoanRequest) { // reply
				LoanRequest loanrequest = (LoanRequest)((ObjectMessage)msg).getObject();
				String messageid = ((ObjectMessage)msg).getJMSMessageID();
				addbrokerobject(messageid, loanrequest);
			}

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	private void finalMessage(BankInterestReply reply, String correlation){
		for (int i = 0; i < waitingForReply.size(); i++) {
			System.out.println("Checking message: " + i);
			System.out.println("Is this ok? " + waitingForReply.get(i).getReply() + " | " + correlation);
			if(waitingForReply.get(i).getReply().equals(correlation)){
				System.out.println("++ Adding message: " + i);
				add(waitingForReply.get(i).getRequest(),reply);
				LoanReply loanreply = new LoanReply(reply.getInterest(), reply.getQuoteId());
				System.out.println("Sending:" + waitingForReply.get(i).getRequest() + "AND" + loanreply + "AND" + correlation);
				sendergateway.messageSomeOne(new RequestReply<>(waitingForReply.get(i).getRequest(), loanreply), correlation, "ReplyToClient");

				break;
			}
		}
	}
}