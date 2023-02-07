package taskManager.logger.argument;



public class RelationArgument {
	private String name;
	private String[] expressionList;
	
	
	public RelationArgument(int i){
		name = "";
		expressionList = new String[i];
	}
	

	public String[] getExpresisonList(){
		return expressionList;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
	
	public String toString(){
		StringBuilder result = new StringBuilder(name);
		if(expressionList.length  > 0) {
			result.append("(");
			for(String exp : expressionList){
				result.append(exp).append(",");
			}
			result = new StringBuilder(result.substring(0,result.length()-1)).append(")");
		}
		
		
		return result.toString();
	}
	
}
