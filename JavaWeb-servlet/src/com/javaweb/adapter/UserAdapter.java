package com.javaweb.adapter;
/**
 * UserService的适配器
 * */
public abstract class UserAdapter implements MyInterface {
    public abstract void doSomething(); //UserService只用这一个方法, 所以改为抽象, 那个类需要什么方法, 就只需要实现一个方法

    @Override
    public void doSomethingElse() {

    }

    @Override
    public void doSomethingElseElse() {

    }

    @Override
    public void core() {

    }
}
