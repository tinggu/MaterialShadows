package com.cyou.materialshadows

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.RelativeLayout
import com.cyou.materialshadows.utils.ShadowGenerator

/**
 * Description:  Create by wangjia_bi on 2017/7/5 11:22
 * Copyright  : Copyright (c) 2015
 * Company    : 北京畅游天下网络科技有限公司
 * Author     : wangjia_bi
 * Date       : 2017/7/5 11:22
 */

class MaterialShadowViewWrapper : RelativeLayout {

    val TAG = "ViewWrapper"

    var shadowGenerator: ShadowGenerator? = null
    val atttrData: AttrData = AttrData()

    var offsetX: Float
        set(value) {
            Log.i(TAG, "setOffsetX " + value)
            atttrData.offsetX = value
            shadowGenerator?.updateShadows(POS_UPDATE_ALL)
        }
        get() {
            return atttrData.offsetX
        }


    var offsetY: Float
        set(value) {
            atttrData.offsetY = value
            shadowGenerator?.updateShadows(POS_UPDATE_ALL)
        }
        get() {
            return atttrData.offsetY
        }

    var shadowAlpha: Float
        set(value) {
            atttrData.shadowAlpha = value
            shadowGenerator?.updateShadows(POS_UPDATE_ALL)
        }
        get() {
            return atttrData.shadowAlpha
        }

    var shouldShowWhenAllReady: Boolean
        set(value) {
            atttrData.shouldShowWhenAllReady = value
        }
        get() {
            return atttrData.shouldShowWhenAllReady
        }

    var shouldCalculateAsync: Boolean
        set(value) {
            atttrData.shouldCalculateAsync = value
        }
        get() {
            return atttrData.shouldCalculateAsync
        }

    var shouldAnimateShadow: Boolean
        set(value) {
            atttrData.shouldAnimateShadow = value
        }
        get() {
            return atttrData.shouldAnimateShadow
        }

    var animationDuration: Int
        set(value) {
            atttrData.animationDuration = value
        }
        get() {
            return atttrData.animationDuration
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initXMLAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initXMLAttrs(context, attrs)
    }


    fun initXMLAttrs(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MaterialShadowViewWrapper)
        val N = a.indexCount
        Log.i(TAG, "init XMLAttrs $N ")

        for (i in 0..N - 1) {
            val attr: Int = a.getIndex(i)
            when (attr) {
                R.styleable.MaterialShadowViewWrapper_shadowAlpha ->
                    atttrData.shadowAlpha = a.getFloat(attr, DEFAULT_SHADOW_ALPHA)
                R.styleable.MaterialShadowViewWrapper_shadowOffsetX ->
                    atttrData.offsetX = a.getFloat(attr, DEFAULT_X_OFFSET)
                R.styleable.MaterialShadowViewWrapper_shadowOffsetY ->
                    atttrData.offsetY = a.getFloat(attr, DEFAULT_Y_OFFSET)
                R.styleable.MaterialShadowViewWrapper_calculateAsync ->
                    atttrData.shouldCalculateAsync = a.getBoolean(attr, DEFAULT_CALCULATE_ASYNC)
                R.styleable.MaterialShadowViewWrapper_animateShadow ->
                    atttrData.shouldAnimateShadow = a.getBoolean(attr, DEFAULT_ANIMATE_SHADOW)
                R.styleable.MaterialShadowViewWrapper_showWhenAllReady ->
                    atttrData.shouldShowWhenAllReady = a.getBoolean(attr, DEFAULT_SHOW_WHEN_ALL_READY)
                R.styleable.MaterialShadowViewWrapper_animationDuration ->
                    atttrData.animationDuration = a.getInt(attr, DEFAULT_ANIMATION_TIME)
            }
        }
        a.recycle()

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        Log.i(TAG, "onLayout")
        if (shadowGenerator == null) {
            Log.i(TAG, "new shadowGenerator")
            shadowGenerator = ShadowGenerator(this, atttrData)
        }
        shadowGenerator!!.generate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        shadowGenerator?.releaseResources()
    }
}
