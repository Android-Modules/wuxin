package h.Hchat.utils;

import de.robv.android.xposed.XposedBridge;

/**
 * LSPosed treats Throwable logs as error logs. Use this only for real failures.
 *
 * WCX 定制：同时把日志镜像到 WCX 自身的日志文件（wcx-*.log），
 * 这样在模块「日志」页就能直接看到 Hchat 内部的失败信息，无需跳 LSPosed。
 */
public final class HLog {

    private static final String MIRROR_TAG = "Hchat";

    private HLog() {
    }

    public static void e(String message) {
        XposedBridge.log(new RuntimeException(message));
        mirror(message, null);
    }

    public static void e(String message, Throwable throwable) {
        if (throwable == null) {
            e(message);
            return;
        }
        XposedBridge.log(new RuntimeException(message, throwable));
        mirror(message, throwable);
    }

    private static void mirror(String message, Throwable throwable) {
        try {
            if (throwable == null) {
                com.Johnny.wcx.utils.WeLogger.INSTANCE.e(MIRROR_TAG, message);
            } else {
                com.Johnny.wcx.utils.WeLogger.INSTANCE.e(MIRROR_TAG, message, throwable);
            }
        } catch (Throwable ignored) {
            // 镜像失败不能影响原始日志行为。
        }
    }
}
