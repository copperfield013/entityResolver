package cn.sowell.datacenter.entityResolver.converter;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.abc.application.BizFusionContext;
import com.abc.panel.Discoverer;
import com.abc.panel.PanelFactory;
import com.abc.util.ValueType;
import com.abc.vo.AttriCoorinatePJ;
import com.abc.vo.BytesInfoVO;

import cn.sowell.copframe.spring.file.FileHaunt;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.EntityElement;
import cn.sowell.datacenter.entityResolver.FieldParserDescription;
import cn.sowell.datacenter.entityResolver.impl.ABCNodeEntityBindContext;
import cn.sowell.datacenter.entityResolver.impl.ABCNodeProxy;
import cn.sowell.datacenter.entityResolver.impl.EntityMultiAttributeElement;
import cn.sowell.datacenter.entityResolver.impl.EntityRelationElement;

public class FilePropertyGetter implements PropertyValueGetter{

	static Logger logger = Logger.getLogger(FilePropertyGetter.class);
	
	
	@Override
	public boolean support(FieldParserDescription field) {
		return field != null && ValueType.BYTES.equals(field.getAbcType());
	}

	
	@Override
	public Object invoke(PropertyValueGetContext context) {
		BizFusionContext fusionConext = context.getContextConfig().getCurrentContext();
		Discoverer discoverer = PanelFactory.getDiscoverer(fusionConext);
		String code = FormatUtils.toString(context.getCurrentContext().getValue(ABCNodeProxy.CODE_PROPERTY_NAME, ValueType.STRING));
		AttriCoorinatePJ pj = new AttriCoorinatePJ();
		
		pj.setRecordCode(code);
		pj.setAttrName(context.getCurrentPropertyPath());
		if(context.getCurrentContext() instanceof ABCNodeEntityBindContext) {
			ABCNodeProxy node = ((ABCNodeEntityBindContext)context.getCurrentContext()).getAbcNode();
			EntityElement entityElement = node.getEntityElement();
			if(entityElement instanceof EntityRelationElement) {
				pj.setMappingName(((EntityRelationElement) entityElement).getFullAbcattrName());
			}else if(entityElement instanceof EntityMultiAttributeElement) {
				String parentCode = FormatUtils.toString(context.getParentEntityContext().getValue(ABCNodeProxy.CODE_PROPERTY_NAME, ValueType.STRING));
				pj.setRecordCode(parentCode);
				pj.setMultiAttrCode(code);
				pj.setMultiAttrName(entityElement.getName());
			}
		}
		
		try {
			BytesInfoVO file = discoverer.discoverBytesInfo(pj);
			if(file.getBody() != null) {
				String fileCode = FormatUtils.coalesce(file.getCode(), TextUtils.uuid());
				return new FileHaunt() {
					
					@Override
					public String getCode() {
						return fileCode;
					}
					
					@Override
					public String getFileName() {
						return file.getName();
					}
					
					@Override
					public boolean isEmpty() {
						return file == null || file.getSize_k() == 0;
					}
					
					@Override
					public long getSize() {
						return FormatUtils.toLong(file.getSize_k() * 1000);
					}
					
					@Override
					public byte[] getBytes() throws IOException {
						return file.getBody();
					}
					
				};
			}
		} catch (Exception e) {
			logger.error("保存文件时发生错误[" + context.getFullPropertyPath() + "]", e);
		}
		return null;
	}

}