package demo;

import taskManager.TaskManager;

public class TaskManager_MainAssembler {

	public static void main(String[] args) {
		String role = "MainAssembler";
		String agentID = "agent://www.mcarbi.com/MainAssembler";
		String brokerAddress = "127.0.0.1";
//		String brokerAddress = "172.16.165.164";
		
		int port = 61116;
		
		TaskManager taskManager = new TaskManager(role, agentID, brokerAddress, brokerAddress, port);
		
	}
}
