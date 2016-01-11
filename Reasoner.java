// Default package
package Examples;

// Libraries
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import Examples.CarHire;
import Examples.Vehicle;
import Examples.Customer;
import Examples.Location;
import Examples.Rental;
import Examples.Employee;
import Examples.SimpleGUI;

public class Reasoner {

	public CarHire theCarHire;
	public SimpleGUI Myface;

	// The lists holding the class instances of all carHire (domain) entities
	public List theCarHireList = new ArrayList();
	public List theVehicleList = new ArrayList();
	public List theCustomerList = new ArrayList();
	public List theLocationList = new ArrayList();
	public List theRentalList = new ArrayList();
	public List theEmployeeList = new ArrayList();
	public List theRecentThing = new ArrayList(); 

	// Gazetteers to store synonyms for the domain entities names
	public Vector<String> carHiresyn = new Vector<String>();
	public Vector<String> vehiclesyn = new Vector<String>();
	public Vector<String> customersyn = new Vector<String>();
	public Vector<String> locationsyn = new Vector<String>();
	public Vector<String> rentalsyn = new Vector<String>();
	public Vector<String> employeesyn = new Vector<String>();
	public Vector<String> recentobjectsyn = new Vector<String>();

	public String questiontype = "";         // questiontype selects method to use in a query
	public List classtype = new ArrayList(); // classtype selects which class list to query
	public String attributetype = "";        // attributetype selects the attribute to check for in the query

	public Object Currentitemofinterest; // Last Object dealt with
	public Integer Currentindex;         // Last Index used

	public String tooltipstring = "";
	public String URL = "";              // URL for Wordnet site
	public String URL2 = "";             // URL for Wikipedia entry
	public boolean loggedIn = false; // Check's whether user logged in or not

	public Reasoner(SimpleGUI myface) {

		Myface = myface; // reference to GUI to update Tooltip-Text
	}

	public void initknowledge() { // load all the library knowledge from XML 

		JAXB_XMLParser xmlhandler = new JAXB_XMLParser(); // we need an instance of our parser
		File xmlfiletoload = new File("carhire2.xml"); // we need a (CURRENT)  file (xml) to load  

		// Init synonmys and typo forms in gazetteers
		carHiresyn.add("carhire");
		carHiresyn.add("hire");
		carHiresyn.add("carrental");
		carHiresyn.add("rental");
		carHiresyn.add("carstorent");
		carHiresyn.add("rentals");
		carHiresyn.add("carhires");
		carHiresyn.add("office");
		carHiresyn.add("headquarters");
		carHiresyn.add("hq");

		vehiclesyn.add("vehicle");
		vehiclesyn.add("car");
		vehiclesyn.add(" car");
		vehiclesyn.add("ar");
		vehiclesyn.add("vehcle");
		vehiclesyn.add(" vehicle");
		vehiclesyn.add("machine");
		vehiclesyn.add("drive");
		vehiclesyn.add("caar");
		vehiclesyn.add("van");
		vehiclesyn.add("minivan");
		vehiclesyn.add("driving");

		customersyn.add("customer");
		customersyn.add("consumer");
		customersyn.add(" customer");
		customersyn.add("client");
		customersyn.add(" client");
		customersyn.add("buyer");
		customersyn.add("user");
		customersyn.add("regular");
		customersyn.add("clientele");

		locationsyn.add("location");
		locationsyn.add("place");
		locationsyn.add("city");
		locationsyn.add("whereabouts");
		locationsyn.add("house");
		locationsyn.add("position");
		locationsyn.add("spot");
		locationsyn.add("point");
		locationsyn.add("destination");
		locationsyn.add("site");
		locationsyn.add("area");
		locationsyn.add("address");

		rentalsyn.add("renting");
		rentalsyn.add("rental");
		rentalsyn.add("get");
		rentalsyn.add("take");
		rentalsyn.add("hire");

		employeesyn.add("manager");
		employeesyn.add("salesman");
		employeesyn.add("personal");
		employeesyn.add("staff");
		employeesyn.add("worker");
		employeesyn.add("employee");
		employeesyn.add("operator");
		employeesyn.add("member");

		recentobjectsyn.add(" this");
		recentobjectsyn.add(" that");
		recentobjectsyn.add(" him");
		recentobjectsyn.add(" her");	// spaces to prevent collision with "wHERe"	
		recentobjectsyn.add(" it");

		try {
			FileInputStream readthatfile = new FileInputStream(xmlfiletoload); // initiate input stream

			theCarHire = xmlhandler.loadXML(readthatfile);
			// Fill the Lists with the objects data just generated from the xml

			theVehicleList = theCarHire.getVehicle();
			theCustomerList = theCarHire.getCustomer();
			theLocationList = theCarHire.getLocation();
			theRentalList = theCarHire.getRental();
			theEmployeeList = theCarHire.getEmployee();
			theCarHireList.add(theCarHire); // force it to be a List
		}

		catch (Exception e) {
			e.printStackTrace();
			System.out.println("error in init");
		}
	}

