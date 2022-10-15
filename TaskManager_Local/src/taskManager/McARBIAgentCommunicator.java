package taskManager;

import java.util.concurrent.BlockingQueue;

import kr.ac.uos.ai.agentCommunicationFramework.agent.ChannelFactory;
import kr.ac.uos.ai.agentCommunicationFramework.agent.Agent;
import kr.ac.uos.ai.agentCommunicationFramework.agent.communication.Channel;
import kr.ac.uos.ai.agentCommunicationFramework.channelServer.ChannelType;
import taskManager.utility.RecievedMessage;

public class McARBIAgentCommunicator extends Agent{
	

	private BlockingQueue<RecievedMessage> messageQueue;

	private boolean isTriggered = false;

	private TaskManagerDataSource dc;
	
	public static void main(String[] args) {
		
	}
	
	public McARBIAgentCommunicator(BlockingQueue<RecievedMessage> queue) {
		
		messageQueue = queue;
		
		System.out.println("mcARBI Agent start..");
		// TODO Auto-generated constructor stub
	}


	public String getConversationID() {
		return this.getConversationID();
	}

	

	public Channel createAgentChannel(String channelName, String channelType) {

		Channel channel;
		if(channelType.equals("ZeroMQ")) {
			channel = ChannelFactory.createChannel(channelName, this, ChannelType.ZeroMQ);
			return channel;
			
		} else if(channelType.equals("ActiveMQ")) {
			channel = ChannelFactory.createChannel(channelName, this, ChannelType.ActiveMQ);
			return channel;
		}
		return null;
		
	}
	
	public Channel connectAgentChannel(String channelName) {

		Channel channel;
		channel = ChannelFactory.connectChannel(channelName, this);
		return channel;
		
	}
	
	@Override
	public void onNotify(String sender, String notification) {
//		System.out.println("recieved Notify from " + sender + " : " + notification);
//		aplViewer.msgReceived(notification, sender);
		RecievedMessage msg = new RecievedMessage(sender, notification);
		messageQueue.add(msg);	
	}

	@Override
	public void onStart() {
	}


	@Override
	public String onQuery(String sender, String query) {
		//System.out.println("recieved query from " + sender + " : " + query);

		RecievedMessage msg = new RecievedMessage(sender, query);
		messageQueue.add(msg);	

		return "(result \"ok\")";
	}


	@Override
	public void onData(String sender, String data) {
		//System.out.println("recieved data from " + sender + " : " + data);
		RecievedMessage msg = new RecievedMessage(sender, data);
		messageQueue.add(msg);
	}

	@Override
	public synchronized String onRequest(String sender, String request) {
		//System.out.println("received data from " + sender + " : " + request);
		RecievedMessage msg = new RecievedMessage(sender, request);
		messageQueue.add(msg);

		return "(ok)";
	}
	


	public void addMessage(String sender,String data){
		RecievedMessage msg = new RecievedMessage(sender,data);
		messageQueue.add(msg);
	}
	
	
	
	@Override
	public String toString() {
		return "TaskManager";
	}
}
