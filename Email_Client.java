package oopAssignment;

// My index number - 190290U 


//import libraries
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.AddressException;
import javax.mail.search.FlagTerm;

public class Email_Client {

      public static void main(String[] args) throws IOException, AddressException, MessagingException, ParseException, ClassNotFoundException 
      {
    	  EmailStatRecorder emailStatRecoder = new EmailStatRecorder();
    	  EmailStatPrinter emailStatPrinter = new EmailStatPrinter(); 
    	  MyBlockingQueue blockingQueue = new MyBlockingQueue(1); //shared resource
    	  
    	  //Email receiver thread
    	  System.out.println("email Receiver Thread started!"); 
    	  Thread emailReceiverThread = new Thread(new EmailReceiver(blockingQueue, emailStatRecoder, emailStatPrinter));
    	  emailReceiverThread.start();
    	  
    	  //Email serializer thread
    	  System.out.println("email serializer Thread started!"); 
    	  Thread emailSerializer = new Thread(new EmailSerializer(blockingQueue));
    	  emailSerializer.start();
    
    	  
    	  // code to create objects for each recipient in clientList.txt
    	  
    	  	ArrayList<Recipient> recipient_list = new ArrayList<>();			//All the Recipients object will be stored in this arraylist.
    	  	ArrayList<Friend> recipients_with_BirthDays = new ArrayList<>();	//All the recipients with birthdays (Friends) will be stored in this arraylist
    	  	BufferedReader buffered_r = new BufferedReader(new FileReader("D:\\java files\\Practicals\\src\\oopAssignment\\clientList.txt"));
    	  	
    	  	String recipientDetails;		//each line of the clientList.txt file
    	  	while ((recipientDetails = buffered_r.readLine()) != null)
    	  	{
    	  		String[] detailsArray = recipientDetails.strip().split("[,: ]+");	//each recipient's details (name, email, designation etc.) can be accessed through this String array.
    	  		
    	  		if (detailsArray[0].equals("Official"))
    	  			recipient_list.add(new OfficialRecipient(detailsArray[1], detailsArray[2], detailsArray[3]));
    	  		
    	  		else if (detailsArray[0].equals("Office_friend"))
    	  		{
    	  			OfficeFriendRecipient ofr = new OfficeFriendRecipient(detailsArray[1], detailsArray[2], detailsArray[3], detailsArray[4]);
    	  			recipient_list.add(ofr);
    	  			recipients_with_BirthDays.add(ofr);
    	  		}
    	  		else 
    	  		{
    	  			PersonalRecipient pr = new PersonalRecipient(detailsArray[1], detailsArray[2], detailsArray[3], detailsArray[4]);
    	  			recipient_list.add(pr);
    	  			recipients_with_BirthDays.add(pr);
    	  		}
    	  		
    	  	}
    	  	buffered_r.close();
    	  	
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter option type: \n"
                  + "1 - Adding a new recipient\n"
                  + "2 - Sending an email\n"
                  + "3 - Printing out all the recipients who have birthdays\n"
                  + "4 - Printing out details of all the emails sent\n"
                  + "5 - Printing out the number of recipient objects in the application");

            int option = scanner.nextInt();
            scanner.nextLine();		//This will read "\n" after the scanner reads the integer in previous line