	public  Vector<String> generateAnswer(String input) { // Generate an answer (String Vector)

		Vector<String> out = new Vector<String>();
		out.clear();                 // just to make sure this is a new and clean vector

		questiontype = "none";
		Integer Answered = 0;        // check if answer was generated
		Integer subjectcounter = 0;  // Counter to keep track of # of identified subjects (classes)

		// Answer Generation Idea: content = Questiontype-method(classtype class) (+optional attribute)
		// ___________________________ IMPORTANT _____________________________
		input = input.toLowerCase(); // all in lower case because thats easier to analyse
		// ___________________________________________________________________
		String answer = "";          // the answer we return

		// Questions below
		if (input.contains("how many")){questiontype = "amount"; input = input.replace("how many", "<b>how many</b>");} 
		if (input.contains("number of")){questiontype = "amount"; input = input.replace("number of", "<b>number of</b>");}
		if (input.contains("hire")){questiontype = "amount"; input = input.replace("hire", "<b>hire</b>");}
		if (input.contains("count")){questiontype = "amount"; input = input.replace("count", "<b>count</b>");}
		if (input.contains("model")){questiontype = "amount"; input = input.replace("model", "<b>model</b>");}
		if (input.contains("total")){questiontype = "amount"; input = input.replace("total", "<b>total</b>");}

		if (input.contains("print")){questiontype = "list"; input = input.replace("print", "<b>print</b>");}
		if (input.contains("list")){questiontype = "list"; input = input.replace("list", "<b>list</b>");}
		if (input.contains("display")){questiontype = "list"; input = input.replace("display", "<b>display</b>");}
		if (input.contains("show")){questiontype = "list"; input = input.replace("show", "<b>show</b>");}
		if (input.contains("can i see")){questiontype = "list"; input = input.replace("can i see", "<b>can i see</b>");}
		if (input.contains("can i view")){questiontype = "list"; input = input.replace("can i view", "<b>can i view</b>");}

		if (input.contains("is there a")){questiontype = "checkfor"; input = input.replace("is there a", "<b>is there a</b>");}
		if (input.contains("i am searching")){questiontype = "checkfor"; input = input.replace("i am searching", "<b>i am searching</b>");}
		if (input.contains("i am looking for")){questiontype = "checkfor"; input = input.replace("i am looking for", "<b>i am looking for</b>");}
		if (input.contains("do you have")&&!input.contains("how many")){questiontype = "checkfor";input = input.replace("do you have", "<b>do you have</b>");}
		if (input.contains("i look for")){questiontype = "checkfor"; input = input.replace("i look for", "<b>i look for</b>");}
		if (input.contains("is there")){questiontype = "checkfor"; input = input.replace("is there", "<b>is there</b>");}
		if (input.contains("will you have")){questiontype = "checkfor"; input = input.replace("will you have", "<b>will you have</b>");}

		if (input.contains("username")){questiontype = "customer"; input = input.replace("username", "<b>username</b>");}

		if (input.contains("where") 
				|| input.contains("can't find")
				|| input.contains("can i find") 
				|| input.contains("way to"))

		{
			questiontype = "location";
			System.out.println("Find Location");
		}
		if (input.contains("can i rent") 
				|| input.contains("can i hire")
				|| input.contains("interested in")
				|| input.contains("feel like")
				|| input.contains("prefer")
				|| input.contains("can i get the vehicle")
				|| input.contains("am i able to")
				|| input.contains("could i rent") 
				|| input.contains("i want to rent")
				|| input.contains("i want to hire"))

		{
			questiontype = "intent";
			System.out.println("Find Vehicle Availability");
		}

		if (input.contains("help") 
				|| input.contains("cmd")
				|| input.contains("commands")
				|| input.contains("need help")
				|| input.contains("elp")
				|| input.contains("how does this work")
				|| input.contains("instructions")
				|| input.contains("steps")
				|| input.contains("ways")
				|| input.contains("navigation")) 			

		{
			questiontype = "help";
			System.out.println("help");
		}

		if (input.contains("thank you") 
				|| input.contains("bye")
				|| input.contains("thanks")
				|| input.contains("have a good one")
				|| input.contains("have a nice day")
				|| input.contains("appreciate")
				|| input.contains("cool thanks")) 			

		{
			questiontype = "farewell";
			System.out.println("farewell");
		}

		if (input.contains("sign out") 
				|| input.contains("disconnect")
				|| input.contains("sign me out")
				|| input.contains("logout")
				|| input.contains("close"))

		{
			questiontype = "signout";
			System.out.println("signout");
		}


		// ------- Checking the Subject of the Question --------------------------------------
		for (int x = 0; x < vehiclesyn.size(); x++) { 
			if (input.contains(vehiclesyn.get(x))) {
				classtype = theVehicleList;

				input = input.replace(vehiclesyn.get(x), "<b>"+vehiclesyn.get(x)+"</b>");

				subjectcounter = 1;
				System.out.println(">>> VEHICLE CLASS <<<");
			}
		}
		for (int x = 0; x < customersyn.size(); x++) {
			if (input.contains(customersyn.get(x))) {
				classtype = theCustomerList;

				input = input.replace(customersyn.get(x), "<b>"+customersyn.get(x)+"</b>");

				subjectcounter = 1;
				System.out.println(">>> CUSTOMER CLASS <<<");
			}
		}
		for (int x = 0; x < locationsyn.size(); x++) {
			if (input.contains(locationsyn.get(x))) {
				classtype = theLocationList;

				input = input.replace(locationsyn.get(x), "<b>"+locationsyn.get(x)+"</b>");

				subjectcounter = 1;	
				System.out.println(">>> LOCATION CLASS <<<");
			}
		}
		for (int x = 0; x < rentalsyn.size(); x++) {
			if (input.contains(rentalsyn.get(x))) {
				classtype = theRentalList;

				input = input.replace(rentalsyn.get(x), "<b>"+rentalsyn.get(x)+"</b>");

				subjectcounter = 1;	
				System.out.println(">>> RENTAL <<<");
			}
		}
		for (int x = 0; x < employeesyn.size(); x++) {
			if (input.contains(employeesyn.get(x))) {
				classtype = theEmployeeList;

				input = input.replace(employeesyn.get(x), "<b>"+employeesyn.get(x)+"</b>");

				subjectcounter = 1;	
				System.out.println(">>> EMPLOYEE <<<");
			}
		}

		if(subjectcounter == 0){
			for (int x = 0; x < recentobjectsyn.size(); x++) {  
				if (input.contains(recentobjectsyn.get(x))) {
					classtype = theRecentThing;

					input = input.replace(recentobjectsyn.get(x), "<b>"+recentobjectsyn.get(x)+"</b>");

					subjectcounter = 1;
					System.out.println(">>> CLASS "+recentobjectsyn.get(x));
				}
			}
		}
		//System.out.println("subjectcounter = "+subjectcounter);

		for (int x = 0; x < carHiresyn.size(); x++) {
			if (input.contains(carHiresyn.get(x))) {
				if (subjectcounter == 0) { // carHire is the first subject in the question
					input = input.replace(carHiresyn.get(x), "<b>"+carHiresyn.get(x)+"</b>");
					classtype = theCarHireList;
					System.out.println(">>> carHIRE CLASS <<<");		
				}
			}
		}

		// Compose Method call and generate answerVector
		if (questiontype == "amount") { // Number of Subject
			Integer numberof = Count(classtype);
			// Total count of vehicles MRT holds + location of branches
			answer=("MRT carHire operates in numerous countries. As of today, we have over <b> " + numberof
					+ classtype.get(0).getClass().getSimpleName() + "s</b> available for you to rent accross all our branches which includes <b>London, GB</b> and <b>Paris, FR</b>"
					+".");

			Answered = 1; // Answer generated
		}

		if (questiontype == "list") { // Prints all the list of kind (vehicles, customers, etc)
			answer=("At the moment, we have all these "
					+ classtype.get(0).getClass().getSimpleName().toLowerCase() + "s to rent accross all our branches:"
					+ ListAll(classtype));
			Answered = 1; // Answer generated
		}

		if (questiontype == "checkfor") { // Prints and checks for whether class
			Vector<String> check = CheckFor(classtype, input);
			answer=(check.get(0));
			Answered = 1; // Answer generated
			if (check.size() > 1) {
				Currentitemofinterest = classtype.get(Integer.valueOf(check
						.get(1)));
				System.out.println("Classtype List = "
						+ classtype.getClass().getSimpleName());
				System.out.println("Index in Liste = "
						+ Integer.valueOf(check.get(1)));
				Currentindex = Integer.valueOf(check.get(1));
				theRecentThing.clear(); // Clear it before adding (changing) the
				// now recent thing
				theRecentThing.add(classtype.get(Currentindex));
			}
		}

		if (questiontype == "location") {
			answer = (VehicleAvailable(classtype, input)
					+ Location(classtype, input)); // Will return the name of the vehicle and if it is available in customers country

			Answered = 1; // Answer generated
		}

		if ((questiontype == "intent" && classtype == theVehicleList) 
				||(questiontype == "intent" && classtype == theRecentThing)) {

			// Can customer have this vehicle?
			answer = (VehicleAvailable(classtype, input));
			Answered = 1; // Answer generated
		}

		// Customer starting point (logging in, logging out, etc.)
		if (questiontype == "customer") {
			if(loggedIn == false) {
				answer = (existingCustomer(classtype, input));

				Answered = 1; // Answer generated
				if ((questiontype == "intent" && classtype == theCustomerList) 
						||(questiontype == "intent" && classtype == theRecentThing)) {

					answer = (existingCustomer(classtype, input));
					Answered = 1; // Answer generated
				}
			}
			else {
				answer = "You're already logged in!<br>Perhaps you need some help? Just ask me for some help"; // Prevents from logging in again
				Answered = 1; // Answer generated
			}
		}

		if (questiontype == "farewell") { 
			answer = ("Have a good day, you're welcome!");
			Answered = 1; // Answer generated
		}

		if (questiontype == "signout") { // Signs out the customer of the system
			if(loggedIn == true) { // Prevents from signing out of blue
				answer = ("You're signed out now! Have a nice day, and see you soon! Perhaps driving one of our vehicles?");
				loggedIn = false;
			} else {
				answer = ("You're not signed in. Perhaps you need some help? Just ask me for some help");
			}
			Answered = 1; // Answer generated
		}

		// Contains list (bullet points) of the help commands
		if (questiontype == "help") {
			String currList = "It is easy to rent a car, however you can follow these examples below to ask and navigate through the system easily.\n<ul>";

			currList += "<li>You can ask me, to log you in using your first and last name?</li>";
			currList += "<li>You can ask me, to log you out of the system?</li>";
			currList += "<li>You can ask me, to list how many cars do we have at the moment?</li>";
			currList += "<li>You can ask me, to list all the vehicles available?</li>";
			currList += "<li>You can ask me, to show you a location of particular vehicle</li>";
			currList += "<li>You can ask me, to rent a particular car</li>";
			currList += "</ul>";

			answer = currList; // Answer is set
			Answered = 1; // Answer generated
		}

		// No answer provided, we ask for user to repeat their question
		if (Answered == 0) {
			answer = ("Excuse me, can you repeat that, please?");
		}

		out.add(input);
		out.add(answer);
		return out;
	}

