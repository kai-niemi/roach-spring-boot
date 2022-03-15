package io.roach.spring.batch.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Address adddress = new Address();

        protected Builder() {
        }

        public Builder setAddress1(String address1) {
            adddress.address1 = address1;
            return this;
        }

        public Builder setAddress2(String address2) {
            adddress.address2 = address2;
            return this;
        }

        public Builder setCity(String city) {
            adddress.city = city;
            return this;
        }

        public Builder setPostcode(String postcode) {
            adddress.postcode = postcode;
            return this;
        }

        public Builder setCountry(String country) {
            adddress.country = country;
            return this;
        }

        public Address build() {
            return adddress;
        }
    }

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

    /**
     * No-arg constructor for JavaBean tools.
     */
    protected Address() {
    }

    public Address(String address1, String address2, String city, String postcode, String country) {
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.postcode = postcode;
        this.country = country;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return "Address{" +
                "address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", city='" + city + '\'' +
                ", postcode='" + postcode + '\'' +
                ", country=" + country +
                '}';
    }
}
