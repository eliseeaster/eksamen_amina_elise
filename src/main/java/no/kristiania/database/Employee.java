package no.kristiania.database;

public class Employee {

    private String email;
    private String firstName;
    private String lastName;
    private Long id;

    public String getEmail(){return email;}

    public void setEmail(String email){
        this.email = email;
    }

    public String getFirstName(){return firstName;}

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getLastName(){return lastName;}

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public Long getId(){return id;}

    public void setId(Long id){
        this.id = id;
    }

    public String getName() {
        return null;
    }

    public void setName(String employee_name) {
    }
}
