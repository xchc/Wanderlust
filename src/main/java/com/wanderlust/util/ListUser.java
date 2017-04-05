package com.wanderlust.util;

import java.util.List;

public class ListUser<T> {
    
    protected final List<T> subject;
    protected int currentIndex = 0;
    
    public ListUser(List<T> subjec){
        this.subject = subjec;
        currentIndex = 0;
    }

    public ListUser(List<T> subject, int startIndex){
        this(subject);
        this.currentIndex = startIndex;
    }

    public boolean atEnd() {
        return currentIndex >= subject.size();
    }

    public T get() {
        
        // if available get the current member
        if(atEnd() == false)
            return subject.get(currentIndex);
        
        return null;
    }

    public T step() {
        
        // get the current member and move along
        if(atEnd() == false){
            return subject.get(currentIndex++);            
        }

        return null;
    }
}
