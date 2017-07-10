package com.cyou.materialshadows

import android.graphics.Outline
import android.graphics.Path
import android.view.View
import android.view.ViewOutlineProvider

/**
 * Description:  Create by wangjia_bi on 2017/7/4 16:27
 * Copyright  : Copyright (c) 2015
 * Company    : 北京畅游天下网络科技有限公司
 * Author     : wangjia_bi
 * Date       : 2017/7/4 16:27
 */
class CustomViewOutlineProvider(private var path: Path, var alpha: Float) : ViewOutlineProvider() {
    
    override fun getOutline(view: View?, outline: Outline) {
        outline.setConvexPath(path)
        if (alpha >= 1.0f) alpha = 0.99f
        else if (alpha < 0.0f)
            alpha = 0.0f
        
        outline.alpha = alpha
    }

}