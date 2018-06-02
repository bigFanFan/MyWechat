package com.hufan.routes;

import com.hufan.controller.AdminController;
import com.jfinal.config.Routes;

/**
 * @Author: HuFan
 * @Description:
 * @Date: Create in 2018/6/2 11:34
 * @Modified By:
 */
public class AdminRoute extends Routes {
    @Override
    public void config() {
        add("/m", AdminController.class);
    }
}
