package demo;

import taskManager.TaskManager;

public class TaskManager_PartsManager {

	public static void main(String[] args) {
		String role = "PartsManager";
		String agentID = "agent://www.mcarbi.com/PartsManager";
		String brokerAddress = "127.0.0.1";
//		String brokerAddress = "172.16.165.164";
		
		int port = 61114;
		
		TaskManager taskManager = new TaskManager(role, agentID, brokerAddress, brokerAddress, port);
		
	}
}