	// Function to check if a customer is new or trying to log in
	public String existingCustomer(List thelist, String input) {
		boolean existingCustomer = false;
		boolean checkEquality = true; // Either check equality or not
		String answer = "";
		Customer curcustomer = new Customer();
		String customertitle = "";

		if(thelist == theCustomerList) {
			int counter = 0;

			for(int i = 0; i < thelist.size(); i++) {
				curcustomer = (Customer) thelist.get(i);

				// First and last name MUST be identical in order for user to log in
				if(input.contains(curcustomer.getFName().toLowerCase()) 
						&& (input.contains(curcustomer.getLName().toLowerCase()))) {

					counter = i;
					Currentindex = counter;
					theRecentThing.clear(); // Clear it before adding (changing)
					classtype = theCustomerList;
					theRecentThing.add(classtype.get(Currentindex));
					customertitle = curcustomer.getTitle();

					// Formatting
					if (input.contains(curcustomer.getFName().toLowerCase())){input = input.replace(curcustomer.getFName().toLowerCase(), "<b>" + curcustomer.getFName().toLowerCase()+"</b>");}
					if (input.contains(curcustomer.getLName().toLowerCase())){input = input.replace(curcustomer.getLName().toLowerCase(), "<b>" + curcustomer.getLName().toLowerCase()+"</b>");}

					i = thelist.size() + 1; // break the loop
				}
				else {
					checkEquality = false; // Error handling: in order to prevent further checks
				}
				
				for (int j = 0; j < theCustomerList.size(); j++) {
					Customer tempCustomer = (Customer) theCustomerList.get(j);
		
					if(checkEquality == true) {
						if(curcustomer.getFName() == null) { curcustomer.setFName("Invalid"); } // Error handling
						if(curcustomer.getLName() == null) { curcustomer.setFName("Invalid"); } // Error handling
						
						// If customer is found check if it is equal to either customers in database
						if(curcustomer != null && tempCustomer != null) // Error handling
						{
							if (curcustomer.getFName().equals(tempCustomer.getFName()) && curcustomer.getLName().equals(tempCustomer.getLName())) {
		
								input = input.replace(tempCustomer.getFName().toLowerCase(), "<b>" + curcustomer.getFName().toLowerCase()+"</b>");	
								existingCustomer = true; // Customer is found in the database
								
								i = thelist.size() + 1; // break the loop
							}
						}
						else {
							System.out.println("Failed, had no cur customer"); // Error handling
						} 
					}
				}
			}
		}

		// Formatting the message 
		if(existingCustomer) {
			answer = "Welcome back, " + curcustomer.getFName() + " " + curcustomer.getLName() + "<br>"; // Welcomes by name and surname
			answer = answer + "<br>You can ask me to rent a car, log out, etc. Feel free to ask me anything!";
			loggedIn = true; // Prevents user from logging in again
			
			// URL
			URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
					+ classtype.get(0).getClass().getSimpleName().toLowerCase();
			URL2 = "http://en.wikipedia.org/wiki/"
					+ customertitle;
			System.out.println("URL = "+URL);
			tooltipstring = readwebsite(URL);
			String html = "<html>" + tooltipstring + "</html>";
			Myface.setmytooltip(html);
			Myface.setmyinfobox(URL2);
		}
		else {
			answer = "Please try again, name or surname are incorrect."; // Error handling
		}
		return(answer);
	}

