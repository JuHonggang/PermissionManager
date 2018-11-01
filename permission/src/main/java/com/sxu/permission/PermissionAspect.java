package com.sxu.permission;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/*******************************************************************************
 * Description: 权限切面
 *
 * Author: Freeman
 *
 * Date: 2018/8/17
 *
 * Copyright: all rights reserved by Freeman.
 *******************************************************************************/
@Aspect
public class PermissionAspect {

	@Pointcut("execution(@com.sxu.permission.CheckPermission * *(..))")
	public void executionCheckPermission() {

	}

	@Around("executionCheckPermission()")
	public void checkPermission(final ProceedingJoinPoint joinPoint) {
		Object object = joinPoint.getThis();
		if (object == null) {
			Object[] args = joinPoint.getArgs();
			if (args != null && args.length > 0) {
				object = args[0];
			} else {
				return;
			}
		}
		Context context = getContext(object);
		if (context == null) {
			return;
		}

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		final CheckPermission checkPermission = method.getAnnotation(CheckPermission.class);
		PermissionActivity.enter(context, checkPermission.permissions(), checkPermission.permissionDesc(),
				checkPermission.settingDesc(), new OnPermissionRequestListener() {
					@Override
					public void onGranted(Activity context) {
						try {
							joinPoint.proceed();
						} catch (Throwable throwable) {
							throwable.printStackTrace(System.err);
						}
						context.finish();
					}

					@Override
					public void onAsked(Activity context) {
						if (!CheckPermission.DEFAULT_IS_BLOCK_VALUE.equals(checkPermission.isBlock())) {
							try {
								joinPoint.proceed();
							} catch (Throwable throwable) {
								throwable.printStackTrace(System.err);
							}
						}
						context.finish();
					}

					@Override
					public void onDenied(Activity context) {

					}
				});
	}

	private Context getContext(Object object) {
		Context context = null;
		if (object instanceof OnContextListener) {
			context = ((OnContextListener) object).getContext();
		} else if (object instanceof Activity) {
			context = (Context) object;
		} else if (object instanceof Fragment) {
			context = ((Fragment) object).getActivity();
		} else if (object instanceof android.support.v4.app.Fragment) {
			context = ((android.support.v4.app.Fragment) object).getActivity();
		} else if (object instanceof Dialog) {
			context = ((Dialog) object).getContext();
		} else if (object instanceof View) {
			context = ((View) object).getContext();
		}

		return context;
	}
}
