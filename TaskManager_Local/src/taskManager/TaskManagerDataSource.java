package taskManager;

import kr.ac.uos.ai.arbi.ltm.DataSource;

public class TaskManagerDataSource extends DataSource{
	private TaskManager taskManager;

	public TaskManagerDataSource(TaskManager taskManager){
		this.taskManager = taskManager;
	}
	
	public void onNotify(String content) {
		taskManager.onNotify("LTM", content);
	}
}
