package com.hufan.config;

import com.hufan.routes.AdminRoute;
import com.hufan.routes.WebRoute;
import com.jfinal.config.*;
import com.revocn.config.RevoConfig;

public class AppConfig extends RevoConfig {

    @Override
    public void configConstant(Constants constants) {

    }

    @Override
    public void configRoute(Routes routes) {
        routes.add(new WebRoute());
        routes.add(new AdminRoute());
    }

    @Override
    public void configPlugin(Plugins plugins) {

    }

    @Override
    public void configInterceptor(Interceptors interceptors) {

    }

    @Override
    public void configHandler(Handlers handlers) {

    }
}
