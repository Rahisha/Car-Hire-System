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

	// The main Class Object holding the Domain knowledge

	// Generate the classes automatically with: Opening a command console and
	// type:
	// Path to YOUR-PROJECTROOT-IN-WORKSPACE\xjc.bat yourschemaname.xsd -d src
	// -p yourclasspackagename

	public CarHire theCarHire;

	public SimpleGUI Myface;

	// The lists holding the class instances of all domain entities

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

	public Reasoner(SimpleGUI myface) {

		Myface = myface; // reference to GUI to update Tooltip-Text
		// basic constructor for the constructors sake :)
	}

	public void initknowledge() { // load all the library knowledge from XML 

		JAXB_XMLParser xmlhandler = new JAXB_XMLParser(); // we need an instance of our parser

		//This is a candidate for a name change
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
		locationsyn.add("whereabouts");
		locationsyn.add("house");
		locationsyn.add("position");
		locationsyn.add("spot");
		locationsyn.add("placement");
		locationsyn.add("point");
		locationsyn.add("destination");
		locationsyn.add("site");
		locationsyn.add("area");
		locationsyn.add("address");

		rentalsyn.add("borrow");
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

			theVehicleList = theCarHire.getVehicle();  		//This is a candidate for a name change
			theCustomerList = theCarHire.getCustomer(); 	//This is a candidate for a name change
			theLocationList = theCarHire.getLocation(); 	//This is a candidate for a name change
			theRentalList = theCarHire.getRental(); 	//This is a candidate for a name change
			theEmployeeList = theCarHire.getEmployee(); 	//This is a candidate for a name change
			theCarHireList.add(theCarHire);             // force it to be a List, //This is a candidate for a name change

			System.out.println("List reading");
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

		// ----- Check for the kind of question (number, location, etc)------------------------------

		if (input.contains("how many")){questiontype = "amount"; input = input.replace("how many", "<b>how many</b>");} 
		if (input.contains("number of")){questiontype = "amount"; input = input.replace("number of", "<b>number of</b>");}
		if (input.contains("hire")){questiontype = "amount"; input = input.replace("hire", "<b>hire</b>");}
		if (input.contains("count")){questiontype = "amount"; input = input.replace("count", "<b>count</b>");}
		if (input.contains("model")){questiontype = "amount"; input = input.replace("model", "<b>model</b>");}
		if (input.contains("total")){questiontype = "amount"; input = input.replace("total", "<b>total</b>");}

		if (input.contains("print all")){questiontype = "list"; input = input.replace("print all", "<b>print all</b>");}
		if (input.contains("list all")){questiontype = "list"; input = input.replace("list all", "<b>list all</b>");}
		if (input.contains("display all")){questiontype = "list"; input = input.replace("display all", "<b>display all</b>");}
		if (input.contains("show all")){questiontype = "list"; input = input.replace("show all", "<b>show all</b>");}
		if (input.contains("can i see")){questiontype = "list"; input = input.replace("can i see", "<b>can i see</b>");}
		if (input.contains("can i view")){questiontype = "list"; input = input.replace("can i view", "<b>can i view</b>");}
		
		if (input.contains("is there a")){questiontype = "checkfor"; input = input.replace("is there a", "<b>is there a</b>");}
		if (input.contains("i am searching")){questiontype = "checkfor"; input = input.replace("i am searching", "<b>i am searching</b>");}
		if (input.contains("i am looking for")){questiontype = "checkfor"; input = input.replace("i am looking for", "<b>i am looking for</b>");}
		if (input.contains("do you have")&&!input.contains("how many")){questiontype = "checkfor";input = input.replace("do you have", "<b>do you have</b>");}
		if (input.contains("i look for")){questiontype = "checkfor"; input = input.replace("i look for", "<b>i look for</b>");}
		if (input.contains("is there")){questiontype = "checkfor"; input = input.replace("is there", "<b>is there</b>");}
		if (input.contains("will you have")){questiontype = "checkfor"; input = input.replace("will you have", "<b>will you have</b>");}

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


		// ------- Checking the Subject of the Question --------------------------------------

		for (int x = 0; x < vehiclesyn.size(); x++) {   //This is a candidate for a name change
			if (input.contains(vehiclesyn.get(x))) {    //This is a candidate for a name change
				classtype = theVehicleList;             //This is a candidate for a name change
				
				input = input.replace(vehiclesyn.get(x), "<b>"+vehiclesyn.get(x)+"</b>");
				
				subjectcounter = 1;
				System.out.println("Class type Book recognised.");
			}
		}
		for (int x = 0; x < customersyn.size(); x++) {  //This is a candidate for a name change
			if (input.contains(customersyn.get(x))) {   //This is a candidate for a name change
				classtype = theCustomerList;            //This is a candidate for a name change
				
				input = input.replace(customersyn.get(x), "<b>"+customersyn.get(x)+"</b>");
				
				subjectcounter = 1;
				System.out.println("Class type Member recognised.");
			}
		}
		for (int x = 0; x < locationsyn.size(); x++) {  //This is a candidate for a name change
			if (input.contains(locationsyn.get(x))) {   //This is a candidate for a name change
				classtype = theLocationList;            //This is a candidate for a name change
				
				input = input.replace(locationsyn.get(x), "<b>"+locationsyn.get(x)+"</b>");
				
				subjectcounter = 1;	
				System.out.println("Class type Catalog recognised.");
			}
		}
		for (int x = 0; x < rentalsyn.size(); x++) {  //This is a candidate for a name change
			if (input.contains(rentalsyn.get(x))) {   //This is a candidate for a name change
				classtype = theRentalList;            //This is a candidate for a name change
				
				input = input.replace(rentalsyn.get(x), "<b>"+rentalsyn.get(x)+"</b>");
				
				subjectcounter = 1;	
				System.out.println("Class type Lending recognised.");
			}
		}
		for (int x = 0; x < employeesyn.size(); x++) {  //This is a candidate for a name change
			if (input.contains(employeesyn.get(x))) {   //This is a candidate for a name change
				classtype = theEmployeeList;            //This is a candidate for a name change
				
				input = input.replace(employeesyn.get(x), "<b>"+employeesyn.get(x)+"</b>");
				
				subjectcounter = 1;	
				System.out.println("Class type Employee recognised.");
			}
		}
		
		if(subjectcounter == 0){
			for (int x = 0; x < recentobjectsyn.size(); x++) {  
				if (input.contains(recentobjectsyn.get(x))) {
					classtype = theRecentThing;
					
					input = input.replace(recentobjectsyn.get(x), "<b>"+recentobjectsyn.get(x)+"</b>");
					
					subjectcounter = 1;
					System.out.println("Class type recognised as"+recentobjectsyn.get(x));
				}
			}
		}

		// More than one subject in question + Library ...
		// "Does the Library has .. Subject 2 ?"

		System.out.println("subjectcounter = "+subjectcounter);

		for (int x = 0; x < carHiresyn.size(); x++) {  //This is a candidate for a name change

			if (input.contains(carHiresyn.get(x))) {   //This is a candidate for a name change

				// Problem: "How many Books does the Library have ?" -> classtype = Library
				// Solution:
				
				if (subjectcounter == 0) { // Library is the first subject in the question
					
					input = input.replace(carHiresyn.get(x), "<b>"+carHiresyn.get(x)+"</b>");
					
					classtype = theCarHireList;        //This is a candidate for a name change

					System.out.println("class type Library recognised");		

				}
			}
		}

		// Compose Method call and generate answerVector

		if (questiontype == "amount") { // Number of Subject

			Integer numberof = Count(classtype);

			answer=("MRT carHire operates in numerous countries. As of today, we have over <b> " + numberof
					+ classtype.get(0).getClass().getSimpleName() + "s</b> available for you to rent accross all our branches which includes <b>London, GB</b> and <b>Paris, FR</b>"
					+".");

			Answered = 1; // An answer was given

		}

		if (questiontype == "list") { // List all Subjects of a kind

			answer=("You asked for the listing of all "
					+ classtype.get(0).getClass().getSimpleName() + "s. <br>"
					+ "We have the following "
					+ classtype.get(0).getClass().getSimpleName() + "s:"
					+ ListAll(classtype));
			Answered = 1; // An answer was given

		}

		if (questiontype == "checkfor") { // test for a certain Subject instance

			Vector<String> check = CheckFor(classtype, input);
			answer=(check.get(0));
			Answered = 1; // An answer was given
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

		// Location Question in Pronomial form "Where can i find it"

		if (questiontype == "location") {   // We always expect a pronomial question to refer to the last
											// object questioned for

			answer=("You can find the "
					+ classtype.get(0).getClass().getSimpleName() + " " + "at "
					+ Location(classtype, input));

			Answered = 1; // An answer was given
		}

		if ((questiontype == "intent" && classtype == theVehicleList) 
				||(questiontype == "intent" && classtype == theRecentThing)) {

			// Can I lend the book or not (Can I lent "it" or not)
			answer=("You "+ VehicleAvailable(classtype, input));
			Answered = 1; // An answer was given
		}

		if (questiontype == "farewell") {       // Reply to a farewell
			
			answer=("You are welcome.");

			Answered = 1; // An answer was given
		}
		
		// Contains list (bullet points) of the help commands
		if (questiontype == "help") {
			
			String currList = "It is easy to rent a car, however you can follow these examples below to ask and navigate through the system easily.\n<ul>";
			
			currList += "<li>You can ask me, to list how many cars do we have at the moment?</li>";
			currList += "<li>You can ask me, to list all the vehicles available?</li>";
			currList += "<li>You can ask me, to show you a location of particular vehicle</li>";
			currList += "<li>You can ask me, to rent a particular car</li>";
			currList += "</ul>";
			
			answer = currList; // Answer is set
			Answered = 1;
		}
		
		if (Answered == 0) { // No answer was given

			answer=("Excuse me, can you repeat that, please?");
		}

		out.add(input);
		out.add(answer);
		
		return out;
	}

	// Methods to generate answers for the different kinds of Questions
	
	// Answer a question of the "Is a book or "it (meaning a book) available ?" kind

	public String VehicleAvailable(List thelist, String input) {

		boolean available =true;
		String answer ="";
		Vehicle curvehicle = new Vehicle();
		String vehicletitle="";

		if (thelist == theVehicleList) {                      //This is a candidate for a name change

			int counter = 0;

			//Identify which book is asked for 

			for (int i = 0; i < thelist.size(); i++) {
				
				curvehicle = (Vehicle) thelist.get(i);         //This is a candidate for a name change

				if (input.contains(curvehicle.getVehicleName().toLowerCase())            //This is a candidate for a name change
						|| input.contains(curvehicle.getVehicleTransmission().toLowerCase())      //This is a candidate for a name change
						|| input.contains(curvehicle.getVehicleModel().toLowerCase())) {  //This is a candidate for a name change

					counter = i;

					Currentindex = counter;
					theRecentThing.clear(); 									//Clear it before adding (changing) the
					classtype = theVehicleList;                                    //This is a candidate for a name change
					theRecentThing.add(classtype.get(Currentindex));
					vehicletitle=curvehicle.getVehicleName();
										
					if (input.contains(curvehicle.getVehicleName().toLowerCase())){input = input.replace(curvehicle.getVehicleName().toLowerCase(), "<b>"+curvehicle.getVehicleName().toLowerCase()+"</b>");}          
					if (input.contains(curvehicle.getVehicleTransmission().toLowerCase())) {input = input.replace(curvehicle.getVehicleTransmission().toLowerCase(), "<b>"+curvehicle.getVehicleId()+"</b>");}     
					if (input.contains(curvehicle.getVehicleModel().toLowerCase())){input = input.replace(curvehicle.getVehicleModel().toLowerCase(), "<b>"+curvehicle.getVehicleModel().toLowerCase()+"</b>");}
										
					i = thelist.size() + 1; 									// force break
				}
			}
		}

		// maybe other way round or double 

		if (thelist == theRecentThing && theRecentThing.get(0) != null) {

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("book")) {                  //This is a candidate for a name change

				curvehicle = (Vehicle) theRecentThing.get(0);               //This is a candidate for a name change		
				vehicletitle=curvehicle.getVehicleName();
			}
		}

		// check all lendings if they contain the books ISBN

		for (int i = 0; i < theRentalList.size(); i++) {
			System.out.println(">>>>>>> DEBUG 1 CYCLE FOR EQUAL >>>>>>>");

			Rental currented = (Rental) theRentalList.get(i);         //This is a candidate for a name change

			// If there is a lending with the books ISBN, the book is not available
			System.out.println("Before EQ...");
			// <-------- HERE 1 --------- >
			if ( curvehicle.getVehicleId().equals(currented.getVehicleId())) { 
				System.out.println("After EQ...");
				input = input.replace(currented.getStartdate().toLowerCase(), "<b>"+curvehicle.getVehicleName().toLowerCase()+"</b>"); // It should be ID
				
				available=false;
				i = thelist.size() + 1; 									// force break
				System.out.println("We get false and we should get else...");
			}
		}

		if(available){
			answer = "New";
			//answer = "<<< Yes this car is available for hiring. It is availabe at our " + curvehicle.getVehicleLocation() + " branch";
		}
		else{ 
			answer="cannot lend the book as someone else has lent it at the moment.";
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

		return(answer);

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

	// Answer a question of the "What kind of..." kind
	
	public String ListAll(List thelist) {

		String listemall = "<ul>";

		if (thelist == theVehicleList) {                                  //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Vehicle curvehicle = (Vehicle) thelist.get(i);                  //This is a candidate for a name change
				listemall = listemall + "<li>" + ("ID: " + curvehicle.getVehicleId() + " <b>" + curvehicle.getVehicleName() + " " + curvehicle.getVehicleModel() + "</b>, " + curvehicle.getVehicleBodyType().toLowerCase() + ", " + curvehicle.getVehicleFuelType().toLowerCase() + ", " + curvehicle.getVehicleMaxSeats() + " seats, " + curvehicle.getVehicleTransmission().toLowerCase() + " transmission\nPrice: <b>£" + curvehicle.getVehiclePrice() + "</b> per day" + "</li>");    //This is a candidate for a name change
			}
		}

		if (thelist == theCustomerList) {                                //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Customer curcustomer = (Customer) thelist.get(i);               //This is a candidate for a name change
				listemall = listemall + "<li>"                         //This is a candidate for a name change
						+ (curcustomer.getFName() + " " + curcustomer.getLName() + "</li>");  //This is a candidate for a name change
			}
		}

		if (thelist == theLocationList) {                               //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Location curlocation = (Location) thelist.get(i);             //This is a candidate for a name change
				listemall = listemall 
						+ "<li>" + (curlocation.getBranchName() + "</li>");      //This is a candidate for a name change
			}
		}
		
		if (thelist == theRentalList) {                               //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Rental currental = (Rental) thelist.get(i);             //This is a candidate for a name change
				listemall = listemall + "<li>" 
						+ (currental.getVehicleId() + "</li>");                //This is a candidate for a name change
			}
		}
		
		listemall += "</ul>";

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

	// Answer a question of the "Do you have..." kind 
	
	public Vector<String> CheckFor(List thelist, String input) {

		Vector<String> yesorno = new Vector<String>();
		if (classtype.isEmpty()){
			yesorno.add("Class not recognised. Please specify if you are searching for a book, catalog, member, or lending?");
		} else {
			yesorno.add("No we don't have such a "
				+ classtype.get(0).getClass().getSimpleName());
		}

		Integer counter = 0;

		if (thelist == theVehicleList) {                         //This is a candidate for a name change

			for (int i = 0; i < thelist.size(); i++) {

				Vehicle curvehicle = (Vehicle) thelist.get(i);                           //This is a candidate for a name change

				if (input.contains(curvehicle.getVehicleName().toLowerCase())            //This is a candidate for a name change
						|| input.contains(curvehicle.getVehicleTransmission().toLowerCase())      //This is a candidate for a name change
						|| input.contains(curvehicle.getVehicleName().toLowerCase())) {  //This is a candidate for a name change

					counter = i;
					yesorno.set(0, "Yes we have such a Book");                  //This is a candidate for a name change
					yesorno.add(counter.toString());
					i = thelist.size() + 1; // force break
				}
			}
		}

		if (thelist == theCustomerList) {                                      //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Customer curcustomer = (Customer) thelist.get(i);                      //This is a candidate for a name change
				if (input.contains(curcustomer.getFName().toLowerCase())         //This is a candidate for a name change
						|| input.contains(curcustomer.getLName().toLowerCase()) //This is a candidate for a name change
						|| input.contains(curcustomer.getCustomerCountry().toLowerCase())) {  //This is a candidate for a name change

					counter = i;
					yesorno.set(0, "Yes we have such a Member");               //This is a candidate for a name change
					yesorno.add(counter.toString());
					i = thelist.size() + 1;
				}
			}
		}

		if (thelist == theLocationList) {                                    //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Location curlocation = (Location) thelist.get(i);                  //This is a candidate for a name change
				if (input.contains(curlocation.getBranchName().toLowerCase())) { //This is a candidate for a name change

				// Original
				//if (input.contains(curlocation.getBranchName().toLowerCase())          //This is a candidate for a name change
						//|| input.contains(curlocation.getUrl().toLowerCase())) { //This is a candidate for a name change

					counter = i;
					yesorno.set(0, "Yes we have such a Catalog");           //This is a candidate for a name change
					yesorno.add(counter.toString());
					i = thelist.size() + 1;
				}
			}
		}
		
		if (thelist == theRentalList) {                                     //This is a candidate for a name change
			for (int i = 0; i < thelist.size(); i++) {
				Rental currented = (Rental) thelist.get(i);                  //This is a candidate for a name change
				if (input.contains((CharSequence) currented.getVehicleId())          //This is a candidate for a name change
					|| input.contains((CharSequence) currented.getCustomerId())){ //This is a candidate for a name change

					counter = i;
					yesorno.set(0, "Yes we have such a Lending");            //This is a candidate for a name change
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
					.toLowerCase().equals("vehicle")) {                  //This is a candidate for a name change

				Vehicle curvehicle = (Vehicle) theRecentThing.get(0);          //This is a candidate for a name change
				location = (curvehicle.getVehicleLocation() + " ");             //This is a candidate for a name change

			}

			if (theRecentThing.get(0).getClass().getSimpleName()
					.toLowerCase().equals("customer")) {               //This is a candidate for a name change

				Customer curcustomer = (Customer) theRecentThing.get(0);      //This is a candidate for a name change
				location = (curcustomer.getCustomerCountry() + " " + curcustomer.getCustomerAge());                                    //This is a candidate for a name change
			}

			if (theRecentThing.get(0).getClass().getSimpleName()  
					.toLowerCase().equals("location")) {                 //This is a candidate for a name change

				Location curlocation = (Location) theRecentThing.get(0);       //This is a candidate for a name change
				location = (curlocation.getBranchName() + " ");                //This is a candidate for a name change
			}

			if (theRecentThing.get(0).getClass().getSimpleName()    
					.toLowerCase().equals("carhire")) {                  //This is a candidate for a name change

				location = (theCarHire.getCity());
				//location = (theCarHire.getCity() + " " + theCarHire.getStreet() + theCarHire   //This is a candidate for a name change
				//		.getHousenumber());                                           //This is a candidate for a name change
			}

		}

		// if a direct noun was used (book, member, etc)

		else {

			if (thelist == theVehicleList) {                         //This is a candidate for a name change

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Vehicle curvehicle = (Vehicle) thelist.get(i);         //This is a candidate for a name change

					if (input.contains(curvehicle.getVehicleName().toLowerCase())            //This is a candidate for a name change
							|| input.contains(curvehicle.getVehicleTransmission().toLowerCase())      //This is a candidate for a name change
							|| input.contains(curvehicle.getVehicleModel().toLowerCase())) {  //This is a candidate for a name change

						counter = i;
						location = (curvehicle.getVehicleLocation() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 									// Clear it before adding (changing) theRecentThing
						classtype = theVehicleList;                                    //This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 									// force break
					}
				}
			}

			if (thelist == theCustomerList) {                                         //This is a candidate for a name change

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Customer curcustomer = (Customer) thelist.get(i);         				  //This is a candidate for a name change

					if (input.contains(curcustomer.getFName().toLowerCase())              //This is a candidate for a name change
							|| input.contains(curcustomer.getLName().toLowerCase())      //This is a candidate for a name change
							|| input.contains((CharSequence) curcustomer.getId())) {   //This is a candidate for a name change

						counter = i;
						location = (curcustomer.getCustomerCountry() + " ");
						Currentindex = counter;
						theRecentThing.clear(); 										// Clear it before adding (changing) the
						classtype = theCustomerList;            	 						//This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1; 				             	        // force break
					}
				}
			}

			if (thelist == theLocationList) {                                       	 //This is a candidate for a name change

				int counter = 0;

				for (int i = 0; i < thelist.size(); i++) {

					Location curlocation = (Location) thelist.get(i);                    //This is a candidate for a name change

					if (input.contains(curlocation.getBranchName().toLowerCase())) {
					//if (input.contains(curlocation.getBranchName().toLowerCase())            //This is a candidate for a name change						     
					//		|| input.contains(curlocation.getUrl().toLowerCase())) {   //This is a candidate for a name change

						counter = i;
						location = (curlocation.getBranchName() + " ");
						Currentindex = counter;
						theRecentThing.clear();                                      // Clear it before adding (changing) the	
						classtype = theLocationList;                                  //This is a candidate for a name change
						theRecentThing.add(classtype.get(Currentindex));
						i = thelist.size() + 1;                                      // force break
					}
				}
			}

			if (thelist == theCarHireList) {                                                  //This is a candidate for a name change

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

		for (int i = 0; i < theVehicleList.size(); i++) {   // check each book in the List, //This is a candidate for a name change

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

			System.out.println("Reader okay");

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
			System.out.println("Error connecting to wordnet");
		}
		return webtext;
	}
}
