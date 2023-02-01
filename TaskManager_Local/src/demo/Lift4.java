package demo;

import kr.ac.uos.ai.arbi.BrokerType;
import taskManager.TaskManager;

public class Lift4 {

	public static void main(String[] args) {
		String role = "carrier";
		String agentID = "agent://www.mcarbi.com/AMR_LIFT4";
		String brokerAddress = "127.0.0.1";
		int port = 61113;
		BrokerType brokerType = BrokerType.ACTIVEMQ;
		
		TaskManager taskManager = new TaskManager(role, agentID, brokerAddress, port, brokerType);
		
	}
}
