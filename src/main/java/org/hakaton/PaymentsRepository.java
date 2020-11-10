package org.hakaton;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.ArrayList;
import java.util.List;

@Contract(
        name = "payments",
        info = @Info(
                title = "Payments Transfer",
                description = "Great heart hakaton",
                version = "0.0.1"))
@Default
public final class PaymentsRepository implements ContractInterface {

    private static final String FUND_WALLET = "fund_wallet";
    private final Genson genson = new Genson();

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitLedger(final Context ctx) {
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Operation addDonation(final Context ctx, final Long userId, final Long amount, final String timestamp,
                                 final String description) {
        ChaincodeStub stub = ctx.getStub();
        if (amount < 0) {
            throw new ChaincodeException("Can\'t donate a negative number amount", "");
        }
        FundWallet fundWallet = readFundWallet(stub);
        setNewBalance(stub, fundWallet.setBalance(fundWallet.getBalance() + amount));
        Operation operation = createOperation(OperationType.DONATION, userId, amount, timestamp, description);
        String assetJSON = genson.serialize(operation);
        stub.putStringState(operation.getId(), assetJSON);
        return operation;
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Operation addConsumption(final Context ctx, final Long userId, final Long amount, final String timestamp,
                                    final String description) {
        ChaincodeStub stub = ctx.getStub();
        FundWallet fundWallet = readFundWallet(stub);
        if (fundWallet.getBalance() - amount < 0) {
            throw new ChaincodeException(String.format("Can\'t take more money than in the fund (balance : %d, " +
                    "amount : %d)", fundWallet.getBalance(), amount), "");
        }
        setNewBalance(stub, fundWallet.setBalance(fundWallet.getBalance() - amount));
        Operation operation = createOperation(OperationType.CONSUMPTION, userId, amount, timestamp, description);
        String assetJSON = genson.serialize(operation);
        stub.putStringState(operation.getId(), assetJSON);
        return operation;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Operation readOperation(final Context ctx, final Long userId, final String timestamp,
                                   final String operationType) {
        String paymentJSON = "shit";
        try {
            ChaincodeStub stub = ctx.getStub();
            String operationId = Operation.generateId(userId, timestamp, operationType);
            paymentJSON = stub.getStringState(operationId);

            if (paymentJSON == null || paymentJSON.isEmpty()) {
                String errorMessage = String.format("Operation %s does not exist", operationId);
                throw new ChaincodeException(errorMessage, errorMessage);
            }

            return genson.deserialize(paymentJSON, Operation.class);
        } catch (Exception exception) {
            throw new ChaincodeException(exception.toString() + paymentJSON, exception.toString() + paymentJSON);
        }
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public FundWallet readFundBalance(final Context ctx) {
        return readFundWallet(ctx.getStub());
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public List<Operation> readAllOperations(final Context context) {
        ChaincodeStub stub = context.getStub();
        List<Operation> queryResults = new ArrayList<>();
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");
        for (KeyValue result: results) {
            Operation operation = genson.deserialize(result.getStringValue(), Operation.class);
            queryResults.add(operation);
        }
        return queryResults;
    }

    private void setNewBalance(ChaincodeStub stub, FundWallet fundWallet) {
        String assetJSON = genson.serialize(fundWallet);
        stub.putStringState(FUND_WALLET, assetJSON);
    }

    private FundWallet readFundWallet(ChaincodeStub stub) {
        String fundJson;
        try {
            fundJson = stub.getStringState(FUND_WALLET);

            if (fundJson == null || fundJson.isEmpty()) {
                FundWallet fundWallet = new FundWallet(0L);
                stub.putStringState(FUND_WALLET, genson.serialize(fundWallet));
                return fundWallet;
            }

            return genson.deserialize(fundJson, FundWallet.class);
        } catch (Exception exception) {
            throw new ChaincodeException(exception.toString(), "");
        }
    }

    private Operation createOperation(final OperationType operationType, final Long userId, final Long amount,
                                      final String timestamp, final String description) {
        return new Operation(amount, userId, timestamp, operationType.toString(),
                Operation.generateId(userId, timestamp, operationType.toString()), description);
    }

}
