package com.example.wifianalysis;

public class Wifi {

    private String ssid;
    private String bssid;
    private int level;
    private int frequency;
    private int channel;
    private int channelWidth;
    private String capablities;
    private int imageId;

    public Wifi(String ssid, String bssid, int level, int frequency, int channel, int channelWidth, String capablities, int imageId) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.level = level;
        this.frequency = frequency;
        this.channel = channel;
        this.channelWidth = channelWidth;
        this.capablities = capablities;
        this.imageId = imageId;
    }

    public String getSsid() {
        return ssid;
    }

    public String getBssid() {
        return "(" + bssid + ")";
    }

    public String getLevel() {
        return level+"dBm"+ "   " +evaluateLevel();
    }

    public String getFrequency() {
        return frequency + "MHz";
    }

    public String getChannel() {
        return "信道" +channel;
    }

    public String getChannelWidth() {
        return convertChannelWidth()+"MHz";
    }

    public String getCapablities() {
        return capablities;
    }

    public int getImageId() {
        return imageId;
    }

    private int convertChannelWidth() {
        switch (channelWidth){
            case 0:
                return 20;
            case 1:
                return 40;
            case 2:
                return 80;
            case 3:
                return 160;
            default:
                return 0;
        }
    }

    private String evaluateLevel(){
        if (level <= 0 && level >= -60) {
            return "很好";
        } else if (level < -60 && level >= -80) {
            return "一般";
        } else if (level < -80 && level >= -100) {
            return "较差";
        } else{
            return "无信号";
        }
    }

}
