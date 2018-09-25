package com.gzz.createcode.mvc.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gzz.createcode.common.Utils;
import com.gzz.createcode.mvc.dao.CodeDao;
import com.gzz.createcode.mvc.model.CodeCond;
import com.gzz.createcode.mvc.model.Field;
import com.gzz.createcode.mvc.model.Table;

/**
 * @功能描述:生成列表类型代码的实现类
 * @author gzz_gzz@163.com
 * @date 2018-02-15
 */
@Service
public class CodeService {

//	private Log logger = LogFactory.getLog(CodeService.class);// 日志类
	@Autowired
	protected CodeDao dao;

	@Autowired
	protected FreemarkerUtils utils;

	/**
	 * @功能描述:生成代码
	 */
	public void create(CodeCond cond) {

		String dateFormart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

		String auth = cond.getAuth();// 作者
		String pName;// 包名
		String path;// 路径
		for (Table table : cond.getC_list()) {
			cond.setT_name_eq(table.getT_name());// 表名
			List<Field> fList = dao.queryFields(cond);// 字段列表
			String cName = table.getC_name();// 表注释中文名
			String upp = table.getCls_upp();// 驼峰类名(首字母大写)
			String low = upp.toLowerCase();// 类名小写(只用包路径)
			String lowUpp = Utils.firstLower(upp);// 驼峰变量类名(首字母小写)
			String idType = Utils.keyType(fList);// 主键数据类型
			String idName = fList.get(0).getName();
			Map<String, Object> params = Maps.newHashMap();
			params.put("fList", fList);
			params.put("auth", auth);
			params.put("cName", cName);
			params.put("upp", upp);
			params.put("lowUpp", lowUpp);
			params.put("idType", idType);
			params.put("table", table.getT_name());
			params.put("id", fList.get(0));
			params.put("cond", cond);
			params.put("tName", table.getT_name());
			params.put("idName", fList.get(0).getName());

			params.put("time", dateFormart);
			List<String> importList = Lists.newArrayList();
			importList.add(Utils.dateImport(fList));
			importList.add(Utils.bigImport(fList));
			params.put("importList", importList);
			params.put("selectFields", Utils.add(fList, "t.", ",", false, "select"));
			params.put("insertFields", Utils.add(fList, "", ",", true, "insert"));
			params.put("insertValuesFields", Utils.add(fList, ":", ",", true, "insert"));
			params.put("replaceFields", Utils.add(fList, "", ",", false, "sql"));
			params.put("replaceValuesFields", Utils.add(fList));
			params.put("paramsFields", Utils.add(fList, "vo.get", "(),", false));
			params.put("updateFields", Utils.add(fList, "", "=?,", true, "sql"));
			params.put("updateParams",
					Utils.add(fList, "vo.get", "(),", true) + ",vo.get" + Utils.firstUpper(idName) + "()");

			pName = cond.pack("createcode", low);
			path = cond.base("createcode", low, upp);
//			path = cond.base("ios", low, upp);
//			Utils.write(path + ".h", IosModelH.create( upp, fList, auth, cName));
//			Utils.write(path + ".m", IosModelM.create( upp, fList, auth, cName));

			pName = cond.pack("common", low);
			path = cond.base("common", low, upp);
			params.put("pName", pName);
			utils.parse("code/Model.java", params, path + ".java");
			utils.parse("code/Cond.java", params, path + "Cond.java");

			pName = cond.pack("createcode", low);
			path = cond.base("createcode", low, upp);
			params.put("pName", pName);
			utils.parse("code/Dao.java", params, path + "Dao.java");
			utils.parse("code/Service.java", params, path + "Service.java");
			utils.parse("code/Controller.java", params, path + "Controller.java");

		}

	}

	/**
	 * @功能描述: 查询数据库中表名列表
	 */
	public List<Table> queryTables(CodeCond para) {
		List<Table> list = dao.queryTables(para);
		list.forEach(item -> {
			item.setCls_upp(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, Utils.delFirWord(item.getT_name())));
			item.setC_name(item.getComment());
		});
		return list;
	}

	/**
	 * @功能描述: 查询数据库中字段名列表
	 */
	public List<Field> queryFields(CodeCond cond) {
		return dao.queryFields(cond);
	}

	public void executeSql(String sql) {
		dao.executeSql(sql);
	}
}
