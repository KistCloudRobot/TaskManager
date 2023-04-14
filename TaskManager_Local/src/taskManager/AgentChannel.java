package taskManager;

import java.util.concurrent.BlockingQueue;

import kr.ac.uos.ai.agentCommunicationFramework.BrokerType;
import kr.ac.uos.ai.agentCommunicationFramework.agent.Agent;
import kr.ac.uos.ai.agentCommunicationFramework.agent.communication.Channel;
import kr.ac.uos.ai.arbi.model.GLFactory;
import kr.ac.uos.ai.arbi.model.GeneralizedList;
import kr.ac.uos.ai.arbi.model.parser.ParseException;
import taskManager.utility.RecievedMessage;

public class AgentChannel extends Channel{

	private BlockingQueue<RecievedMessage> messageQueue;
	
	public AgentChannel(String channelName, Agent agent, BrokerType brokerType, BlockingQueue<RecievedMessage> queue) {
		super(channelName, agent, brokerType);
		this.messageQueue = queue;
	}
	
	@Override
	public void onData(String sender, String data) {
		GeneralizedList gl;
		try {
			gl = GLFactory.newGLFromGLString(data);
		//	System.out.println(gl.toString());
			if(gl.getName().equals("GoalResult")) {
				data = "(GoalResultReportedFrom " + gl.getExpression(0).toString() + " " + gl.getExpression(1).toString()+ " \"" + this.getChannelName() + "\" \"" + sender +"\")";
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
//		System.out.println("channel on data : " + data);
		RecievedMessage msg = new RecievedMessage(sender, data);
		messageQueue.add(msg);		
	}

	@Override
	public String onRequest(String sender, String request) {
		GeneralizedList gl;
		try {
			gl = GLFactory.newGLFromGLString(request);
			System.out.println(gl.toString() + " timestamp : " + System.currentTimeMillis());
			
			request = "(GoalRequestedFrom " + request + " \"" + this.getChannelName() + "\" \"" + sender +"\")";
		} catch (ParseException e) {
			e.printStackTrace();
		}
		RecievedMessage msg = new RecievedMessage(sender, request);
		messageQueue.add(msg);	
		return "(ok)";
	}

	@Override
	public String onQuery(String sender, String query) {
		RecievedMessage msg = new RecievedMessage(sender, query);
		messageQueue.add(msg);	
		return null;
	}

	@Override
	public String onSubscribe(String sender, String subscribe) {
		RecievedMessage msg = new RecievedMessage(sender, subscribe);
		messageQueue.add(msg);	
		return null;
	}

	@Override
	public void onUnsubscribe(String sender, String unsubscribe) {
		RecievedMessage msg = new RecievedMessage(sender, unsubscribe);
		messageQueue.add(msg);	
	}

	@Override
	public void onNotify(String sender, String data) {
		RecievedMessage msg = new RecievedMessage(sender, data);
		messageQueue.add(msg);	
	}

	@Override
	public void onSystem(String sender, String data) {
		RecievedMessage msg = new RecievedMessage(sender,data);
		messageQueue.add(msg);	
		
	}
	
}
