package taskManager;

import kr.ac.uos.ai.arbi.ltm.DataSource;

public class TaskManagerDataSource extends DataSource{
	private TaskManager_Local taskManager;

	public TaskManagerDataSource(TaskManager_Local taskManager){
		this.taskManager = taskManager;
	}
	
	public void onNotify(String content) {
		
		System.out.println("Notified! : "+content);
		taskManager.onNotify("LTM", content);
	}
}
