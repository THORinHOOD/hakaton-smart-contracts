package org.hakaton;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public class Operation {

    @Property()
    private final Long amount;

    @Property()
    private final Long userId;

    @Property()
    private final String timestamp;

    @Property()
    private final String operationType;

    @Property()
    private final String id;

    @Property()
    private final String description;

    public Operation(@JsonProperty("amount") Long amount,
                     @JsonProperty("userId") Long userId,
                     @JsonProperty("timestamp") String timestamp,
                     @JsonProperty("operationType") String operationType,
                     @JsonProperty("id") String id,
                     @JsonProperty("description") String description) {
        this.amount = amount;
        this.userId = userId;
        this.timestamp = timestamp;
        this.operationType = operationType;
        this.id = id;
        this.description = description;
    }

    public Long getAmount() {
        return amount;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getOperationType() {
        return operationType;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static String generateId(Long userId, String timestamp, String operationType) {
        return operationType + "_" + userId + "_" + timestamp;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Operation other = (Operation) obj;

        return Objects.equals(userId, other.getUserId())
                && Objects.equals(amount, other.getAmount())
                && Objects.equals(timestamp, other.getTimestamp())
                && Objects.equals(operationType, other.getOperationType())
                && Objects.equals(description, other.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getAmount(), getTimestamp(), getOperationType(), getDescription());
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [userId=" + userId +
                ", amount=" + amount + ", timestamp=" + timestamp + ", operationType=" + operationType + ", id="
                + id + ", description=" + description + "]";
    }
}
