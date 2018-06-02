package com.hufan.model;

import com.jfinal.plugin.activerecord.Model;

import java.util.List;

/**
 * @Author: HuFan
 * @Description:
 * @Date: Create in 2018/6/2 13:03
 * @Modified By:
 */
public class SysMenu extends Model<SysMenu> {
    private static final long serialVersionUID = 1L;

    public static final SysMenu dao = new SysMenu();

    public void sortMenu(boolean root, SysUser user, List<SysMenu> childMenu, StringBuffer menu) {
        if (root) {
            childMenu = dao.getMenu(user.getInt("id"), Integer.valueOf(0));
            menu.append("<ul class=\'nav nav-list\'>");
        } else if (childMenu.size() > 0){
            menu.append("<ul class=\'submenu\'>\n");
        } else {
            return;
        }
        root = false;
        for (SysMenu item : childMenu) {
            List _childMenu = null;
            menu.append("<li>\n");
            menu.append("<a href=\"");
            menu.append(item.getStr("href"));
            menu.append("\"");

            if ("#".equals(item.getStr("href"))) {
                menu.append("class=\"dropdown-toggle\">");
            }
            menu.append("\n");
            menu.append("<i class=\"" + item.getStr("ico_path") + "\"></i>\n");
            menu.append("<span class=\"menu-text\">");
            menu.append(item.getStr("menu_name"));
            menu.append("</span>\n");
            menu.append("</a>\n");
            sortMenu(root, user, _childMenu, menu);
            menu.append("</li>\n");
        }
        menu.append("</ul>");
    }

    public List<SysMenu> getMenu(Integer userId, Integer pid) {
        StringBuffer sb = new StringBuffer();
        sb.append(" select * from sys_user_role a,sys_role_menu b,sys_menu c");
        sb.append(" where a.role_id = b.role_id and b.menu_id = c.id");
        sb.append(" and c.valid_flag = '1' ");
        sb.append(" and a.user_id = " + userId);
        sb.append(" and c.pid = " + pid);
        return dao.find(sb.toString());
    }
}
