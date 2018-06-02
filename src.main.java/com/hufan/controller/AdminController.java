package com.hufan.controller;

import com.hufan.config.AppConst;
import com.hufan.model.PublicAccount;
import com.hufan.model.SysMenu;
import com.hufan.model.SysUser;
import com.jfinal.core.JFinal;
import com.jfinal.log.Logger;
import com.revocn.controller.BaseController;
import com.revocn.mapping.RoleMapping;
import com.revocn.util.BlankUtil;
import com.revocn.util.MD5Util;
import com.revocn.util.StringUtil;


/**
 * @Author: HuFan
 * @Description:
 * @Date: Create in 2018/6/2 11:36
 * @Modified By:
 */
public class AdminController extends BaseController {
    protected final static Logger logger = Logger.getLogger(AdminController.class);

    public void index() {
        SysUser user = getSessionAttr(AppConst.KEY_SESSION_USER);
        if (BlankUtil.isBlankModel(user)) {
            if (JFinal.me().getConstants().getDevMode()) {
                setAttr("uuu", "admin");
            }
            render(AppConst.PATH_MANAGE_PC + "/login.ftl");
        } else {
            render(AppConst.PATH_MANAGE_PC + "/index.ftl");
        }
    }

    public void login() {
        if (StringUtil.isNull(getPara("user_name"))) {
            redirect("m/index");
        }
        SysUser user = getSessionAttr(AppConst.KEY_SESSION_USER);
        if (user != null) {
            redirect("/m/index");
            return;
        }
        String username = getPara("user_name");
        String pwd = MD5Util.md5(getPara("pwd"));
        String rememberme = getPara("rememberme");
        String errormsg = "";

        user = SysUser.dao.findUserByLoginName(username);

        if (user == null) {
            errormsg = "用户名或密码错误";
        } else {
            PublicAccount pa = PublicAccount.dao.findUserByLoginName(username);
            if (pa.get("valid_flag").equals("1")) {
                errormsg = "账户状态异常，可能已被锁定，或者未审核";
                render(AppConst.PATH_ERROR + "/noPermission.html");
                return;
            }
            if (user.getStr("pwd").equals(pwd) && user.getStr("user_name").equals(username)) {
                setSessionAttr(AppConst.KEY_SESSION_USER, user);
                StringBuffer menu = new StringBuffer();
                new SysMenu().sortMenu(true, user, null, menu);
                setSessionAttr(AppConst.KEY_SESSION_MENU, menu.toString());
                setSessionAttr(AppConst.KEY_SESSION_RIGHT, RoleMapping.getRightMap());
                redirect("/m/index");
                return;
            } else {
                errormsg = "用户名或密码不正确";
            }
            setAttr("errormsg", errormsg);
            render(AppConst.PATH_MANAGE_PC + "/login.ftl");
        }
    }
}
