/*
package condorcet.Models.Entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "flight")
public class Flight {
    private int id;
    private String flightNumber;
    private Route route;
private Aircraft aircraft;
    private Set<Passenger> passengers = new HashSet<>();


    public Flight() {
    }

    public Flight(int id, String flightNumber, Route route, Aircraft aircraft) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.route = route;
        this.aircraft = aircraft;
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

    @Column(name = "flight_number", length = 45)
    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id")
    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aircraft_id")
    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "flight")
    public Set<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(Set<Passenger> passengers) {
        this.passengers = passengers;
    }

}*/
