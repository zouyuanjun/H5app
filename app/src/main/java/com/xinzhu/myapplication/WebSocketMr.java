package com.xinzhu.myapplication;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketMr extends WebSocketListener {

    @Override
    public void onOpen(WebSocket webSocket, Response response) {

        webSocket.send("hello world");
        webSocket.send("welcome");
        webSocket.send(ByteString.decodeHex("adef"));
     //   webSocket.close(1000, "再见");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d("onMessage: " + text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Log.d("onMessage byteString: " + bytes);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(1000, null);
        Log.d("onClosing: " + code + "/" + reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.d("onClosed: " + code + "/" + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.d("onFailure: " + t.getMessage());
    }


}