            switch(option){
                  case 1:
                      // input format - Official: nimal,nimal@gmail.com,ceo
                	  System.out.println("Enter the new recipients's details in following corresponding format , \n"	
                	  				+ "For official type recipients \t\t => Official: <name>,<emai>,<designation> \n"
                	  				+ "For office friend type recipients \t => Office_friend: <name>,<emai>,<designation>,<birthdate in yyyy/mm/dd format> \n"
                	  				+ "For personal type recipients \t\t => Personal: <name>,<nick name>,<emai>,<birthdate in yyyy/mm/dd format>"); 	//this will tell the user, how the next command-line argument should be 
                	  
                      // Use a single input to get all the details of a recipient
                	  String recipientDataString = scanner.nextLine();
                	  String[] recipientData = recipientDataString.strip().split("[,: ]+");
                	  
                	  // code to add a new recipient
                	  try 
                	  {
	                	  if (recipientData[0].equals("Official"))
	          	  		      recipient_list.add(new OfficialRecipient(recipientData[1], recipientData[2], recipientData[3]));
	          	  		
	          	  		  else if (recipientData[0].equals("Office_friend"))
	          	  		      recipient_list.add(new OfficeFriendRecipient(recipientData[1], recipientData[2], recipientData[3], recipientData[4]));
	          	  		
	          	  		  else if (recipientData[0].equals("Personal"))
	          	  		      recipient_list.add(new PersonalRecipient(recipientData[1], recipientData[2], recipientData[3], recipientData[4]));
	          	  		  else 
	          	  		  {
	          	  			  System.out.println("INVALID INPUT!!");
	          	  			  break;
	          	  		  }
                	  } catch(Exception e) {System.out.println("INVALID INPUT!!");}
                	  
          	  		  // store details in clientList.txt file
                	  BufferedWriter buffered_w = new BufferedWriter(new FileWriter("D:\\java files\\Practicals\\src\\oopAssignment\\clientList.txt",true));	//file opened in append mode
                	  buffered_w.append(recipientDataString+"\n");
                	  buffered_w.close();
                	  
                	  System.out.println("Data has been added successfully stored!");
                      // Hint: use methods for reading and writing files
                      break;
                      
                  case 2:
                      // input format - email, subject, content
                	  System.out.println("Enter your email in this format: <email address of recipient>, <subject>, <content>");	//this will tell the user, how the next command-line argument should be
                	  String[] emailData = scanner.nextLine().strip().split(",");
                	  
                      // code to send an email
                	  JavaMailUtil.sendMail(emailData[0].strip(), emailData[1].strip(), emailData[2].strip());
                      break;
                      
                  case 3:
                      // input format - yyyy/MM/dd (ex: 2018/09/17)
                	  System.out.println("Enter the date in yyyy/MM/dd format: ");	//this will tell the user, how the next command-line argument should be
                	  
                	  boolean hasFoundRecipient = false;
                	  String inputDate = scanner.nextLine().strip();
                	  
                      // code to print recipients who have birthdays on the given date
                	  for (Friend rec : recipients_with_BirthDays)
                	  {
            			  String reipientBirthDay = rec.birthday;
            			  if (reipientBirthDay.substring(5).equals(inputDate.substring(5)))
            			  {
            				  hasFoundRecipient = true;
            				  System.out.println(rec.name);
            			  }
                	  }
                	  if (hasFoundRecipient == false) System.out.println("No recipients found.");
                      break;
                      
                  case 4:
                      // input format - yyyy/MM/dd (ex: 2018/09/17)
                	  System.out.println("Enter the date in yyyy/MM/dd format: ");   //this will tell the user, how the next command-line argument should be
                	        	  
                	  String inputDate1 = scanner.nextLine().strip();          	  
                	  
                      // code to print the details of all the emails sent on the input date
                	  
                      ArrayList<JavaMailUtil> email_objects = JavaMailUtil.deserialize();  //this will return all the email objects (after deserializing) sent through this email client application.
                      
                      boolean hasFoundEmailObject = false;
                      
                      for (JavaMailUtil email_object: email_objects)
                      {
                    	  if (email_object.getDate().equals(inputDate1))
                    	  {
                    		  hasFoundEmailObject = true;
                    		  System.out.println("Recipient's email address:");
                    		  System.out.println("\t" + email_object.getRecepient() + "\n");
                    		  
                    		  System.out.println("Subject:");
                    		  System.out.println("\t" + email_object.getSubject() + "\n");
                    	  }
                      }
                      
                      if (hasFoundEmailObject == false) System.out.println("No emails were sent in that day.");
                      
                      break;
                  case 5:
                      // code to print the number of recipient objects in the application
                	  System.out.println("Number of recipients objects in the application is: " + Recipient.count);
                      break;
            }

            // start email client
            
            // use necessary variables, methods and classes
            
