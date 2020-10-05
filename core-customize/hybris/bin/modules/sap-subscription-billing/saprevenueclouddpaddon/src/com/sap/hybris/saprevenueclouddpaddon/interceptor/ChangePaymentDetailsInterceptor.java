/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenueclouddpaddon.interceptor;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ChangePaymentDetailsInterceptor extends HandlerInterceptorAdapter {
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
        modelAndView.addObject("showChangePaymentBtn", true);
    }
}
