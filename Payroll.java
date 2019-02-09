/** Payroll.java
    Program 6: GUI.
   @author    Hema Kothari
   @Date   12/01/2018
*/

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

public class Payroll extends Application {

	// --------------Payroll Class data members
	private ArrayList<Employee> emp = new ArrayList<Employee>();
	// New Arraylist to store employee who quit or have been fired by boss
	private ArrayList<Employee> empQuitFired = new ArrayList<Employee>();
	private ArrayList<Employee> temp1 = new ArrayList<Employee>();
	private Employee currentUser;
	private Employee employee;
	static Scanner employeeFile;
	PrintWriter pw;
	PrintWriter pw2; // printWriter for a second output file payroll.txt.
	private int currentId = -1;
	Stage stage;
	Scene scene;
	int count =0;

	Font Arial = Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20);
	Font Verdana = Font.font("Verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20);

	TextField EmpIDField = new TextField();
	TextField loginNameField = new TextField();
	TextField baseSalaryField = new TextField();
	TextField nameField = new TextField();
	TextField empTypeField = new TextField();

	PasswordField passwordField = new PasswordField();
	PasswordField confirmPasswordField = new PasswordField();

	Label EmpIDLabel = new Label("Employee ID");
	Label loginNameLabel = new Label("Username");
	Label passwordLabel = new Label("Password");
	Label confirmpasswordLabel = new Label("Re-enter Password");
	Label baseSalaryLabel = new Label("Salary/Hour Rate");
	Label nameLabel = new Label("Name");
	Label empTypeLabel = new Label("Type");
	Label payEmpText = new Label();
	Label message = new Label("Enter the Boss's information: ");
	Label hoursLabel = new Label("Hours");
	
	TextArea textspace = new TextArea();
	
	Button btSubmit = new Button("Submit");
	Button btLogin = new Button("Login");
	Button btNewEmp = new Button("Enter New Employee");
	Button btListEmp = new Button("List Employee");
	Button btChangeEmpData = new Button("Change/Terminate Emp");
	Button btTerminateEmp = new Button("Quit");
	Button btPayEmp = new Button("Pay Employees");
	Button btLogout = new Button("Logout");
	Button btTerminate = new Button("Terminate");
	Button btMainmenu = new Button("Main Menu");
	Button btSearch = new Button("Search");

	RadioButton rbSalaried = new RadioButton("Salaried");
	RadioButton rbHourly = new RadioButton("Hourly");
	ToggleGroup empType = new ToggleGroup();

	//------------------------------Function to read employee file and create Boss.
	public void initial() throws IOException, ClassNotFoundException, ParseException {
		File report = new File("payroll.txt");
		pw2 = new PrintWriter(report);

		try {
			FileInputStream fis = new FileInputStream("Employee.txt"); // FileNotFoundException
			ObjectInputStream ois = new ObjectInputStream(fis);
			ArrayList<Employee> read_emp = (ArrayList<Employee>) ois.readObject();

			for (Employee e : read_emp) {
				int empID = e.getEmpId();
				String loginName = e.getLoginName();
				String password = e.getPassword();
				double baseSalary = e.getSal();
				String date1 = e.getDate();
				Date date = new SimpleDateFormat("MM/dd/yyyy").parse(date1);
				String name = e.getName();

				if (e instanceof Salaried) {
					e = new Salaried(empID, loginName, password, baseSalary, date, name); // Create Employee object using 5 param cons
					emp.add(e); // Store in employee collection
				} else if (e instanceof Hourly) {
					e = new Hourly(empID, loginName, password, baseSalary, date, name); // Create Employee object using 5 param cons
					emp.add(e); // Store in employee collection
				}
			}
			ois.close();
			buildGui("Login");
			
		} catch (FileNotFoundException ex) {
			System.out.println("Employee File Not Found!!"); //Create Boss
			btSubmit.setOnAction(e -> {
				if(!loginNameField.getText().isEmpty() && !passwordField.getText().isEmpty() && !confirmPasswordField.getText().isEmpty() 
						&& !nameField.getText().isEmpty() && !baseSalaryField.getText().isEmpty()){
					String loginName = loginNameField.getText();
					double baseSalary = Double.parseDouble(baseSalaryField.getText());
					String name = nameField.getText();
					String password1 = passwordField.getText();
					String password2 = confirmPasswordField.getText();
					String password = getNewPassword(password1, password2);
					if (password != null) {
						employee = new Salaried(loginName, password, baseSalary, name);
						emp.add(employee);
						buildGui("Login");
					} else {
						textspace.setText("Incorrect password and confirm password");
						System.out.println("Incorrect password");
					}
				}else if(loginNameField.getText().isEmpty()){
					buildGui("BossInfo");
					textspace.setText("Enter Login Name");
				}else if(passwordField.getText().isEmpty()){
					buildGui("BossInfo");
					textspace.setText("Enter Password");
				} else if(confirmPasswordField.getText().isEmpty()){
					buildGui("BossInfo");
					textspace.setText("Enter Confirm Password");
				}else if(nameField.getText().isEmpty()){
					buildGui("BossInfo");
					textspace.setText("Enter Name");
				}else if(baseSalaryField.getText().isEmpty()){
					buildGui("BossInfo");
					textspace.setText("Enter Salary/Hourly Rate");
				}
			});
		}
	}

	//-----------------------------Function to verify Password and Confirm Password are same.
	public String getNewPassword(String passwordInput, String confirmPasswordInput) {
		String password = encryptPassword(passwordInput);
		String confirmPassword = encryptPassword(confirmPasswordInput);
		if (password.equals(confirmPassword))
			return password;
		else
			return null;
	}

	//--------------------Function to encrypt password
	public String encryptPassword(String password) {
		byte[] pass = password.getBytes();
		try {
			MessageDigest d = MessageDigest.getInstance("SHA-256");
			d.update(pass);
			pass = d.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return new String(pass);
	}

	// --------------Private utility function to implement option 1 Login
	private void dologin() {
		String l_name = loginNameField.getText();
		String pass = passwordField.getText();
		String password = encryptPassword(pass);
		boolean found = false;
		for (Employee k : emp) {
			if (l_name.equals(k.getLoginName()) && password.equals(k.getPassword())) {
				found = true;
				currentUser = k;
				currentId = k.getEmpId();
				break;
			}
		}
		if (!found) {
			System.out.println("Invalid Login and Password");
		}else{
			if (currentId == 00000)
				buildGui("BossGUI");
			else
				buildGui("EmpGUI");
		}
	}

	// --------------Private utility function to implement option 2 Create new employee
	private void newEmployee() {
		double baseSalary = 0;
		char employeeType = 'a';
		String name = nameField.getText();
		String loginName = loginNameField.getText();
		if (!isUnique(loginName))
			System.out.println("Enter Unique Login Name");

		try{
			baseSalary = Double.parseDouble(baseSalaryField.getText());
		}catch(NumberFormatException exc){
			buildGui("AddNewEmp");
			textspace.setText("Enter Base Salary");
		}

		employeeType = empType.getSelectedToggle().getUserData().toString().charAt(0);

		String password1 = passwordField.getText();
		String password2 = confirmPasswordField.getText();
		String password = getNewPassword(password1, password2); //Calling method to verify passwords are equal
		// --------------Create new employee using 3 parameter constructor and add it into Arraylist
		if (password != null && isUnique(loginName) && !baseSalaryField.getText().isEmpty() && !nameField.getText().isEmpty() 
				&& !loginNameField.getText().isEmpty() && !passwordField.getText().isEmpty() && !confirmPasswordField.getText().isEmpty()) {
			if (employeeType == 'S')
				employee = new Salaried(loginName, password, baseSalary, name);
			else
				employee = new Hourly(loginName, password, baseSalary, name);
			emp.add(employee);
			buildGui("BossGUI");
			loginNameField.setText("");
			passwordField.setText("");
			confirmPasswordField.setText("");
			baseSalaryField.setText("");
			nameField.setText("");			
		} else {
			if (!isUnique(loginName)){
				buildGui("AddNewEmp");
				textspace.setText("LoginName Exits");
			} else if(loginNameField.getText().isEmpty()){
				buildGui("AddNewEmp");
				textspace.setText("Enter Login Name");
			} else if(passwordField.getText().isEmpty()){
				buildGui("AddNewEmp");
				textspace.setText("Enter Password");
			} else if(confirmPasswordField.getText().isEmpty()){
				buildGui("AddNewEmp");
				textspace.setText("Enter Confirm Password");
			} else if(baseSalaryField.getText().isEmpty()){
				buildGui("AddNewEmp");
				textspace.setText("Enter Base Salary");
			} else if(nameField.getText().isEmpty()){
				buildGui("AddNewEmp");
				textspace.setText("Enter Name");
			} else if (password == null){
				buildGui("AddNewEmp");
				textspace.setText("Incorrect password and confirm password");
			} 				
		}

	}

	// Function to check if employee loginname is unique or not, return boolean value
	private boolean isUnique(String loginName) {
		for (Employee e : emp) {
			if (loginName.equals(e.getLoginName())) {
				return false;
			}
		}
		return true;
	}

	// --------------Private utility function to implement option 3 List Employee
	// List details of all employees if Boss else display personal information of current user
	private void listEmployee() {
		if (currentId == 00000) {
			StringBuilder sb = new StringBuilder();
			sb.append("This is boss, find below list of all employees");
			sb.append("\nEmpId\tLogin\t\tBase Salary\tDate\t\t\tName\t\tPassword");
			for (Employee i : emp) {
				sb.append("\n");
				sb.append(i);
			}
			textspace.setText(sb.toString());
		} else {
			if (currentUser != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("Your details are:");
				sb.append("\nEmployeeId : ");
				sb.append(currentUser.getEmpId());
				sb.append("\nLogin Name : ");
				sb.append(currentUser.getLoginName());
				sb.append("\nPassword : ");
				sb.append(currentUser.getPassword());
				sb.append("\nSalary : ");
				sb.append(currentUser.getSal());
				sb.append("\nDate : ");
				sb.append(currentUser.getDate());
				sb.append("\nName : ");
				sb.append(currentUser.getName());
				textspace.setText(sb.toString());
			} else {
				System.out.println("User details not found");
			}

		}
	}

	// --------------Private utility function to implement option 4 Change employee data
	// ---- Boss can Change employee details: name and salary
	private void changeEmployeeData() {
		boolean found = false;
		double newSal;
		
		if (!EmpIDField.getText().isEmpty() && !baseSalaryField.getText().isEmpty() && !nameField.getText().isEmpty()) {
			double baseSalary;
			int changeID = -1;
			if (currentId == 00000) {
				try{
					baseSalary = Double.parseDouble(baseSalaryField.getText());
					changeID = Integer.parseInt(EmpIDField.getText());
				}catch(NumberFormatException exc){
					buildGui("UpdateEmp");
					textspace.setText("Enter Employee Id and Search");
				}
				for (Employee k : emp) {
					if (k.getEmpId() == changeID) { 
						found = true;
						k.setName(nameField.getText());
						newSal = Double.parseDouble(baseSalaryField.getText());
						k.setSal(newSal);
						textspace.setText("Employee Data Updated");
						nameField.setText("");
						baseSalaryField.setText("");
						EmpIDField.setText("");
						buildGui("BossGUI");
						break;
					}
				}
				if (!found) {
					textspace.setText("Employee not found");
				}

			} 
		} else if(EmpIDField.getText().isEmpty()){
			buildGui("UpdateEmp");
			textspace.setText("Enter Employee Id and Search"); 
		} else if(baseSalaryField.getText().isEmpty()){
			buildGui("UpdateEmp");
			textspace.setText("Enter Employee Id and Search");
		}else if(nameField.getText().isEmpty()){
			buildGui("UpdateEmp");
			textspace.setText("Enter Employee Id and Search");
		}
	}
	
	private void findEmployee() {
		boolean found = false;
		if (currentId == 00000) {
			int changeID = Integer.parseInt(EmpIDField.getText());
			for (Employee k : emp) {
				if (k.getEmpId() == changeID) { // If employee found ask which details to change
					found = true;
					String sal = String.valueOf(k.getSal());
					baseSalaryField.setText(sal);
					nameField.setText(k.getName());
					break;
				}
			}
			if (!found) {
				textspace.setText("Employee not found. Please enter other user Id.\n You can view list of UserId on List Employee");
			}

		} 
	}

	// --------------Private utility function to implement option 5 Terminate or Quit Employee
	// ---- employee can quit when he is logged in, and the Boss can fire anyone.
	private void terminate() {
		int id = currentId;
		boolean found = false;
		if (currentId == 00000) { // If currentId is boss then get the details of employee to be fired.
				id = Integer.parseInt(EmpIDField.getText());
		}
		if(id!=0){
			for (Employee e : emp) {
				if (e.getEmpId() == id) {
					emp.remove(e);// removing from Employee list
					empQuitFired.add(e); // adding employee to QuitFired list
					textspace.setText("Employee Quit/Fired");
					found = true;
					buildGui("BossGUI");
					break;
				}
			}
			if (currentId != 00000) { // Resetting the value after Employee quits
				currentId = -1;
				currentUser = null;
			}
			if (!found) {
				textspace.setText("Employee not found with specified Id");
			}
		}else{
			buildGui("Terminate");
			textspace.setText("You cannot delete Boss");
		}
	}

	// -------------Private utility function to implement option 6 Pay Employee
	private void payEmployees() throws FileNotFoundException {
		
		// Write Pay Employee data to File and Text Area
		
		StringBuilder sbPay = new StringBuilder();
		sbPay.append("\nPayroll Report - " + new Date());
		pw2.println("\nPayroll Report - " + new Date());
		sbPay.append("\n-----------------------------------------------------");
		pw2.println("-----------------------------------------------------");
		sbPay.append("\n\tPay\t\t|ID\t\t\t|Name");
		pw2.println("\tPay\t|ID\t\t|Name");
		sbPay.append("\n-----------------------------------------------------");
		pw2.println("-----------------------------------------------------");
		for (Employee e : temp1) {
			String test = String.format("%12.2f\t|%05d\t\t|%s", e.getSal(), e.getEmpId(), e.getName());
			pw2.println(test);
			sbPay.append("\n"+ test);
		}
		
		textspace.setText(sbPay.toString());

	}

	//---------------------Logout function to write Employee Details to database File
	public void logout() {
		System.out.println("Writting below Employee details to file");
		try {
			FileOutputStream fos = new FileOutputStream("Employee.txt");
			ObjectOutputStream oos = new ObjectOutputStream(fos); // Writing employee file as object file
			try {
				oos.writeObject(emp);
			} finally {
				for (Employee i : emp)
					System.out.println(i);
				System.out.println("Details of Employee who have quit or fired");
				if (empQuitFired.isEmpty() == false)
					for (Employee i : empQuitFired)
						System.out.println(i);
				else
					System.out.println("No employee fired");
			}
			oos.close();
			pw2.close();
		} catch (Exception exp) {
			System.out.println("Exception occured while writing");
		}

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		stage = primaryStage;
		buildGui("BossInfo");
		initial();
		stage.setScene(scene);
		stage.setTitle("Payroll");
		stage.show();
	}

	public void buildGui(String functionCall) {
		String s1 = functionCall;

		GridPane gp = new GridPane();
		VBox vb = new VBox(10);
		StackPane sp = new StackPane();

		if (s1.equals("BossInfo")) {
			
			btSubmit.setText("Submit");
			message.setTextFill(Color.BLACK);
			message.setFont(Arial);
			loginNameField.setFont(Verdana);
			passwordField.setFont(Verdana);
			confirmPasswordField.setFont(Verdana);
			baseSalaryField.setFont(Verdana);
			nameField.setFont(Verdana);
			loginNameLabel.setFont(Arial);
			passwordLabel.setFont(Arial);
			confirmpasswordLabel.setFont(Arial);
			baseSalaryLabel.setFont(Arial);
			nameLabel.setFont(Arial);
			btSubmit.setFont(Arial);
			textspace.setFont(Verdana);

			btSubmit.setPrefWidth(300);
			btMainmenu.setPrefWidth(300);
			loginNameField.setPrefWidth(300);
			passwordField.setPrefWidth(300);
			confirmPasswordField.setPrefWidth(300);
			baseSalaryField.setPrefWidth(300);
			nameField.setPrefWidth(300);

			gp.add(loginNameLabel, 0, 0);
			gp.add(loginNameField, 1, 0);
			gp.add(passwordLabel, 0, 1);
			gp.add(passwordField, 1, 1);
			gp.add(confirmpasswordLabel, 0, 2);
			gp.add(confirmPasswordField, 1, 2);
			gp.add(baseSalaryLabel, 0, 3);
			gp.add(baseSalaryField, 1, 3);
			gp.add(nameLabel, 0, 4);
			gp.add(nameField, 1, 4);
			gp.add(btSubmit, 1, 5);
			gp.setVgap(10);
			gp.setHgap(10);
			gp.setPadding(new Insets(20, 20, 20, 20));
			gp.setAlignment(Pos.CENTER);

			vb.getChildren().addAll(message, gp, textspace);
			vb.setAlignment(Pos.CENTER);
			sp.getChildren().addAll(vb);
			scene = new Scene(sp, 800, 600);
			stage.setScene(scene);
		}

		if (s1.equals("Login")) {
			message.setText("Welcome to Payroll System!");
			message.setFont(Arial);
			
			loginNameField.setText("");
			passwordField.setText("");
			
			btSubmit.setFont(Arial);
			btLogin.setFont(Arial);

			gp.add(loginNameLabel, 0, 0);
			gp.add(loginNameField, 1, 0);
			gp.add(passwordLabel, 0, 1);
			gp.add(passwordField, 1, 1);
			gp.add(btLogin, 1, 2);
			gp.setVgap(10);
			gp.setHgap(10);
			gp.setPadding(new Insets(20, 20, 20, 20));
			gp.setAlignment(Pos.CENTER);
			vb.getChildren().addAll(message, gp);
			vb.setAlignment(Pos.CENTER);

			btLogin.setOnAction(e -> {
				dologin();
				loginNameField.setText("");
				passwordField.setText("");
			});

			sp.getChildren().addAll(vb);
			scene = new Scene(sp, 800, 600);
			stage.setScene(scene);
		}

		if (s1.equals("BossGUI")) {
			vb.setPadding(new Insets(20, 20, 20, 20));
			message.setText("Welcome Boss. Choose an option.");
			
			textspace.setText("");
			
			btNewEmp.setFont(Arial);
			btListEmp.setFont(Arial);
			btChangeEmpData.setFont(Arial);
			btPayEmp.setFont(Arial);
			btLogout.setFont(Arial);

			btNewEmp.setPrefWidth(300);
			btListEmp.setPrefWidth(300);
			btChangeEmpData.setPrefWidth(300);
			btPayEmp.setPrefWidth(300);
			btLogout.setPrefWidth(300);

			vb.getChildren().addAll(message, btNewEmp, btListEmp, btChangeEmpData, btPayEmp, btLogout,
					textspace);
			vb.setAlignment(Pos.CENTER);
			
			btNewEmp.setOnAction(e -> {
				textspace.setText("");
				loginNameField.setText("");
				passwordField.setText("");
				confirmPasswordField.setText("");
				baseSalaryField.setText("");
				nameField.setText("");
				buildGui("AddNewEmp");
			});
			
			btListEmp.setOnAction(e -> listEmployee());
			
			btChangeEmpData.setOnAction(e -> {
				textspace.setText("");
				EmpIDField.setText("");
				baseSalaryField.setText("");
				nameField.setText("");
				buildGui("UpdateEmp");
			});
			
			btPayEmp.setOnAction(e -> {
				textspace.setText("");
				baseSalaryField.setText("");
				buildGui("PayEmp");
			});
			
			btLogout.setOnAction(e -> {
				textspace.setText("");
				logout(); 
				buildGui("Login");
			});

			sp.getChildren().addAll(vb);
			scene = new Scene(sp, 800, 600);
			stage.setScene(scene);
		}
		
		if(s1.equals("AddNewEmp")){
			textspace.setText("Enter employees Details");
			message.setText("Enter New Employee Details");
			
			btSubmit.setText("Submit");
			btMainmenu.setPrefWidth(300);
			btSubmit.setPrefWidth(300);

			rbSalaried.setToggleGroup(empType);
			rbSalaried.setUserData("S");
			rbSalaried.setSelected(true);
			rbHourly.setToggleGroup(empType);
			rbHourly.setUserData("H");

			btMainmenu.setFont(Arial);
			rbSalaried.setFont(Arial);
			rbHourly.setFont(Arial);

			gp.add(loginNameLabel, 0, 1);
			gp.add(loginNameField, 1, 1);
			gp.add(passwordLabel, 0, 2);
			gp.add(passwordField, 1, 2);
			gp.add(confirmpasswordLabel, 0, 3);
			gp.add(confirmPasswordField, 1, 3);
			gp.add(baseSalaryLabel, 0, 4);
			gp.add(baseSalaryField, 1, 4);
			gp.add(nameLabel, 0, 5);
			gp.add(nameField, 1, 5);
			gp.add(rbSalaried, 0, 6);
			gp.add(rbHourly, 1, 6);
			gp.add(btSubmit, 0, 7);
			gp.add(btMainmenu, 1, 7);
			gp.setVgap(10);
			gp.setHgap(10);
			gp.setPadding(new Insets(20, 20, 20, 20));
			gp.setAlignment(Pos.CENTER);

			vb.getChildren().addAll(message, gp, textspace);
			vb.setAlignment(Pos.CENTER);
			
			btSubmit.setOnAction(e -> {
				newEmployee();
				});
			
			btMainmenu.setOnAction(e -> {textspace.setText(""); buildGui("BossGUI");});
			
			sp.getChildren().addAll(vb);
			scene = new Scene(sp, 800, 600);
			stage.setScene(scene);
		}
		
		if(s1.equals("EmpGUI")){
			message.setText("Choose an option by clicking the buttons.");

			btListEmp.setFont(Arial);
			btTerminateEmp.setFont(Arial);
			btLogout.setFont(Arial);
			btListEmp.setPrefWidth(300);
			btTerminateEmp.setPrefWidth(300);
			btLogout.setPrefWidth(300);

			btListEmp.setOnAction(e -> listEmployee());
			
			btTerminateEmp.setOnAction(e -> {
				textspace.setText("");
				buildGui("Terminate");
			});
			
			btLogout.setOnAction(e -> {
				textspace.setText("");
				buildGui("Login");
			});

			vb.getChildren().addAll(message, btListEmp, btTerminateEmp, btLogout, textspace);
			vb.setAlignment(Pos.CENTER);
			vb.setPadding(new Insets(10));
			sp.getChildren().addAll(vb);
			scene = new Scene(sp, 800, 600);
			stage.setScene(scene);
		}
		
		if(s1.equals("Terminate")){
			EmpIDField.setPrefWidth(300);
			EmpIDField.setFont(Verdana);
			EmpIDLabel.setFont(Arial);
			btMainmenu.setFont(Arial);
			btSubmit.setText("Submit");

			gp.add(EmpIDLabel, 0, 0);
			gp.add(EmpIDField, 1, 0);
			gp.add(btSubmit, 1, 1);
			gp.add(btMainmenu, 2, 1);

			gp.setVgap(10);
			gp.setHgap(10);
			gp.setPadding(new Insets(20,20,20,20));
			gp.setAlignment(Pos.CENTER);
			
			textspace.setText("Enter Employee Id to terminate");
			
			if(currentId != 0){
				message.setText("Are you sure you want to quit? Press Submit to quit!");
				gp.getChildren().remove(EmpIDLabel);
				gp.getChildren().remove(EmpIDField);
				btMainmenu.setOnAction(e -> {textspace.setText(""); buildGui("EmpGUI");});
				btSubmit.setOnAction( e-> {terminate(); logout(); buildGui("Login");});
				vb.getChildren().addAll(message, gp);
			}

			vb.setAlignment(Pos.CENTER);
			sp.getChildren().addAll(vb);
			scene = new Scene(sp, 800, 600);
			stage.setScene(scene);
		}
		
		if(s1.equals("UpdateEmp")){
			message.setText("Enter the ID of the employee and Click Search button");
			btMainmenu.setText("Main Menu");
			btTerminate.setText("Terminate");
			btSubmit.setText("Update");
			EmpIDField.setFont(Verdana);
			EmpIDLabel.setFont(Arial);
			btMainmenu.setFont(Arial);
			btTerminate.setFont(Arial);
			btSearch.setFont(Arial);
			EmpIDField.setPrefWidth(300);
			btMainmenu.setPrefWidth(300);
			btTerminate.setPrefWidth(300);
			btSubmit.setPrefWidth(300);
			btSearch.setPrefWidth(100);

			gp.add(EmpIDLabel, 0, 0);
			gp.add(EmpIDField, 1, 0);
			gp.add(btSearch, 2, 0);
			gp.add(baseSalaryLabel, 0, 1);
			gp.add(baseSalaryField, 1, 1);
			gp.add(nameLabel, 0, 2);
			gp.add(nameField, 1, 2);
			gp.add(btSubmit, 0, 3);
			gp.add(btTerminate, 1, 3);
			gp.add(btMainmenu, 2, 3);

			gp.setVgap(10);
			gp.setHgap(10);
			gp.setPadding(new Insets(20,20,20,20));
			gp.setAlignment(Pos.CENTER);

			vb.getChildren().addAll(message, gp, textspace);
			vb.setAlignment(Pos.CENTER);
			
			btSearch.setOnAction(e -> {
				if(!EmpIDField.getText().isEmpty()){
					findEmployee();
				}					
				else
					textspace.setText("Enter Employee Id");
			});
			
			btSubmit.setOnAction(e -> {
				changeEmployeeData();
				EmpIDField.setText("");
				nameField.setText("");
				baseSalaryField.setText("");
			});
			
			btMainmenu.setOnAction(e -> {textspace.setText(""); buildGui("BossGUI");});
			
			btTerminate.setOnAction(
					e-> {
						if(!EmpIDField.getText().isEmpty()){
							terminate();
							EmpIDField.setText("");
						}					
						else
							textspace.setText("Enter Employee Id to Terminate");
					});

			sp.getChildren().addAll(vb);
			scene = new Scene(sp, 800, 600);
			stage.setScene(scene);
		}
		
		if(s1.equals("PayEmp")){
			message.setText("Pay Employees for two weeks");
			btMainmenu.setText("Main Menu");
			btSubmit.setText("Submit");
			btMainmenu.setFont(Arial);
			payEmpText.setFont(Arial);
			hoursLabel.setFont(Arial);

			gp.add(hoursLabel, 0, 0);
			gp.add(baseSalaryField, 1, 0);
			gp.add(btSubmit, 1, 1);
			gp.add(btMainmenu, 2, 1);
			gp.setVgap(20);
			gp.setHgap(20);
			gp.setPadding(new Insets(20,20,20,20));
			gp.setAlignment(Pos.CENTER);
			payEmpText.setText ("Add Hours for Hourly Paid Employee ");
			vb.getChildren().addAll(message, payEmpText, gp, textspace);
			vb.setAlignment(Pos.CENTER);
			
			count = 0;
			textspace.setText("Boss is salaried. Please click Submit ");
			temp1 = new ArrayList<>();
			btSubmit.setOnAction( e-> {
				if(count<emp.size()){
					Employee temp = emp.get(count);
					
					String date1 = temp.getDate();
					Date date;
					try {
						date = new SimpleDateFormat("MM/dd/yyyy").parse(date1);
						if(temp instanceof Hourly){
							if(!baseSalaryField.getText().isEmpty()){
								int h = Integer.parseInt(baseSalaryField.getText());
								((Hourly) temp).setHour(h);
								temp1.add(new Hourly(temp.getEmpId(), temp.getLoginName(), temp.getPassword(), temp.getPay(), date, temp.getName()));
							}else{
								count --;
							}
						}else{
							temp1.add(new Salaried(temp.getEmpId(), temp.getLoginName(), temp.getPassword(), temp.getPay(), date, temp.getName()));
						}
						count++;
						if(count < emp.size()){
							if(emp.get(count) instanceof Hourly){
								textspace.setText("Enter the hours worked by employee : " + emp.get(count).getName() + " and Press Submit ");
							} else {
								textspace.setText("Employee : " + emp.get(count).getName() + " is Salaried. Press Submit ");
							}
						}else{
							textspace.setText("All employee Hours added ");
							payEmployees();
						}
						
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}				
			});
			
			btMainmenu.setOnAction(e -> {textspace.setText(""); buildGui("BossGUI");});
			
			sp.getChildren().addAll(vb);
			scene = new Scene(sp, 800, 600);
			stage.setScene(scene);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}