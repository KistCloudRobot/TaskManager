package taskManager.logger;

import kr.ac.uos.ai.arbi.agent.logger.LogTiming;
import kr.ac.uos.ai.arbi.agent.logger.LoggerManager;
import kr.ac.uos.ai.arbi.agent.logger.action.AgentAction;
import taskManager.TaskManager;
import taskManager.logger.*;
import taskManager.logger.argument.GoalArgument;
import taskManager.logger.argument.RelationArgument;
import uos.ai.jam.Goal;
import uos.ai.jam.IntentionStructureChangeListener;
import uos.ai.jam.Interpreter;
import uos.ai.jam.WorldModelChangeListener;
import uos.ai.jam.expression.Binding;
import uos.ai.jam.expression.Expression;
import uos.ai.jam.expression.Relation;
import uos.ai.jam.expression.Variable;
import uos.ai.jam.plan.APLElement;
import uos.ai.jam.plan.action.GoalAction;


public class TaskManagerLogger implements WorldModelChangeListener, IntentionStructureChangeListener {

	private AssertFactLogger assertLogger;
	private AgentAction assertAction;
	private AgentAction retractAction;
	private AgentAction newGoalAction;
	private AgentAction unpostGoalAction;
	private AgentAction intendAction;
	private TaskManager taskManager;
	private Interpreter interpreter;
	
	public TaskManagerLogger(TaskManager taskManager,Interpreter interpreter){
		this.taskManager = taskManager;
		this.interpreter = interpreter;
		init();
	}
	
	
	
	public void init(){
		assertAction = new AgentAction("AssertWorldModel",new AssertFactLogger());
		retractAction = new AgentAction("RetractWorldModel",new RetractFactLogger());
		newGoalAction = new AgentAction("PostGoal",new NewGoalLogger());
		unpostGoalAction = new AgentAction("UnpostGoal",new UnpostGoalLogger());
		intendAction = new AgentAction("IntendGoal",new IntendLogger());
		
		LoggerManager loggerManager = LoggerManager.getInstance();
		
		loggerManager.registerAction(intendAction,LogTiming.NonAction);
		loggerManager.registerAction(newGoalAction,LogTiming.NonAction);
		loggerManager.registerAction(unpostGoalAction, LogTiming.NonAction);
		loggerManager.registerAction(assertAction,LogTiming.NonAction);
		loggerManager.registerAction(retractAction,LogTiming.NonAction);
		//System.out.println("inited");
		
		interpreter.getWorldModelManager().getWorldModel().addChangeListener(this);
		interpreter.getIntentionStructure().addChangeListener(this);
	}
	

	@Override
	public void worldModelChanged(Relation[] retracted, Relation asserted) {
		if (asserted != null) {
			if (!asserted.getName().equals("CURRENT_TIME") && !asserted.getName().equals("APL")) {
				//System.out.println("notified WorldModelChange asserted :" + asserted.getName());
				RelationArgument argument = generateRelationArgument(asserted);
	
				assertAction.execute(argument);
			}
		}
		if (retracted != null) {
			for (int i = 0; i < retracted.length; i++) {
				if (!retracted[i].getName().equals("CURRENT_TIME")){
					//System.out.println("notified WorldModelChange Retracted : " + retracted[i].getName());
					RelationArgument argument = generateRelationArgument(retracted[i]);
					
					retractAction.execute(argument);
				}
			}
		}
	}
	
	public RelationArgument generateRelationArgument(Relation argument){
		RelationArgument newArgument = new RelationArgument(argument.getArity());
		newArgument.setName(argument.getName());
		
		for(int i = 0; i < argument.getArity();i++){
			
			newArgument.getExpresisonList()[i] = argument.getArg(i).toString();
		}
		
		return newArgument;
	}
	
	public GoalArgument generateGoalArgument(APLElement g){
		GoalAction action = g.getFromGoal().getGoalAction();
		Binding b = g.getBinding();
		Variable[] variables = b.getVariables();
		if(action == null) {
			return null;
		}else {
			GoalArgument newArgument = new GoalArgument();
			newArgument.setName(g.getFromGoal().getName());

			Relation goalRelation = g.getFromGoal().getGoalAction().getGoal();
			for(int i = 0; i < goalRelation.getArity();i++){
				Expression e = goalRelation.getArg(i);
				if (e.isVariable()) {
					Expression v = null;
					for(Variable var : variables) {
						if (var.getName().equals(e.toString())) {
							v = b.getValue(var);
							break;
						}
					}
					if (v == null) {
						newArgument.addExpression(e.toString());
					} else newArgument.addExpression(v.toString());
				} else {
					newArgument.addExpression(e.toString());
				}
			}
			return newArgument;
		}
	}
	
	public GoalArgument generateNewGoalArgument(Goal goal){
		GoalAction action = goal.getGoalAction();
		if(action == null) {
			return null;
		}else {
			GoalArgument newArgument = new GoalArgument();
			newArgument.setName(goal.getName());

			Relation goalRelation = goal.getGoalAction().getGoal();
			for(int i = 0; i < goalRelation.getArity();i++){
				Expression e = goalRelation.getArg(i);
				newArgument.addExpression(e.toString());	
			}
			
			return newArgument;
		}
		
	}
	
	@Override
	public void goalAdded(Goal goal) {
		//System.out.println("notified New Goal " + goal.getGoalAction());
		GoalArgument ga = generateNewGoalArgument(goal);
		if(ga != null)
			newGoalAction.execute(ga);
	}

	@Override
	public void goalRemoved(APLElement goal) {
		//System.out.println("notified Goal Removal");
		GoalArgument ga = generateGoalArgument(goal);
		if(ga != null)
			unpostGoalAction.execute(ga);
	}

	


	@Override
	public void intended(APLElement goal) {
		// TODO Auto-generated method stub
		GoalArgument ga = generateGoalArgument(goal);
		if(ga != null)
			intendAction.execute(ga);
	}

}
