package cn.sowell.datacenter.entityResolver.config;

import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;

import cn.sowell.copframe.dao.deferedQuery.ColumnMapResultTransformer;
import cn.sowell.copframe.dao.deferedQuery.DeferedParamQuery;
import cn.sowell.copframe.dao.deferedQuery.SimpleMapWrapper;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.config.abst.Module;
import cn.sowell.datacenter.entityResolver.config.param.QueryModuleCriteria;

class DatabaseQuery {
	private static DatabaseQuery instance;
	public static DatabaseQuery getInstance() {
		if(instance == null) {
			synchronized (DatabaseQuery.class) {
				if(instance == null) {
					instance = new DatabaseQuery();
				}
			}
		}
		return instance;
	}

	public DeferedParamQuery getModuleQuery(QueryModuleCriteria criteria) {
		String sql = "select m.* from @moduleTable m where m.@moduleId is not null @moduleNameCriteria @disabledCriteria order by m.@createTimeCol desc";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setSnippet("moduleTable", ModuleDatabaseConfig.TABLE_MODULE.toString());
		dQuery.setSnippet("moduleId", ModuleDatabaseConfig.COLUMN_MODULE_ID.toString());
		if(TextUtils.hasText(criteria.getModuleName())) {
			dQuery.setSnippet("moduleNameCriteria", "and m." + ModuleDatabaseConfig.COLUMN_MODULE_NAME + " = :moduleName");
			dQuery.setParam("moduleName", criteria.getModuleName());
		}else {
			dQuery.setSnippet("moduleNameCriteria", null);
		}
		dQuery.setSnippet("disabledCriteria", criteria.isFilterDisabled()? "and m." + ModuleDatabaseConfig.COLUMN_MODULE_DISABLED + " is null": null);
		dQuery.setSnippet("createTimeCol", ModuleDatabaseConfig.COLUMN_MODULE_CREATE_TIME.toString());
		return dQuery;
	}

	
	public String getColumnName(ModuleDatabaseConfig column) {
		return column.getValue();
	}



	/*
	
	@SuppressWarnings("serial")
	private class EntityResultTransformer extends ColumnMapResultTransformer<DBEntity>{

		@Override
		public DBEntity build(SimpleMapWrapper mapWrapper) {
			DBEntity entity = new DBEntity();
			entity.setDbId(mapWrapper.getLong(getColumnName(ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_ID)));
			entity.setModuleId(mapWrapper.getLong(getColumnName(ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_MODULE_ID)));
			entity.setId(mapWrapper.getString(getColumnName(ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_ENTITY_ID)));
			entity.setMappingName(mapWrapper.getString(getColumnName(ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_MAPPING_NAME)));
			entity.setDefault(Integer.valueOf(1).equals(mapWrapper.getInteger(getColumnName(ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_DEFAULT))));
			entity.setCodeName(mapWrapper.getString(getColumnName(ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_CODE_NAME)));
			entity.setTitleName(mapWrapper.getString(getColumnName(ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_TITLE_NAME)));
			return entity;
		}
	}*/
	
	@SuppressWarnings({ "serial", "unchecked" })
	public List<Module> queryModule(SQLQuery configQuery, Session session) {
		return configQuery.setResultTransformer(new ColumnMapResultTransformer<TheModule>() {

			@Override
			protected TheModule build(SimpleMapWrapper mapWrapper) {
				DBModule module = new DBModule();
				module.setId(mapWrapper.getLong(ModuleDatabaseConfig.COLUMN_MODULE_ID.getValue()));
				module.setName(mapWrapper.getString(ModuleDatabaseConfig.COLUMN_MODULE_NAME.getValue()));
				module.setTitle(mapWrapper.getString(ModuleDatabaseConfig.COLUMN_MODULE_TITLE.getValue()));
				module.setDisabled(Integer.valueOf(1).equals(mapWrapper.getInteger(ModuleDatabaseConfig.COLUMN_MODULE_DISABLED.getValue())));
				module.setMappingName(mapWrapper.getString(ModuleDatabaseConfig.COLUMN_MODULE_MAPPING_NAME.getValue()));
				module.setCodeName(mapWrapper.getString(ModuleDatabaseConfig.COLUMN_MODULE_CODE_NAME.getValue()));
				module.setTitleName(mapWrapper.getString(ModuleDatabaseConfig.COLUMN_MODULE_TITLE_NAME.getValue()));
				return module;
			}
		}).list();
	}

	/**
	 * 创建模块的sql
	 * @param module
	 * @return
	 */
	public DeferedParamQuery getCreateModuleQuery(DBModule module) {
		String sql = "insert into " 
				+ ModuleDatabaseConfig.TABLE_MODULE 
				+ "(" + ModuleDatabaseConfig.COLUMN_MODULE_NAME
				+ "," + ModuleDatabaseConfig.COLUMN_MODULE_TITLE
				+ "," + ModuleDatabaseConfig.COLUMN_MODULE_MAPPING_NAME
				+ "," + ModuleDatabaseConfig.COLUMN_MODULE_CODE_NAME
				+ "," + ModuleDatabaseConfig.COLUMN_MODULE_TITLE_NAME
				+ "," + ModuleDatabaseConfig.COLUMN_MODULE_DISABLED
				+ "," + ModuleDatabaseConfig.COLUMN_MODULE_CREATE_TIME
				+ ") values(:moduleName, :moduleTitle, "
				+ ":mappingName, :codeName, :titleName, "
				+ ":disabled, :createTime)";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		if(module.getCreateTime() == null) {
			module.setCreateTime(new Date());
		}
		dQuery.setParam("moduleName", module.getName())
			.setParam("moduleTitle", module.getTitle())
			.setParam("mappingName", module.getMappingName())
			.setParam("codeName", module.getCodeName(), StandardBasicTypes.STRING)
			.setParam("titleName", module.getTitleName(), StandardBasicTypes.STRING)
			.setParam("disabled", module.isDisabled()? 1: null, StandardBasicTypes.INTEGER)
			.setParam("createTime", module.getCreateTime());
		return dQuery;
	}