            String currentDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date()); 	//Getting today's date as yyyy/MM/dd string;
            String current_Month = currentDate.substring(5, 7);		
            String current_dayOfMonth = currentDate.substring(8, 10);
            
            ArrayList<Friend> today_birthday_friends = new ArrayList<>();	//Friends who have birthdays today will be stored in this arraylist
            
            for (Friend rec : recipients_with_BirthDays)		
	      	{	
      			String birthDay = rec.birthday;
      			String birthDay_Month = birthDay.substring(5, 7);
                String birthDay_dayOfMonth = birthDay.substring(8, 10);
                
                if (current_Month.equals(birthDay_Month) && current_dayOfMonth.equals(birthDay_dayOfMonth))
                {
                	today_birthday_friends.add(rec);
                }
      		
	      	}
            if (today_birthday_friends.size() == 0) 	//If no one has a birthday today
            {
            	scanner.close();
            	return;
            }
            
            //These codes will run if at least one friend has a birthday today
            
            System.out.println("\nSystem found recipient/s who has bithday today. " 		
        			+ "Do you wish to send the Email automatically? "
        			+ "type 'Y' if yes, 'N' if no"); 	//If the Email_client program runs more than one time, user can type "N" here. This will avoid sending birthday wishing emails more than one time to the recipients who have birthdays today. 				
        	
        	String consent = scanner.next();
        	
        	if ( ! (consent.equals("Y"))) 	//If user does not want to send birthday wishing emails
        	{
        		scanner.close();
        		System.out.println("Thank you for using the system!");
        		return;
        	}
        	
        	//These codes will run if user wants to send birthday wishing emails     	
        	String emailBody;
        	
        	for (Friend birthday_friend : today_birthday_friends)
        	{
        		if (birthday_friend instanceof OfficeFriendRecipient)	
            		emailBody = "Wish you a Happy Birthday.\n <Pasan Kalansooriya> ";	//birthday wish for official recipients.
            	else	
            		emailBody = "hugs and love on your birthday.\n <Pasan Kalansooriya>";	//birthday wish for office_friends and personal recipients.
        		
        		JavaMailUtil.sendMail(birthday_friend.email, "Birthday Wish", emailBody);	// code to send the email
        	}
	
            scanner.close();
            System.out.println("Thank you for using the system!");
        }
      
}

// create more classes needed for the implementation (remove the  public access modifier from classes when you submit your code)
abstract class Recipient
{
	static int count=0;
	String name;
	String email;
	public Recipient() {
		count += 1;
	}
}


abstract class Friend extends Recipient
{
	String birthday;
}


class OfficialRecipient extends Recipient 
{
	String designation;

	public OfficialRecipient(String name, String email, String designation) {
		super();
		this.name = name;
		this.email = email;
		this.designation = designation;
	}	
}

class OfficeFriendRecipient extends Friend
{
	String designation;	
	public OfficeFriendRecipient(String name, String email, String designation, String birthday) {
		super();
		this.name = name;
		this.email = email;
		this.designation = designation;
		this.birthday = birthday;
	}
}

class PersonalRecipient extends Friend
{
	String nickName;	
	public PersonalRecipient(String name, String nickName, String email, String birthday) {
		super();
		this.name = name;
		this.email = email;
		this.nickName = nickName;
		this.birthday = birthday;
	}
}

/*
 * Blocking Queue Implementation with thread safe enqueue, dequeue operations 
 */
class MyBlockingQueue
{
	private LinkedList<JavaMailUtil> queue;
	private int max_queue_size;
	
	public MyBlockingQueue(int max_queue_size )
	{
		this.queue = new LinkedList<>();
		this.max_queue_size = max_queue_size;
	}
	
	public synchronized void enqueue(JavaMailUtil emailObject)
	{
		if (queue.size() == max_queue_size)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		queue.add(emailObject);
		System.out.println("email enqueued!");
		notifyAll();
	
	}
	
	public synchronized JavaMailUtil dequeue()
	{
		if (queue.size() == 0)
		{
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		JavaMailUtil resultEmail = queue.removeLast();
		System.out.println("email dequeued!");
		notifyAll();
		return resultEmail;	
	}
}


interface Observable
{
	public void attach(Observer o);
	public void notifyObservers();
}

/*
 * class for Email receiving thread 
 */
class EmailReceiver implements Runnable, Observable
{
	private MyBlockingQueue queue;						
	private ArrayList<Observer> observers;				//List of observers
	private String email_id = "************"; 	/*****************EMAIL ADDRESS****************/
	private String password = "**************";			/*****************PASSWORD****************/
	private Properties properties;
	private Boolean canRun;
	
	public EmailReceiver(MyBlockingQueue queue, EmailStatRecorder emailStatRecoder,
			EmailStatPrinter emailStatPrinter) throws MessagingException {
		
		this.queue = queue;
		observers = new ArrayList<>(2);
		attach(emailStatRecoder);	//Attaching observer - email stat recoder
		attach(emailStatPrinter);	//Attaching observer - email stat printer
		this.properties = new Properties();
		
		//required settings for IMAP protocol
		properties.put("mail.store.protocol", "imaps");
		properties.put("mail.imaps.host", "imap.gmail.com");
		properties.put("mail.imaps.port", "993");		
		canRun = true;
	}

