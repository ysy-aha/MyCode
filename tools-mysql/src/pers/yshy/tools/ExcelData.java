package pers.yshy.tools;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 读取excel文件数据，然后生成sql语句
 *
 * @author ysy
 * @date 2021/11/21
 */
public class ExcelData {

	public static void main(String[] args) {
		String filePath = "tools-mysql/excel/数据库设计.xlsx";
		try {
			// 读取Excel中的数据
			List<Map> tables = readExcel(filePath);
			// 写出sql文件
			writeData(tables);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取Excel文件中的数据
	 *
	 * @param filePath excel文件地址
	 * @return
	 */
	private static List<Map> readExcel(String filePath) throws Exception {
		List<Map> res = new ArrayList<>();
		// 获取excel文件, 如果文件不存在，抛出异常
		File file = new File(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException("文件不存在");
		}
		InputStream is = new FileInputStream(file);

		// 读取excel文件，获取所有的表
		XSSFWorkbook sheets = new XSSFWorkbook(is);
		try {
			for (int i = 0; i < sheets.getNumberOfSheets(); i++) {
				XSSFSheet sheet = sheets.getSheetAt(i);
				Map<String, Object> table = getTable(sheet);
				res.add(table);
			}
		} finally {
			sheets.close();
			is.close();
		}
		return res;
	}

	/**
	 * 读取表数据
	 *
	 * @param sheet 表名称
	 * @return
	 */
	private static Map<String, Object> getTable(XSSFSheet sheet) throws Exception {
		Map<String, Object> table = new HashMap<>(4);
		//取中英文表名和自增长
		XSSFRow row0 = sheet.getRow(0);
		XSSFRow row1 = sheet.getRow(1);
		table.put("tabName_zh", analyticTabName(row0).get(0));
		table.put("tabName_en", analyticTabName(row1).get(1));
		table.put("autoIncrement", analyticTabName(row1).get(3));
		// 循环取接下来的数据
		List<Map> tableStructre = new ArrayList<>();
		for (int i = 3; i < sheet.getPhysicalNumberOfRows(); i++) {
			XSSFRow row = sheet.getRow(i);
			Map<String, String> tableRow = new HashMap<>(8);
			// 如果字段名为空，说明到了最底下，直接退出
			if (row.getCell(0).getCellTypeEnum() == CellType.BLANK) {
				break;
			}
			// 字段名
			tableRow.put("nameEN", checkVal(row.getCell(0)));
			// 字段中文名
			tableRow.put("nameZH", checkVal(row.getCell(1), true));
			// 类型
			tableRow.put("type", checkVal(row.getCell(2), true));
			// 长度
			tableRow.put("length", checkVal(row.getCell(3)));
			// 是否必填
			tableRow.put("notNull", checkVal(row.getCell(4)));
			// 默认
			tableRow.put("default", checkVal(row.getCell(5)));
			// 是否是主键
			tableRow.put("isKey", checkVal(row.getCell(6)));
			// 备注
			tableRow.put("remark", checkVal(row.getCell(7)));
			tableStructre.add(tableRow);
		}
		table.put("body", tableStructre);
		return table;
	}

	/**
	 * 解析第一行和第二行那种合并单元格内容(只有String内容)
	 *
	 * @param row
	 * @return
	 * @throws Exception
	 */
	private static List analyticTabName(XSSFRow row) throws Exception {
		if (row.getPhysicalNumberOfCells() == 0) {
			throw new Exception("表格错误");
		}
		List<String> list = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			if (row.getCell(i).getCellTypeEnum() == CellType.BLANK) {
				continue;
			}
			list.add(row.getCell(i).getStringCellValue());
		}
		return list;
	}

	/**
	 * 判断列中的数据
	 *
	 * @param cell 列数据
	 * @return
	 */
	private static String checkVal(XSSFCell cell) {
		if (cell == null) {
			return "";
		}
		if (cell.getCellTypeEnum() == CellType.NUMERIC) {
			return String.valueOf(cell.getNumericCellValue());
		}
		return cell.getStringCellValue().trim();
	}

	/**
	 * 判断列中的数据
	 *
	 * @param cell    列数据
	 * @param notNull 是否能为空
	 * @return
	 * @Exception 如果列数据为null或者
	 */
	private static String checkVal(XSSFCell cell, boolean notNull) throws NullPointerException {
		if (cell == null || cell.getStringCellValue().trim().isEmpty()) {
			throw new NullPointerException("必填数据不能为空");
		}
		if (cell.getCellTypeEnum() == CellType.NUMERIC) {
			return String.valueOf(cell.getNumericCellValue());
		}
		return cell.getStringCellValue().trim();
	}

	private static void writeData(List<Map> tables) throws IOException {
		OutputStream os = null;
		for (int i = 0; i < tables.size(); i++) {
			HashMap<String, Object> table = (HashMap<String, Object>) tables.get(i);
			StringBuilder str = new StringBuilder();
			StringBuilder remark = new StringBuilder();
			List<String> keys = new ArrayList<>();

			String tabName_en = table.get("tabName_en").toString();
			boolean autoIncrement = table.get("autoIncrement").toString().equals("Y");
			ArrayList<HashMap<String, String>> tableStructre = (ArrayList<HashMap<String, String>>) table.get("body");
			// 删除已存在的表
			str.append("drop table if exists `" + tabName_en + "`;\n");
			// 组装创建表语句
			str.append("create table if not exists `" + tabName_en + "` (\n");
			for (int j = 0; j < tableStructre.size(); j++) {
				HashMap<String, String> structre = tableStructre.get(j);
				// 类型
				str.append("\t`" + structre.get("nameEN") + "` " + structre.get("type"));
				// 长度
				if (!structre.get("length").isEmpty()) {
					str.append("(" + structre.get("length") + ")");
				}
				// 是否为空
				if (structre.get("notNull").equals("Y")) {
					str.append(" not null");
				}
				boolean key = structre.get("isKey").equals("Y");
				// 是否存在默认值，如果是主键自增，就不能添加默认值
				if (structre.get("default").equals("Y") && (!key || !autoIncrement)) {
					str.append(" default " + structre.get("default"));
				}
				// 主键，自增
				if (key) {
					keys.add(structre.get("nameEN"));
					if (autoIncrement) {
						str.append(" AUTO_INCREMENT");
					}
				}
				// 注释
				str.append(" comment \"" + structre.get("nameZH"));
				if (!structre.get("remark").isEmpty()) {
					str.append(" " + structre.get("remark"));
				}
				str.append("\",\n");
			}
			if (keys.size() > 0) {
				str.append("\tPRIMARY KEY (`");
				for (String key : keys) {
					str.append(key + "`,");
				}
				str.delete(str.length() - 1, str.length());
				str.append(")\n");
			}
			str.append(") COMMENT \"" + table.get("tabName_zh").toString() + "\";");


			File file = new File("tools-mysql/sql/" + tabName_en + ".sql");

			os = new FileOutputStream(file);
			byte[] data = str.toString().getBytes();
			os.write(data);
			os.close();
		}
	}
}
