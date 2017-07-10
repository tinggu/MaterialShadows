package com.cyou.materialshadows.utils

import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import com.cyou.materialshadows.AttrData
import com.cyou.materialshadows.CustomViewOutlineProvider
import com.cyou.materialshadows.MaterialShadowViewWrapper
import com.cyou.materialshadows.POS_UPDATE_ALL
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.collections.ArrayList

/**
 * Description:  Create by wangjia_bi on 2017/7/4 17:55
 * Copyright  : Copyright (c) 2015
 * Company    : 北京畅游天下网络科技有限公司
 * Author     : wangjia_bi
 * Date       : 2017/7/4 17:55
 */
class ShadowGenerator(val viewGroup: ViewGroup, val attrData: AttrData) {
    val TAG = "ShadowGenerator"

    private val TASKS_LOCK = Any()
    private val tasksInProgress = ArrayList<Future<*>>()
    private val workerPool: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    private val uiThreadHandler: Handler = Handler(Looper.getMainLooper())

    private var viewPaths: SparseArray<Path>

//    var shouldShowWhenAllReady: Boolean = DEFAULT_SHOW_WHEN_ALL_READY,
//    var shouldCalculateAsync: Boolean = DEFAULT_CALCULATE_ASYNC,
//    var shouldAnimateShadow: Boolean = DEFAULT_ANIMATE_SHADOW,
//    var animationDuration: Int = DEFAULT_ANIMATION_TIME

//    var offsetX: Float = DEFAULT_X_OFFSET
//        set(value) {
//            field = value
//            updateShadows(POS_UPDATE_ALL)
//        }
//
//    var offsetY: Float = DEFAULT_Y_OFFSET
//        set(value) {
//            field = value
//            updateShadows(POS_UPDATE_ALL)
//        }

//    var shadowAlpha: Float = DEFAULT_SHADOW_ALPHA
//        set(value) {
//            field = value
//            updateShadows(POS_UPDATE_ALL)
//        }

    private var childrenWithShadow: Int

    init {
//        this.offsetX = offsetX
//        this.offsetY = offsetY
//        this.shadowAlpha = shadowAlpha
        viewPaths = SparseArray<Path>()
        childrenWithShadow = 0
    }

    fun generate() {
        Log.i(TAG, "generate ")

        clearShadowCache()//Maybe some children changed their size
        childrenWithShadow = 0
        for (i in 0..viewGroup.childCount - 1) {
            val view = viewGroup.getChildAt(i)
            if (view is MaterialShadowViewWrapper || view == null) 
                continue
            childrenWithShadow++
            if (attrData.shouldCalculateAsync)
                calculateAndRenderShadowAsync(view, i)
            else
                calculateAndRenderShadow(view, i)
        }
    }

    fun releaseResources() {
        workerPool.shutdown()
        uiThreadHandler.removeCallbacksAndMessages(null)
    }

    private fun clearShadowCache() {
        cancelTasksInProgress()
        uiThreadHandler.removeCallbacksAndMessages(null)
        viewPaths = SparseArray<Path>()
    }

    fun updateShadows(pos: Int) {
        if (pos == POS_UPDATE_ALL) {
            for (i in 0..viewGroup.childCount - 1) {
                setShadowOutlineProviderAt(i)
            }
        } else {
            setShadowOutlineProviderAt(pos)
        }
    }

    private fun setShadowOutlineProviderAt(childIndex: Int) {
        val shadowPath = getViewPathWithPffsetAt(childIndex) ?: return
        val child = viewGroup.getChildAt(childIndex)
        val outlinProvider = CustomViewOutlineProvider(shadowPath, attrData.shadowAlpha)
        child.outlineProvider = outlinProvider
        if (attrData.shouldAnimateShadow) {
            animationOutlineAlpha(child, outlinProvider)
        }

    }

    private fun animationOutlineAlpha(child: View, outlinProvider: CustomViewOutlineProvider) {
        val animator = ObjectAnimator.ofFloat(outlinProvider, "alpha", 0.0f, attrData.shadowAlpha)
        animator.duration = attrData.animationDuration.toLong()
        animator.addUpdateListener({ child.invalidateOutline() })
    }

    private fun getViewPathWithPffsetAt(positon: Int): Path? {

        val noOffsetPath = viewPaths.get(positon) ?: return null
        val path = Path()
        path.set(noOffsetPath)
        path.offset(attrData.offsetX, attrData.offsetY)
        return path
    }

    private fun calculateAndRenderShadowAsync(view: View, pos: Int) {
        Log.i(TAG, "calculateAndRenderShadowAsync $pos ")
        var future: Future<*>? = null
        future = workerPool.submit {
            calculateAndRenderShadow(view, pos)
            synchronized(TASKS_LOCK) {
                tasksInProgress.remove(future)
                Log.i(TAG, "tasksInProgress remove $pos ")
            }
        }
        tasksInProgress.add(future)
    }

