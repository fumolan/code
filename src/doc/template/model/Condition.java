package com.gzz.createcode.template.model;

import java.util.List;

import com.gzz.createcode.common.Utils;
import com.gzz.createcode.mvc.model.Field;

public class Condition {
	public static StringBuilder create(String pName, String clsUpp, List<Field> fList, String auth, String cName) {
		StringBuilder sb = new StringBuilder();
		sb.append("package " + pName + ";");
		sb.append(Utils.dateImport(fList));
		sb.append(Utils.bigImport(fList));
		sb.append("\r\nimport lombok.Getter;");
		sb.append("\r\nimport lombok.Setter;");
 		sb.append("\r\nimport lombok.AllArgsConstructor;");
		sb.append("\r\nimport lombok.Builder;");
 		sb.append("\r\nimport lombok.NoArgsConstructor;");
		sb.append("\r\nimport lombok.experimental.Accessors;");
		sb.append("\r\n");
		sb.append("\r\nimport com.dl.keep.common.util.base.BaseCondition;");
		sb.append(Utils.classNote(auth, cName + "查询条件实体类"));
		sb.append("\r\n@Setter");
		sb.append("\r\n@Getter");
		sb.append("\r\n//@Accessors(chain = true)");
		sb.append("\r\n@Builder");
 		sb.append("\r\n@AllArgsConstructor");
 		sb.append("\r\n@NoArgsConstructor");
		sb.append("\r\n@ApiModel(value = \"" + clsUpp + "\", description = \"" + cName + "查询条件实体\")");
		sb.append("\r\npublic class " + clsUpp + "Cond extends BaseCondition {");
		StringBuilder field = new StringBuilder();
		StringBuilder cond = new StringBuilder();
		for (Field fie : fList) {
			String fName = fie.getName();
			String type = fie.getType();
			field.append("\r\n\t@ApiModelProperty(hidden = true)");
			field.append("\r\n\tprivate " + type + " " + fName + "; // " + fie.getComment());
			cond.append("\r\n\t\t//add(" + fName + ", \"AND t." + fName + (type.equals("String") ? " LIKE ?\", 3);" : " = ?\");"));
		}
		cond.append("\r\n\t\t//add(ids, \"AND t.id IN \");");

		sb.append(Utils.methodNote("拼加自定义条件 "));
		sb.append("\r\n	@Override");
		sb.append("\r\n	public void addCondition() { ");
		sb.append(cond);
		sb.append("\r\n	}");
		sb.append("\r\n");
		sb.append("\r\n\t//查询条件,把不用的条件清理掉");
		sb.append(field);
		
		sb.append("\r\n\t//private List<Long> ids;// 主键列表");

		sb.append("\r\n");
		sb.append("\r\n}");
		return sb;
	}
}