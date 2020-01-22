package com.fileutils

import android.content.Context

open class Shape {
    constructor()

    open fun draw() {

    }
}

class Circle : Shape {
    constructor() : super()


    override fun draw() {
        System.out.println("Draw")
    }

    fun radius(r: Int) {
        var result: Int = r / 2
        System.out.println("radius of circle " + result)
    }
}

fun testMain() {
    val circle = Circle()
    circle.draw()
    circle.radius(10)
}

