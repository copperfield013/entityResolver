package cn.sowell.datacenter.entityResolver.valsetter;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.log4j.Logger;

import cho.carbon.meta.constant.ModelItemValueParter;
import cho.carbon.meta.enun.AttributeValueType;
import cn.sowell.copframe.spring.file.FileHaunt;
import cn.sowell.copframe.utils.date.CommonDateFormat;
import cn.sowell.copframe.utils.date.FrameDateFormat;
import cn.sowell.datacenter.entityResolver.EntityProxy;
import cn.sowell.datacenter.entityResolver.impl.CommonPropertyValueBindReport;
import cn.sowell.datacenter.entityResolver.impl.PropertyValueBindReport.PropertyType;

public class FileValueSetter implements PropertyValueSetter{

	Logger logger = Logger.getLogger(FileValueSetter.class);
	
	NumberFormat kbSizeFormat = new DecimalFormat("0.00");
	
	FrameDateFormat dateFormat = new CommonDateFormat();
		
	
	@Override
	public boolean support(AttributeValueType dataType, Object val) {
		return AttributeValueType.FILE.equals(dataType) && (val == null || val instanceof FileHaunt || "".equals(val));
	}
	
	
	@Override
	public void invoke(EntityProxy entity, String propName, Object val, CommonPropertyValueBindReport report) {
		val = "".equals(val)? null: val;
		FileHaunt file = (FileHaunt) val;
		try {
			report.setValueAsNull(file == null);
			report.setPropertyType(PropertyType.FILE);
			String suffixPropName = ModelItemValueParter.getFileSuffixCNName(propName),
					sizePropName = ModelItemValueParter.getFileKBSizeCNName(propName),
					fileNamePropName = ModelItemValueParter.getFileNameCNName(propName);
			entity.putValue(propName, file == null? null: file.getBytes());
			entity.putValue(fileNamePropName, file == null? null: file.getFileName());
			entity.putValue(suffixPropName, file == null? null: file.getSuffix());
			entity.putValue(sizePropName, file == null? null: kbSizeFormat.format(file.getSize() / 1000));
		} catch (IOException e) {
			logger.error("保存文件字段时发生错误[字段名=" + propName + ", 文件=" + file.getFileName() + "]", e);
		}
		
	}


}
