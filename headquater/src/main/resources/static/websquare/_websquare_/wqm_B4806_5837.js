(self.webpackChunkwebpack_test=self.webpackChunkwebpack_test||[]).push([[5837],{5837:(t,i,e)=>{"use strict";e.r(i),e.d(i,{grid:()=>a});var o=e(2981),s=e(6615),n=e(7416),p=e(2712),a=function(t,i,e){s.s.call(this,t,i,e)};o.x.extend(a.prototype,s.s.prototype),o.x.extend(a.prototype,n.gridView.prototype),o.x.extend(a.prototype,p.v.prototype),a.prototype.defaultOptions={},o.x.extend(a.prototype.defaultOptions,n.gridView.prototype.defaultOptions),a.prototype.defaultOptions.pluginType="uiplugin.grid",a.prototype.defaultOptions.pluginName="grid",a.prototype.initialize=function(t){if(isNaN(parseInt(this.options.visibleRowNum,10))||(this.options.initFixedHeightByRowNum=-1),this.options.initFixedHeightByRowNum>0&&""===this.options.visibleRowNum&&(this.options.visibleRowNum=this.options.initFixedHeightByRowNum),this.firstEmbeddedInputResize=!0,this.bodyElement=WebSquare.Elem.api.getElementsByTagName(this.element,"gBody",WebSquare.core._XML_NAMESPACE.W2)[0],"none"===this.options.autoFit&&(this.options.autoFit=""),this.bodyElement){this.initializeType="grid",this._grid=this,this.dataList=new WebSquare.uiplugin.mappingController(this);for(var i={id:this.id+"??dataList",elementType:"JSON",type:"dataList",option:this.options,columnInfo:[]},e=WebSquare.Elem.api.getElementsByTagName(this.bodyElement,"column",WebSquare.core._XML_NAMESPACE.W2),o=0;o<e.length;o++){var s=e[o],n={},p=WebSquare.jsonUtil.getAttributesObject(s._element);for(var a in p)p.hasOwnProperty(a)&&(n[a]=p[a]);i.columnInfo.push(n)}this._dataList=new WebSquare.DataCollection.dataList(this.id+"??dataList",this.options,i,"grid",this.parentFrame),this._dataList.addChild(this),this.successBinding=!0}this._initialize()},a.prototype.onComplete=function(){this.options.ref&&this.setRef({ref:this.options.ref,aliasDataCollection:this.options.aliasDataCollection||void 0}),s.s.prototype.onComplete.call(this)}}}]);