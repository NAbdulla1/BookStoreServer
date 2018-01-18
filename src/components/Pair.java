/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import java.io.Serializable;

public class Pair<T1, T2> implements Serializable {

    private T1 first;
    private T2 second;

    public Pair() {
        this(null, null);
    }

    public Pair(T1 f, T2 s) {
        first = f;
        second = s;
    }

    /*
    @Override
    public int compareTo(Pair<T1, T2> o) {
        int cmp = first.compareTo(o.first);
        if (cmp == 0) {
            return second.compareTo(o.second);
        }
        return cmp;
    }
     */
    public T1 getFirst() {
        return first;
    }

    public T2 getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return String.format("First: %s\nSecond: %s",
                first.toString(), second.toString());
    }

}
