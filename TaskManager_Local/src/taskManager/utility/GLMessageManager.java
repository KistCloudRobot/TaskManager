package taskManager.utility;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import kr.ac.uos.ai.arbi.model.GLFactory;
import kr.ac.uos.ai.arbi.model.GeneralizedList;
import kr.ac.uos.ai.arbi.model.parser.ParseException;
import taskManager.TaskManagerDataSource;
import uos.ai.jam.Goal;
import uos.ai.jam.Interpreter;
import uos.ai.jam.exception.AgentRuntimeException;
import uos.ai.jam.expression.Binding;
import uos.ai.jam.expression.Expression;
import uos.ai.jam.expression.Relation;
import uos.ai.jam.expression.Value;
import uos.ai.jam.expression.ValueLong;
import uos.ai.jam.plan.action.AchieveGoalAction;

public class GLMessageManager {
	public Interpreter interpreter;
//	private Queue<String> messageQueue;

	public String findAllocation(String gl1, String gl2, int i) {
		String result = "";
		
		try {
			GeneralizedList glAlloc = GLFactory.newGLFromGLString(gl1);
			GeneralizedList glRe = GLFactory.newGLFromGLString(gl2);
			
			String goalID = glAlloc.getExpression(i).asGeneralizedList().getExpression(0).asValue().stringValue();
			for (int j =0; j < glRe.getExpressionsSize(); j++) {
				String id = glRe.getExpression(j).asGeneralizedList().getExpression(0).asValue().stringValue();
				if (id.equals(goalID)) {
					result = glRe.getExpression(j).asGeneralizedList().getExpression(1).asValue().stringValue();
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		return result;
	}
	
	public static void main(String[] ar) {
		
		GLMessageManager manager = new GLMessageManager();
//		String str = "\"(goal (metadata &quot;Local1&quot;) &quot;PalletTransported&quot; (argument &quot;http://www.arbi.com/ontologies/arbi.owl#pallet_02&quot; &quot;http://www.arbi.com/ontologies/arbi.owl#station3&quot; &quot;http://www.arbi.com/ontologies/arbi.owl#station48&quot;))\"";
//		str = manager.removeQuotationMarks(str);
//		str = manager.unescapeGL(str);
		String str1 = "(TaskAllocation (PalletTransported \"Local1\" \"http://www.arbi.com/ontologies/arbi.owl#station1001\") (PalletTransported \"Local2\" \"http://www.arbi.com/ontologies/arbi.owl#station1002\") (PalletTransported \"Local3\" \"http://www.arbi.com/ontologies/arbi.owl#station1004\"))";
		String str2 = "(AgentRecommended (Allocation \"Local3\" \"AMR_LIFT5\") (Allocation \"Local2\" \"AMR_LIFT6\") (Allocation \"Local1\" \"AMR_LIFT7\"))";
		System.out.println(manager.findAllocation(str1, str2, 0));
		System.out.println(manager.findAllocation(str1, str2, 1));
		System.out.println(manager.findAllocation(str1, str2, 2));
		System.out.println(manager.findAllocation(str1, str2, 3));
	}

	public int toInteger(String input) {
		double doub = Double.parseDouble(input);
		int result = (int) doub;

		return result;
	}
	

	public String contains(String input, String text) {
		if(input.contains(text)) {
			return "true";
		}
		
		return "false";
	}
//	
//	public void dequeueMessage() {
//
//		if (messageQueue.isEmpty() == false) {
//			String message = messageQueue.poll();
//			GeneralizedList gl = null;
//			try {
//				gl = GLFactory.newGLFromGLString(message);
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			if (gl.getName().equals("PostGoal")) {
//				if (gl.getExpressionsSize() == 1)
//					this.postGoal(gl.getExpression(0).toString(), "100");
//				else {
//					this.postGoal(gl.getExpression(0).toString(), gl.getExpression(1).toString());
//				}
//			} else if (gl.getName().equals("AssertFact")) {
//				this.assertGL(gl.getExpression(0).toString());
//			}
//		}
//
//	}

	public void removePlan(String planID) {
		interpreter.getPlanLibrary().removePlan(planID);
	}

	public GLMessageManager(Interpreter interpreter) {
		this.interpreter = interpreter;
//		messageQueue = new LinkedList<String>();
	}

	public GLMessageManager() {
	}

	public String changeName(String input, String newName) {
		//System.out.println("name Changed : " + input);

		String[] splitResult = input.split(" ");
		String result = splitResult[0].substring(1, splitResult[0].length());

		for (int i = 1; i < splitResult.length; i++) {
			input += " " + removeQuotationMarks(splitResult[i]);
		}

		return input;
	}

//	public String retrieveElement(String input) {
//
//		GeneralizedList list = null;
//		String result = "";
//		try {
//			list = GLFactory.newGLFromGLString(input);
//
//			for (int i = 0; i < list.getExpressionsSize(); i++) {
//				result += removeQuotationMarks(list.getExpression(i).toString()) + ", ";
//			}
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return result;
//	}
//	
//	
	public String removeQuotationMarks(Object input) {
		String data = input.toString();
		if (data.startsWith("\"")) {
			data = data.substring(1, data.length() - 1);
		}
		return data;
	}
	
	public String retrieveTime(String input) {

		String[] resultList = input.split("T");

		resultList = resultList[1].split(":");

		String result = resultList[0].substring(1, resultList[0].length());
		result = result + "�떆";

		return result;

	}
	
	public String retrieveGLName(String glString) {
		String result = "";
		try {
			GeneralizedList gl = GLFactory.newGLFromGLString(glString);
			result = gl.getName();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		return result;
	}
	
	
	public int retrieveExpressionSize(String expression) {
		int result = 0;

		try {
			GeneralizedList gl = GLFactory.newGLFromGLString(expression);
			result = gl.getExpressionsSize();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public int getUtility(int value) {
		System.out.println("utility value : " + value);

		return value;
	}

//	public String postGoal(String goal, String utility) {
//		String result = null;
//		GeneralizedList gl = null;
//		
//		try {
//			gl = GLFactory.newGLFromGLString(goal);
//			String name = gl.getName();
//			List<Expression> expList = new ArrayList<Expression>();
//
//			for (int i = 0; i < gl.getExpressionsSize(); i++) {
//				String expressionValue = gl.getExpression(i).toString();
//				expressionValue = this.removeQuotationMarks(expressionValue);
//				System.out.println("=======expression Value : " + expressionValue);
//				if(expressionValue.startsWith("$")) {
//					expressionValue = "null";
//				}
//				expList.add(new Value(expressionValue));
//			}
//
//			Relation r = interpreter.getWorldModel().newRelation(name, expList);
//			int utilityValue = 0;
//			if(!utility.equals("")) {
//				Integer.valueOf(removeQuotationMarks(utility));	
//			}
//
//			AchieveGoalAction generatedGoal = new AchieveGoalAction(name, r, new Value(utilityValue), null, null,
//					interpreter);
//			interpreter.getIntentionStructure().addUnique(generatedGoal, null, null);
//			System.out.println("achieve goal posted : " + r.toString());
//		} catch (ParseException e) {
//			e.printStackTrace();
//		} catch (AgentRuntimeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return result;
//	}
//
//	public String unpostGoal(String goal) {
//
//		String result = null;
//		GeneralizedList gl = null;
//
//		try {
//			gl = GLFactory.newGLFromGLString(goal);
//			String name = gl.getName();
//			List<Expression> expList = new ArrayList<Expression>();
//
//			for (int i = 0; i < gl.getExpressionsSize(); i++) {
//				String expressionValue = gl.getExpression(i).toString();
//				expList.add(new Value(expressionValue));
//			}
//
//			Relation r = interpreter.getWorldModel().newRelation(name, expList);
//
//			AchieveGoalAction generatedGoal = new AchieveGoalAction(name, r, new Value(0), null, null, interpreter);
//
//			interpreter.getIntentionStructure().drop(generatedGoal, null);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//
//		return result;
//	}
	/*
	 * public String switchUtility(String goal1,String goal2){ String result = null;
	 * GeneralizedList gl = null;
	 * 
	 * try { gl = GLFactory.newGLFromGLString(goal1); String name = gl.getName();
	 * List<Expression> expList = new ArrayList<Expression>();
	 * 
	 * for(int i = 0; i < gl.getExpressionsSize();i++){ String expressionValue =
	 * gl.getExpression(i).toString(); expList.add(new Value(expressionValue)); }
	 * Relation r = interpreter.getWorldModel().newRelation(name, expList);
	 * AchieveGoalAction generatedGoal1 = new AchieveGoalAction(name, r, new
	 * ValueLong(0), null, null, interpreter);
	 * 
	 * gl = GLFactory.newGLFromGLString(goal2); name = gl.getName(); expList = new
	 * ArrayList<Expression>();
	 * 
	 * for(int i = 0; i < gl.getExpressionsSize();i++){ String expressionValue =
	 * gl.getExpression(i).toString(); expList.add(new Value(expressionValue)); } r
	 * = interpreter.getWorldModel().newRelation(name, expList); AchieveGoalAction
	 * generatedGoal2 = new AchieveGoalAction(name, r, new ValueLong(0), null, null,
	 * interpreter);
	 * 
	 * 
	 * Goal foundGoal1 =
	 * interpreter.getIntentionStructure().searchGoal(generatedGoal1, null); Goal
	 * foundGoal2 = interpreter.getIntentionStructure().searchGoal(generatedGoal2,
	 * null); Expression tempUtility = foundGoal1.getGoalAction().getUtility();
	 * 
	 * System.out.println("goal swapped");
	 * 
	 * System.out.println(foundGoal1.getGoalAction().getName() + " " +
	 * foundGoal1.getGoalAction().getUtility().toString());
	 * System.out.println(foundGoal2.getGoalAction().getName() + " " +
	 * foundGoal2.getGoalAction().getUtility().toString());
	 * 
	 * foundGoal1.getGoalAction().setUtility(foundGoal2.getGoalAction().getUtility()
	 * ); foundGoal2.getGoalAction().setUtility(tempUtility);
	 * 
	 * System.out.println(foundGoal1.getGoalAction().getName() + " " +
	 * foundGoal1.getGoalAction().getUtility().toString());
	 * System.out.println(foundGoal2.getGoalAction().getName() + " " +
	 * foundGoal2.getGoalAction().getUtility().toString());
	 * 
	 * } catch (ParseException e) { e.printStackTrace(); }
	 * 
	 * return result; }
	 * 
	 * 
	 * public String setUtility(String goal,String utility){ String result = null;
	 * GeneralizedList gl = null;
	 * 
	 * try { gl = GLFactory.newGLFromGLString(goal); String name = gl.getName();
	 * List<Expression> expList = new ArrayList<Expression>();
	 * 
	 * for(int i = 0; i < gl.getExpressionsSize();i++){ String expressionValue =
	 * gl.getExpression(i).toString(); expList.add(new Value(expressionValue)); }
	 * 
	 * Relation r = interpreter.getWorldModel().newRelation(name, expList); long
	 * utilityValue = Long.valueOf(this.removeQuotationMarks(utility));
	 * AchieveGoalAction generatedGoal = new AchieveGoalAction(name, r, new
	 * ValueLong(utilityValue), null, null, interpreter);
	 * 
	 * Goal foundGoal =
	 * interpreter.getIntentionStructure().searchGoal(generatedGoal, null);
	 * 
	 * foundGoal.getGoalAction().setUtility(new Value(utility));
	 * 
	 * System.out.println(foundGoal.getGoalAction().getName() + " " +
	 * foundGoal.getGoalAction().getUtility().toString()); } catch (ParseException
	 * e) { e.printStackTrace(); }
	 * 
	 * return result; }
	 * 
	 */
//
//	public void startGoal(String arg) {
//		try {
//
//			GeneralizedList gl = GLFactory.newGLFromGLString(arg);
//			String goal = "(ServicePerformed ";
//
//			for (int i = 0; i < gl.getExpressionsSize(); i++) {
//				goal += gl.getExpression(i).toString() + " ";
//			}
//
//			goal = goal.substring(0, goal.length() - 1);
//
//			goal += ")";
//
//			this.postGoal(goal, "100");
//
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//	
//	

	public String retrieveGLExpression(String input, int i) {
		String result = "";
		
		//System.out.println("why? : " + input);
		if (input.startsWith("\"")) {
			input = removeQuotationMarks(input);
		}
		try {
			GeneralizedList gl = GLFactory.newGLFromGLString(input);

			if(gl.getExpression(i).isValue()) {

				result = gl.getExpression(i).asValue().stringValue();
			} else if (gl.getExpression(i).isGeneralizedList()) {
				result = gl.getExpression(i).asGeneralizedList().toString();
			}
				

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//result = this.removeQuotationMarks(result);
		return result;
	}

	public String retrieveNonGLExpression(String input, int i) {
		String result = "";
		result = input.split(" ")[i];

		return result;
	}

	public String retrieveGLGoal(String input) {
		String result = "";

		try {
			GeneralizedList gl = GLFactory.newGLFromGLString(input);
			result = gl.getName();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public void assertFact(String name, Object... args) {
		interpreter.getWorldModel().assertFact(name, args);
	}

	public void retractFact(String expression) {

		GeneralizedList gl = null;
		try {
			gl = GLFactory.newGLFromGLString(expression);
			String name = gl.getName();
			List<Expression> expList = new ArrayList<Expression>();

			for (int i = 0; i < gl.getExpressionsSize(); i++) {
				String expressionValue = gl.getExpression(i).toString();
				expList.add(new Value(expressionValue));
			}

			Relation r = interpreter.getWorldModel().newRelation(name, expList);
			interpreter.getWorldModel().retract(r, null);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	public void assertGL(String input) {
		String name = "";

		if (input.startsWith("(")) {

			try {
				GeneralizedList gl = GLFactory.newGLFromGLString(input);
				name = gl.getName();
				Object[] expressionList = new Object[gl.getExpressionsSize()];

				for (int i = 0; i < gl.getExpressionsSize(); i++) {
					if (gl.getExpression(i).isGeneralizedList()) {
						String glString = gl.getExpression(i).toString();
						expressionList[i] = GLFactory.unescape(glString);
					} else {
						kr.ac.uos.ai.arbi.model.Value value = gl.getExpression(i).asValue();
						if (value.getType() == kr.ac.uos.ai.arbi.model.Value.Type.FLOAT) {
							expressionList[i] = value.floatValue();
						} else if (value.getType() == kr.ac.uos.ai.arbi.model.Value.Type.INT) {
							expressionList[i] = value.intValue();
						} else if (value.getType() == kr.ac.uos.ai.arbi.model.Value.Type.STRING) {
							String glString = value.stringValue();
							expressionList[i] = GLFactory.unescape(glString);
						} else {
							String glString = value.stringValue();
							expressionList[i] = GLFactory.unescape(glString);
						}
					}

				}

				assertFact(name, expressionList);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void assertGoal(String input) {
		GeneralizedList gl = null;

		try {
			gl = GLFactory.newGLFromGLString(input);
			String name = gl.getName();

			if (name.contains(":")) {

				String goalName = gl.getName().split(":")[1];
				String prefix = gl.getName().split(":")[0];

				name = prefix + ":Post" + goalName;
			}else {
				name = "Post" + name;
			}
			String[] expressionList = new String[gl.getExpressionsSize()];

			for (int i = 0; i < gl.getExpressionsSize(); i++) {
				
				//System.out.println("=======expression Value : " + gl.getExpression(i).toString());
				expressionList[i] = removeQuotationMarks(gl.getExpression(i).toString());
				if(expressionList[i].startsWith("$")) {
					expressionList[i] = "";
					
				}
				//expressionList[i] = GLFactory.unescape(expressionList[i]);
				//expressionList[i] = this.removeQuotationMarks(expressionList[i]);
			}
			
		//	System.out.println("?????" + expressionList[0]);

			//System.out.println("gl message assert goal");
			assertFact(name, expressionList);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void updateContextGL(String context) {
		String oldContext = "";
		if (context.startsWith("(")) {
			try {
				GeneralizedList gl = GLFactory.newGLFromGLString(context);
				oldContext = "(" + gl.getName();
				for(int i = 0; i < gl.getExpressionsSize(); i++) {
					oldContext = oldContext + " $v";
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		oldContext = oldContext + ")";
		updateFact(oldContext, context);
	}
	
	public void updateFact(String string, String string2) {
		String name = "";

		if (string.startsWith("(")) {

			try {
				GeneralizedList gl = GLFactory.newGLFromGLString(string);
				name = gl.getName();
				gl = GLFactory.newGLFromGLString(string2);

				String[] expressionList = new String[gl.getExpressionsSize()];

				for (int i = 0; i < gl.getExpressionsSize(); i++) {
					expressionList[i] = gl.getExpression(i).toString();
				}

				this.retractFact(string);
				assertFact(name, expressionList);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String escapeGL(String gl) {
//		System.out.println("start escape : " + gl);
		String result = GLFactory.escape(gl);
		System.out.println(result);
		return result;
	}
	public String unescapeGL(Object input) {

//		System.out.println("unescape GL ?????? " + input.getClass().getSimpleName());
		String gl = input.toString();
		
		return GLFactory.unescape(gl);
	}
}
