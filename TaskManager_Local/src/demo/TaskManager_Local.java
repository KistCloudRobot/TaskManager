package demo;

import taskManager.TaskManager;

public class TaskManager_Local {

	public static void main(String[] args) {
		String role = "logisticManager";
		String agentID = "agent://www.mcarbi.com/Local2";
		String brokerAddress = "127.0.0.1";
//		String brokerAddress = "172.16.165.185";
		int port = 61314;

		TaskManager taskManager = new TaskManager(role, agentID, brokerAddress, brokerAddress, port);	
//		taskManager.onData("test", "(TaskRequest (PersonCall \"ff0c24af-6d57-4872-a6e3-fd438310468b\" (commands (command \"http://www.arbi.com/ontologies/arbi.owl#station2\" \"Storing\") (command \"http://www.arbi.com/ontologies/arbi.owl#station3\" \"Unstoring\") (command \"http://www.arbi.com/ontologies/arbi.owl#station1\" \"Storing\") (command \"http://www.arbi.com/ontologies/arbi.owl#station4\" \"PrepareUnstoring\"))))");
	}
}
