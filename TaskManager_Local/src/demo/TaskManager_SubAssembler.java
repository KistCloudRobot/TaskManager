package demo;

import taskManager.TaskManager;

public class TaskManager_SubAssembler {

	public static void main(String[] args) {
		String role = "SubAssembler";
		String agentID = "agent://www.mcarbi.com/SubAssembler";
		String brokerAddress = "127.0.0.1";
//		String brokerAddress = "172.16.165.164";
		
		int port = 61115;
		
		TaskManager taskManager = new TaskManager(role, agentID, brokerAddress, brokerAddress, port);
		
	}
}
