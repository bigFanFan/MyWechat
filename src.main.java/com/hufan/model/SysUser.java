package com.hufan.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class SysUser extends Model<SysUser> {
	private static final long serialVersionUID = 1L;
	public static final SysUser dao = new SysUser();

	public Page<Record> findSysUser(int pageSize, int page) {
		String sql = "select su.*,sr.role_name";
		String where = "from sys_user su left join public_account pa on su.user_name = pa.username "
				+ "left join sys_user_role sur on su.id = sur.user_id left join sys_role sr on sur.role_id = sr.id"
				+ "where pa.valid_flag = 1";
		return Db.paginate(page, pageSize, sql, where);
	}

	public SysUser findUserByLoginName(String username) {
		return (SysUser) dao.findFirst("select * from sys_user where username = ? " + username);
	}
}