//#for(f: table.fields)#--
--##(f.name):#(f.label)(#(f.type))#if(!for.last)__#end#end

//{#for(f: table.fields)#--
--##(f.name):#@fieldValue(f)#if(!for.last),#end#end}
### 字段类型输出模版 ###
#define fieldValue(field)
#switch(field.type.toString())
	#case ("CHAR","VARCHAR","DATE","TIME","TIMESTAMP")#--
		--#"#(field.type)"#--
	--##default#--
		--##(field.type)#--
--##end#end