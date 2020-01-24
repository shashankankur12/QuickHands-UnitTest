package com.fileutils
open class File(val name: String){

    init {
        println("Base class")
    }

    open val num: Int = 0


   open fun anim() {
    println("anim")
   }

    open fun layout() {
       println("layout")
    }
}

open class Utils(name:String): File(name.capitalize()) {

    init {
        println("Utils class")
    }
    override var num: Int = 4
    override fun anim() {

    }

    override fun layout() {

    }

}

fun mainTest() {
  val utils = Utils("number")
    System.out.println("Number Message " + utils.num)
}
