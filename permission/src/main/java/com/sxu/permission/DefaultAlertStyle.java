package com.sxu.permission;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

/*******************************************************************************
 *
 * 默认的解释申请权限和引导用户开启权限的样式
 *
 * @author Freeman
 *
 * @date 2018/11/1
 *
 *******************************************************************************/
public class DefaultAlertStyle implements AlertStyle {

	@Override
	public void onExplain(@NonNull Activity context, String reason) {
		if (!TextUtils.isEmpty(reason)) {
			Toast.makeText(context.getApplicationContext(), reason, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onSettingGuide(@NonNull final Activity context, String settingDesc) {
		if (TextUtils.isEmpty(settingDesc)) {
			context.finish();
			return;
		}

		new AlertDialog.Builder(context)
				.setMessage(settingDesc)
				.setNegativeButton("暂不", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setPositiveButton("去开启", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
						intent.setData(Uri.fromParts("package", context.getPackageName(), null));
						if (intent.resolveActivity(context.getPackageManager()) != null) {
							context.startActivity(intent);
						} else {
							Toast.makeText(context, "无法打开应用详情，请手动开启权限~", Toast.LENGTH_LONG).show();
						}
						dialog.dismiss();
					}
				})
				.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						context.finish();
					}
				})
				.show();
	}
}
