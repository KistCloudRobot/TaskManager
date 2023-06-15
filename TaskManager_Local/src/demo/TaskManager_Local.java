package demo;

import kr.ac.uos.ai.arbi.BrokerType;
import taskManager.TaskManager;

public class TaskManager_Local {

	public static void main(String[] args) {
		String role = "logisticManager";
		String agentID = "agent://www.mcarbi.com/Local";
//		String brokerAddress = "127.0.0.1";
		String brokerAddress = "172.16.165.185";
		int port = 61316;

		TaskManager taskManager = new TaskManager(role, agentID, brokerAddress, brokerAddress, port);		
	}
}
