package taskManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import kr.ac.uos.ai.agentCommunicationFramework.agent.AgentExecutor;
import kr.ac.uos.ai.arbi.agent.ArbiAgent;
import kr.ac.uos.ai.arbi.agent.ArbiAgentExecutor;
import kr.ac.uos.ai.arbi.model.GLFactory;
import kr.ac.uos.ai.arbi.model.GeneralizedList;
import kr.ac.uos.ai.arbi.model.parser.ParseException;
import kr.ac.uos.ai.agentCommunicationFramework.channelServer.ChannelType;
import taskManager.aplview.APLViewer;
import taskManager.logger.TaskManagerLogger;
import taskManager.utility.CommunicationUtility;
import taskManager.utility.GLMessageManager;
import taskManager.utility.JAMUtilityManager;
import taskManager.utility.RecievedMessage;
import uos.ai.jam.Interpreter;
import uos.ai.jam.JAM;
import uos.ai.jam.parser.JAMParser;

public class TaskManager_Local extends ArbiAgent {
	private Interpreter interpreter;
	private GLMessageManager msgManager;
	private BlockingQueue<RecievedMessage> messageQueue;
	private TaskManagerLogger logger;
	private McARBIAgentCommunicator mcARBIAgentCommunicator;
	
	private boolean isTriggered = false;
	//private APLViewer aplViewer;
	public static String ENV_JMS_BROKER;
	public static String ENV_ROBOT_NAME;
	public static String MY_mcARBI_AGENT_ADRRESS = "agent://www.mcarbi.com/Local";
	public static final String JMS_BROKER_URL = "tcp://172.16.165.222:61313";
	public static final String TASKMANAGER_ADRESS = "www.arbi.com/TaskManager";
	public static  String CONTEXTMANAGER_ADRESS = "agent://www.arbi.com/ContextManager";
	public static final String KNOWLEDGEMANAGER_ADRESS = "agent://www.arbi.com/KnowledgeManager";
	public static  String BEHAVIOUR_INTERFACE_ADDRESS = "agent://www.arbi.com/BehaviorInterface";
	public static final String PERCEPTION_ADRESS = "agent://www.arbi.com/perception";
	public static final String ACTION_ADRESS = "agent://www.arbi.com/action";
	public static  String REASONER_ADRESS = "agent://www.arbi.com/TaskReasoner";
	public static final String PREFIX = "http://www.arbi.com/ontologies#";
	
	

	public static final String AGENT_PREFIX = "agent://";
	public static final String DATASOURCE_PREFIX = "ds://";
	private TaskManagerDataSource dc;

	
	public TaskManager_Local() {
		
		initAddress();
		messageQueue = new LinkedBlockingQueue<RecievedMessage>();
		
		
		ArbiAgentExecutor.execute( ENV_JMS_BROKER, AGENT_PREFIX + TASKMANAGER_ADRESS, this,2);
		interpreter = JAM.parse(new String[] { "./TaskManagerLocalPlan/boot.jam" });

		msgManager = new GLMessageManager(interpreter);
		
//		aplViewer = new APLViewer(interpreter);
		//logger = new TaskManagerLogger(this,interpreter);
		init();
	}
	
	public TaskManager_Local(String robotID, String brokerAddress) {
		ENV_JMS_BROKER = brokerAddress;
		messageQueue = new LinkedBlockingQueue<RecievedMessage>();
		ArbiAgentExecutor.execute(ENV_JMS_BROKER, AGENT_PREFIX + TASKMANAGER_ADRESS, this,2);
		interpreter = JAM.parse(new String[] { "./TaskManagerLocalPlan/boot.jam" });

		msgManager = new GLMessageManager(interpreter);
		
		init();
	}
	
	
	public void initAddress() {
		//ENV_JMS_BROKER = "tcp://" + System.getenv("JMS_BROKER");
		//ENV_ROBOT_NAME = System.getenv("ROBOT");
		
	
		ENV_JMS_BROKER = "tcp://172.16.165.141:61313";		
		
	}
	public void test(){
		
		if(isTriggered == false){
			System.out.println("test");
			
			messageQueue.add(new RecievedMessage("test","(WakeupService)"));
			isTriggered = true;
		}
		
	}
	
	public String justRemove(Object input) {
		
		//System.out.println("??????" + input.getClass().getSimpleName());
		String data = input.toString();
		return data.substring(1, data.length()-1);
	}
	
