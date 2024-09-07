package com.learning.javavt;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {

        String regex = "(?<=@)(ForkJoinPool-\\d+-worker-\\d+)";
        Pattern pattern = Pattern.compile(regex);
        AtomicInteger c = new AtomicInteger();
        var list = new ConcurrentSkipListSet<String>();
        var threadList = new ArrayList<Thread>();
        while (c.get() < 20000) {
            c.getAndIncrement();
            Thread t = Thread.ofVirtual().unstarted(() -> {
               try {
                   Thread curr = Thread.currentThread();
                   if (curr.isVirtual()) {
                       String thStr = curr.toString();
                       Matcher matcher = pattern.matcher(thStr);
                       if (matcher.find()) {
                           String extracted = matcher.group(1);
                           list.add(extracted);
                       }
                   } else {
                        list.add(curr.getName());
                   }
                   System.out.println("process count : " +  c);
                   TimeUnit.SECONDS.sleep(100);
                   System.out.println("after sleep");
               } catch (Exception e) {
                   throw new RuntimeException(e);
               }
            });
            threadList.add(t);
        }
        threadList.forEach(Thread::start);
        System.out.println("process count : " +  c);
        System.out.println("thread list " +  list);
        System.out.println("thread list size " +  list.size());
//        threadList.forEach(thread -> {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        });
    }
}