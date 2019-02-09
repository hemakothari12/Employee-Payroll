/** Salaried.java
    Program 6: GUI.
   @author    Hema Kothari
   @Date   12/01/2018
*/

import java.io.Serializable;
import java.util.Date;

public class Salaried extends Employee implements Serializable {

	//--------Derived class three argument constructor to match employee constructor
	public Salaried(String loginName, String password, double baseSalary, String name) {
		super(loginName, password, baseSalary, name);
	}

	//--------Derived class five argument constructor to match employee constructor
	public Salaried(int employeeID, String loginName, String password, double baseSalary, Date date, String name) {
		super(employeeID, loginName, password, baseSalary, date, name);
	}
	
	//Overriding the abstract method inherited from employee class 
	@Override
	public double getPay() {
		return baseSalary / 24;
	}

}

