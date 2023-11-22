package demo;

import taskManager.TaskManager;

public class TaskManager_AssemblyManager {

	public static void main(String[] args) {
		String role = "AssemblyManager";
		String agentID = "agent://www.mcarbi.com/AssemblyManager";
		String brokerAddress = "127.0.0.1";
//		String brokerAddress = "10.201.217.97";
		
		int port = 61316;
		
		TaskManager taskManager = new TaskManager(role, agentID, brokerAddress, brokerAddress, port);
		
	}
}
