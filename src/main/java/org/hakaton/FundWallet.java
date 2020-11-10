package org.hakaton;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public class FundWallet {

    @Property()
    private final Long balance;

    public FundWallet(@JsonProperty("balance") final Long balance) {
        this.balance = balance;
    }

    public Long getBalance() {
        return balance;
    }

    public FundWallet setBalance(Long balance) {
        return new FundWallet(balance);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        FundWallet other = (FundWallet) obj;

        return Objects.equals(balance, other.getBalance());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBalance(), "fund_wallet");
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [balance=" + balance + "]";
    }
}
