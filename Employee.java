/** Employee.java
   Program 6: GUI.
   @author    Hema Kothari
   @Date   12/01/2018
*/

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Employee implements Serializable {

	// --------------Employee data Member changed to protected
	protected String loginName;
	protected double baseSalary;	//This is referred to store hourly or salaried payrate
	protected String name;
	protected Date date;
	protected final int employeeID;
	protected static int nextID;
	protected String password;

	// --------------Constructor with 3 parameter to initialize all data members
	public Employee(String loginName, String password, double baseSalary, String name) {
		this.loginName = loginName;
		this.password = password;
		this.baseSalary = baseSalary;
		this.name = name;
		this.date = new Date();
		this.employeeID = Employee.nextID; // EmployeeID generated using static class member
		nextID++; // Incremented nextID
	}

	// --------------Constructor with 5 parameters to read value from Employee File
	public Employee(int employeeID, String loginName, String password, double baseSalary, Date date, String name) {
		this.loginName = loginName;
		this.baseSalary = baseSalary;
		this.name = name;
		this.date = date;
		this.employeeID = employeeID;
		this.password = password;
		nextID = ++employeeID;	//Assigned nextID to next available ID
	}

	// --------------Set function for salary
	public void setSal(double Sal) {
		baseSalary = Sal;
	}

	// --------------toString function that will format data members of Employee
	public String toString() {
		return String.format("%05d\t%s\t\t%.2f\t\t%s\t%s\t%s", employeeID, loginName, baseSalary, this.getDate(), name, password.toString());
	}
	
	//--------------Set function for Name
	public void setName(String name) {
		this.name = name;
	}

	// --------------Getter to convert the date format
	public String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		return sdf.format(date);
	}

	// --------------Getter for loginName
	public String getLoginName() {
		return loginName;
	}

	// --------------Getter for Name
	public String getName() {
		return name;
	}

	// --------------Getter for EmpID
	public int getEmpId() {
		return employeeID;
	}
	
	//-------------------Getter for salary
	public double getSal() {
		return baseSalary;
	}
	
	//-------------------Setter for password
	public void setPassword(String password) {
		this.password = password;
	}
	
	//-------------------Getter for Password
	public String getPassword(){
		return password;
	}
	
	//------------------Abstract method defined in employee class that will be implemented in hourly and salaried.
	public abstract double getPay();
}