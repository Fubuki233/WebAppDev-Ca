/**
 * Detailed description of the class.
 *
 * @author YunHe / SunRui
 * @date 2025-10-08
 * @version 1.1
 */

package sg.com.aori.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "Customer_Address")
public class CustomerAddress {

    @Id
    @Column(name = "address_id", length = 36, nullable = false)
    private String addressId = UUID.randomUUID().toString();

    @Column(name = "customer_id", length = 36, nullable = false)
    private String customerId;

    @NotBlank
    @Length(max = 100)
    @Column(name = "recipient_name", length = 100, nullable = false)
    private String recipientName;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @NotBlank
    @Length(max = 255)
    @Column(name = "address_line1", length = 255, nullable = false)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @NotBlank
    @Length(max = 100)
    @Column(name = "city", length = 100, nullable = false)
    private String city;

    @NotBlank
    @Length(max = 20)
    @Column(name = "postal_code", length = 20, nullable = false)
    private String postalCode;

    @NotBlank
    @Length(max = 100)
    @Column(name = "country", length = 100, nullable = false)
    private String country;

    @Column(name = "is_billing", nullable = false)
    private Boolean isBilling = false;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public CustomerAddress() {
    }

    public CustomerAddress(String customerId, String recipientName, String addressLine1, String city, String postalCode,
            String country) {
        this.customerId = customerId;
        this.recipientName = recipientName;
        this.addressLine1 = addressLine1;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Boolean getIsBilling() {
        return isBilling;
    }

    public void setIsBilling(Boolean isBilling) {
        this.isBilling = isBilling;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "{" +
                "addressId='" + addressId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", recipientName='" + recipientName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                ", isBilling=" + isBilling +
                ", isDefault=" + isDefault +
                ", createdAt=" + createdAt +
                '}';
    }
}
