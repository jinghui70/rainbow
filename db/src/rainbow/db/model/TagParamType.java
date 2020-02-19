package rainbow.db.model;

public enum TagParamType {
	
	NONE, // 无参数
	STRING, // 有参数
	TABLE, // 指向其它表，如果是字段Tag，认为是一个Link
}
