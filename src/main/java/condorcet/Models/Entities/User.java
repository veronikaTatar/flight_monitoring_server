package condorcet.Models.Entities;

import condorcet.Enums.Roles;
import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {
    private int id;
    private String name;
    private String login;
    private String password;
    private Roles role;
    private PersonData personData;

    public User() {}

    public User(String name, String login, String password, Roles role, PersonData personData) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.role = role;
        this.personData = personData;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Рекомендуется для MySQL
    @Column(name = "id")
    public int getId() {
        return id;
    }

    @Column(name = "name", length = 45, nullable = false)
    public String getName() {
        return name;
    }

    @Column(name = "login", length = 45, unique = true, nullable = false)
    public String getLogin() {
        return login;
    }

    @Column(name = "password", length = 100, nullable = false)  // Увеличена длина для хэша
    public String getPassword() {
        return password;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    public Roles getRole() {
        return role;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_data_id", nullable = false)
    public PersonData getPersonData() {
        return personData;
    }

    // Сеттеры
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public void setPersonData(PersonData personData) {
        this.personData = personData;
    }
}