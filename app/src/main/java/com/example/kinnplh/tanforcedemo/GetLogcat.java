package com.example.kinnplh.tanforcedemo;

import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by kinnplh on 2017/8/14.
 */

public class GetLogcat extends Thread {
    private String logTag;
    final Deque<String> logLines;
    MainActivity ac;
    String ip;
    String port;
    Socket socket;
    BufferedReader reader;
    GetLogcat(String logTag, String _ip, String _port) {
        this.logTag = logTag;
        logLines = new ArrayDeque<>();
        ip = _ip;
        port = _port;
        this.start();
    }

    public void run(){
        // connect the internet
        try {
            socket = new Socket(ip, Integer.valueOf(port));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true){
                String line = reader.readLine();
                if(logTag == null || logTag.isEmpty() || line.contains(logTag)){
                    synchronized (logLines){
                        if(!logLines.isEmpty())
                            logLines.clear();
                        logLines.addLast(line);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
           // android.os.Process.killProcess(android.os.Process.myPid());
        }

    }

    public String getOneLineLog(){
        synchronized (logLines){
            if(logLines.isEmpty())
                return "";
            return logLines.removeFirst();
        }
    }
}
