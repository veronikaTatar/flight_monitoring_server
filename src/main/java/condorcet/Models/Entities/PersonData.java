package condorcet.Models.Entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "person_data")
public class PersonData {

    private int id;
    private int age;
    private String mail;
    private String address;
    private String sex;
    private Set<User> users = new HashSet<>();
    private Set<Passenger> passengers = new HashSet<>();

    public PersonData() {
    }

    public PersonData(int id, int age, String mail, String address, String sex, Set<User> users, Set<Passenger> passengers) {
        this.id = id;
        this.age = age;
        this.mail = mail;
        this.address = address;
        this.sex = sex;
        this.users = users;
        this.passengers = passengers;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "age")
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Column(name = "mail", length = 45)
    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Column(name = "address", length = 45)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "sex", length = 1)
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "personData")
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "personData")
    public Set<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(Set<Passenger> passengers) {
        this.passengers = passengers;
    }
}