	/**
	 * 根据模块的名称获得模块数据库主键的sql
	 * @param moduleName
	 * @return
	 */
	public DeferedParamQuery getQueryModuleIdQuery(String moduleName) {
		String sql = "select e." + ModuleDatabaseConfig.COLUMN_MODULE_ID + " from " + ModuleDatabaseConfig.TABLE_MODULE
				+ " e where e." + ModuleDatabaseConfig.COLUMN_MODULE_NAME + " = :moduleName";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		return dQuery.setParam("moduleName", moduleName);
	}

	/**
	 * 启用/禁用模块的sql
	 * @param moduleId
	 * @param toEnable
	 * @return
	 */
	public DeferedParamQuery getEnableModuleQuery(Long moduleId, boolean toEnable) {
		String sql = "update " + ModuleDatabaseConfig.TABLE_MODULE 
				+ " set " + ModuleDatabaseConfig.COLUMN_MODULE_DISABLED + " = :toEnable where " + ModuleDatabaseConfig.COLUMN_MODULE_ID + " = :moduleId";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setParam("moduleId", moduleId);
		return dQuery.setParam("toEnable", toEnable? null: 1, StandardBasicTypes.INTEGER);
	}

	public DeferedParamQuery getRemoveModuleQuery(Long moduleId) {
		String sql = "delete from " + ModuleDatabaseConfig.TABLE_MODULE
				+ " where " + ModuleDatabaseConfig.COLUMN_MODULE_ID + " = :moduleId";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		return dQuery.setParam("moduleId", moduleId);
	}


	public DeferedParamQuery getReassignModuleMappingNameQuery(String moduleName, String mappingName, String codeName,
			String titleName) {
		String sql = "update " + ModuleDatabaseConfig.TABLE_MODULE
				+ " set " + ModuleDatabaseConfig.COLUMN_MODULE_MAPPING_NAME + " = :mappingName"
				+ ", " + ModuleDatabaseConfig.COLUMN_MODULE_CODE_NAME + " = :codeName"
				+ ", " + ModuleDatabaseConfig.COLUMN_MODULE_TITLE_NAME + " = :titleName "
				+ "where " + ModuleDatabaseConfig.COLUMN_MODULE_NAME + " = :moduleName";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setParam("mappingName", mappingName);
		dQuery.setParam("codeName", codeName, StandardBasicTypes.STRING);
		dQuery.setParam("titleName", titleName, StandardBasicTypes.STRING);
		dQuery.setParam("moduleName", moduleName);
		return dQuery;
	}

	public DeferedParamQuery getReassignModuleMappingNameQuery(String moduleName, String mappingName) {
		String sql = "update " + ModuleDatabaseConfig.TABLE_MODULE
				+ " set " + ModuleDatabaseConfig.COLUMN_MODULE_MAPPING_NAME + " = :mappingName"
				+ "where " + ModuleDatabaseConfig.COLUMN_MODULE_NAME + " = :moduleName";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setParam("mappingName", mappingName);
		dQuery.setParam("moduleName", moduleName);
		return dQuery;
	}

	public DeferedParamQuery getUpdateModulePropertyNameQuery(String moduleName, String codeName, String titleName) {
		String sql = "update " + ModuleDatabaseConfig.TABLE_MODULE
				+ " set " + ModuleDatabaseConfig.COLUMN_MODULE_CODE_NAME + " = :codeName "
				+ " , " + ModuleDatabaseConfig.COLUMN_MODULE_TITLE_NAME + " = :titleName "
				+ "where " + ModuleDatabaseConfig.COLUMN_MODULE_NAME + " = :moduleName";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setParam("codeName", codeName, StandardBasicTypes.STRING);
		dQuery.setParam("titleName", titleName, StandardBasicTypes.STRING);
		dQuery.setParam("moduleName", moduleName);
		return dQuery;
	}

	public DeferedParamQuery getUpdateModuleCodeNameQuery(String moduleName, String codeName) {
		String sql = "update " + ModuleDatabaseConfig.TABLE_MODULE
				+ " set " + ModuleDatabaseConfig.COLUMN_MODULE_CODE_NAME + " = :codeName "
				+ "where " + ModuleDatabaseConfig.COLUMN_MODULE_NAME + " = :moduleName";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setParam("codeName", codeName, StandardBasicTypes.STRING);
		dQuery.setParam("moduleName", moduleName);
		return dQuery;
	}

	public DeferedParamQuery getUpdateModuleTitleNameQuery(String moduleName, String titleName) {
		String sql = "update " + ModuleDatabaseConfig.TABLE_MODULE
				+ " set " + ModuleDatabaseConfig.COLUMN_MODULE_TITLE_NAME + " = :titleName "
				+ "where " + ModuleDatabaseConfig.COLUMN_MODULE_NAME + " = :moduleName";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setParam("titleName", titleName, StandardBasicTypes.STRING);
		dQuery.setParam("moduleName", moduleName);
		return dQuery;
	}
	
}
