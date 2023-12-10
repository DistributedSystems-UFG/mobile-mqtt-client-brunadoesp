package com.example.basicandroidmqttclient;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.basicandroidmqttclient.MESSAGE";
    public static final String brokerURI = "3.220.83.84";

    Activity thisActivity;
    ListView listView;
    ArrayAdapter<String> adapter;
    TextView subMsgTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thisActivity = this;
        listView = findViewById(R.id.minhaLista);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(adapter);
    }

    /** Called when the user taps the Send button */
    public void publishMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText topicName = (EditText) findViewById(R.id.editTextTopicName);
        EditText value = (EditText) findViewById(R.id.editTextValue);

        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(brokerURI)
                .buildBlocking();

        client.connect();
        client.publishWith()
                .topic(topicName.getText().toString())
                .qos(MqttQos.AT_LEAST_ONCE)
                .payload(value.getText().toString().getBytes())
                .send();
        client.disconnect();

        String message = topicName.getText().toString() + " " + value.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void sendSubscription(View view) {
        EditText topicName = (EditText) findViewById(R.id.editTextTopicNameSub);

        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(brokerURI)
                .buildBlocking();

        client.connect();

        // Use a callback lambda function to show the message on the screen
        client.toAsync().subscribeWith()
                .topicFilter(topicName.getText().toString())
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(msg -> {
                    thisActivity.runOnUiThread(() -> {
                        String message = new String(msg.getPayloadAsBytes(), StandardCharsets.UTF_8);
                        adapter.insert(message, 0);
                    });;
                })
                .send();
    }

    public void lightON(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        String topicName = "light";
        String value = "on";

        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(brokerURI)
                .buildBlocking();

        client.connect();
        client.publishWith()
                .topic(topicName)
                .qos(MqttQos.AT_LEAST_ONCE)
                .payload(value.getBytes())
                .send();
        client.disconnect();

    }

    public void lightOFF(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        String topicName = "light";
        String value = "off";

        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(brokerURI)
                .buildBlocking();

        client.connect();
        client.publishWith()
                .topic(topicName)
                .qos(MqttQos.AT_LEAST_ONCE)
                .payload(value.getBytes())
                .send();
        client.disconnect();

    }



}