	private void init() {
		msgManager.assertFact("GLUtility", msgManager);
		msgManager.assertFact("Communication", new CommunicationUtility(this, dc));
		msgManager.assertFact("ExtraUtility", new JAMUtilityManager(interpreter));
		msgManager.assertFact("TaskManager", this);
		
		mcARBIAgentCommunicator = new McARBIAgentCommunicator(messageQueue);
		
		AgentExecutor.execute(MY_mcARBI_AGENT_ADRRESS, mcARBIAgentCommunicator, ChannelType.ZeroMQ);
		msgManager.assertFact("McARBIAgentCommunicator", mcARBIAgentCommunicator);
		
		//aplViewer.init();
 
		
		Thread t = new Thread() {
			public void run() {
				interpreter.run();
			}
		};
		
		t.start();
	}
	
	public String getConversationID() {
		return this.getConversationID();
	}

	@Override
	public void onNotify(String sender, String notification) {
		//System.out.println("recieved Notify from " + sender + " : " + notification);
		//aplViewer.msgReceived(notification, sender);
		RecievedMessage msg = new RecievedMessage(sender, notification);
		messageQueue.add(msg);	
	}

	@Override
	public void onStart() {
		dc = new TaskManagerDataSource(this);
		
		dc.connect(ENV_JMS_BROKER, DATASOURCE_PREFIX +TASKMANAGER_ADRESS,2);

		System.out.println("======Start Test Agent======");
		System.out.println("??");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String subscribeStatement = "(rule (fact (context $context)) --> (notify (context $context)))";
		System.out.println("??");
		dc.subscribe(subscribeStatement);
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
				} else if(gl.getName().equals("context")){
					String recievedContext = "(ContextRecieved " + gl.getExpression(0).toString() + ")";
					msgManager.assertGL(recievedContext);
				} else if(gl.getName().equals("relationChanged")) {
					String relationChanged = "(relationChanged " + gl.getExpression(0).toString() + ")";
					msgManager.assertGL(relationChanged);
				} else if (gl.getName().equals("GoalRequest")) {
					GeneralizedList goalGL = gl.getExpression(0).asGeneralizedList();
					msgManager.assertFact(goalGL.getName() + "RequestedFrom", goalGL.getExpression(0), goalGL.getExpression(1));
				} else if (gl.getName().equals("GoalReport")) {
					GeneralizedList goalGL = gl.getExpression(0).asGeneralizedList();
					msgManager.assertFact(goalGL.getName() + "ReportedFrom", sender, goalGL.getExpression(1), goalGL.getExpression(2));
				} else {
					//System.out.println("assert context : " + data);
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
			// TODO Auto-generated catch block
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

			retrieve = dc.retrieveFact(data);
			System.out.println("plan" + i + " retrieval");

			if (retrieve.equals("(fail)")) {
				break;
			}

			try {
				gl = GLFactory.newGLFromGLString(retrieve);
				plan = GLFactory.unescape(gl.getExpression(3).toString());
				plan = plan.substring(1, plan.length() - 1);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			JAMParser.parseString(interpreter, plan);
		}
		i = 0;

		while (true) {
			i++;

			String data = "(Context \"" + i + "\" $a)";
			retrieve = dc.retrieveFact(data);
			System.out.println("context retrieval : " + retrieve);

			if (retrieve.equals("(fail)")) {
				break;
			}

			try {
				gl = GLFactory.newGLFromGLString(retrieve);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String context = GLFactory.unescape(gl.getExpression(1).toString());
			context = context.substring(1, context.length() - 1);
			GeneralizedList contextGL = null;
			try {
				contextGL = GLFactory.newGLFromGLString(context);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String contextStatement = "(" + contextGL.getName();
			String tempText = "";
			
			for (int j = 0; j < contextGL.getExpressionsSize(); j++) {
				tempText = contextGL.getExpression(j).asGeneralizedList().getExpression(0).toString();
				System.out.println(tempText);
				tempText = tempText.substring(1, tempText.length() - 1);
				contextStatement += " " + tempText;
			}
			
			contextStatement += ")";
			String subscribeStatement = "(rule (fact (context " + contextStatement + ")) --> (notify " + contextStatement + "))";

			System.out.println("subscribe statement : " + subscribeStatement);

			String id = dc.subscribe(subscribeStatement);
			System.out.println("context : " + context);
		}
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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
		if(args.length == 0) {
			brokerAddress = "tcp://172.16.165.141:61313";
			robotID = "Local";	
		} else {
			robotID = args[0];
			brokerAddress = args[1];
		}
		
		new TaskManager_Local(robotID, brokerAddress);
	}
}