    //    @Synchronized
    private fun cancelTasksInProgress() {
        synchronized(TASKS_LOCK) {
            for (i in tasksInProgress.indices.reversed()) {
                val task = tasksInProgress[i]
                task.cancel(true)
                tasksInProgress.removeAt(i)
            }
        }


//        tasksInProgress.forEach { 
//            it.cancel(true)
//        } 
//        for (i in 0..tasksInProgress.size - 1) {
//                    
//        }
    }

    private fun calculateAndRenderShadow(view: View, pos: Int) {
        view.buildDrawingCache()
        if (Thread.currentThread().isInterrupted)
            return

        val outlinePoints: List<Point2D>
        var bitmap = view.drawingCache

        try {
            //We need to copy it, because drawing cache will be recycled if view is detached
            bitmap = bitmap.copy(bitmap.config, false)
            outlinePoints = getOutlinePoints(bitmap)
            
            Log.i(TAG, "calculateAndRenderShadow outlinePoints size ${outlinePoints.size} ")

            if (outlinePoints.isEmpty())
                return
        } catch (e: Exception) {
            e.printStackTrace()
            //If drawing cache has been recycled, IllegalStateException will be thrown on copy
            return
        } finally {
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }
        }
        
//        val scanOutlinePoints = outlinePoints.toTypedArray<Point2D>()
        val grahamScan = GrahamScan(outlinePoints.toTypedArray())
//        val arrayListHullPoints = ArrayList<Point2D>()
//        arrayListHullPoints = grahamScan.hull().toList()
        val hullPoints = grahamScan.hull()

        Log.i(TAG, "grahamScan size ${hullPoints.size} ")
        val path = Path()
        path.moveTo(hullPoints[0].x.toFloat(), hullPoints[0].y.toFloat())
        for (i in 1..hullPoints.size - 1) {
            path.lineTo(hullPoints[i].x.toFloat(), hullPoints[i].y.toFloat())
        }
        synchronized(TASKS_LOCK) {
            if (Thread.currentThread().isInterrupted) {
                return
            }
            //内部类实现 
//            uiThreadHandler.postAtFrontOfQueue(SetViewOutlineTask(pos, path))


//            uiThreadHandler.postAtFrontOfQueue(object :Runnable(){
//                override fun run() {
//
//                    if (viewGroup.isAttachedToWindow) {
//                        viewPaths.put(pos, path)
//                        if (shouldShowWhenAllReady){
//                            if (viewPaths.size() == childrenWithShadow){
//                                updateShadows(POS_UPDATE_ALL)
//                            }
//                            return
//                        }
//                        updateShadows(pos)
//                    }
//                }
//
//            })

            uiThreadHandler.postAtFrontOfQueue(Runnable {
                if (viewGroup.isAttachedToWindow) {
                    viewPaths.put(pos, path)
                    if (attrData.shouldShowWhenAllReady) {
                        if (viewPaths.size() == childrenWithShadow) {
                            updateShadows(POS_UPDATE_ALL)
                        }
                        return@Runnable
                    }
                    updateShadows(pos)
                }
            })
        }


    }

    private fun getOutlinePoints(bitmap: Bitmap): List<Point2D> {
        val arrayList = ArrayList<Point2D>()

        for (i in 0..bitmap.height - 1) {
            if (Color.alpha(bitmap.getPixel(0, i)) > 0) {
                arrayList.add(Point2D(0, i))
            }

            if (Color.alpha(bitmap.getPixel(bitmap.width - 1, i)) > 0) {
                arrayList.add(Point2D(bitmap.width - 1, i))
            }
        }

//        Log.i(TAG, "get outLine size ${arrayList.size}")
        
//        (0..bitmap.height - 1)
//                .filter {
//                    Color.alpha(bitmap.getPixel(0, it)) > 0 
//                    Color.alpha(bitmap.getPixel(bitmap.width - 1, it)) > 0 
//                }
//                .mapTo(arrayList) {
//                    Point2D(0.0, it.toDouble()) 
//                    Point2D((bitmap.width - 1).toDouble(), it.toDouble()) 
//                }

        if (Thread.currentThread().isInterrupted) 
            return Collections.emptyList()

        for (i in 0..bitmap.height - 1) {
            for (j in 1..bitmap.width - 2) {
                if (Color.alpha(bitmap.getPixel(j - 1, i)) == 0 && Color.alpha(bitmap.getPixel(j, i)) > 0) {
                    arrayList.add(Point2D(j, i))
                }

                if (Color.alpha(bitmap.getPixel(j - 1, i)) > 0 && Color.alpha(bitmap.getPixel(j, i)) == 0) {
                    arrayList.add(Point2D(j - 1, i))
                }
            }
        }

//        Log.i(TAG, "isInterrupted false get outLine size ${arrayList.size}")
        
        return arrayList
    }

    private inner class SetViewOutlineTask(val viewPos: Int, val shadowPath: Path) : Runnable {
        override fun run() {
            if (viewGroup.isAttachedToWindow) {
                viewPaths.put(viewPos, shadowPath)
                if (attrData.shouldShowWhenAllReady) {
                    if (viewPaths.size() == childrenWithShadow) {
                        updateShadows(POS_UPDATE_ALL)
                    }
                    return
                }
                updateShadows(viewPos)
            }
        }
    }


}

