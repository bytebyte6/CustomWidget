package com.example.widget;

import java.util.List;


public class SleepData {

    private List<Integer> mDurations;

    //sleep 睡眠值数据 睡眠质量 x<=5为深睡，6<=x<=14为浅睡，15为清醒
    private List<Integer> mQualitys;

    private List<Integer> timeStamp;

    public List<Integer> getTimeStamp() {
        return timeStamp;
    }

    public SleepData(List<Integer> durations, List<Integer> quality) {
        mDurations = durations;
        mQualitys = quality;
    }
    public SleepData(List<Integer> durations, List<Integer> quality, List<Integer> timeStamp) {
        mDurations = durations;
        mQualitys = quality;
        this.timeStamp=timeStamp;
    }

    public List<Integer> getDurations() {
        return mDurations;
    }

    public void setDurations(List<Integer> durations) {
        mDurations = durations;
    }

    public List<Integer> getQualitys() {
        return mQualitys;
    }

    public void setQuality(List<Integer> quality) {
        mQualitys = quality;
    }
}