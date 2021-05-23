package com.jaythree.myapplication

//FILE TO STORE THE STATE OF THE PROCESS

private var running = false

public fun setRunning(){
    running = true
}

public fun setStopped(){
    running = false
}

public fun isRunning() : Boolean {
    return running
}