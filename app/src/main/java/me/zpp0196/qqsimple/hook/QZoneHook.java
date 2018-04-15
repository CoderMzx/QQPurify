package me.zpp0196.qqsimple.hook;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import me.zpp0196.qqsimple.hook.base.BaseHook;

/**
 * Created by zpp0196 on 2018/4/1.
 */

class QZoneHook extends BaseHook {

    private ClassLoader qqClassLoader;
    private ClassLoader qzoneClassLoader;

    QZoneHook(ClassLoader qqClassLoader, ClassLoader qzoneClassLoader) {
        this.qqClassLoader = qqClassLoader;
        this.qzoneClassLoader = qzoneClassLoader;
        hidePlusMenuConstants();
        hideNavConstants();
        hideMoodConstants();
    }

    /**
     * 隐藏加号菜单内容
     */
    private void hidePlusMenuConstants() {
        Class<?> PlusMenuContainer = findClassInQzone("com.qzone.plusoperation.PlusMenuContainer");
        Class<?> OperationItem = findClassInQzone("com.qzone.plusoperation.OperationItem");
        findAndHookMethod(PlusMenuContainer, "b", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (PlusMenuContainer != null && OperationItem != null) {
                    ArrayList arrayList = (ArrayList) findField(PlusMenuContainer, ArrayList.class, "a").get(param.thisObject);
                    ArrayList needRemove = new ArrayList();
                    for (Object item : arrayList) {
                        String title = (String) findField(OperationItem, String.class, "a").get(item);
                        if (title.equals("说说") && getBool("hide_qzone_plus_mood")) {
                            needRemove.add(item);
                        }
                        if (title.equals("相册") && getBool("hide_qzone_plus_album")) {
                            needRemove.add(item);
                        }
                        if (title.equals("拍摄") && getBool("hide_qzone_plus_shoot")) {
                            needRemove.add(item);
                        }
                        if (title.equals("签到") && getBool("hide_qzone_plus_signIn")) {
                            needRemove.add(item);
                        }
                        if (title.equals("小红包") && getBool("hide_qzone_plus_redPacket")) {
                            needRemove.add(item);
                        }
                        if (title.equals("直播") && getBool("hide_qzone_plus_live")) {
                            needRemove.add(item);
                        }
                    }
                    arrayList.removeAll(needRemove);
                }
            }
        });
    }

    /**
     * 隐藏导航栏
     */
    private void hideNavConstants() {
        // 隐藏小红点
        if (getBool("hide_qzone_nav_redDot")) {
            Class<?> NavigatorItem = findClassInQzone("com.qzone.navigationbar.NavigatorItem");
            findAndHookMethod(NavigatorItem, "b", Context.class, XC_MethodReplacement.returnConstant(null));
        }
        Class<?> NavigatorBar = findClassInQzone("com.qzone.navigationbar.NavigatorBar");
        Class<?> entrance_cfg = findClassInQQ("NS_UNDEAL_COUNT.entrance_cfg");
        findAndHookMethod(NavigatorBar, "b", Context.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if (NavigatorBar != null && entrance_cfg != null) {
                    ArrayList arrayList = (ArrayList) findField(NavigatorBar, ArrayList.class, "a").get(param.thisObject);
                    ArrayList needRemove = new ArrayList();
                    for (Object item : arrayList) {
                        String title = (String) findField(entrance_cfg, String.class, "sEntranceName").get(item);
                        if (title.equals("相册") && getBool("hide_qzone_nav_album")) {
                            needRemove.add(item);
                        }
                        if (title.equals("说说") && getBool("hide_qzone_nav_mood")) {
                            needRemove.add(item);
                        }
                        if (title.equals("个性化") && getBool("hide_qzone_nav_dress")) {
                            needRemove.add(item);
                        }
                        if (title.equals("小游戏") && getBool("hide_qzone_nav_game")) {
                            needRemove.add(item);
                        }
                        if (title.equals("小视频") && getBool("hide_qzone_nav_video")) {
                            needRemove.add(item);
                        }
                    }
                    arrayList.removeAll(needRemove);
                }
            }
        });
        // 隐藏消息
        if (!getBool("hide_qzone_nav_news")) return;
        Class<?> QZoneFriendFeedFragment = findClassInQzone("com.qzone.feed.ui.activity.QZoneFriendFeedFragment");
        findAndHookMethod(QZoneFriendFeedFragment, "t_", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (QZoneFriendFeedFragment != null) {
                    ImageView imageView = (ImageView) findField(QZoneFriendFeedFragment, ImageView.class, "d").get(param.thisObject);
                    imageView.setVisibility(View.GONE);
                }

            }
        });
    }

    /**
     * 隐藏说说内容
     */
    private void hideMoodConstants() {
        // 隐藏机型
        if (!getBool("hide_qzone_mood_attach")) return;
        Class<?> FeedOperation = findClassInQzone("com.qzone.module.feedcomponent.ui.FeedOperation");
        Class<?> CanvasAttachView = findClassInQzone("com.qzone.module.feedcomponent.ui.canvasui.CanvasAttachView");
        findAndHookMethod(FeedOperation, "b", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (FeedOperation == null || CanvasAttachView == null) return;
                View attach = (View) findField(FeedOperation, CanvasAttachView, "a").get(param.thisObject);
                attach.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected Class<?> findClassInQQ(String className) {
        try {
            return qqClassLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            log("%s Can't find the Class of name: %s!", getQQ_Version(), className);
        }
        return null;
    }

    private Class<?> findClassInQzone(String className) {
        try {
            return qzoneClassLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            log("%s Can't find the Class of name: %s in QzonePlugin!", getQQ_Version(), className);
        }
        return null;
    }
}
