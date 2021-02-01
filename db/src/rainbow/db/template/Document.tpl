# #(model.name) #(model.comment) 数据库说明书
[toc]
#for(table: model.tables)
#@tableT(table, for.index+1, 2)
#end
#for(unit: model.units)
#@unitT(unit, for.count + (model.tables.size() ?? 0), 2)
#end
### 包输出模版 ###
#define unitT(unit,inx,level)
#("#".repeat(level)) #(inx) #(unit.label)
#for(table: unit.tables)
#@tableT(table, inx+"."+for.count, level+1)
#end
#for(subUnit: unit.units)
#@unitT(subUnit, inx+"."+(for.count+unit.tables.size()??0), level+1)
#end
#end
### 表输出模版 ###
#define tableT(table, inx, level)
#("#".repeat(level)) #(inx) #(table.label)(#(table.name))
* 表名: #(table.code)
#if(!table.tagString().isEmpty())
* 标签: #(table.tagString())
#end
#if(table.comment!=null&&!table.comment.isEmpty())
* 描述: #(table.comment)
#end
|字段名|标题|字段类型|非空|主键|属性名|描述|
|----|----|----|:----:|:----:|----|----|
#for(c: table.fields)
|#(c.code)|#(c.label)|#@fieldType(c)|#(c.isMandatory() ?"是":"")|#(c.isKey()?"是":"")|#(c.name)|#(c.comment)|
#end
#end
### 字段类型输出模版 ###
#define fieldType(field)
#switch(field.type.toString())
	#case ("CHAR","VARCHAR")#--
		--##(field.type)(#(field.length))#--
	--##case ("NUMERIC")#--
		--##(field.type)(#(field.length),#(field.precision))#--
	--##default#--
		--##(field.type)#--
--##end
#end