	// Methods to generate answers for the different kinds of Questions
	// Answer a question of the "Is a car or "it (meaning a car) available ?" kind
	public String VehicleAvailable(List thelist, String input) {
		boolean available =true;
		String answer ="";
		Vehicle curvehicle = new Vehicle();
		String vehicletitle="";

		if (thelist == theVehicleList) {

			int counter = 0;

			//Identify which vehicle (model and name) is asked for 
			for (int i = 0; i < thelist.size(); i++) {

				curvehicle = (Vehicle) thelist.get(i);

				// Atributtes of that particular make and model (incl. fuel type, transmission, etc.)
				if (input.contains(curvehicle.getVehicleName().toLowerCase())
						|| input.contains(curvehicle.getVehicleTransmission().toLowerCase())
						|| input.contains(curvehicle.getVehicleFuelType().toLowerCase())
						|| input.contains(curvehicle.getVehicleModel().toLowerCase())) {

					counter = i;

					Currentindex = counter;
					theRecentThing.clear(); // Clear it before adding (changing) the
					classtype = theVehicleList;                                 
					theRecentThing.add(classtype.get(Currentindex));
					vehicletitle=curvehicle.getVehicleName();

					// Might need to add more atributes >>> SELF NOTES TOMAS, OR SHOULD I SAY THOMAS <<<
					if (input.contains(curvehicle.getVehicleName().toLowerCase())){input = input.replace(curvehicle.getVehicleName().toLowerCase(), "<b>"+curvehicle.getVehicleName().toLowerCase()+"</b>");}
					if (input.contains(curvehicle.getVehicleTransmission().toLowerCase())){input = input.replace(curvehicle.getVehicleTransmission().toLowerCase(), "<b>"+curvehicle.getVehicleTransmission().toLowerCase()+"</b>");}
					if (input.contains(curvehicle.getVehicleFuelType().toLowerCase())){input = input.replace(curvehicle.getVehicleFuelType().toLowerCase(), "<b>"+curvehicle.getVehicleFuelType().toLowerCase()+"</b>");}
					if (input.contains(curvehicle.getVehicleModel().toLowerCase())){input = input.replace(curvehicle.getVehicleModel().toLowerCase(), "<b>"+curvehicle.getVehicleModel().toLowerCase()+"</b>");}

					i = thelist.size() + 1; // break the loop
				}
			}
		}

		if (thelist == theRecentThing && theRecentThing.get(0) != null) {

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("vehicle")) {

				curvehicle = (Vehicle) theRecentThing.get(0);	
				vehicletitle = curvehicle.getVehicleName();
			}
		}

