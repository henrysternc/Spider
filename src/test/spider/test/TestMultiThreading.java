package test.spider.test;

import java.util.ArrayList;
import java.util.List;

import test.spider.pojo.Series;

public class TestMultiThreading extends Thread {
    
    /**
     * ���̴߳���list
     * 
     * @param data  ����list
     * @param threadNum  �߳���
     */
    public synchronized void handleList(List<Series> data, int threadNum) {
        int length = data.size();
        for(int j=0;j<length;j++) {
            for (int i = 0; i < threadNum; i++) {
                if(data.get(j+i).getStatus()) {
                    HandleThread thread = new HandleThread("�߳�[" + (i + 1) + "] ",  data.get(j+i));
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
            // TODO ���ﴦ������
           System.err.println("handle :threadName:"+threadName+" : "+series.getSeriesUrl());
           series.setStatus(false);
            //System.out.println(threadName);
        }
 
    }
 
    public static void main(String[] args) {
        TestMultiThreading test = new TestMultiThreading();
        // ׼������
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
