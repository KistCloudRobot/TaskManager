package demo;

import taskManager.TaskManager;

public class TaskManager_Docker {
	public static void main(String[] args) {
		String role = System.getenv("ROLE");
		String agentID = System.getenv("AGENT_ID");
		String channelAddress = System.getenv("BASE_CHANNEL_ADDRESS");
		String brokerAddress = System.getenv("BROKER_ADDRESS");
		String stringPort = System.getenv("BROKER_PORT");
		int port = Integer.parseInt(stringPort);
		
		TaskManager taskManager = new TaskManager(role, agentID, channelAddress, brokerAddress, port);
		
	}
}