		// Check all the rentals (rented vehicle's if they contain the same ID, etc.) in order to determinate if it is availabe or not
		for (int i = 0; i < theRentalList.size(); i++) {
			Rental currented = (Rental) theRentalList.get(i);

			// Vehicle is not available if it is found
			if ( curvehicle.getVehicleId().equals(currented.getVehicleId())) { // <<< SELF NOTE HERE TOMAS, OR SHOULD I SAY THOMAS>>> INSTEAD OF ID use REG NO!!!
				input = input.replace(currented.getStartdate().toLowerCase(), "<b>"+curvehicle.getVehicleName().toLowerCase()+"</b>"); // It should be ID

				available = false;
				i = thelist.size() + 1; // break the loop
			}
		}

		if(available) {
			answer = "You can hire " + curvehicle.getVehicleName() + " " + curvehicle.getVehicleModel() + " at our " + curvehicle.getVehicleLocation() + " branch today. <br>"; // add details below
			answer = answer + "ID: " + curvehicle.getVehicleId() + " <b>" + curvehicle.getVehicleName() + " " + curvehicle.getVehicleModel() + "</b>, " + curvehicle.getVehicleBodyType().toLowerCase() + ", " + curvehicle.getVehicleFuelType().toLowerCase() + ", " + curvehicle.getVehicleMaxSeats() + " seats, " + curvehicle.getVehicleTransmission().toLowerCase() + " transmission\nPrice: <b>£" + curvehicle.getVehiclePrice() + "</b> per day";
		}
		else { 
			answer = "Unfortunately, this particular " + curvehicle.getVehicleName() + " " + curvehicle.getVehicleModel() + " is taken by one of our customer already. However, we do have other makes and models available for you - just ask me to list them for you";
		}

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ vehicletitle;
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return(answer); // Gives an answer to the user

	}

	// Answer a question of the "How many ...." kind 
	public Integer Count(List thelist) { // List "thelist": List of Class Instances (e.g. theVehicleList)

		//URL = "http://en.wiktionary.org/wiki/"		

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return thelist.size();
	}

	// Answer a question of the "Show me all the cars" kind
	public String ListAll(List thelist) {

		String listemall = "<ul>";

		if (thelist == theVehicleList) {
			for (int i = 0; i < thelist.size(); i++) {
				Vehicle curvehicle = (Vehicle) thelist.get(i);
				listemall = listemall + "<li>" + ("ID: " + curvehicle.getVehicleId() + " <b>" + curvehicle.getVehicleName() + " " + curvehicle.getVehicleModel() + "</b>, " + curvehicle.getVehicleBodyType().toLowerCase() + ", " + curvehicle.getVehicleFuelType().toLowerCase() + ", " + curvehicle.getVehicleMaxSeats() + " seats, " + curvehicle.getVehicleTransmission().toLowerCase() + " transmission\nPrice: <b>£" + curvehicle.getVehiclePrice() + "</b> per day" + "</li><br>");
			} // Prints all the vehicles with particular details in bullet points
		}

		// Prints customer's >>> Needs to be fixed <<< SELF NOTES Maybe... add employee to check whether
		if (thelist == theCustomerList) {
			for (int i = 0; i < thelist.size(); i++) {
				Customer curcustomer = (Customer) thelist.get(i);
				listemall = listemall + "<li>"
						+ (curcustomer.getFName() + " " + curcustomer.getLName() + "</li>");
			}
		}

		// Prints location's >>> Needs to be fixed <<< SELF NOTES
		if (thelist == theLocationList) {
			for (int i = 0; i < thelist.size(); i++) {
				Location curlocation = (Location) thelist.get(i);
				listemall = listemall 
						+ "<li>" + (curlocation.getBranchName() + "</li>");
			}
		}

		// Prints rental's >>> Needs to be fixed <<< SELF NOTES
		if (thelist == theRentalList) {
			for (int i = 0; i < thelist.size(); i++) {
				Rental currental = (Rental) thelist.get(i);
				listemall = listemall + "<li>" 
						+ (currental.getVehicleId() + "</li>");
			}
		}

		listemall += "</ul>"; // Closes bullet points

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return listemall;
	}

	// Answer a question of the "I am interested in" kind 
	public Vector<String> CheckFor(List thelist, String input) {

		Vector<String> yesorno = new Vector<String>();
		if (classtype.isEmpty()){
			yesorno.add("Please specify what are you looking for? Vehicle, customer, employee or rental information?");
		} else {
			yesorno.add("No we don't have such a "
					+ classtype.get(0).getClass().getSimpleName()); // SELF NOTES <<<
		}

		Integer counter = 0;

		if (thelist == theVehicleList) {


			for (int i = 0; i < thelist.size(); i++) {

				Vehicle curvehicle = (Vehicle) thelist.get(i);

				// Check if MRT has this particular vehicle
				if (input.contains(curvehicle.getVehicleName().toLowerCase())
						|| input.contains(curvehicle.getVehicleTransmission().toLowerCase())
						|| input.contains(curvehicle.getVehicleFuelType().toLowerCase())
						|| input.contains(curvehicle.getVehicleModel().toLowerCase())) {

					counter = i; // SELF NOTES <<< Include more details below
					yesorno.set(0, "Yes this particular " + curvehicle.getVehicleName() + " " + curvehicle.getVehicleModel().toLowerCase() + " is available");
					yesorno.add(counter.toString());
					i = thelist.size() + 1; // break the loop
				}
			}
		}

		if (thelist == theCustomerList) {
			for (int i = 0; i < thelist.size(); i++) {
				Customer curcustomer = (Customer) thelist.get(i);
				if (input.contains(curcustomer.getFName().toLowerCase())
						|| input.contains(curcustomer.getLName().toLowerCase())
						|| input.contains(curcustomer.getCustomerCountry().toLowerCase())) {

					counter = i;
					yesorno.set(0, "Yes we have such a Customer");
					yesorno.add(counter.toString());
					i = thelist.size() + 1;
				}
			}
		}

		if (thelist == theLocationList) {
			for (int i = 0; i < thelist.size(); i++) {
				Location curlocation = (Location) thelist.get(i);
				if (input.contains(curlocation.getBranchName().toLowerCase())) {

					// Original
					//if (input.contains(curlocation.getBranchName().toLowerCase())
					//|| input.contains(curlocation.getUrl().toLowerCase())) {

					counter = i;
					yesorno.set(0, "Yes we have such a Location");
					yesorno.add(counter.toString());
					i = thelist.size() + 1;
				}
			}
		}

		if (thelist == theRentalList) {
			for (int i = 0; i < thelist.size(); i++) {
				Rental currented = (Rental) thelist.get(i);
				if (input.contains((CharSequence) currented.getVehicleId())
						|| input.contains((CharSequence) currented.getCustomerId())){

					counter = i;
					yesorno.set(0, "Yes we have such a Rental");
					yesorno.add(counter.toString());
					i = thelist.size() + 1;
				}
			}
		}

		if (classtype.isEmpty()) {
			System.out.println("Not class type given.");
		} else {
			URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
					+ classtype.get(0).getClass().getSimpleName().toLowerCase();
			URL2 = "http://en.wikipedia.org/wiki/"
					+ classtype.get(0).getClass().getSimpleName().toLowerCase();
			System.out.println("URL = "+URL);
			tooltipstring = readwebsite(URL);
			String html = "<html>" + tooltipstring + "</html>";
			Myface.setmytooltip(html);
			Myface.setmyinfobox(URL2);
		}

		return yesorno;
	}

	//  Method to retrieve the location information from the object (Where is...) kind
	public String Location(List classtypelist, String input) {

		List thelist = classtypelist;
		String location = "";

		// if a pronomial was used "it", "them" etc: Reference to the recent thing

		if (thelist == theRecentThing && theRecentThing.get(0) != null) {

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("vehicle")) {

				Vehicle curvehicle = (Vehicle) theRecentThing.get(0);
				location = (curvehicle.getVehicleLocation() + " "); // SELF NOTES take a closer look here <<<

			}

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("customer")) {

				Customer curcustomer = (Customer) theRecentThing.get(0);
				location = (curcustomer.getCustomerCountry() + " " + curcustomer.getCustomerAge());
			}

			if (theRecentThing.get(0).getClass().getSimpleName()  
					.toLowerCase().equals("location")) {

				Location curlocation = (Location) theRecentThing.get(0);
				location = (curlocation.getBranchName() + " ");
			}

			if (theRecentThing.get(0).getClass().getSimpleName()    
					.toLowerCase().equals("carhire")) {

				location = (theCarHire.getCity());
				//location = (theCarHire.getCity() + " " + theCarHire.getStreet() + theCarHire
				//		.getHousenumber());
			}

		}

		// if a direct noun was used (vehicle, customer, employee, etc)
		else {

			if (thelist == theVehicleList) {

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Vehicle curvehicle = (Vehicle) thelist.get(i);

					if (input.contains(curvehicle.getVehicleName().toLowerCase())
							|| input.contains(curvehicle.getVehicleTransmission().toLowerCase())
							|| input.contains(curvehicle.getVehicleFuelType().toLowerCase())
							|| input.contains(curvehicle.getVehicleModel().toLowerCase())) {

						counter = i;
						location = (curvehicle.getVehicleLocation() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = theVehicleList;                                    //This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; // break the loop
					}
				}
			}

			if (thelist == theCustomerList) {

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Customer curcustomer = (Customer) thelist.get(i);

					if (input.contains(curcustomer.getFName().toLowerCase())
							&& input.contains(curcustomer.getLName().toLowerCase())) {

						counter = i;
						location = curcustomer.getCustomerCountry() + " ";
						Currentindex = counter;
						theRecentThing.clear();
						classtype = theCustomerList;
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; // break the loop
					}
				}
			}

			if (thelist == theLocationList) {

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Location curlocation = (Location) thelist.get(i);

					if (input.contains(curlocation.getBranchName().toLowerCase())) {
						//if (input.contains(curlocation.getBranchName().toLowerCase())				     
						//		|| input.contains(curlocation.getUrl().toLowerCase())) {

						counter = i;
						location = (curlocation.getBranchName() + " ");
						Currentindex = counter;
						theRecentThing.clear();                                      // Clear it before adding (changing) the	
						classtype = theLocationList;                                  //This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; // break the loop
					}
				}
			}

			if (thelist == theCarHireList) {

				location = (theCarHire.getCity());
			}
		}

		URL = "http://wordnetweb.princeton.edu/perl/webwn?o2=&o0=1&o8=1&o1=1&o7=&o5=&o9=&o6=&o3=&o4=&s="
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		URL2 = "http://en.wikipedia.org/wiki/"
				+ classtype.get(0).getClass().getSimpleName().toLowerCase();
		System.out.println("URL = "+URL);
		tooltipstring = readwebsite(URL);
		String html = "<html>" + tooltipstring + "</html>";
		Myface.setmytooltip(html);
		Myface.setmyinfobox(URL2);

		return location;
	}

	public String testit() {   // Test data

		String answer = "";

		System.out.println(">>> TESTING MRT carHire System <<<"); // Initials
		System.out.println("Vehicle List contains of = " + theVehicleList.size() + " vehicles in Vehicle class");

		for (int i = 0; i < theVehicleList.size(); i++) {   // Check each vehicle in the array

			Vehicle curvehicle = (Vehicle) theVehicleList.get(i);									
			System.out.println("Brand: " + curvehicle.getVehicleName() + " Model: " + curvehicle.getVehicleModel() + " Reg. number: " + curvehicle.getRegNo()); // Print every vehicle on the Vehicle class

			answer = ">>> END OF TESTING <<<"; // End of testing
		}
		return answer;
	}

	public String readwebsite(String url) {

		String webtext = "";
		try {
			BufferedReader readit = new BufferedReader(new InputStreamReader(
					new URL(url).openStream()));

			String lineread = readit.readLine();

			System.out.println("Green light!");

			while (lineread != null) {
				webtext = webtext + lineread;
				lineread = readit.readLine();				
			}

			// Hard coded cut out from "wordnet website source text": 
			//Check if website still has this structure   vvvv ...definitions...  vvvv 		

			webtext = webtext.substring(webtext.indexOf("<ul>"),webtext.indexOf("</ul>"));                                 //               ^^^^^^^^^^^^^^^^^              

			webtext = "<table width=\"700\"><tr><td>" + webtext
					+ "</ul></td></tr></table>";

		} catch (Exception e) {
			webtext = "Not yet";
			System.out.println("ERROR: cannot connect to wordnet");
		}
		return webtext;
	}
}
