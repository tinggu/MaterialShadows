package com.cyou.materialshadows.utils

import android.util.Log
import java.util.*

/**
 * Description:  Create by wangjia_bi on 2017/7/4 11:39
 * Copyright  : Copyright (c) 2015
 * Company    : 北京畅游天下网络科技有限公司
 * Author     : wangjia_bi
 * Date       : 2017/7/4 11:39
 */
class GrahamScan {
    val TAG = "GrahamScan"
    private val hull = ArrayDeque<Point2D>()

    constructor(pts: Array<Point2D>) {
        val N = pts.size
//        Log.i(TAG, "GrahamScan constructor N $N")
        val points = arrayOfNulls<Point2D>(N)
//        val points:Array<Point2D> = arrayOf(pts)
        System.arraycopy(pts, 0, points, 0, N)
        points.sort()

        Arrays.sort<Point2D>(points, 1, N, PolarOrder(points[0]!!))
        
        hull.push(points[0])// p[0] is first extreme point
        
        var k1 = 1
        while (k1 < N) {
            if (points[0] != points[k1])
                break
            k1++
        }

        Log.i(TAG, "k1: $k1")
        if (k1 == N)
            return // all points equal

        var k2 = k1 + 1
        while (k2 < N) {
            if (ccw(points[0]!!, points[k1]!!, points[k2]!!) != 0)
                break
            k2++
        }
        hull.push(points[k2 - 1])// points[k2-1] is second extreme point

        Log.i(TAG, "k2: $k2 ; N = $N")

//        for (i in k2..N - 1) {
//            var top = hull.pop()
//            Log.i(TAG, "top: $top")
//            while (ccw(hull.peek(), top, points[i]!!) <= 0) {
//                top = hull.pop()
//            }
//            hull.push(top)
//            hull.push(points[i])
//        }
        Log.i(TAG, "GrahamScan: hull.size = " + hull.size)

        for (i in k2..N - 1) {
            var top = hull.pop()
            while (ccw(hull.peek(), top, points[i]!!) <= 0) {
                top = hull.pop()
                Log.i(TAG, "GrahamScan: " + top)
            }
            hull.push(top)
            hull.push(points[i])
        }

        

        Log.i(TAG, "GrahamScan: hull.size = " + hull.size)

        assert(isConvex())
    }

    fun hull(): Array<Point2D> {
        Log.i(TAG, "hull: ")
        val s = Stack<Point2D>()
        for (p in hull)
            s.push(p)

        Log.i(TAG, "stack size ${s.size}")
        val points = arrayOfNulls<Point2D>(s.size)
        return s.toArray(points)
//        var i = 0
//
//        s.forEach {
//            points[i++] = it
//        }
//        return points
    }

    fun isConvex(): Boolean {
        Log.i(TAG, "isConvex")
        val points = hull()
        val N = hull.size
        Log.i(TAG, "isConvex $N")
        if (N <= 2) return true
//        val points = arrayOfNulls<Point2D>(N)
//        var n = 0
//        hull.forEach {
//            points[n++] = it
//        }

        for (i in 0..N - 1) {
            if (ccw(points[i], points[(i + 1) % N], points[(i + 2) % N]) <= 0) {
                Log.i(TAG, "isConvex is false")
                return false
            }
        }
        Log.i(TAG, "isConvex is true")
        return true

//        return (0..N - 1).none { ccw(points[it], points[(it + 1) % N], points[(it + 2) % N]) <= 0 }

    }
}

//fun ccw(a: Point2D, b: Point2D, c: Point2D): Int {
//    val area2: Double = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)
//    if (area2 < 0)
//        return -1
//    else if (area2 > 0)
//        return +1
//    else
//        return 0
//}

