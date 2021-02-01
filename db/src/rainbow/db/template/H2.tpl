#if(table!=null) ### 单表处理
#@createtable(table)
#else
#if(drop) ### 全模型处理，drop表示是否要
#@unitT(model,true)
#end
#@unitT(model,false)
#end
### 包输出模版 ###
#define unitT(unit,drop)
#for(table: unit.tables)
#if(drop)
#@droptable(table)
#else
#@createtable(table)
#end

#end
#for(subUnit: unit.units)
#@unitT(subUnit,drop)
#end
#end
### 删除表模版 ###
#define droptable(table)
DROP TABLE IF EXISTS `#(table.code)`;
#end
### 创建表模版 ###
#define createtable(table)
CREATE TABLE #(table.code) (
	#for(field: table.fields)
	#(field.code)	#if(field.code.length()<=5)	#end#--
	--##@fieldType(field)#--
	--##if(field.isMandatory())	NOT NULL#end#--
	--##if(!for.last||!table.keyFields().isEmpty()),#end
	#end
	#if(!table.keyFields().isEmpty())
	PRIMARY KEY(#--
	--##for(field: table.keyFields())#--
		--##(field.code)#--
		--##if(!for.last),#end#--
	--##end#--
	--#)
	#end
);
#end
#define fieldType(field)
#switch(field.type.toString())#--
	--##case ("CHAR","VARCHAR")#(field.type)(#(field.length))#--
	--##case ("NUMERIC")DECIMAL(#(field.length),#(field.precision))#--
	--##default#--
		--##(field.type)#--
--##end
#end