	@Override
	public void run() 
	{	
		while(canRun)
		{
			try 
			{
				Session session = Session.getDefaultInstance(properties, null);
				Store store = session.getStore("imaps");
				store.connect(email_id, password);
				Folder inbox = store.getFolder("inbox");
				inbox.open(Folder.READ_WRITE);
				
				if (inbox.getUnreadMessageCount() > 0) {
					int messageCount = inbox.getUnreadMessageCount();
					
					Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

					int i = 0;
					while (i < messageCount) {
						Message message = messages[i];
						
						String emailAddress = message.getFrom()[0].toString();
						String subject = message.getSubject();
						String content = message.getContent().toString();
						
						String SentDate = new SimpleDateFormat("yyyy/MM/dd").format(message.getSentDate());
						
						//creating the email object
						JavaMailUtil emailObject = new JavaMailUtil(emailAddress, subject, content, SentDate);
						message.setFlags(new Flags(Flags.Flag.SEEN), true);
						
						//Notifying the observers
						notifyObservers();
						
						//Enqueue the email object to blocking queue
						queue.enqueue(emailObject);
						i++;
					}
				} 
				inbox.close(true);
				store.close();
			}
			catch (MessagingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void attach(Observer o) {
		observers.add(o);
	}

	@Override
	public void notifyObservers() {
		observers.get(0).getNotified();
		observers.get(1).getNotified();
	}
}

/*
 * Class for email serializing thread
 */
class EmailSerializer implements Runnable 
{
	private MyBlockingQueue queue;
	
	public EmailSerializer(MyBlockingQueue queue) {
		this.queue = queue;
	}


	@Override
	public void run() 
	{
		while (true)
		{
			//fetching an email object from the blocking queue
			JavaMailUtil emailObject = queue.dequeue();
			
			/*
			 * Serializing the email object
			 */
			try
			{
				File f = new File("D:\\ReceivedEmailDetails.txt");	//File to save serialized email objects
				if (!(f.exists() && !f.isDirectory()))	//If the specified file doesn't exsist
				{
					FileOutputStream fileOut = new FileOutputStream("D:\\ReceivedEmailDetails.txt"); 	// new file willbe created 
					ObjectOutputStream out = new ObjectOutputStream(fileOut);	
					out.writeObject(emailObject);
				    out.close();
				    fileOut.close();
				}
				else 	// If the file is already created
				{
					FileOutputStream fileOut = new FileOutputStream("D:\\ReceivedEmailDetails.txt",true);	// Append mode
					
					/*
					 * Since the file is already exsisting, we have to reset the Stream header created 
					 * at previous object serialization. Otherwise we get StreamCorruptedException when deserializing
					 * since there will be 2 Stream headers.
					 */
					ObjectOutputStream out = new ObjectOutputStream(fileOut) {		
						protected void writeStreamHeader() throws IOException {
							reset();		//Resetting the previous Stream header
						}
					};
					out.writeObject(emailObject);
				    out.close();
				    fileOut.close();
				}
			}
			catch (Exception e)
			{
				System.out.println("Error when serializing received email!");
				e.printStackTrace();
			}
		}
	}	
}

abstract class Observer
{
	protected abstract void getNotified();
}

class EmailStatRecorder extends Observer
{

	@Override
	protected void getNotified() {
		System.out.println("\nAn email is received at: " + new Date()); //Printing to console
	}
}

class EmailStatPrinter extends Observer
{

	@Override
	protected void getNotified(){	
		
		FileWriter file_w;
		try 
		{
			file_w = new FileWriter("D:\\EmailStatPrinterDetails.txt",true); //file to print
			BufferedWriter buffered_w = new BufferedWriter(file_w);
	        PrintWriter print_w = new PrintWriter(file_w);

	        print_w.println("an email is received at: " + new Date());	//prints to file

	        print_w.flush();
	        print_w.close();
	        buffered_w.close();
	        file_w.close();
	        
		} catch (IOException e) {
			System.out.println("Error Occured while printing!!!");
			e.printStackTrace();
		}              
	}
}

