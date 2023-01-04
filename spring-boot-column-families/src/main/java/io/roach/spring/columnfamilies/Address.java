package io.roach.spring.columnfamilies;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address {
    @Column(length = 255, nullable = false)
    private String address1;

    @Column(length = 255)
    private String address2;

    @Column(length = 255)
    private String city;

    @Column(length = 255, nullable = false)
    private String postcode;

    @Column(length = 255, nullable = false)
    private String country;

    protected Address() {
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
