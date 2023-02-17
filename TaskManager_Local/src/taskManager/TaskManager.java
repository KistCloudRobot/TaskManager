package taskManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import kr.ac.uos.ai.agentCommunicationFramework.agent.AgentExecutor;
import kr.ac.uos.ai.agentCommunicationFramework.agent.communication.Channel;
import kr.ac.uos.ai.arbi.BrokerType;
import kr.ac.uos.ai.arbi.agent.ArbiAgent;
import kr.ac.uos.ai.arbi.agent.ArbiAgentExecutor;
import kr.ac.uos.ai.arbi.model.GLFactory;
import kr.ac.uos.ai.arbi.model.GeneralizedList;
import kr.ac.uos.ai.arbi.model.parser.ParseException;
import taskManager.aplview.APLViewer;
import taskManager.logger.TaskManagerLogger;
import taskManager.utility.CommunicationUtility;
import taskManager.utility.GLMessageManager;
import taskManager.utility.JAMUtilityManager;
import taskManager.utility.RecievedMessage;
import uos.ai.jam.Interpreter;
import uos.ai.jam.JAM;
import uos.ai.jam.parser.JAMParser;

public class TaskManager extends ArbiAgent {
	private Interpreter interpreter;
	private GLMessageManager msgManager;
	private BlockingQueue<RecievedMessage> messageQueue;
	private TaskManagerLogger taskManagerLogger;
	private MultiAgentCommunicator agentCommunicator;
	private TaskManagerDataSource dataSource;
	
	private String ENV_JMS_BROKER;
	private int brokerPort;
	private String myAgentID;
	
	private String channelHost;
	 
	private static final String TASKMANAGER_ADRESS = "agent://www.arbi.com/TaskManager";	
	private static final String TASKMANAGER_DATASOURCE_PREFIX = "ds://www.arbi.com/TaskManager";
	
//	public TaskManager_Local() {
//		
//		initAddress();
//		messageQueue = new LinkedBlockingQueue<RecievedMessage>();
//		
//		
//		ArbiAgentExecutor.execute( ENV_JMS_BROKER, AGENT_PREFIX + TASKMANAGER_ADRESS, this,BrokerType.ZEROMQ);
//		interpreter = JAM.parse(new String[] { "./TaskManagerLocalPlan/boot.jam" });
//
//		msgManager = new GLMessageManager(interpreter);
//		
//		aplViewer = new APLViewer(interpreter);
//		logger = new TaskManagerLogger(this,interpreter);
//		init();
//	}
//	
	public TaskManager(String role, String agentID, String channelHost,String brokerAddress, int port) {
		messageQueue = new LinkedBlockingQueue<RecievedMessage>();
		agentCommunicator = new MultiAgentCommunicator(messageQueue);
		dataSource = new TaskManagerDataSource(this);
		this.channelHost = channelHost;
		this.ENV_JMS_BROKER = brokerAddress;
		this.brokerPort = port;
		this.myAgentID = agentID;
		
		interpreter = JAM.parse(new String[] { "./plan/boot.jam" });
		msgManager = new GLMessageManager(interpreter);

		msgManager.assertFact("AssignedRole", role);
		msgManager.assertFact("isro:agent", agentID);
	
		init();
	}
		
	private void init() {
		ArbiAgentExecutor.execute(ENV_JMS_BROKER, brokerPort, TASKMANAGER_ADRESS, this,BrokerType.ACTIVEMQ);
		AgentExecutor.execute(channelHost, myAgentID, agentCommunicator, kr.ac.uos.ai.agentCommunicationFramework.BrokerType.ZEROMQ);
		dataSource.connect(ENV_JMS_BROKER, brokerPort, TASKMANAGER_DATASOURCE_PREFIX +TASKMANAGER_ADRESS, BrokerType.ACTIVEMQ);
		
		msgManager.assertFact("GLUtility", msgManager);
		msgManager.assertFact("Communication", new CommunicationUtility(this, dataSource));
		msgManager.assertFact("ExtraUtility", new JAMUtilityManager(interpreter));
		msgManager.assertFact("TaskManager", this);
		msgManager.assertFact("AgentCommunication", agentCommunicator);
		msgManager.assertFact("Channel", "base", agentCommunicator.getBaseChannel());
		
		taskManagerLogger = new TaskManagerLogger(this,interpreter);

		Thread t = new Thread() {
			public void run() {
				interpreter.run();
			}
		};
		
		t.start();
	}
	@Override
	public void onNotify(String sender, String notification) {
		RecievedMessage msg = new RecievedMessage(sender, notification);
		messageQueue.add(msg);	
	}

	@Override
	public void onStart() {
	}
	
