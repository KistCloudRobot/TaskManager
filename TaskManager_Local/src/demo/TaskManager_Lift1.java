package demo;

import kr.ac.uos.ai.arbi.BrokerType;
import taskManager.TaskManager;

public class TaskManager_Lift1 {

	public static void main(String[] args) {
		String role = "carrier";
		String agentID = "agent://www.mcarbi.com/AMR_LIFT1";
//		String brokerAddress = "127.0.0.1";
		String brokerAddress = "172.16.165.158";
		int port = 61116;
		
		TaskManager taskManager = new TaskManager(role, agentID, brokerAddress, brokerAddress, port);
		
	}
}
