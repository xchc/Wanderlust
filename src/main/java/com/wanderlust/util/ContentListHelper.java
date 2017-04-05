package com.wanderlust.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bson.types.ObjectId;

import com.wanderlust.api.UserProfile;
import com.wanderlust.api.UserProfileID;

public class ContentListHelper {

    private static Comparator<UserProfile> backwardTimeComparator = new Comparator<UserProfile>() {
        
        public int compare(UserProfile o1, UserProfile o2) {
            final ObjectId oid2 = (ObjectId)o2.getId();
            final ObjectId oid1 = (ObjectId)o1.getId();
            final int compare = oid2.compareTo(oid1);
            return compare;
        }};
                    
    private static Comparator<UserProfile> timeOrderComparator = new Comparator<UserProfile>() {
        
        public int compare(UserProfile o1, UserProfile o2) {
            final ObjectId oid2 = (ObjectId)o2.getId();
            final ObjectId oid1 = (ObjectId)o1.getId();
            final int compare = oid1.compareTo(oid2);
            return compare;
        }};

    public static List<UserProfile> extractContent(
            final List<UserProfile> source, final UserProfileID anchor, 
            final int limit, final boolean allowFutureAnchor){
        int count = Math.abs(limit);
        final boolean forward = limit > 0;       
        List<UserProfile> results = new ArrayList<UserProfile>(count);
        
        if(anchor != null){                
           
            UserProfile markerObject = new UserProfile(anchor.toDBObject());
            int index = Collections.binarySearch(source, markerObject, timeOrderComparator);
            
            
            if(Math.abs(index) < source.size() || allowFutureAnchor){
                if(forward == true){
                    index = index < 0 ? Math.abs(index) - 2 : Math.abs(index) - 1;
                    for(int i = index; i >= 0 && count-- > 0; --i){
                        results.add(source.get(i));
                    }                                        
                } else {
                    index = index < 0 ? Math.abs(index) - 1 : Math.abs(index) + 1;
                    for(int i = index; i < source.size() && count-- > 0; ++i){
                        results.add(source.get(i));
                    }
                    Collections.reverse(results);
                }
            }
        } else if(forward == true) {
            for(int i = source.size() - 1; i >= 0 && count-- > 0; --i)
                results.add(source.get(i));               
        }
    
        return results;
    }
    
    public static ListUser<UserProfile> getContentWalker(
            final List<UserProfile> source, final UserProfileID anchor, final int limit) {
        
        ListUser<UserProfile> walker = null;        
        if(anchor == null){ 
           
            return new ReverseListUser<UserProfile>(source);
        } else {
            UserProfile markerObject = new UserProfile(anchor.toDBObject());
            int index = Collections.binarySearch(source, markerObject, timeOrderComparator);
            if(limit > 0){

               
                index = index < 0 ? Math.abs(index) - 2 : Math.abs(index) - 1;
                walker = new ReverseListUser<UserProfile>(source, index);
            } else {

               
                index = index < 0 ? Math.abs(index) - 1 : Math.abs(index) + 1;
                walker = new ListUser<UserProfile>(source, index);
            }
        }
        
        return walker;
    }

    public static List<UserProfile> merge(final List<ListUser<UserProfile>> walkers, final int limit) {
        int count = Math.abs(limit);
        boolean forward = limit > 0;       
        Comparator<UserProfile> comparator = forward == true ? 
                backwardTimeComparator : timeOrderComparator;
        List<UserProfile> result = new ArrayList<UserProfile>(count);
        ListUser<UserProfile> lowest;

        while (result.size() < count) {
            lowest = null;
            for (ListUser<UserProfile> l : walkers) {
                if (! l.atEnd()) {
                    if (lowest == null) {
                        lowest = l;
                    }
                    else if (l.get() != null && comparator.compare(l.get(), lowest.get()) <= 0) {
                        lowest = l;
                    }
                }
            }
            
         
            if(lowest != null)
                result.add(lowest.step());
            else
                break;
        }
        
        
        if(forward == false)
            Collections.reverse(result);
        
        return result;
    }
}
