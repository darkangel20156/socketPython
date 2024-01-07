package com.example.socketpython;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    Button sendImage;
    ImageView imageView;

    public void sendTextMsg(DataOutputStream out, String msg) throws IOException {
        byte[] bytes = msg.getBytes();
        long len = bytes.length;
        // Send the length first, then send the content
        out.writeLong(len);
        out.write(bytes);
    }

    public void sendImgMsg(DataOutputStream out) throws IOException {
        // The picture sent is an icon, which is the Android robot, convert the bitmap into a byte array
        Log.i("sendImgMsg", "len: " + "1");
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.drone);

        if (bitmap != null) {
            Log.i("sendImgMsg", "len: " + "2");
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, bout);
            // Write the length of bytes, and then write the bytes of the picture
            byte[] imageData = bout.toByteArray();
            long len = imageData.length;

            // Print the length sent here
            Log.i("sendImgMsg", "len: " + len);
            out.writeLong(len);
            out.write(imageData);
        } else {
            Log.e("sendImgMsg", "Bitmap is null");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendImage = findViewById(R.id.buttonSend);
        imageView = findViewById(R.id.image1);

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    Socket socket;
                    String host = "10.0.2.2"; // Replace with the actual IP address of the server
                    int port = 8999;

                    public void run() {
                        try {
                            // Establish connection
                            Log.i("Connection", "Connecting to " + host + ":" + port);
                            InetAddress serverAddr = InetAddress.getByName(host);
                            socket = new Socket(serverAddr, port);
                            Log.i("Connection", "Connected!");

                            // Get the output stream and send messages through this stream
                            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                            // Send text message
                            // sendTextMsg(out, "Message from mobile client");

                            // Send image message
                            sendImgMsg(out);

                            out.close();
                            socket.close();
                            Log.i("Connection", "Disconnected!");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }
}
