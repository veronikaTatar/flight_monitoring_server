package condorcet.Models.Entities;

import javax.persistence.*;

@Entity
@Table(name = "passenger")
public class Passenger {
   private int id;
    private int placeNumber;
   // private Flight flight;
    private PersonData personData;

    public Passenger() {
    }

    public Passenger(int id, int placeNumber, /*Flight flight,*/ PersonData personData) {
        this.id = id;
        this.placeNumber = placeNumber;
       // this.flight = flight;
        this.personData = personData;
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

    @Column(name = "place_number")
    public int getPlaceNumber() {
        return placeNumber;
    }

    public void setPlaceNumber(int placeNumber) {
        this.placeNumber = placeNumber;
    }

    /*@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "flight_id")
    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }*/

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_data_id")
    public PersonData getPersonData() {
        return personData;
    }

    public void setPersonData(PersonData personData) {
        this.personData = personData;
    }
}