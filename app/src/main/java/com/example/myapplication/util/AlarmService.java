package com.example.myapplication.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class AlarmService extends Service implements Runnable{

    private static final String RABBITMQ_HOST = "175.6.178.224";
    private static final int RABBITMQ_PORT = 5672;
    private static final String VIRTUAL_HOST = "/";
    private static final String QUEUE_NAME = "mc_alarm";
    private static final String USER = "admin";
    private static final String PASSWORD = "190032";
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private DBHelper dbHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        initialRabbit();
        dbHelper=new DBHelper(getApplicationContext(),"alarm_flag",null,1);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(this).start();
        return START_STICKY;
    }

    @Override
    public void run() {
        startConsumerMessage();
    }
    public void initialRabbit(){
        factory = new ConnectionFactory();
        factory.setHost(RABBITMQ_HOST);
        factory.setPort(RABBITMQ_PORT);
        factory.setVirtualHost(VIRTUAL_HOST);
        factory.setUsername(USER);
        factory.setPassword(PASSWORD);
    }
    public void startConsumerMessage(){
        try {
            connection=factory.newConnection();
            channel=connection.createChannel();
            channel.queueDeclare(QUEUE_NAME,true,false,false,null);
            channel.basicQos(1);
            DefaultConsumer consumer=new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message=new String(body,"UTF-8");
                    Log.d("TEST", "handleDelivery: "+message);
                    if (message.contains("normal")){
                        dbHelper.updateFlag(0);
                    }
                    else{
                        dbHelper.updateFlag(1);
                    }
                    channel.basicAck(envelope.getDeliveryTag(),false);
                }
            };
            channel.basicConsume(QUEUE_NAME,false,consumer);
        }catch (IOException | TimeoutException e){
            e.printStackTrace();
        }
    }
}
