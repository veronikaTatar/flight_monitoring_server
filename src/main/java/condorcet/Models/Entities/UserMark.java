package condorcet.Models.Entities;

import javax.persistence.*;

@Entity
@Table(name = "user_mark")
public class UserMark implements Comparable<UserMark> {
    private int id;
    private int mark;
    private Flight flight;
    private User user;

    public UserMark() {
    }

    public UserMark(int id, int mark, Flight flight, User user) {
        this.id = id;
        this.mark = mark;
        this.flight = flight;
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "mark")
    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "flight_id")
    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int compareTo(UserMark o) {
        return Integer.compare(this.mark, o.mark);
    }
}