	public boolean dequeueMessage() {
		if (messageQueue.isEmpty())
			return false;
		else {
			try {
				RecievedMessage message = messageQueue.take();
				GeneralizedList gl = null;
				String data = message.getMessage();
				String sender = message.getSender();

				//aplViewer.msgReceived(data, sender);
				gl = GLFactory.newGLFromGLString(data);

				//System.out.println("message dequeued : " + gl.toString());

				if (gl.getName().equals("PostGoal")) {
					//System.out.println("test");
					gl = gl.getExpression(0).asGeneralizedList();

					System.out.println("goal to post " + gl.toString());
					
					msgManager.assertGoal(gl.toString());
					
				} else if (gl.getName().equals("InitiateServicePackage")) {
					String packageName = gl.getExpression(0).toString();
					packageName = packageName.substring(1, packageName.length() - 1);
					initServicePackage(packageName);
				} else if (gl.getName().equals("update")){
					//System.out.println("assert context : " + data);
					System.out.println("update context : " + data);
					msgManager.updateContextGL(data);
				} else {
					msgManager.assertGL(data);
				}

			} catch (InterruptedException | ParseException e) {
				e.printStackTrace();
			}

			return true;
		}
	}

	@Override
	public String onQuery(String sender, String query) {
		System.out.println("recieved query from " + sender + " : " + query);

		//aplViewer.msgReceived(query, sender);

		String result = handleQuery(query);

		return "(result \"" + result + "\")";
	}

	private String handleQuery(String query) {
		String result = "success";
		try {
			GeneralizedList gl = GLFactory.newGLFromGLString(query);
			String name = gl.getName();
			if (name.equals("retractFact")) {
				System.out.println("retractFact called");
				msgManager.retractFact(gl.getExpression(0).toString());
			} else if (name.equals("updateFact")) {
				System.out.println("updateFact called");
				msgManager.updateFact(gl.getExpression(0).toString(), gl.getExpression(1).toString());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return result;
	}


	@Override
	public void onData(String sender, String data) {
		//System.out.println("recieved data from " + sender + " : " + data);
		RecievedMessage msg = new RecievedMessage(sender, data);
		messageQueue.add(msg);
	}

	@Override
	public String onRequest(String sender, String request) {
		//System.out.println("received data from " + sender + " : " + request);
		RecievedMessage msg = new RecievedMessage(sender, request);
		messageQueue.add(msg);

		return "(ok)";
	}
	

	public void initServicePackage(String packageName) {
		String retrieve = "";
		String plan = "";
		System.out.println("retrieving service package start");

		GeneralizedList gl = null;
		int i = 0;
		while (true) {
			i++;
			String data = "(ServicePackage \"" + packageName + "\" \"plan\" \"" + i + "\" $a)";

			retrieve = dataSource.retrieveFact(data);
			System.out.println("plan" + i + " retrieval");

			if (retrieve.equals("(fail)")) {
				break;
			}

			try {
				gl = GLFactory.newGLFromGLString(retrieve);
				plan = GLFactory.unescape(gl.getExpression(3).toString());
				plan = plan.substring(1, plan.length() - 1);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			JAMParser.parseString(interpreter, plan);
		}
		i = 0;

		while (true) {
			i++;

			String data = "(Context \"" + i + "\" $a)";
			retrieve = dataSource.retrieveFact(data);
			System.out.println("context retrieval : " + retrieve);

			if (retrieve.equals("(fail)")) {
				break;
			}

			try {
				gl = GLFactory.newGLFromGLString(retrieve);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String context = GLFactory.unescape(gl.getExpression(1).toString());
			context = context.substring(1, context.length() - 1);
			GeneralizedList contextGL = null;
			try {
				contextGL = GLFactory.newGLFromGLString(context);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String contextStatement = "(" + contextGL.getName();
			String tempText = "";
			
			for (int j = 0; j < contextGL.getExpressionsSize(); j++) {
				tempText = contextGL.getExpression(j).asGeneralizedList().getExpression(0).toString();
				
				tempText = tempText.substring(1, tempText.length() - 1);
				contextStatement += " " + tempText;
			}
			
			contextStatement += ")";
			String subscribeStatement = "(rule (fact (context " + contextStatement + ")) --> (notify " + contextStatement + "))";

			System.out.println("subscribe statement : " + subscribeStatement);

			String id = dataSource.subscribe(subscribeStatement);
			System.out.println("context : " + context);
		}
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		this.addMessage("dispatcher", "(PostGoal (initiation))");
		
		System.out.println("****ServicePackage Parse Completed****");
		System.out.println("****Initiated Service : " + packageName + " ****");
	}

	public void addMessage(String sender,String data){
		RecievedMessage msg = new RecievedMessage(sender,data);
		messageQueue.add(msg);
	}
	
	public GLMessageManager getMsgManager() {
		return msgManager;
	}
	
	public String toString() {
		return "TaskManager";
	}

	public static void main(String[] args) {
		String brokerAddress;
		String robotID;
		int port;
		if(args.length == 0) {
			brokerAddress = "172.16.165.141";
			robotID = "Local";
			port = 61316;
		} else {
			robotID = args[0];
			brokerAddress = args[1];
			port = Integer.parseInt(args[2]);
		}
		
		//new TaskManager("logisticManager",robotID, brokerAddress, port, BrokerType.ACTIVEMQ);
	}
}
