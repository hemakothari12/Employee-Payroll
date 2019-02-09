/** Hourly.java
    Program 6: GUI.
   @author    Hema Kothari
   @Date   12/01/2018
*/

import java.io.Serializable;
import java.util.Date;
import java.util.Scanner;

public class Hourly extends Employee implements Serializable {
	private int hour;
	
	//--------Derived class three argument constructor to match employee constructor
	public Hourly(String loginName, String password, double baseSalary, String name) {
		super(loginName, password, baseSalary, name);
	}

	//--------Derived class five argument constructor to match employee constructor
	public Hourly(int employeeID, String loginName, String password, double baseSalary, Date date, String name) {
		super(employeeID, loginName, password, baseSalary, date, name);
	}
	
	//Overriding the abstract method inherited from employee class
	@Override
	public double getPay() {
		return baseSalary * hour;
	}
	
	//-----------------Get to get hours
	public int getHour() {
		return hour;
	}

	//-----------------------Set to set hours
	public void setHour(int hour) {
		this.hour = hour;
	}

}
