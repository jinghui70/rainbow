(function(e){function t(t){for(var i,l,c=t[0],o=t[1],s=t[2],d=0,f=[];d<c.length;d++)l=c[d],Object.prototype.hasOwnProperty.call(a,l)&&a[l]&&f.push(a[l][0]),a[l]=0;for(i in o)Object.prototype.hasOwnProperty.call(o,i)&&(e[i]=o[i]);u&&u(t);while(f.length)f.shift()();return r.push.apply(r,s||[]),n()}function n(){for(var e,t=0;t<r.length;t++){for(var n=r[t],i=!0,c=1;c<n.length;c++){var o=n[c];0!==a[o]&&(i=!1)}i&&(r.splice(t--,1),e=l(l.s=n[0]))}return e}var i={},a={query:0},r=[];function l(t){if(i[t])return i[t].exports;var n=i[t]={i:t,l:!1,exports:{}};return e[t].call(n.exports,n,n.exports,l),n.l=!0,n.exports}l.m=e,l.c=i,l.d=function(e,t,n){l.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:n})},l.r=function(e){"undefined"!==typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},l.t=function(e,t){if(1&t&&(e=l(e)),8&t)return e;if(4&t&&"object"===typeof e&&e&&e.__esModule)return e;var n=Object.create(null);if(l.r(n),Object.defineProperty(n,"default",{enumerable:!0,value:e}),2&t&&"string"!=typeof e)for(var i in e)l.d(n,i,function(t){return e[t]}.bind(null,i));return n},l.n=function(e){var t=e&&e.__esModule?function(){return e["default"]}:function(){return e};return l.d(t,"a",t),t},l.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},l.p="";var c=window["webpackJsonp"]=window["webpackJsonp"]||[],o=c.push.bind(c);c.push=t,c=c.slice();for(var s=0;s<c.length;s++)t(c[s]);var u=o;r.push([0,"chunk-vendors"]),n()})({0:function(e,t,n){e.exports=n("a50d")},"034d":function(e,t,n){},"04ee":function(e,t,n){"use strict";var i=n("2b0e"),a=n("2ef0"),r=n.n(a);i["default"].prototype._=r.a},"0c37":function(e,t,n){"use strict";var i=n("ccaa"),a=n.n(i);a.a},"114b":function(e,t,n){},1191:function(e,t,n){"use strict";var i=n("fbd4"),a=n.n(i);a.a},"162a":function(e,t,n){},"18a2":function(e,t,n){},"1cbc":function(e,t,n){"use strict";var i=n("2af9"),a=n.n(i);a.a},"1d11":function(e,t,n){"use strict";var i=n("b84b"),a=n.n(i);a.a},"2af9":function(e,t,n){},"2f2b":function(e,t,n){"use strict";var i=n("6d48"),a=n.n(i);a.a},"436a":function(e,t,n){},"48c2":function(e,t,n){"use strict";var i=n("436a"),a=n.n(i);a.a},6539:function(e,t,n){"use strict";var i=n("d877"),a=n.n(i);a.a},"6d48":function(e,t,n){},7378:function(e,t,n){"use strict";var i=n("2b0e"),a=n("5c96"),r=n.n(a);n("0fae");i["default"].use(r.a)},a50d:function(e,t,n){"use strict";n.r(t);n("e260"),n("e6cf"),n("cca6"),n("a79d");var i=n("2b0e"),a=(n("7378"),n("04ee"),n("8c4f")),r=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"index-page"},[n("item-tree-panel",{model:{value:e.selectObject,callback:function(t){e.selectObject=t},expression:"selectObject"}}),n("div",{staticClass:"panel"},[n("unit-panel",{directives:[{name:"show",rawName:"v-show",value:e.isUnit,expression:"isUnit"}],attrs:{unit:e.selectObject},on:{selectTable:function(t){return e.selectObject=t}}}),n("table-panel",{directives:[{name:"show",rawName:"v-show",value:!e.isUnit,expression:"!isUnit"}],attrs:{entity:e.selectObject}})],1)],1)},l=[],c=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"item-tree-panel line"},[n("el-input",{staticClass:"filter",attrs:{placeholder:"输入关键字进行过滤"},model:{value:e.keywords,callback:function(t){e.keywords=t},expression:"keywords"}}),n("div",{staticClass:"tree-wrapper"},[n("el-tree",{ref:"tree",attrs:{data:e.tree,"filter-node-method":e.filterNode,"expand-on-click-node":!1,"node-key":"id","render-content":e.renderNode,"highlight-current":""},on:{"current-change":e.nodeChange}})],1)],1)},o=[],s=(n("4de4"),n("c975"),n("b0c0"),n("5530")),u=n("2f62"),d={name:"ItemTreePanel",data:function(){return{keywords:""}},props:{value:Object},computed:Object(s["a"])({},Object(u["c"])(["tree"])),watch:{keywords:function(e){this.$refs.tree.filter(e)},value:function(e){var t=this;e&&this.$nextTick((function(){return t.$refs.tree.setCurrentKey(e.id)}))}},methods:{renderNode:function(e,t){var n=t.node,i=t.data,a="el-icon-document",r=i.label;return i.root?(a="el-icon-s-home",n.expanded=!0):i.unit&&(a=n.expanded?"el-icon-folder-opened":"el-icon-folder"),e("span",[e("i",{class:a}),"  ",r])},filterNode:function(e,t){return!e||(-1!==t.label.indexOf(e)||t.name&&-1!=t.name.indexOf(e))},nodeChange:function(e){this.$emit("input",e)}}},f=d,h=(n("e482"),n("2877")),p=Object(h["a"])(f,c,o,!1,null,null,null),m=p.exports,b=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"unit-panel"},[n("block-head",{attrs:{title:e.unit.label,actions:e.actions},on:{"action-click":e.actionClick}}),n("div",{staticClass:"unit-panel-table"},[n("el-table",{attrs:{data:e.tables,height:"100%","highlight-current-row":!1,border:"","cell-class-name":"nopadding","header-cell-class-name":"header-cell-class"},on:{"row-dblclick":e.selectTable}},[n("el-table-column",{attrs:{fixed:"",type:"index",label:"序号",width:"50"}}),n("el-table-column",{attrs:{prop:"label",label:"显示标题",width:"160"}}),n("el-table-column",{attrs:{prop:"name",label:"对象名",width:"160"}}),n("el-table-column",{attrs:{label:"标签"},scopedSlots:e._u([{key:"default",fn:function(t){return t.row.tags?e._l(t.row.tags,(function(t){return n("el-tag",{key:t,attrs:{size:"mini"}},[e._v(e._s(t))])})):void 0}}],null,!0)})],1)],1)],1)},y=[],v=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"block-head"},[e.icon?n("el-button",{attrs:{icon:e.icon,type:"info",circle:"",size:"mini"},on:{click:function(t){return e.actionClick(e.iconAction)}}}):e._e(),n("span",{staticClass:"block-head__title",class:{action:!!e.titleAction},on:{click:function(t){return e.actionClick(e.titleAction)}}},[e._v(" "+e._s(e.title)+" ")]),n("div",{staticClass:"block-head__actions"},[e._l(e.actions,(function(t){return[n("span",{key:t.id,staticClass:"block-head__actionBtn",class:{disabled:t.disabled},on:{click:function(n){return e.actionClick(t.id)}}},[t.icon?[n("i",{class:t.icon,attrs:{slot:"icon"},slot:"icon"})]:e._e(),e._v(" "+e._s(t.title)+" ")],2)]}))],2)],1)},g=[],k={name:"BlockHead",props:{icon:String,iconAction:String,title:String,titleAction:String,actions:Array},methods:{actionClick:function(e){e&&!e.disabled&&this.$emit("action-click",e)}}},_=k,w=(n("e052"),Object(h["a"])(_,v,g,!1,null,null,null)),O=w.exports,x={name:"UnitPanel",components:{BlockHead:O},computed:{tables:function(){return this.unit.children?this.unit.children.filter((function(e){return!e.unit})):[]}},props:{unit:{type:Object}},data:function(){return{actions:[{id:"sql",title:"SQL",icon:"el-icon-s-tools"}]}},methods:{actionClick:function(e){this[e]()},sql:function(){this.$router.push({name:"sql"})},selectTable:function(e){this.$emit("selectTable",e)}}},q=x,S=(n("bc88"),Object(h["a"])(q,b,y,!1,null,null,null)),j=S.exports,C=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"table-panel"},[n("block-head",{attrs:{title:e.entity.label+" - "+e.entity.name,actions:e.tableActions},on:{"action-click":e.actionClick}}),e.entity.tags?n("div",{staticClass:"table-panel-tag"},e._l(e.entity.tags,(function(t){return n("el-tag",{key:t,attrs:{size:"mini"}},[e._v(e._s(t))])})),1):e._e(),n("div",{staticClass:"table-wrapper"},[n("el-table",{ref:"fieldsTable",attrs:{data:e.columns,height:"100%","highlight-current-row":!1,border:"","row-key":"id","cell-class-name":"nopadding","header-cell-class-name":"header-cell-class"}},[n("el-table-column",{attrs:{fixed:"",type:"index",label:"序号",width:"50"}}),n("el-table-column",{attrs:{prop:"label",label:"显示标题",width:"160"}}),n("el-table-column",{attrs:{prop:"name",label:"属性名",width:"160"}}),n("el-table-column",{attrs:{prop:"type",label:"类型",width:"140"},scopedSlots:e._u([{key:"default",fn:function(t){return[t.row.link?n("el-tag",{attrs:{type:"success",size:"mini"}},[e._v(e._s(t.row.many?"link列表":"link"))]):n("span",[e._v(e._s(t.row.type))])]}}])}),n("el-table-column",{attrs:{label:"标签"},scopedSlots:e._u([{key:"default",fn:function(t){return t.row.tags?e._l(t.row.tags,(function(t){return n("el-tag",{key:t,attrs:{size:"mini"}},[e._v(e._s(t))])})):void 0}}],null,!0)})],1)],1)],1)},E=[],$=(n("99af"),n("2909")),z={name:"TablePanel",components:{BlockHead:O},data:function(){return{tableActions:[{id:"query",title:"去查询",icon:"el-icon-search"}],columns:[]}},props:{entity:Object},watch:{entity:function(e){var t=this;this.columns=[],e&&!e.unit&&this.getEntity(e.name).then((function(e){t.columns=[].concat(Object($["a"])(e.columns),Object($["a"])(e.links))}))}},methods:Object(s["a"])({},Object(u["b"])(["getEntity"]),{actionClick:function(e){this[e]()},query:function(){this.$router.push({name:"query",params:{name:this.entity.name}})}})},N=z,T=(n("cca1"),Object(h["a"])(N,C,E,!1,null,null,null)),P=T.exports,D={name:"Index",components:{ItemTreePanel:m,UnitPanel:j,TablePanel:P},data:function(){return{selectObject:{name:"",label:""}}},computed:{isUnit:function(){return this.selectObject.unit}}},I=D,B=(n("1191"),Object(h["a"])(I,r,l,!1,null,null,null)),L=B.exports,F=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"query-page"},[n("query-column-list",{ref:"columns"}),n("div",{staticClass:"query-part"},[n("query-fields"),n("query-cnds"),n("query-orders")],1),n("query-text",{attrs:{queryInfo:e.queryInfo}})],1)},Q=[],R=(n("4160"),n("9911"),n("159b"),function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"query-column-list"},[n("block-head",{attrs:{icon:"el-icon-caret-left","icon-action":"goBack",title:"查询生成器:"+e.query.name},on:{"action-click":e.actionClick}}),n("div",{staticClass:"head"},e._l(e.query.data,(function(t,i){return n("query-entity",{key:i,attrs:{data:t,current:i==e.query.currentIndex,main:e.query.data.length>1&&0==i},on:{click:function(t){return e.nodeChange(i)}}})})),1),n("div",{staticClass:"body"},[n("el-table",{ref:"columnList",attrs:{data:e.query.columns,height:"100%","highlight-current-row":!1,border:"","row-key":"id","cell-class-name":"nopadding","header-cell-class-name":"header-cell-class"},on:{"selection-change":function(t){return e.query.selection=t}}},[n("el-table-column",{attrs:{fixed:"",type:"selection",resizable:!1,selectable:function(e){return!e.link},width:"30"}}),n("el-table-column",{attrs:{prop:"label",label:"属性"},scopedSlots:e._u([{key:"default",fn:function(t){return[t.row.link?n("el-tag",{attrs:{type:"success",size:"mini"}},[e._v("link")]):e._e(),n("span",[e._v(e._s(t.row.label))]),n("el-tag",{attrs:{size:"mini"}},[e._v(e._s(t.row.name))]),t.row.link&&t.row.link.many?n("i",{staticClass:"el-icon-notebook-2"}):e._e()]}}])})],1)],1),n("page-dialog",{ref:"dialog"})],1)}),A=[],U=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"query-entity",class:{current:e.current,main:e.main,used:e.used},on:{click:function(t){return e.$emit("click")}}},[n("el-button",{attrs:{size:"mini",type:"text"},on:{click:e.config}},[e._v("配置")]),n("i",{staticClass:"el-icon-caret-right"}),n("span",[e._v(e._s(e.node.label)+" ")]),n("el-tag",{attrs:{size:"mini"}},[e._v(e._s(e.infoText))])],1)},H=[],M={name:"QueryEntity",props:{data:Object,current:Boolean,main:Boolean},computed:{node:function(){return this.data.node},infoText:function(){var e=this.node.name;return this.node.link?this.node.many?e="".concat(e,"[ ]").concat(this.pageText):this.data.shrink&&(e+=" (shrink)"):e+=this.pageText,e},pageText:function(){return this.data.pageNo&&this.data.pageSize?1==this.data.pageNo?" 前".concat(this.data.pageSize,"条"):" ".concat(this.data.pageSize,"条,第").concat(this.data.pageNo,"页"):""},used:function(){return this.data.fields.length>0||(this.data.cnds.length>0||this.data.orders.length>0)}},methods:{config:function(){!this.node.link||this.node.many?this.$parent.$refs.dialog.open(this.data):this.$set(this.data,"shrink",!this.data.shrink)}}},J=M,G=(n("0c37"),Object(h["a"])(J,U,H,!1,null,null,null)),K=G.exports,V=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("el-dialog",{attrs:{title:"分页设置",width:"280px",visible:e.visible},on:{"update:visible":function(t){e.visible=t}}},[n("el-form",{ref:"form",attrs:{"label-width":"90px",size:"mini"}},[n("el-form-item",{attrs:{label:"不分页"}},[n("el-switch",{model:{value:e.noPage,callback:function(t){e.noPage=t},expression:"noPage"}})],1),n("el-form-item",{attrs:{label:"每页记录数"}},[n("el-input-number",{attrs:{disabled:e.noPage},model:{value:e.pageSize,callback:function(t){e.pageSize=t},expression:"pageSize"}})],1),n("el-form-item",{attrs:{label:"页数"}},[n("el-input-number",{attrs:{disabled:e.noPage},model:{value:e.pageNo,callback:function(t){e.pageNo=t},expression:"pageNo"}})],1)],1),n("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[n("el-button",{on:{click:function(t){e.visible=!1}}},[e._v("取 消")]),n("el-button",{attrs:{type:"primary"},on:{click:e.save}},[e._v("确 定")])],1)],1)},W=[],X={name:"RefineryDialog",data:function(){return{visible:!1,entityData:null,noPage:!1,pageSize:30,pageNo:1}},watch:{noPage:function(e){e||(this.pageSize||(this.pageSize=30),this.pageNo||(this.pageNo=1))}},methods:{open:function(e){this.entityData=e,e.pageSize?(this.noPage=!1,this.pageSize=e.pageSize,this.pageNo=e.pageNo):(this.noPage=!0,this.pageSize=0,this.pageNo=0),this.visible=!0},save:function(){this.noPage?(this.entityData.pageNo=0,this.entityData.pageSize=0):(this.entityData.pageNo=this.pageNo,this.entityData.pageSize=this.pageSize),this.visible=!1}}},Y=X,Z=Object(h["a"])(Y,V,W,!1,null,null,null),ee=Z.exports,te={name:"QueryColumnList",components:{BlockHead:O,QueryEntity:K,PageDialog:ee},inject:["query"],methods:{clearSelection:function(){this.$refs.columnList.clearSelection()},actionClick:function(e){this[e]()},goBack:function(){this.$router.go(-1)},nodeChange:function(e){this.query.currentIndex=e}}},ne=te,ie=(n("1d11"),Object(h["a"])(ne,R,A,!1,null,null,null)),ae=ie.exports,re=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"query-text"},[n("block-head",{attrs:{icon:"el-icon-search","icon-action":"query","title-action":"changeFormat",title:"查询代码"},on:{"action-click":e.actionClick}}),n("textarea",{domProps:{value:e.text}}),e._v(" "),e.result?n("block-head",{attrs:{icon:"el-icon-close","icon-action":"close",title:"查询结果"},on:{"action-click":e.actionClick}}):e._e(),e.result?n("textarea",{attrs:{readonly:""},domProps:{value:e.result}}):e._e()],1)},le=[],ce=(n("ac1f"),n("5319"),n("f523")),oe={name:"QueryText",components:{BlockHead:O},data:function(){return{result:null,format:!0,actions:[{id:"copy",title:"复制",icon:"el-icon-document-copy"}]}},props:{queryInfo:Object},computed:{text:function(){return this.format?JSON.stringify(this.queryInfo,null,2):JSON.stringify(this.queryInfo).replace(/"/g,"'")}},methods:{actionClick:function(e){this[e]()},close:function(){this.result=null},query:function(){var e=this;Object(ce["a"])("data/query",{model:"EG",query:this.queryInfo}).then((function(t){return e.result=JSON.stringify(t,null,2)}))},changeFormat:function(){this.format=!this.format}}},se=oe,ue=(n("e03b"),Object(h["a"])(se,re,le,!1,null,null,null)),de=ue.exports,fe=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"query-fields"},[n("block-head",{attrs:{title:"查询属性","title-action":"java",actions:e.actions},on:{"action-click":e.actionClick}}),n("div",{staticClass:"query-fields-wrapper"},[n("el-table",{ref:"fieldsTable",attrs:{data:e.query.fields,height:"100%","highlight-current-row":!1,border:"","row-key":"id","cell-class-name":"nopadding","header-cell-class-name":"header-cell-class"},on:{"selection-change":function(t){return e.localSelection=t}}},[n("el-table-column",{attrs:{fixed:"",type:"selection",resizable:!1,width:"38"}}),n("el-table-column",{attrs:{prop:"label",label:"属性","min-width":"160"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("span",[e._v(e._s(t.row.label))]),n("el-tag",{attrs:{size:"mini"}},[e._v(e._s(t.row.name))])]}}])}),n("el-table-column",{attrs:{prop:"alias",label:"别名","min-width":"100"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("el-input",{attrs:{size:"mini"},model:{value:t.row.alias,callback:function(n){e.$set(t.row,"alias",n)},expression:"scope.row.alias"}})]}}])}),n("el-table-column",{attrs:{prop:"refinery",label:"加工方法","min-width":"160","class-name":"refinery"},scopedSlots:e._u([{key:"default",fn:function(t){return[t.row.refinery?[n("el-tag",{attrs:{type:"success",size:"mini"},on:{click:function(n){return e.handleSet(t.row)}}},[e._v(e._s(t.row.refinery))]),t.row.param?n("span",[e._v(e._s(t.row.param))]):e._e()]:n("span",{on:{click:function(n){return e.handleSet(t.row)}}},[e._v("无")])]}}])})],1)],1),n("refinery-dialog",{ref:"dialog"})],1)},he=[],pe=(n("a434"),function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("el-dialog",{attrs:{title:"设置数据处理方法",width:"400px",visible:e.visible},on:{"update:visible":function(t){e.visible=t}}},[n("el-form",{ref:"form",attrs:{model:e.editObject,rules:e.rules,"label-width":"90px",size:"mini"}},[n("el-form-item",{attrs:{label:"属性名称",prop:"label"}},[n("el-input",{attrs:{readonly:""},model:{value:e.column.label,callback:function(t){e.$set(e.column,"label",t)},expression:"column.label"}})],1),n("el-form-item",{attrs:{label:"处理方法",prop:"refinery"}},[n("el-select",{model:{value:e.editObject.refinery,callback:function(t){e.$set(e.editObject,"refinery",t)},expression:"editObject.refinery"}},[n("el-option",{attrs:{value:null,label:"无"}}),e._l(e.column.refineries,(function(e){return n("el-option",{key:e.name,attrs:{value:e.name,label:e.name}})}))],2)],1),e.hasParam?n("el-form-item",{attrs:{label:"处理参数",prop:"refinery"}},[n("el-select",{attrs:{"allow-create":e.canInput,filterable:e.canInput},model:{value:e.editObject.param,callback:function(t){e.$set(e.editObject,"param",t)},expression:"editObject.param"}},e._l(e.paramList,(function(e){return n("el-option",{key:e,attrs:{value:e}})})),1)],1):e._e()],1),n("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[n("el-button",{on:{click:function(t){e.visible=!1}}},[e._v("取 消")]),n("el-button",{attrs:{type:"primary"},on:{click:e.save}},[e._v("确 定")])],1)],1)}),me=[],be=(n("7db0"),{name:"RefineryDialog",data:function(){return{visible:!1,refineries:[],editObject:{refinery:null,param:null},hasParam:!1,canInput:!1,paramList:[],column:{},rules:{code:[{required:!0,message:"请输入处理参数",trigger:"blur"}]}}},watch:{"editObject.refinery":function(e){this.setRefinery(e)}},methods:{open:function(e){this.refineries=e.refineries,this.editObject.refinery=e.refinery,this.editObject.param=e.param,this.column=e,this.visible=!0},setRefinery:function(e){var t=null==e?null:this.refineries.find((function(t){return t.name==e}));null==t?(this.editObject.param=null,this.canInput=!1,this.hasParam=!1):(this.paramList=t.params,this.canInput=t.canInput,0==this.paramList?this.hasParam=this.canInput:(this.hasParam=!0,null==this.editObject.param&&(this.editObject.param=this.paramList[0])))},save:function(){var e=this;this.$refs.form.validate((function(t){t&&e.doSave()}))},doSave:function(){this.column.refinery=this.editObject.refinery,this.column.param=this.editObject.param,this.editObject.refinery=null,this.visible=!1}}}),ye=be,ve=Object(h["a"])(ye,pe,me,!1,null,null,null),ge=ve.exports,ke={name:"QueryFields",components:{BlockHead:O,RefineryDialog:ge},data:function(){return{localSelection:[]}},computed:{actions:function(){return[{id:"addFields",title:"加入字段",icon:"el-icon-plus",disabled:0==this.query.selection.length},{id:"delFields",title:"删除",icon:"el-icon-delete",disabled:0==this.localSelection.length},{id:"autoRefinery",title:"自动处理",icon:"el-icon-thumb",disabled:0==this.query.fields.length}]}},inject:["query"],methods:{actionClick:function(e){this[e]()},addFields:function(){var e=this;this.query.selection.forEach((function(t){return e.query.fields.push(Object(s["a"])({},t,{refinery:null,param:null}))})),this.query.$refs.columns.clearSelection()},delFields:function(){var e=this;this.localSelection.forEach((function(t){var n=e.query.fields.indexOf(t);n>=0&&e.query.fields.splice(n,1)}))},handleSet:function(e){var t=this;if(e.refineries)e.refineries.length>0&&this.$refs.dialog.open(e);else{var n=0==this.query.currentIndex?e.name:"".concat(this.query.currentData.node.name,".").concat(e.name);Object(ce["a"])("data/getRefinery",{model:"EG",entityName:this.query.name,columnName:n}).then((function(n){e.refineries=n,n.length>0&&t.$refs.dialog.open(e)}))}},autoRefinery:function(){var e=this;this.query.fields.forEach((function(t){if(!t.refinery)if(t.refineries){if(t.refineries.length>0){var n=t.refineries[0];t.refinery=n.name,n.params.length>0&&(t.param=n.params[0])}}else{var i=0==e.query.currentIndex?t.name:"".concat(e.query.currentData.node.name,".").concat(t.name);Object(ce["a"])("data/getRefinery",{model:"EG",entityName:e.query.name,columnName:i}).then((function(e){if(t.refineries=e,e.length>0){var n=e[0];t.refinery=n.name,n.params.length>0&&(t.param=n.params[0])}}))}}))},java:function(){var e="public class ".concat(this.query.name," {\n");this.query.data[0].fields.forEach((function(t){var n="String";switch(t.type){case"INT":n="int";break;case"LONG":n="long";break;case"DOUBLE":n="double";break;case"NUMERIC":n="BigDecimal";break;case"DATE":n="LocalDate";break;case"TIME":n="LocalTime";break;case"TIMESTAMP":n="LocalDateTime";break;case"BLOB":n="byte[]";break}e+="\n\t//".concat(t.label,"\n"),e+="\tprivate ".concat(n," ").concat(t.name,";\n")})),e+="}\n";var t=this.$createElement;this.$msgbox({title:"Java Class",message:t("textarea",{style:"width: 100%;height: 400px"},[e]),confirmButtonText:"关闭"})}}},_e=ke,we=(n("cd0f"),Object(h["a"])(_e,fe,he,!1,null,null,null)),Oe=we.exports,xe=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"query-cnds"},[n("block-head",{attrs:{title:"查询条件",actions:e.actions},on:{"action-click":e.actionClick}}),n("div",{staticClass:"body"},[n("el-table",{attrs:{data:e.query.cnds,height:"100%","highlight-current-row":!1,border:"","row-key":"id","cell-class-name":"nopadding","header-cell-class-name":"header-cell-class"},on:{"selection-change":function(t){return e.localSelection=t}}},[n("el-table-column",{attrs:{fixed:"",type:"selection",resizable:!1,width:"38"}}),n("el-table-column",{attrs:{prop:"label",label:"属性","min-width":"100"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("span",[e._v(e._s(t.row.label))]),n("el-tag",{attrs:{size:"mini"}},[e._v(e._s(t.row.name))])]}}])}),n("el-table-column",{attrs:{prop:"op",label:"Op","min-width":"60"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("el-select",{attrs:{size:"mini"},model:{value:t.row.op,callback:function(n){e.$set(t.row,"op",n)},expression:"scope.row.op"}},e._l(e.opList,(function(e){return n("el-option",{key:e,attrs:{label:e,value:e}})})),1)]}}])}),n("el-table-column",{attrs:{prop:"value",label:"参数","min-width":"100"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("el-input",{attrs:{size:"mini"},model:{value:t.row.value,callback:function(n){e.$set(t.row,"value",n)},expression:"scope.row.value"}})]}}])})],1)],1)],1)},qe=[],Se={name:"QueryCnds",components:{BlockHead:O},data:function(){return{localSelection:[],opList:["=","<>",">",">=","<","<="," like "," not like "," in "]}},computed:{actions:function(){return[{id:"addFields",title:"加入字段",icon:"el-icon-plus",disabled:0==this.query.selection.length},{id:"delFields",title:"删除",icon:"el-icon-delete",disabled:0==this.localSelection.length}]}},inject:["query"],methods:{actionClick:function(e){this[e]()},addFields:function(){var e=this;this.query.selection.forEach((function(t){return e.query.cnds.push(Object(s["a"])({},t,{op:"=",value:""}))})),this.query.$refs.columns.clearSelection()},delFields:function(){var e=this;this.localSelection.forEach((function(t){var n=e.query.cnds.indexOf(t);n>=0&&e.query.cnds.splice(n,1)}))}}},je=Se,Ce=(n("c10d"),Object(h["a"])(je,xe,qe,!1,null,null,null)),Ee=Ce.exports,$e=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"query-orders"},[n("block-head",{attrs:{title:"排序",actions:e.actions},on:{"action-click":e.actionClick}}),n("div",{staticClass:"body"},[n("el-table",{attrs:{data:e.query.orders,height:"100%","highlight-current-row":!1,border:"","row-key":"id","cell-class-name":"nopadding","header-cell-class-name":"header-cell-class"},on:{"selection-change":function(t){return e.localSelection=t}}},[n("el-table-column",{attrs:{fixed:"",type:"selection",resizable:!1,width:"38"}}),n("el-table-column",{attrs:{prop:"label",label:"属性","min-width":"160"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("span",[e._v(e._s(t.row.label))]),n("el-tag",{attrs:{size:"mini"}},[e._v(e._s(t.row.name))])]}}])}),n("el-table-column",{attrs:{prop:"desc",label:"倒序","min-width":"100"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("el-switch",{attrs:{"active-color":"#13ce66",size:"mini"},model:{value:t.row.desc,callback:function(n){e.$set(t.row,"desc",n)},expression:"scope.row.desc"}})]}}])})],1)],1)],1)},ze=[],Ne=(n("d81d"),n("d3b7"),n("6062"),n("3ca3"),n("ddb0"),{name:"QueryOrders",components:{BlockHead:O},data:function(){return{localSelection:[]}},computed:{actions:function(){return[{id:"addFields",title:"加入字段",icon:"el-icon-plus",disabled:0==this.query.selection.length},{id:"delFields",title:"删除",icon:"el-icon-delete",disabled:0==this.localSelection.length}]}},inject:["query"],methods:{actionClick:function(e){this[e]()},addFields:function(){var e=this,t=new Set(this.query.orders.map((function(e){return e.id})));this.query.selection.filter((function(e){return!t.has(e.id)})).forEach((function(t){return e.query.orders.push(Object(s["a"])({},t,{desc:!1}))})),this.query.$refs.columns.clearSelection()},delFields:function(){var e=this;this.localSelection.forEach((function(t){var n=e.query.orders.indexOf(t);n>=0&&e.query.orders.splice(n,1)}))}}}),Te=Ne,Pe=(n("1cbc"),Object(h["a"])(Te,$e,ze,!1,null,null,null)),De=Pe.exports,Ie={name:"Query",components:{QueryColumnList:ae,QueryText:de,QueryFields:Oe,QueryCnds:Ee,QueryOrders:De},provide:function(){return{query:this}},data:function(){return{currentIndex:0,data:[]}},props:{name:{type:String,required:!0}},created:function(){this.fetchData()},watch:{$route:"fetchData"},computed:{currentData:function(){return 0==this.data.length?null:this.data[this.currentIndex]},selection:{get:function(){return this.currentData?this.currentData.selection:[]},set:function(e){null!=this.currentData&&(this.currentData.selection=e)}},fields:function(){return this.currentData?this.currentData.fields:[]},cnds:function(){return this.currentData?this.currentData.cnds:[]},orders:function(){return this.currentData?this.currentData.orders:[]},columns:function(){return this.currentData?this.currentData.node.columns:[]},noSelection:function(){return 0==this.selection.length},queryInfo:function(){var e=this,t={entity:this.name,fields:[]},n=t.fields,i=[],a=[],r=[],l=[];return this.data.forEach((function(c){if(c.node.link)if(c.node.many){if(c.fields.length>0){var o={name:c.node.name,fields:[]};c.fields.forEach((function(t){return o.fields.push(e.convertField(t))})),c.cnds.length>0&&(o.conditions=[],c.cnds.forEach((function(t){return o.conditions.push(e.convertCnd(t))}))),c.orders.length>0&&(o.orders=[],c.orders.forEach((function(t){return o.orders.push(e.convertOrder(t))}))),c.pageSize&&(o.pageSize=c.pageSize,o.pageNo=c.pageNo),l.push(o)}}else{var s=c.node.name;c.fields.length>0&&(c.fields.forEach((function(t){return n.push(e.convertField(t,s))})),c.shrink&&r.push(s)),c.cnds.length>0&&c.cnds.forEach((function(t){return i.push(e.convertCnd(t,s))})),c.orders.length>0&&c.orders.forEach((function(t){return a.push(e.convertOrder(t,s))}))}else c.fields.length>0&&c.fields.forEach((function(t){return n.push(e.convertField(t))})),c.cnds.length>0&&c.cnds.forEach((function(t){return i.push(e.convertCnd(t))})),c.orders.length>0&&c.orders.forEach((function(t){return a.push(e.convertOrder(t))})),c.pageSize&&(t.pageSize=c.pageSize,t.pageNo=c.pageNo)})),i.length>0&&(t.conditions=i),a.length>0&&(t.orders=a),r.length>0&&(t.shrinks=r),l.length>0&&(t.listProps=l),t}},methods:Object(s["a"])({},Object(u["b"])(["getEntity"]),{fetchData:function(){var e=this;this.getEntity(this.name).then((function(t){e.data=[{node:t,pageSize:0,pageNo:0,selection:[],fields:[],cnds:[],orders:[]}],t.links.forEach((function(t){e.data.push({node:t,pageSize:0,pageNo:0,selection:[],fields:[],cnds:[],orders:[]})})),e.currentIndex=0}))},clearSelection:function(){this.$refs.columnList.clearSelection()},actionClick:function(e){this[e]()},goBack:function(){this.$router.go(-1)},convertField:function(e,t){var n=t?"".concat(t,".").concat(e.name):e.name;return e.alias&&(n=n+":"+e.alias),e.refinery&&(n=n+"|"+e.refinery,e.param&&(n=n+"("+e.param+")")),n},convertCnd:function(e,t){return{prop:t?"".concat(t,".").concat(e.name):e.name,op:e.op,param:""==e.value?null:e.value}},convertOrder:function(e,t){var n=t?"".concat(t,".").concat(e.name):e.name;return e.desc&&(n+=" desc"),n}})},Be=Ie,Le=(n("6539"),Object(h["a"])(Be,F,Q,!1,null,null,null)),Fe=Le.exports,Qe=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"sql-page"},[n("block-head",{attrs:{icon:"el-icon-caret-left","icon-action":"goBack",title:"Sql查询",actions:e.actions},on:{"action-click":e.actionClick}}),n("textarea",{ref:"input"}),e._v(" "),n("block-head",{attrs:{title:"查询结果"}}),n("textarea",{attrs:{readonly:""},domProps:{value:e.result}})],1)},Re=[],Ae={name:"QueryText",components:{BlockHead:O},data:function(){return{text:"",result:"",actions:[{id:"query",title:"执行SQL",icon:"el-icon-search"}]}},props:{queryInfo:Object},methods:{actionClick:function(e){this[e]()},goBack:function(){this.$router.go(-1)},query:function(){var e=this,t=this.$refs.input,n=t.select();this._.isEmpty(n)&&(n=t.value),Object(ce["a"])("data/sql",{model:"EG",text:n}).then((function(t){return e.result=JSON.stringify(t,null,2)}))}}},Ue=Ae,He=(n("48c2"),Object(h["a"])(Ue,Qe,Re,!1,null,null,null)),Me=He.exports;i["default"].use(a["a"]);var Je=new a["a"]({routes:[{path:"/",name:"index",component:L},{path:"/query/:name",name:"query",component:Fe,props:!0},{path:"sql",name:"sql",component:Me}]});i["default"].use(u["a"]);var Ge=new u["a"].Store({state:{tree:[],entities:{}},mutations:{setTree:function(e,t){t.root=!0,e.tree=[t]},setEntity:function(e,t){e.entities[t.name]=t.data}},actions:{loadTree:function(e){var t=e.commit;Object(ce["a"])("data/dataTree",{model:"EG"}).then((function(e){return t("setTree",e)}))},getEntity:function(e,t){var n=e.commit,i=e.state,a=i.entities[t];return null==a?Object(ce["a"])("data/entity",{model:"EG",name:t}).then((function(e){return n("setEntity",{name:t,data:e}),e})):a}}}),Ke=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{attrs:{id:"app"}},[n("keep-alive",{attrs:{include:"Index"}},[n("router-view")],1)],1)},Ve=[],We={name:"app",created:function(){this.loadTree()},methods:Object(s["a"])({},Object(u["b"])(["loadTree"]))},Xe=We,Ye=(n("2f2b"),Object(h["a"])(Xe,Ke,Ve,!1,null,null,null)),Ze=Ye.exports;i["default"].config.productionTip=!1,new i["default"]({router:Je,store:Ge,render:function(e){return e(Ze)}}).$mount("#app")},b84b:function(e,t,n){},b99b:function(e,t,n){},bc88:function(e,t,n){"use strict";var i=n("18a2"),a=n.n(i);a.a},c10d:function(e,t,n){"use strict";var i=n("162a"),a=n.n(i);a.a},cca1:function(e,t,n){"use strict";var i=n("e53d"),a=n.n(i);a.a},ccaa:function(e,t,n){},cd0f:function(e,t,n){"use strict";var i=n("b99b"),a=n.n(i);a.a},d877:function(e,t,n){},e03b:function(e,t,n){"use strict";var i=n("114b"),a=n.n(i);a.a},e052:function(e,t,n){"use strict";var i=n("ed0b"),a=n.n(i);a.a},e482:function(e,t,n){"use strict";var i=n("034d"),a=n.n(i);a.a},e53d:function(e,t,n){},ed0b:function(e,t,n){},f523:function(e,t,n){"use strict";n.d(t,"a",(function(){return c}));n("d3b7");var i=n("bc3a"),a=n.n(i),r=n("5c96"),l=a.a.create({baseURL:"service/",timeout:5e3});function c(e,t){return t=JSON.stringify(t),new Promise((function(n,i){l.post(e,t).then((function(e){n(e.data)})).catch((function(e){var t=e.response?e.response.data:"";Object(r["MessageBox"])({type:"error",title:e.message,message:t}),i()}))}))}},fbd4:function(e,t,n){}});
//# sourceMappingURL=query.94a7d916.js.map