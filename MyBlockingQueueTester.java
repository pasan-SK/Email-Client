package oopAssignment;

/*
 * Producer thread for testing purposes
 */
class MyProducer implements Runnable
{
	private MyBlockingQueue queue;
	
	public MyProducer(MyBlockingQueue queue) {
		this.queue = queue;
	}


	@Override
	public void run() {
		int i = 0;
		while (true)
		{
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			JavaMailUtil o = new JavaMailUtil("test@gmail.com" + i, "testSubject" + i, "testcontent" + i);
			queue.enqueue(o);
			i++;
		}
	}
	
}

/*
 * Consumer thread for testing purposes
 */
class MyConsumer implements Runnable
{
	MyBlockingQueue queue;

	public MyConsumer(MyBlockingQueue queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		while (true)
		{
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			queue.dequeue();
		}
		
	}
	
}
public class MyBlockingQueueTester {

	public static void main(String[] args) {
		MyBlockingQueue myBQ = new MyBlockingQueue(2);
		Thread producer = new Thread(new MyProducer(myBQ));
		Thread consumer = new Thread(new MyConsumer(myBQ));
		
		producer.start();
		consumer.start();
	}

}
