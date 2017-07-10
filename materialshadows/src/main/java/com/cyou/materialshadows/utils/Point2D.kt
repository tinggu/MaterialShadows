package com.cyou.materialshadows.utils

/**
 * Description:  Create by wangjia_bi on 2017/7/3 17:56
 * Copyright  : Copyright (c) 2015
 * Company    : 北京畅游天下网络科技有限公司
 * Author     : wangjia_bi
 * Date       : 2017/7/3 17:56
 */
class Point2D(x: Int, y: Int) : Comparable<Point2D> {

    //    val POLAR_ORDER = PolarOrder()

    val x: Double = x.toDouble()
    val y: Double = y.toDouble()
    val r: Double
        get() {
            return Math.sqrt(x * x + y * y)
        }

//    init {
//        if (x.isInfinite() || y.isInfinite())
//            throw IllegalArgumentException("Coordinates must be finite")
//        if (x.isNaN() || y.isInfinite())
//            throw IllegalArgumentException("Coordinates cannot be NaN")

//        if (x == 0.0)
//            this.x = 0.0 // convert -0.0 to +0.0
//        else
//            this.x = x
//        if (y == 0.0)
//            this.y = 0.0 // convert -a0.0 to +0.0
//        else
//            this.y = y
//    }

    override fun compareTo(other: Point2D): Int {
        if (this.y < other.y)
            return -1
        if (this.y > other.y)
            return 1
        if (this.x < other.x)
            return -1
        if (this.x > other.x)
            return 1
        return 0
    }

    //    inner class PolarOrder : Comparator<Point2D> {
//
//        override fun compare(q1: Point2D, q2: Point2D): Int {
//            val dx1 = q1.x - x
//            val dy1 = q1.y - y
//            val dx2 = q2.x - x
//            val dy2 = q2.y - y
//
//            if (dy1 >= 0 && dy2 < 0) return -1 // q1 above; q2 below
//            else if (dy2 >= 0 && dy1 < 0) return +1 // q1 below; q2 above
//            else if (dy1 == 0.0 && dy2 == 0.0) { // 3-collinear and horizontal
//                if (dx1 >= 0 && dx2 < 0) return -1
//                else if (dx2 >= 0 && dx1 < 0) return +1
//                else return 0
//            } else return -ccw(this@Point2D, q1, q2) // both above or below
//
//        }
//
//
//    }
    public override fun equals(other: Any?): Boolean {
        if (other === this)
            return true
        if (other == null)
            return false
        if (other.javaClass != this.javaClass)
            return false
        val that = other as Point2D?
        return this.x == that!!.x && this.y == that.y
    }

    override fun toString(): String {
        return "($x, $y)"
    }

    override fun hashCode(): Int {
        val hashX = x.toDouble().hashCode()
        val hashY = y.toDouble().hashCode()
        return 31 * hashX + hashY
    }
}

class PolarOrder(val base: Point2D) : Comparator<Point2D> {

    override fun compare(q1: Point2D, q2: Point2D): Int {
        val dx1 = q1.x - base.x
        val dy1 = q1.y - base.y
        val dx2 = q2.x - base.x
        val dy2 = q2.y - base.y

        if (dy1 >= 0 && dy2 < 0) return -1 // q1 above; q2 below
        else if (dy2 >= 0 && dy1 < 0) return +1 // q1 below; q2 above
        else if (dy1 == 0.0 && dy2 == 0.0) { // 3-collinear and horizontal
            if (dx1 >= 0 && dx2 < 0) return -1
            else if (dx2 >= 0 && dx1 < 0) return +1
            else return 0
        } else return -ccw(base, q1, q2) // both above or below

    }

//    override fun equals(other: Any?): Boolean {
//        if (other == this)
//            return true
//        if (other == null)
//            return false
//        if (other.javaClass != this.javaClass)
//            return false
//        if (other is Point2D) {
//            return base.x == other.x && base.y == other.y
//        }
//        return false
////        val that = other as Point2D
////        return base.x == that.x && base.y == that.y
//
//    }


}

fun ccw(a: Point2D, b: Point2D, c: Point2D): Int {
    val area2 = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)
    if (area2 < 0)
        return -1
    else if (area2 > 0)
        return +1
    else
        return 0
}


