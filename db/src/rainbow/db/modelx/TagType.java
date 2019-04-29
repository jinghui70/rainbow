package rainbow.db.modelx;

public enum TagType {

	FLAG, // 仅仅就是一个标记
	STRING, // 保存有一个字符串值
	LIST, // 保存有一个字符串值，取自一个列表的项之value，列表内容以 { value，label } 格式保存
	TABLE, // 保存一个字符串，是指向的某个表名
	FIELD // 保存一个对象 { table: tableName, field: fieldName }
}
