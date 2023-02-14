package taskManager.utility;

import kr.ac.uos.ai.arbi.agent.ArbiAgent;
import kr.ac.uos.ai.arbi.model.GLFactory;
import kr.ac.uos.ai.arbi.model.GeneralizedList;
import kr.ac.uos.ai.arbi.model.parser.ParseException;
import taskManager.TaskManager;
import taskManager.TaskManagerDataSource;

public class CommunicationUtility {
	private ArbiAgent taskManager;
	private RequestManager rm;
	private TaskManagerDataSource ds;
	
	public CommunicationUtility(ArbiAgent agent,TaskManagerDataSource ds) {
		taskManager = agent;
		rm = new RequestManager();
		this.ds = ds;
		
	}

	public void assertToLTM(String data) {
		ds.assertFact(data);
	}
	
	public void retractFromLTM(String data) {
		String result = ds.retractFact(data);
	}
	public void inform(String receiver, String content) {
		taskManager.send(receiver, content);
	}	
	
	public void unsubscribe(String receiver, String content) {
		System.out.println("unsubscribe : " + receiver + " " + content);
		taskManager.unsubscribe(receiver, content);
	}

	public String sendQuery(String receiver, String content) {
		//System.out.println("query : " + receiver + " " + content);
		String result = "";
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = taskManager.query(receiver, content);

		//System.out.println("query result :" + result);
		return result;

	}

	public String request(String receiver, String content) {
		//System.out.println("Request : " + receiver + " " + content);
		String result = "";
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = taskManager.request(receiver, content);
		//System.out.println("result : " + result);

		return result;
	}

	public void test() {
		System.out.println("testPrint");

	}

	public void setResponse(String response) {
		rm.setResponse(response);
	}
	
	public void subscribe(String receiver,String content){
		taskManager.subscribe(receiver, content);
	}
	public String subscribeToLTM(String content){
		String result = ds.subscribe(content);
		return result;
	}
	
	
	public void updateToLTM(String content) {
		//System.out.println("==== updateFact To LTM : " + content + " ====");
		ds.updateFact(content);
	}
	
	public String toString(){
		return "CommunicationUtility";
	}
}
