package cn.sowell.datacenter.entityResolver.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;

import cn.sowell.copframe.dao.deferedQuery.ColumnMapResultTransformer;
import cn.sowell.copframe.dao.deferedQuery.DeferedParamQuery;
import cn.sowell.copframe.dao.deferedQuery.SimpleMapWrapper;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.entityResolver.config.abst.Composite;
import cn.sowell.datacenter.entityResolver.config.abst.Function;
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
		String sql = "select m.* from " + ModuleDatabaseConfig.TABLE_MODULE 
				+ " m where m." + ModuleDatabaseConfig.COLUMN_MODULE_ID + " is not null";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		if(TextUtils.hasText(criteria.getModuleName())) {
			dQuery.appendCondition("and m." + ModuleDatabaseConfig.COLUMN_MODULE_NAME + " = :moduleName");
			dQuery.setParam("moduleName", criteria.getModuleName());
		}
		if(criteria.isFilterDisabled()) {
			dQuery.appendCondition("and m." + ModuleDatabaseConfig.COLUMN_MODULE_DISABLED + " is null");
		}
		return dQuery;
	}

	private DeferedParamQuery createDeferredQuery(Set<Long> moduleIds) {
		String sql = "select e.* from @table e where e.@moduleIdName in (:moduleIds)";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setParam("moduleIds", moduleIds, StandardBasicTypes.LONG);
		return dQuery;
	}
	
	public String getColumnName(ModuleDatabaseConfig column) {
		return column.getValue();
	}

	public SQLQuery getEntitiesQuery(Session session, Set<Long> moduleIds) {
		DeferedParamQuery dQuery = createDeferredQuery(moduleIds);
		dQuery.setSnippet("table", ModuleDatabaseConfig.TABLE_MODULE_ENTITY.getValue());
		dQuery.setSnippet("moduleIdName", ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_MODULE_ID.getValue());
		return dQuery.createSQLQuery(session, false, null);
	}

	public SQLQuery getImportCompositeByEntitiesQuery(Session session, Set<Long> entityIds) {
		String sql = "select c.* from @compositeTable c where c.@entityIdCol in (:entityIds)";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setSnippet("compositeTable", ModuleDatabaseConfig.TABLE_MODULE_IMPORT_COMPOSITE.toString())
			.setSnippet("entityIdCol", ModuleDatabaseConfig.COLUMN_MODULE_IMPORT_COMPOSITE_ENTITY_ID.toString())
			.setParam("entityIds", entityIds, StandardBasicTypes.LONG)
			;
		return dQuery.createSQLQuery(session, false, null);
	}
	
	public DeferedParamQuery getImportCompositeQuery(String entityId) {
		String sql = "select c.* from @compositeTable c where c.@entityIdCol = :entityId";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setSnippet("compositeTable", ModuleDatabaseConfig.TABLE_MODULE_IMPORT_COMPOSITE.toString())
			.setSnippet("entityIdCol", ModuleDatabaseConfig.COLUMN_MODULE_IMPORT_COMPOSITE_ENTITY_ID.toString())
			.setParam("entityId", entityId);
		return dQuery;
	}

	

	public SQLQuery getFunctionQuery(Session session, Set<Long> moduleIds) {
		DeferedParamQuery dQuery = createDeferredQuery(moduleIds);
		dQuery.setSnippet("table", ModuleDatabaseConfig.TABLE_MODULE_FUNCTION.getValue());
		dQuery.setSnippet("moduleIdName", ModuleDatabaseConfig.COLUMN_MODULE_FUNCTION_MODULE_ID.getValue());
		return dQuery.createSQLQuery(session, false, null);
	}
	
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
	}
	private final EntityResultTransformer entityResultTransformer = new EntityResultTransformer();
	
	@SuppressWarnings("serial")
	public Set<Module> queryModule(SQLQuery configQuery, Session session) {
		Map<Long, TheModule> moduleMap = new LinkedHashMap<>();
		configQuery.setResultTransformer(new ColumnMapResultTransformer<TheModule>() {

			@Override
			protected TheModule build(SimpleMapWrapper mapWrapper) {
				TheModule module = new TheModule();
				Long moduleId = mapWrapper.getLong(getColumnName(ModuleDatabaseConfig.COLUMN_MODULE_ID));
				String moduleName = mapWrapper.getString(getColumnName(ModuleDatabaseConfig.COLUMN_MODULE_NAME));
				String moduleTitle = mapWrapper.getString(getColumnName(ModuleDatabaseConfig.COLUMN_MODULE_TITLE));
				module.setName(moduleName);
				module.setTitle(moduleTitle);
				module.setDisabled(Integer.valueOf(1).equals(mapWrapper.getInteger(getColumnName(ModuleDatabaseConfig.COLUMN_MODULE_DISABLED))));
				moduleMap.put(moduleId, module);
				return module;
			}
		}).list();
		if(!moduleMap.isEmpty()) {
			SQLQuery entityQuery = getEntitiesQuery(session, moduleMap.keySet());
			Map<Long, Set<DBEntity>> moduleEntitiesMap = new HashMap<>();
			Map<Long, DBEntity> allEntitiesMap = new HashMap<>();
			entityQuery.setResultTransformer(new ColumnMapResultTransformer<DBEntity>() {

				@Override
				protected DBEntity build(SimpleMapWrapper mapWrapper) {
					DBEntity entity = entityResultTransformer.build(mapWrapper);
					Long moduleId = entity.getModuleId();
					if(!moduleEntitiesMap.containsKey(moduleId)) {
						moduleEntitiesMap.put(moduleId, new LinkedHashSet<>());
					}
					moduleEntitiesMap.get(moduleId).add(entity);
					allEntitiesMap.put(entity.getDbId(), entity);
					return entity;
				}
			}).list();
			
			Map<Long, Set<Composite>> compositeMap = new HashMap<>();
			if(!allEntitiesMap.isEmpty()) {
				SQLQuery compositeQuery = getImportCompositeByEntitiesQuery(session, allEntitiesMap.keySet());
				compositeQuery.setResultTransformer(new ColumnMapResultTransformer<TheComposite>() {
					
					@Override
					protected TheComposite build(SimpleMapWrapper mapWrapper) {
						TheComposite composite = new TheComposite();
						Long entityId = mapWrapper.getLong(getColumnName(ModuleDatabaseConfig.COLUMN_MODULE_IMPORT_COMPOSITE_ENTITY_ID));
						composite.setTitle(mapWrapper.getString(getColumnName(ModuleDatabaseConfig.COLUMN_MODULE_IMPORT_COMPOSITE_TITLE)));
						composite.setName(mapWrapper.getString(getColumnName(ModuleDatabaseConfig.COLUMN_MODULE_IMPORT_COMPOSITE_NAME)));
						if(allEntitiesMap.containsKey(entityId)) {
							composite.setEntityId(allEntitiesMap.get(entityId).getId());
						}
						Long moduleId = allEntitiesMap.get(entityId).getModuleId();
						if(!compositeMap.containsKey(moduleId)) {
							compositeMap.put(moduleId, new LinkedHashSet<>());
						}
						compositeMap.get(moduleId).add(composite);
						return composite;
					}
				}).list();
			}
			
			
			SQLQuery functionQuery = getFunctionQuery(session, moduleMap.keySet());
			Map<Long, Set<Function>> functionsMap = new HashMap<>(); 
			functionQuery.setResultTransformer(new ColumnMapResultTransformer<Function>() {

				@Override
				protected Function build(SimpleMapWrapper mapWrapper) {
					TheFunction function = new TheFunction();
					Long moduleId = mapWrapper.getLong(getColumnName(ModuleDatabaseConfig.COLUMN_MODULE_FUNCTION_MODULE_ID));
					function.setName(mapWrapper.getString(getColumnName(ModuleDatabaseConfig.COLUMN_MODULE_FUNCTION_NAME)));
					if(!functionsMap.containsKey(moduleId)) {
						functionsMap.put(moduleId, new LinkedHashSet<>());
					}
					functionsMap.get(moduleId).add(function);
					return function;
				}
			}).list();
			
			moduleMap.forEach((moduleId, module)->{
				module.setEntities(new LinkedHashSet<>(moduleEntitiesMap.get(moduleId)));
				TheImport imp = new TheImport();
				imp.setComposites(new LinkedHashSet<>(compositeMap.get(moduleId)));
				module.setImport(imp);
				TheFunctions functions = new TheFunctions();
				functions.setFunctions(new LinkedHashSet<>(functionsMap.get(moduleId)));
				module.setFunctions(functions);
			});
		}
		return new LinkedHashSet<Module>(moduleMap.values());
	}

	/**
	 * 创建模块的sql
	 * @param module
	 * @return
	 */
	public DeferedParamQuery getCreateModuleQuery(TheModule module) {
		String sql = "insert into " 
				+ ModuleDatabaseConfig.TABLE_MODULE 
				+ "(" + ModuleDatabaseConfig.COLUMN_MODULE_NAME
				+ "," + ModuleDatabaseConfig.COLUMN_MODULE_TITLE
				+ "," + ModuleDatabaseConfig.COLUMN_MODULE_DISABLED
				+ ") values(:moduleName, :moduleTitle, :disabled)";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setParam("moduleName", module.getName())
			.setParam("moduleTitle", module.getTitle())
			.setParam("disabled", module.isDisabled()? 1: null, StandardBasicTypes.INTEGER);
		return dQuery;
	}

	/**
	 * 创建实体的sql
	 * @param entity
	 * @return
	 */
	public DeferedParamQuery getCreateEntityQuery(DBEntity entity) {
		String sql = "insert into "
				+ ModuleDatabaseConfig.TABLE_MODULE_ENTITY
				+ "(" + ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_ENTITY_ID
				+ "," + ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_MAPPING_NAME
				+ "," + ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_DEFAULT
				+ "," + ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_CODE_NAME
				+ "," + ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_TITLE_NAME
				+ "," + ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_MODULE_ID
				+ ") values(:entityId, :mappingName, :default, :codeName, :titleName, :moduleId)";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setParam("entityId", entity.getId())
			.setParam("mappingName", entity.getMappingName())
			.setParam("default", entity.isDefault()? 1: null, StandardBasicTypes.INTEGER)
			.setParam("codeName", entity.getCodeName(), StandardBasicTypes.STRING)
			.setParam("titleName", entity.getTitleName(), StandardBasicTypes.STRING)
			.setParam("moduleId", entity.getModuleId());
		return dQuery;
		
		
	}

	/**
	 * 根据entityId字符串获得对应的数据库主键
	 * @param entityId
	 * @return
	 */
	public DeferedParamQuery getQueryEntityIdQuery(String entityId) {
		String sql = "select e." + ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_ID + " from " + ModuleDatabaseConfig.TABLE_MODULE_ENTITY
						+ " e where e." + ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_ENTITY_ID + " = :entityId";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		return dQuery.setParam("entityId", entityId);
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
				+ " set " + ModuleDatabaseConfig.COLUMN_MODULE_DISABLED + " = :toEnable";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		return dQuery.setParam("toEnable", toEnable? null: 1, StandardBasicTypes.INTEGER);
	}

	public DeferedParamQuery getRemoveModuleQuery(Long moduleId) {
		String sql = "delete from " + ModuleDatabaseConfig.TABLE_MODULE
				+ " where " + ModuleDatabaseConfig.COLUMN_MODULE_ID + " = :moduleId";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		return dQuery.setParam("moduleId", moduleId);
	}

	/**
	 * 根据实体对象
	 * @param entityId
	 * @param session
	 * @return
	 */
	public SQLQuery getQueryEntityQuery(String entityId, Session session) {
		String sql = "select * from " + ModuleDatabaseConfig.TABLE_MODULE_ENTITY
				+ " e where e." + ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_ENTITY_ID
				+ " = :entityId";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setParam("entityId", entityId);
		SQLQuery query = dQuery.createSQLQuery(session, false, null);
		query.setResultTransformer(entityResultTransformer);
		return query;
	}

	public DeferedParamQuery getReassignMappingNameQuery(String entityId, String mappingName, String codeName,
			String titleName) {
		String sql = "update " + ModuleDatabaseConfig.TABLE_MODULE_ENTITY
				+ " set " + ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_MAPPING_NAME 
				+ " = :mappingName"
				+ ", " + ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_CODE_NAME + " = :codeName"
				+ ", " + ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_TITLE_NAME + " = :titleName";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setParam("mappingName", mappingName);
		dQuery.setParam("codeName", codeName, StandardBasicTypes.STRING);
		dQuery.setParam("titleName", titleName, StandardBasicTypes.STRING);
		return dQuery;
	}
	
	/**
	 * 重新指定实体配置名的sql
	 * @param entityId
	 * @param mappingName
	 * @return
	 */
	public DeferedParamQuery getReassignMappingNameQuery(String entityId, String mappingName) {
		String sql = "update " + ModuleDatabaseConfig.TABLE_MODULE_ENTITY
				+ " set " + ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_MAPPING_NAME 
				+ " = :mappingName";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setParam("mappingName", mappingName);
		return dQuery;
	}

	public DeferedParamQuery getRemoveEntityQuery(String entityId) {
		String sql = "delete from " + ModuleDatabaseConfig.TABLE_MODULE_ENTITY
				+ " where " + ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_ENTITY_ID
				+ " = :entityId";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setParam("entityId", entityId);
		return dQuery;
	}

	public DeferedParamQuery getDefaultEntityOfEntitySiblingsQuery(String entityId) {
		String sql = "select d.* from @entityTable m "
				+ " left join @moduleTable d on m.@moduleIdCol = d.@moduleIdCol and d.@defaultCol = 1 "
				+ " where m.@entityIdCol = :entityId";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setSnippet("entityTable", ModuleDatabaseConfig.TABLE_MODULE_ENTITY.toString())
			.setSnippet("moduleIdCol", ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_MODULE_ID.toString())
			.setSnippet("entityIdCol", ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_ENTITY_ID.toString())
			.setSnippet("defaultCol", ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_DEFAULT.toString())
			.setParam("entityId", entityId);
		dQuery.setResultTransformer(entityResultTransformer);
		dQuery.setResultTransformer(entityResultTransformer);
		return dQuery;
	}

	public DeferedParamQuery getChangeEntityDefaultStatusQuery(String entityId, boolean asDefault) {
		String sql = "update @entityTable set @defaultCol = :default where @entityIdCol = :entityId";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setSnippet("defaultCol", ModuleDatabaseConfig.TABLE_MODULE_ENTITY.toString())
			.setSnippet("entityIdCol", ModuleDatabaseConfig.COLUMN_MODULE_ENTITY_ENTITY_ID.toString())
			.setParam("default", asDefault? 1: null, StandardBasicTypes.INTEGER)
			.setParam("entityId", entityId);
		return dQuery;
	}

	public DeferedParamQuery getAddImportCompositeQuery(TheComposite composite) {
		String sql = "insert into @compositeTable("
				+ ModuleDatabaseConfig.COLUMN_MODULE_IMPORT_COMPOSITE_NAME
				+ "," + ModuleDatabaseConfig.COLUMN_MODULE_IMPORT_COMPOSITE_TITLE
				+ "," + ModuleDatabaseConfig.COLUMN_MODULE_IMPORT_COMPOSITE_ENTITY_ID
				+ ") values(:name, :title, :entityId, :moduleId)";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setSnippet("compositeTable", ModuleDatabaseConfig.TABLE_MODULE_IMPORT_COMPOSITE.toString())
			.setParam("name", composite.getName())
			.setParam("title", composite.getTitle())
			.setParam("entityId", composite.getEntityId());
		return dQuery;
	}

	public DeferedParamQuery getRetitleImportCompositeQuery(String entityId, String impTitle) {
		String sql = "update @compositeTable set @titleCol = :title where @entityIdCol = :entityId";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setSnippet("compositeTable", ModuleDatabaseConfig.TABLE_MODULE_IMPORT_COMPOSITE.toString())
			.setSnippet("titleCol", ModuleDatabaseConfig.COLUMN_MODULE_IMPORT_COMPOSITE_TITLE.toString())
			.setSnippet("entityIdCol", ModuleDatabaseConfig.COLUMN_MODULE_IMPORT_COMPOSITE_ENTITY_ID.toString())
			.setParam("title", impTitle)
			.setParam("entityId", entityId);
		return dQuery;
	}

	public DeferedParamQuery getRemoveImportCompositeQuery(String entityId) {
		String sql = "delete from @compositeTable set @entityIdCol = :entityId";
		DeferedParamQuery dQuery = new DeferedParamQuery(sql);
		dQuery.setSnippet("compositeTable", ModuleDatabaseConfig.TABLE_MODULE_IMPORT_COMPOSITE.toString())
			.setSnippet("entityIdCol", ModuleDatabaseConfig.COLUMN_MODULE_IMPORT_COMPOSITE_ENTITY_ID.toString())
			.setParam("entityId", entityId);
		return dQuery;
	}

	
	
	

}
