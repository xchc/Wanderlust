package com.wanderlust.util;

import java.util.List;

public class ReverseListUser<T> extends ListUser<T>{
    
    public ReverseListUser(List<T> subject){
        super(subject);
        currentIndex = subject.size() - 1;
    }

    public ReverseListUser(List<T> subject, int startIndex){
        super(subject, startIndex);
    }

    @Override
    public boolean atEnd() {
        return currentIndex < 0;
    }

    @Override
    public T step() {
        
        // get the current member and move along
        if(atEnd() == false){
            return subject.get(currentIndex--);            
        }

        return null;
    }
}
