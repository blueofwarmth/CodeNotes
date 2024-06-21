package com.javaweb.jdbc.javabean;

import java.util.Objects;

public class User {
    private int deptno;
    private String deptname;
    private String deptloc;
    public User() {
    }
    public User(int deptno, String deptname, String deptloc) {
        this.deptno = deptno;
        this.deptname = deptname;
        this.deptloc = deptloc;
    }

    public void setDeptno(int deptno) {
        this.deptno = deptno;
    }

    public void setDeptname(String deptname) {
        this.deptname = deptname;
    }

    public void setDeptloc(String deptloc) {
        this.deptloc = deptloc;
    }

    public int getDeptno() {
        return deptno;
    }

    public String getDeptname() {
        return deptname;
    }

    public String getDeptloc() {
        return deptloc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return deptno == user.deptno && Objects.equals(deptname, user.deptname) && Objects.equals(deptloc, user.deptloc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deptno, deptname, deptloc);
    }

    @Override
    public String toString() {
        return "User{" +
                "deptno=" + deptno +
                ", deptname='" + deptname + '\'' +
                ", deptloc='" + deptloc + '\'' +
                '}';
    }
}
