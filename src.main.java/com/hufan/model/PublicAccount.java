package com.hufan.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * @Author: HuFan
 * @Description:
 * @Date: Create in 2018/6/2 12:05
 * @Modified By:
 */
public class PublicAccount extends Model<PublicAccount> {
    private static final long serialVersionUID = 1L;

    public static final PublicAccount dao = new PublicAccount();

    public PublicAccount findUserByLoginName(String username) {
        return dao.findFirst("select * from public_account where username = ?",username);
    }
}
