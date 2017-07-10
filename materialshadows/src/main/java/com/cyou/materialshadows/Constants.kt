package com.cyou.materialshadows

/**
 * Description:  Create by wangjia_bi on 2017/7/3 17:48
 * Copyright  : Copyright (c) 2015
 * Company    : 北京畅游天下网络科技有限公司
 * Author     : wangjia_bi
 * Date       : 2017/7/3 17:48
 */

const val DEFAULT_X_OFFSET = 0.0f
const val DEFAULT_Y_OFFSET = 0.0f
const val DEFAULT_SHADOW_ALPHA = 0.99f
const val DEFAULT_SHOW_WHEN_ALL_READY = true
const val DEFAULT_CALCULATE_ASYNC = true
const val DEFAULT_ANIMATE_SHADOW = true
const val DEFAULT_ANIMATION_TIME = 300
const val POS_UPDATE_ALL = -1

data class AttrData(var offsetX: Float = DEFAULT_X_OFFSET,
                    var offsetY: Float = DEFAULT_Y_OFFSET,
                    var shadowAlpha: Float = DEFAULT_SHADOW_ALPHA,
                    var shouldShowWhenAllReady: Boolean = DEFAULT_SHOW_WHEN_ALL_READY,
                    var shouldCalculateAsync: Boolean = DEFAULT_CALCULATE_ASYNC,
                    var shouldAnimateShadow: Boolean = DEFAULT_ANIMATE_SHADOW,
                    var animationDuration: Int = DEFAULT_ANIMATION_TIME
)