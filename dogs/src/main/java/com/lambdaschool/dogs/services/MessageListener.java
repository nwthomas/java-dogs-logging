package com.lambdaschool.dogs.services;

import com.lambdaschool.dogs.DogsApplication;
import com.lambdaschool.dogs.model.MessageDetail;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MessageListener
{
	@RabbitListener(queues = DogsApplication.QUEUE_NAME_HIGH)
	public void receiveHighMessage(MessageDetail msg)
	{
		System.out.println("Received High Queue message {" + msg.toString() + "}");
	}

	@RabbitListener(queues = DogsApplication.QUEUE_NAME_LOW)
	public void receiveLowMessage(MessageDetail msg)
	{
		System.out.println("Received Low Queue message {" + msg.toString() + "}");
	}

	@RabbitListener(queues = DogsApplication.QUEUE_ERROR)
	public void receiveErrorMessage(MessageDetail msg)
	{
		System.out.println("Received Error Queue message {" + msg.toString() + "}");
	}
}
