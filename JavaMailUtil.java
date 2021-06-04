package oopAssignment;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailUtil implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String recepient;
	private String subject;	
	private String content;
	private String date;
	
	/*
	 * This constructor automatically puts current date to the 'date' field of the object.
	 */
	public JavaMailUtil(String recepient, String subject, String content) {
		this.recepient = recepient;
		this.subject = subject;
		this.content = content;
		this.date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());	// when email object is created, date will also be saved as a field.
	}
	/*
	 * Contructor overloading - Have to specify the 'date' string when creating the object 
	 */
	public JavaMailUtil(String recepient, String subject, String content, String date) {
		this.recepient = recepient;
		this.subject = subject;
		this.content = content;
		this.date = date;
	}

	//getters
	public String getRecepient() {
		return recepient;
	}

	public String getSubject() {
		return subject;
	}

	public String getContent() {
		return content;
	}

	public String getDate() {
		return date;
	}

	/*
	 * Email sending functionality
	 */
	public static void sendMail(String recepient, String subject, String content) throws AddressException, MessagingException, IOException
	{
		System.out.println("Preparing to send the email...");
		
		Properties properties = new Properties();
		
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");
		
		/*
		 * Email address and the password should be put to these variables
		 */
		String myAccountEmail = "***********"; /*****************EMAIL ADDRESS****************/
		String password = "**********";	/*****************PASSWORD****************/
		
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(myAccountEmail, password);
			}
		});
		
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(myAccountEmail));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
		message.setSubject(subject);
		message.setText(content);
		
		Transport.send(message);
		
		//email object serialization
		
		JavaMailUtil email_object = new JavaMailUtil(recepient, subject, content);	
		
		File f = new File("D:\\EmailDetails.txt");	//File to save serialized email objects
		if (!(f.exists() && !f.isDirectory()))	//If the specified file doesn't exsist
		{
			FileOutputStream fileOut = new FileOutputStream("D:\\EmailDetails.txt"); 	// new file willbe created 
			ObjectOutputStream out = new ObjectOutputStream(fileOut);	
			out.writeObject(email_object);
		    out.close();
		    fileOut.close();
		}
		else 	// If the file is already created
		{
			FileOutputStream fileOut = new FileOutputStream("D:\\EmailDetails.txt",true);	// Append mode
			
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
			out.writeObject(email_object);
		    out.close();
		    fileOut.close();
		}
		
		System.out.println("Email sent successfully");
	}
	
	/*
	 * Email Deserialize functionalty
	 */
	public static ArrayList<JavaMailUtil> deserialize() throws IOException, ClassNotFoundException
	{
		ArrayList<JavaMailUtil> email_objects = new ArrayList<>(); 
		
		FileInputStream fileIn = new FileInputStream("D:\\EmailDetails.txt");
		ObjectInputStream in = new ObjectInputStream(fileIn);
			
		try
		{
			while (true)
			{
				JavaMailUtil email_object = (JavaMailUtil) in.readObject();
				email_objects.add(email_object);
			}
		}
		catch (EOFException ex) {}
			
		in.close();
		fileIn.close();
		return email_objects;
	}
}
