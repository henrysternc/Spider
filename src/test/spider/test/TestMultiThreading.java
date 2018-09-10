package test.spider.test;

import java.util.ArrayList;
import java.util.List;

import test.spider.pojo.Series;

public class TestMultiThreading extends Thread {
    
    /**
     * 多线程处理list
     * 
     * @param data  数据list
     * @param threadNum  线程数
     */
    public synchronized void handleList(List<Series> data, int threadNum) {
        int length = data.size();
        for(int j=0;j<length;j++) {
            for (int i = 0; i < threadNum; i++) {
                if(data.get(j+i).getStatus()) {
                    HandleThread thread = new HandleThread("线程[" + (i + 1) + "] ",  data.get(j+i));
                    thread.start();
                }
               j++;
            }
            
        }
       
    }
 
    class HandleThread extends Thread {
        private String threadName;
        private Series series;
 
        public HandleThread(String threadName, Series series) {
            this.threadName = threadName;
            this.series = series;
        }
 
        public void run() {
            // TODO 这里处理数据
           System.err.println("handle :threadName:"+threadName+" : "+series.getSeriesUrl());
           series.setStatus(false);
            //System.out.println(threadName);
        }
 
    }
 
    public static void main(String[] args) {
        TestMultiThreading test = new TestMultiThreading();
        // 准备数据
        List<Series> data = new ArrayList<Series>();
        for (int i = 0; i < 500; i++) {
            Series series = new Series();
            series.setBrandName("Brand"+i);
            series.setSeriesName("Series"+i);
            series.setSeriesUrl("url"+i);
            series.setStatus(true);
            data.add(series);
        }
        test.handleList(data, 5);
       // System.out.println(ArrayUtils.toString(data));
    }

}
